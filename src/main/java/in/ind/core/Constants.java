package in.ind.core;

/**
 * The type Constants.
 *
 * Created by abhay on 20/10/19.
 */
public abstract class Constants {
    /**
     * The constant FAILED_MESSAGE.
     */
    public static final String FAILED_MESSAGE = "FAILED";
    /**
     * The constant SUCCESS_MESSAGE.
     */
    public static final String SUCCESS_MESSAGE = "SUCCESS";
    /**
     * The constant APP_NAME.
     */
    public static final String APP_NAME = "APP";
    /**
     * The constant SECRET_KEY.
     */
    public static final String SECRET_KEY = "366xhegrndhfspwiryrskwhd0801";
    /**
     * The constant APP_ENV.
     */
    public static final String APP_ENV = "ENV";
    /**
     * The constant JOB_SCHEDULE_SUCCESS_MESSAGE.
     */
    public static final String JOB_SCHEDULE_SUCCESS_MESSAGE = "Job scheduled successfully";
    /**
     * The constant REQUEST_ACCEPTED_SUCCESSFULLY.
     */
    public static final String REQUEST_ACCEPTED_SUCCESSFULLY = "Request accepted successfully";
    /**
     * The constant JOB_RESCHEDULE_SUCCESS_MESSAGE.
     */
    public static final String JOB_RESCHEDULE_SUCCESS_MESSAGE = "Job rescheduled successfully";
    /**
     * The constant SCHEDULED_JOB_DELETED_SUCCESS_MESSAGE.
     */
    public static final String SCHEDULED_JOB_DELETED_SUCCESS_MESSAGE = "Scheduled job has been deleted successfully";
    /**
     * The constant WEBHOOK_URL.
     */
    public static final String WEBHOOK_URL = "WEBHOOK_URL";
    /**
     * The constant COUNT.
     */
    public static final String COUNT = "count";

    /**
     * The enum Exception code.
     */
    public enum ExceptionCode {

        /**
         * Webhook url missing exception code.
         */
        WEBHOOK_URL_MISSING,
        /**
         * Invalid job class name error exception code.
         */
        INVALID_JOB_CLASS_NAME_ERROR,
        /**
         * Job scheduler error exception code.
         */
        JOB_SCHEDULER_ERROR,
        /**
         * Db fetch error exception code.
         */
        DB_FETCH_ERROR,
        /**
         * Unique constraint on job name and group error exception code.
         */
        UNIQUE_CONSTRAINT_ON_JOB_NAME_AND_GROUP_ERROR,
        /**
         * Job name and group can not be empty error exception code.
         */
        JOB_NAME_AND_GROUP_CAN_NOT_BE_EMPTY_ERROR,
        /**
         * Cron expression and status empty error exception code.
         */
        CRON_EXPRESSION_AND_STATUS_EMPTY_ERROR,
        /**
         * Webhook url empty error exception code.
         */
        WEBHOOK_URL_EMPTY_ERROR,
        /**
         * Db insert error exception code.
         */
        DB_INSERT_ERROR,
        /**
         * Down stream service error exception code.
         */
        DOWN_STREAM_SERVICE_ERROR,
        /**
         * Deserialization failed error exception code.
         */
        DESERIALIZATION_FAILED_ERROR,
        /**
         * Serialization failed error exception code.
         */
        SERIALIZATION_FAILED_ERROR,
        /**
         * Invalid input error exception code.
         */
        INVALID_INPUT_ERROR,
        /**
         * Un supported operation error exception code.
         */
        UN_SUPPORTED_OPERATION_ERROR,
        /**
         * Connection time out error exception code.
         */
        CONNECTION_TIME_OUT_ERROR,
        /**
         * Authentication failed error exception code.
         */
        AUTHENTICATION_FAILED_ERROR,
        /**
         * Authorization failed error exception code.
         */
        AUTHORIZATION_FAILED_ERROR,
        /**
         * Https secure gateway error exception code.
         */
        HTTPS_SECURE_GATEWAY_ERROR,
        /**
         * Connection refused error exception code.
         */
        CONNECTION_REFUSED_ERROR,
        /**
         * Resource not found error exception code.
         */
        RESOURCE_NOT_FOUND_ERROR,
        /**
         * Connection reset error exception code.
         */
        CONNECTION_RESET_ERROR,
        /**
         * Throttle error exception code.
         */
        THROTTLE_ERROR,
        /**
         * Rate limit exceed error exception code.
         */
        RATE_LIMIT_EXCEED_ERROR,
        /**
         * Mysql db error exception code.
         */
        MYSQL_DB_ERROR
    }
}
