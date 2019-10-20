package in.ind.core.jobs;

import in.ind.core.Constants;
import in.ind.core.exceptions.NonRecoverableException;
import in.ind.core.helpers.JobContextHelper;
import in.ind.core.models.JobDetailInfo;
import in.ind.core.models.ServiceResponse;
import in.ind.core.services.RestApiService;
import in.ind.core.utils.JsonUtility;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.spi.OperableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * The type Invoke api job.
 *
 * Created by abhay on 20/10/19.
 */
@Slf4j
public class InvokeApiJob implements Job {
    @Autowired
    private RestApiService service;
    @Autowired
    private JobContextHelper jobContextHelper;
    private static final long RETRY_INTERVAL_INITIAL = 10000;
    private static final int MAX_ATTEMPT = 5;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException{
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        if(!dataMap.containsKey(Constants.COUNT)) {
            dataMap.put(Constants.COUNT, 0);
        }
        int count = dataMap.getIntValue(Constants.COUNT);
        JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
        log.info(String.format("Executing job %s with group %s with attempt %d ", jobKey.getName(),
                jobKey.getGroup(), count));
        // allow 5 retries
        if(count > MAX_ATTEMPT) {
            log.warn(String.format("All attempt completed for job %s and group %s", jobKey.getName(),
                    jobKey.getGroup()));
        } else {
            try {
                executeJob(jobKey, jobExecutionContext, dataMap);
            } catch (NonRecoverableException ex) {
                log.error(String.format("Exception occurred during execution for job %s, group %s and message %s",
                        jobKey.getName(), jobKey.getGroup(), ex.getMessage()), ex.getCause());
            } catch (Throwable ex) {
                handleRetry(count, jobExecutionContext, dataMap, jobKey, ex);
            }
        }
    }
    private void executeJob(JobKey jobKey, JobExecutionContext jobExecutionContext, JobDataMap dataMap)
            throws NonRecoverableException {
        log.info(String.format("Executing job %s with group %s and data Map %s",
                jobKey.getName(), jobKey.getGroup(), JsonUtility.toString(dataMap)));
        ServiceResponse serviceResponse = service.invokeApi(jobExecutionContext);
        if(!StringUtils.isEmpty(serviceResponse.getStatusCode()) &&
                serviceResponse.getStatusCode().equals(Constants.SUCCESS_MESSAGE)){
            JobDetailInfo jobDetailInfo = JsonUtility.fromString(JsonUtility.toString(serviceResponse.getPayload()),
                    JobDetailInfo.class);
            log.info(String.format("Executed job %s, group %s with response %s", jobDetailInfo.getName(),
                    jobDetailInfo.getGroup(), JsonUtility.toString(jobDetailInfo)));
            dataMap.putAsString("count", 0);
        } else {
            log.warn(String.format("Downstream system returning error %s for job %s and group %s, " +
                            "returning response %s ", JsonUtility.toString(serviceResponse), jobKey.getName(),
                    jobKey.getGroup(), JsonUtility.toString(serviceResponse)));
        }
    }

    private void handleRetry(int retries, JobExecutionContext jobExecutionContext, JobDataMap dataMap, JobKey jobKey,
                             Throwable ex) throws JobExecutionException {
        log.warn("Retry job " + jobExecutionContext.getJobDetail());

        // increment the number of retries
        dataMap.putAsString(Constants.COUNT, retries + 1);

        final JobDetail job = jobExecutionContext
                .getJobDetail()
                .getJobBuilder()
                // to track the number of retries
                .withIdentity(jobExecutionContext.getJobDetail().getKey().getName() + " - " + retries, "FailingJobsGroup")
                .usingJobData(dataMap)
                .build();

        final OperableTrigger trigger = (OperableTrigger) TriggerBuilder
                .newTrigger()
                .forJob(job)
                .startAt(new Date(jobExecutionContext.getFireTime().getTime() + (retries * RETRY_INTERVAL_INITIAL)))
                .build();

        try {
            // schedule another job to avoid blocking threads
            jobExecutionContext.getScheduler().scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("Error creating job");
            throw new JobExecutionException(e);
        }
    }
}
