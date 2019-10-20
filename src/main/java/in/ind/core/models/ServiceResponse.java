package in.ind.core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import in.ind.core.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 *
 * Created by abhay on 20/10/19.
 */
@NoArgsConstructor
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponse implements Serializable {
    private static final long serialVersionUID = -8059180865633357287L;
    private String statusCode;
    private String statusMessage;
    private Constants.ExceptionCode exceptionCode;
    private transient Object payload;
}
