package in.ind.core.controllers;

import in.ind.core.Constants;
import in.ind.core.exceptions.NonRecoverableException;
import in.ind.core.models.ServiceResponse;
import in.ind.core.utils.JsonUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by abhay on 20/10/19.
 */
@RestController
@RequestMapping("/v1/health")
@Slf4j
public class HealthCheckController {

  /**
   * Health check response entity.
   *
   * @param secretKey the secret key
   * @return the response entity
   * @throws Exception the exception
   */
  @RequestMapping(value = "/{secretKey}", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<ServiceResponse<String>> healthCheck(@PathVariable("secretKey") String secretKey) throws Exception {
    ServiceResponse<String> serviceResponse = new ServiceResponse<>();
    if(StringUtils.isEmpty(secretKey) || !secretKey.equals(Constants.SECRET_KEY)) {
      throw new NonRecoverableException("Authorization failed for HealthCheckEndpoint", Constants.ExceptionCode.AUTHORIZATION_FAILED_ERROR);
    }
    log.info(JsonUtility.toString(secretKey));
    serviceResponse.setStatusCode(Constants.SUCCESS_MESSAGE);
    serviceResponse.setPayload("Application is up and running");
    serviceResponse.setStatusMessage(Constants.REQUEST_ACCEPTED_SUCCESSFULLY);
    return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
  }

}
