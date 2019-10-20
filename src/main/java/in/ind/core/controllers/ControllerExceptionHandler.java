package in.ind.core.controllers;

import in.ind.core.Constants;
import in.ind.core.exceptions.NonRecoverableException;
import in.ind.core.exceptions.RecoverableException;
import in.ind.core.models.ServiceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The type Controller exception handler.
 *
 * Created by abhay on 20/10/19.
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    /**
     * Non recoverable exception handler response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler(NonRecoverableException.class)
    ResponseEntity<ServiceResponse> nonRecoverableExceptionHandler(NonRecoverableException ex) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setExceptionCode(ex.getExceptionCode());
        serviceResponse.setStatusMessage(ex.getMessage());
        serviceResponse.setStatusCode(Constants.FAILED_MESSAGE);
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Recoverable exception handler response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler(RecoverableException.class)
    ResponseEntity<ServiceResponse> recoverableExceptionHandler(RecoverableException ex) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setExceptionCode(ex.getExceptionCode());
        serviceResponse.setStatusMessage(ex.getMessage());
        serviceResponse.setStatusCode(Constants.FAILED_MESSAGE);
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Json exception response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler({HttpMessageConversionException.class})
    ResponseEntity<ServiceResponse> jsonException(HttpMessageConversionException ex) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setExceptionCode(Constants.ExceptionCode.INVALID_INPUT_ERROR);
        serviceResponse.setStatusMessage(ex.getMessage());
        serviceResponse.setStatusCode(Constants.FAILED_MESSAGE);
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Generic exception handler response entity.
     *
     * @param ex the ex
     * @return the response entity
     */
    @ResponseBody
    @ExceptionHandler({Throwable.class})
    ResponseEntity<ServiceResponse> genericExceptionHandler(Throwable ex) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setExceptionCode(Constants.ExceptionCode.DOWN_STREAM_SERVICE_ERROR);
        serviceResponse.setStatusMessage(ex.getMessage());
        serviceResponse.setStatusCode(Constants.FAILED_MESSAGE);
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<ServiceResponse>(serviceResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
