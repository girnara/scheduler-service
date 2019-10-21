package in.ind.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Job detail info.
 *
 * Created by abhay on 20/10/19.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "Class represent Job Details")
public class JobDetailInfo implements Serializable {
    private static final long serialVersionUID = 4459803055808095524L;
    @ApiModelProperty(notes = "Name of schedule job. i.e. EVERY_MINUTE_NET_WORTH_JOB. In case of delete scheduled job, " +
            "client don't need to provide the name as mandatory", example = "EVERY_MINUTE_NET_WORTH_JOB", position = 0)
    private String name;
    @ApiModelProperty(notes = "Schedule Job Group. i.e. EVERY_MINUTE_JOBS. Not mandatory if we use in delete API",
            example = "DAILY_JOBS", position = 1)
    private String group;
    @ApiModelProperty(notes = "Cron expression for scheduling a job. i.e. 0 * * * * ? for scheduling job every minute",
            example = "0 * * * * ?",
            position = 2)
    private String cronExpression;
    @ApiModelProperty(notes = "Status of schedule job. status=true for activating and status=false for deleting a job",
            required = true, example = "true", position = 3)
    private Boolean status;
    @ApiModelProperty(notes = "If job was scheduled or not i.e. true for successfully scheduled. It will be update " +
            "by server once job is scheduled",
            example = "true", position = 4)
    private Boolean scheduled;
    @ApiModelProperty(notes = "It will be set to true by client after receiving the callback from scheduler service",
            example = "true", position = 5)
    private Boolean invoked;
    @ApiModelProperty(notes = "Job Meta info to provide required parameters for executing a job. i.e. " +
            "WEBHOOK_UR:http://localhost:18080/v1/test/webhook," +
            " APP_NAME: TEST_APP, APP_ENV:TEST_ENV", position = 6)
    private Map<String, ?> parameters = new HashMap<>();
    @ApiModelProperty(notes = "If it is true it will schedule the job right now. i.e. else service will check for" +
            " startAt time",
            example = "true", position = 7)
    private Boolean startNow;
    @ApiModelProperty(notes = "UTC long timestamp to start the job in future. ",
            example = "0", position = 8)
    private long startAt;

}
