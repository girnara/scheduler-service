package in.ind.core.services;

import in.ind.core.Constants;
import in.ind.core.exceptions.NonRecoverableException;
import in.ind.core.exceptions.RecoverableException;
import in.ind.core.helpers.JobContextHelper;
import in.ind.core.jobs.InvokeApiJob;
import in.ind.core.models.JobDetailInfo;
import in.ind.core.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * The type Scheduler service.
 *
 * Created by abhay on 20/10/19.
 */
@Slf4j
@Service
public class SchedulerService {
    @Autowired
    private SchedulerFactoryBean schedulerFactory;
    @Autowired
    private JobContextHelper jobContextHelper;

    /**
     * Schedule job detail info.
     *
     * @param jobDetailInfo the job detail info
     * @return the job detail info
     * @throws NonRecoverableException the non recoverable exception
     * @throws RecoverableException    the recoverable exception
     */
    public JobDetailInfo schedule(JobDetailInfo jobDetailInfo) throws NonRecoverableException, RecoverableException {
        jobContextHelper.validateScheduleData(jobDetailInfo);
        JobKey jobKey = new JobKey(jobDetailInfo.getName(), jobDetailInfo.getGroup());
        Scheduler scheduler = schedulerFactory.getScheduler();
        try {
            if (scheduler.checkExists(jobKey)) {
                throw new NonRecoverableException(String.format("Job %s with group %s Already available in scheduled state",
                        jobDetailInfo.getName(), jobDetailInfo.getGroup()),
                        Constants.ExceptionCode.UNIQUE_CONSTRAINT_ON_JOB_NAME_AND_GROUP_ERROR);
            }
        } catch (SchedulerException ex) {
            throw new RecoverableException(String.format("Server error occurred for Job %s with group %s " +
                            " with error message %s",
                    jobDetailInfo.getName(), jobDetailInfo.getGroup(), ex.getMessage()),
                    Constants.ExceptionCode.DOWN_STREAM_SERVICE_ERROR);
        }
        JobDataMap jobDataMap = new JobDataMap(jobDetailInfo.getParameters());
        JobDetail job = newJob(InvokeApiJob.class)
                .withIdentity(jobDetailInfo.getName(), jobDetailInfo.getGroup()).usingJobData(jobDataMap)
                .build();
        Trigger trigger = getTrigger(jobDetailInfo);
        try {
            scheduler.scheduleJob(job, trigger);
            jobDetailInfo.setScheduled(Boolean.TRUE);
            return jobDetailInfo;
        } catch (SchedulerException e) {
            log.info(e.getMessage());
            throw new RecoverableException(String.format("Server error occurred for Job %s with group %s " +
                            " with error message %s",
                    jobDetailInfo.getName(), jobDetailInfo.getGroup(), e.getMessage()),
                    Constants.ExceptionCode.DOWN_STREAM_SERVICE_ERROR);
        }
    }

    private Trigger getTrigger(JobDetailInfo jobDetailInfo) {
        if (jobDetailInfo.getStartNow()) {
            return newTrigger().withIdentity(jobDetailInfo.getName(), jobDetailInfo.getGroup()).startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobDetailInfo.getCronExpression())).build();
        } else {
            return newTrigger().withIdentity(jobDetailInfo.getName(), jobDetailInfo.getGroup())
                    .startAt(DateUtils.getDateFromUTCTimestamp(jobDetailInfo.getStartAt()).toDate())
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobDetailInfo.getCronExpression())).build();
        }
    }

    /**
     * Reschedule job detail info.
     *
     * @param jobDetailInfo the job detail info
     * @return the job detail info
     * @throws NonRecoverableException the non recoverable exception
     * @throws RecoverableException    the recoverable exception
     */
    public JobDetailInfo reschedule(JobDetailInfo jobDetailInfo) throws NonRecoverableException, RecoverableException {
        jobContextHelper.validateJobNameAndGroup(jobDetailInfo);
        Scheduler scheduler = schedulerFactory.getScheduler();
        JobKey jobKey = new JobKey(jobDetailInfo.getName(), jobDetailInfo.getGroup());
        try {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            } else {
                throw new NonRecoverableException(String.format("Job %s with group %s does not exist in scheduled history",
                        jobDetailInfo.getName(), jobDetailInfo.getGroup()),
                        Constants.ExceptionCode.INVALID_JOB_CLASS_NAME_ERROR);
            }
            if(jobDetailInfo.getStatus() != null && jobDetailInfo.getStatus().equals(Boolean.TRUE)) {
                return schedule(jobDetailInfo);
            }

        } catch (SchedulerException e) {
            log.info(e.getMessage());
            throw new RecoverableException(String.format("Server error occurred for Job %s with group %s does not" +
                            " exist in scheduled history with error message %s",
                    jobDetailInfo.getName(), jobDetailInfo.getGroup(), e.getMessage()),
                    Constants.ExceptionCode.DOWN_STREAM_SERVICE_ERROR);
        }
        return jobDetailInfo;

    }

}
