package in.ind.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class JobDetailInfo implements Serializable {
    private static final long serialVersionUID = 4459803055808095524L;
    private String name;
    private String group;
    private String cronExpression;
    private Boolean status;
    private Boolean scheduled;
    private Boolean invoked;
    private Map<String, ?> parameters = new HashMap<>();
    private Boolean startNow;
    private long startAt;

}
