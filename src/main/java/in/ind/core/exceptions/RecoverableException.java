package in.ind.core.exceptions;

import in.ind.core.Constants;

/**
 * The type Recoverable exception.
 *
 * Created by abhay on 20/10/19.
 */
public class RecoverableException extends BaseException {

    /**
     * Instantiates a new Recoverable exception.
     *
     * @param cause         the cause
     * @param message       the message
     * @param exceptionCode the exception code
     */
    public RecoverableException(Throwable cause, String message, Constants.ExceptionCode exceptionCode) {
        super(cause, message, exceptionCode);
    }

    /**
     * Instantiates a new Recoverable exception.
     *
     * @param message       the message
     * @param exceptionCode the exception code
     */
    public RecoverableException(String message, Constants.ExceptionCode exceptionCode) {
        super(message, exceptionCode);
    }
}
