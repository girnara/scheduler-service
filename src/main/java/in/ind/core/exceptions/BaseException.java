package in.ind.core.exceptions;

import in.ind.core.Constants;

/**
 * The type Base exception.
 *
 * Created by abhay on 20/10/19.
 */
public class BaseException extends Exception {
    private final String message;
    private final Constants.ExceptionCode exceptionCode;

    /**
     * Instantiates a new Base exception.
     *
     * @param cause         the cause
     * @param message       the message
     * @param exceptionCode the exception code
     */
    public BaseException(Throwable cause, String message, Constants.ExceptionCode exceptionCode) {
        super(cause);
        this.message = message;
        this.exceptionCode = exceptionCode;
    }

    /**
     * Instantiates a new Base exception.
     *
     * @param message       the message
     * @param exceptionCode the exception code
     */
    public BaseException(String message, Constants.ExceptionCode exceptionCode) {
        super(message);
        this.message = message;
        this.exceptionCode = exceptionCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }

    /**
     * Gets exception code.
     *
     * @return the exception code
     */
    public Constants.ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

}
