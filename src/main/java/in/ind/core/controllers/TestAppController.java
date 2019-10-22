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
 * The type Test app controller.
 *
 * Created by abhay on 20/10/19.
 */
@Slf4j
@RestController
@RequestMapping("/v1/test/")
public class TestAppController {

    /**
     * Webhook response entity.
     *
     * @param jobDetailInfo the job detail info
     * @return the response entity
     * @throws Exception the exception
     */
    @RequestMapping(value = "webhook", method = RequestMethod.POST,
            produces = "application/json")
    public ResponseEntity<ServiceResponse<JobDetailInfo>> webhook(@RequestBody JobDetailInfo jobDetailInfo) throws Exception {
        log.info(String.format("Request received %s", JsonUtility.toString(jobDetailInfo)));
        // Do your stuff Async Way i.e. Invoke Celery Task for computing triggers
        ServiceResponse<JobDetailInfo> serviceResponse = new ServiceResponse<>();
        jobDetailInfo.setInvoked(Boolean.TRUE);
        serviceResponse.setStatusCode(Constants.SUCCESS_MESSAGE);
        serviceResponse.setStatusMessage(Constants.REQUEST_ACCEPTED_SUCCESSFULLY);
        serviceResponse.setPayload(jobDetailInfo);
        log.info(String.format("Response sent %s", JsonUtility.toString(serviceResponse)));
        return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
    }
}
