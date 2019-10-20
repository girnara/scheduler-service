package in.ind.core.controllers;

import in.ind.core.Constants;
import in.ind.core.models.JobDetailInfo;
import in.ind.core.models.ServiceResponse;
import in.ind.core.services.SchedulerService;
import in.ind.core.utils.JsonUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The type Job controller.
 *
 * Created by abhay on 20/10/19.
 */
@Slf4j
@RestController
@RequestMapping("/v1/")
public class JobController {

    @Autowired
    private SchedulerService schedulerService;

    /**
     * Schedule response entity.
     *
     * @param jobDetailInfo the job detail info
     * @return the response entity
     * @throws Exception the exception
     */
    @RequestMapping(value = "schedule", method = RequestMethod.POST,
            produces = "application/json")
    public ResponseEntity<ServiceResponse> schedule(@RequestBody JobDetailInfo jobDetailInfo) throws Exception {
        log.info(String.format("Schedule Job Request received %s", JsonUtility.toString(jobDetailInfo)));
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatusCode(Constants.SUCCESS_MESSAGE);
        serviceResponse.setStatusMessage(Constants.JOB_SCHEDULE_SUCCESS_MESSAGE);
        JobDetailInfo jobDetailInfoUpdated = schedulerService.schedule(jobDetailInfo);
        serviceResponse.setPayload(jobDetailInfoUpdated);
        log.info(String.format("Schedule Job Response sent %s", JsonUtility.toString(serviceResponse)));
        return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.ACCEPTED);
    }

    /**
     * Reschedule response entity.
     *
     * @param name          the name
     * @param group         the group
     * @param jobDetailInfo the job detail info
     * @return the response entity
     * @throws Exception the exception
     */
    @RequestMapping(value = "reschedule/{name}/{group}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<ServiceResponse> reschedule(@PathVariable("name") String name,
                                                      @PathVariable("group") String group,
                                                      @RequestBody JobDetailInfo jobDetailInfo) throws Exception {
        jobDetailInfo.setName(name);
        jobDetailInfo.setGroup(group);
        log.info(String.format("Reschedule Request received %s", JsonUtility.toString(jobDetailInfo)));
        JobDetailInfo jobDetailInfoUpdated = schedulerService.reschedule(jobDetailInfo);
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatusCode(Constants.SUCCESS_MESSAGE);
        if(jobDetailInfo.getStatus()) {
            serviceResponse.setStatusMessage(Constants.JOB_RESCHEDULE_SUCCESS_MESSAGE);
        } else {
            serviceResponse.setStatusMessage(Constants.SCHEDULED_JOB_DELETED_SUCCESS_MESSAGE);
        }
        serviceResponse.setPayload(jobDetailInfoUpdated);
        log.info(String.format("Reschedule Response sent %s", JsonUtility.toString(serviceResponse)));
        return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.OK);
    }
}
