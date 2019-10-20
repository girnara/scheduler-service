package in.ind.core.exceptions;

import in.ind.core.Constants;

/**
 * The type Non recoverable exception.
 *
 * Created by abhay on 20/10/19.
 */
public class NonRecoverableException extends BaseException {

    /**
     *
     */
    private static final long serialVersionUID = 2662879568402830555L;

    /**
     * Instantiates a new Non recoverable exception.
     *
     * @param cause         the cause
     * @param message       the message
     * @param exceptionCode the exception code
     */
    public NonRecoverableException(Throwable cause, String message, Constants.ExceptionCode exceptionCode) {
        super(cause, message, exceptionCode);
    }

    /**
     * Instantiates a new Non recoverable exception.
     *
     * @param message       the message
     * @param exceptionCode the exception code
     */
    public NonRecoverableException(String message, Constants.ExceptionCode exceptionCode) {
        super(message, exceptionCode);
    }
}
