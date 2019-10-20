package in.ind.core.helpers;

import in.ind.core.Constants;
import in.ind.core.exceptions.NonRecoverableException;
import in.ind.core.models.JobDetailInfo;
import in.ind.core.utils.DateUtils;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * The type Job context helper.
 *
 * Created by abhay on 20/10/19.
 */
@Service
public class JobContextHelper {

    /**
     * Gets job detail info from context.
     *
     * @param jobExecutionContext the job execution context
     * @return the job detail info from context
     */
    public JobDetailInfo getJobDetailInfoFromContext(JobExecutionContext jobExecutionContext) {
        JobDetailInfo jobDetailInfo = new JobDetailInfo();
        jobDetailInfo.setName(jobExecutionContext.getJobDetail().getKey().getName());
        jobDetailInfo.setGroup(jobExecutionContext.getJobDetail().getKey().getGroup());
        jobDetailInfo.setParameters(jobExecutionContext.getJobDetail().getJobDataMap());
        jobDetailInfo.setStatus(Boolean.TRUE);
        jobDetailInfo.setStartAt(DateUtils.getCurrentUTCTimestamp());
        return jobDetailInfo;
    }

    /**
     * Gets end point url.
     *
     * @param jobDetailInfo the job detail info
     * @return the end point url
     * @throws NonRecoverableException the non recoverable exception
     */
    public String getEndPointUrl(JobDetailInfo jobDetailInfo) throws NonRecoverableException {
        Map<String, ?> dataMap = jobDetailInfo.getParameters();
        if(dataMap.containsKey(Constants.WEBHOOK_URL) && dataMap.get(Constants.WEBHOOK_URL) != null) {
            return (String)dataMap.get(Constants.WEBHOOK_URL);
        }
        throw new NonRecoverableException(String.format("%s Url can not be null for job %s and group %s",
                Constants.WEBHOOK_URL, jobDetailInfo.getName(), jobDetailInfo.getGroup()),
                Constants.ExceptionCode.WEBHOOK_URL_MISSING);
    }

    /**
     * Validate job name and group job context helper.
     *
     * @param jobDetailInfo the job detail info
     * @return the job context helper
     * @throws NonRecoverableException the non recoverable exception
     */
    public JobContextHelper validateJobNameAndGroup(JobDetailInfo jobDetailInfo) throws NonRecoverableException {
        if(StringUtils.isEmpty(jobDetailInfo.getName()) || StringUtils.isEmpty(jobDetailInfo.getGroup())) {
            throw new NonRecoverableException("JobName and group name can not be null",
                    Constants.ExceptionCode.JOB_NAME_AND_GROUP_CAN_NOT_BE_EMPTY_ERROR);
        }
        return this;
    }

    /**
     * Validate schedule data job context helper.
     *
     * @param jobDetailInfo the job detail info
     * @return the job context helper
     * @throws NonRecoverableException the non recoverable exception
     */
    public JobContextHelper validateScheduleData(JobDetailInfo jobDetailInfo) throws NonRecoverableException {
        validateJobNameAndGroup(jobDetailInfo);
        if(StringUtils.isEmpty(jobDetailInfo.getCronExpression()) || jobDetailInfo.getStatus() == null) {
            throw new NonRecoverableException("Cron Expression and status can not be empty",
                    Constants.ExceptionCode.CRON_EXPRESSION_AND_STATUS_EMPTY_ERROR);
        }
        Map<String, ?> dataMap = jobDetailInfo.getParameters();
        if(CollectionUtils.isEmpty(dataMap) || !dataMap.containsKey(Constants.WEBHOOK_URL)) {
            throw new NonRecoverableException("Webhook URL can not be empty",
                    Constants.ExceptionCode.WEBHOOK_URL_EMPTY_ERROR);
        }
        return this;
    }


}
