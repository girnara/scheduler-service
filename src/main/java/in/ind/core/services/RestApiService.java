package in.ind.core.services;

import in.ind.core.Constants;
import in.ind.core.exceptions.NonRecoverableException;
import in.ind.core.exceptions.RecoverableException;
import in.ind.core.helpers.JobContextHelper;
import in.ind.core.models.JobDetailInfo;
import in.ind.core.models.ServiceResponse;
import in.ind.core.utils.JsonUtility;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * The type Rest api service.
 *
 * Created by abhay on 20/10/19.
 */
@Service
@Slf4j
public class RestApiService {
    private static final String SSL = "SSL";
    private static final String HTTPS = "https";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JobContextHelper jobContextHelper;

    private void enableSSL() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };
        try {
            SSLContext sc = SSLContext.getInstance(SSL);
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            log.error(String.format("Exception occurred while enabling SSL with error message %s ", e.getMessage()),e);
        }
    }

    /**
     * Invoke api service response.
     *
     * @param jobExecutionContext the job execution context
     * @return the service response
     */
    public ServiceResponse invokeApi(JobExecutionContext jobExecutionContext) {
        log.info(String.format("Invoking API for job %s and group %s",
                jobExecutionContext.getJobDetail().getKey().getName(),
                jobExecutionContext.getJobDetail().getKey().getGroup()));
        try {
            JobDetailInfo jobDetailInfo = jobContextHelper.getJobDetailInfoFromContext(jobExecutionContext);
            HttpHeaders headers = getHeaders(jobDetailInfo);
            String body = JsonUtility.toString(jobDetailInfo);
            HttpEntity<String> entity = new HttpEntity<String>(body, headers);
            String url = jobContextHelper.getEndPointUrl(jobDetailInfo);
            log.info(String.format("Invoking API for job %s and group %s endpoint url %s and body %s",
                    jobExecutionContext.getJobDetail().getKey().getName(),
                    jobExecutionContext.getJobDetail().getKey().getGroup(), url,body ));
            if(url.contains(HTTPS)) {
                enableSSL();
            }
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    jobContextHelper.getEndPointUrl(jobDetailInfo),entity, String.class);
            if (responseEntity.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL) {
                throw new RecoverableException(
                        String.format("Api %s is not returning http successful status , returning http-status %d ",
                                jobContextHelper.getEndPointUrl(jobDetailInfo), responseEntity.getStatusCode().value()),
                        Constants.ExceptionCode.DOWN_STREAM_SERVICE_ERROR);
            }
            log.info(String.format("InvokingAPIResponse %s  for job %s and group %s endpoint url %s and body %s",
                    responseEntity.getBody(), jobExecutionContext.getJobDetail().getKey().getName(),
                    jobExecutionContext.getJobDetail().getKey().getGroup(), url,body ));
            return JsonUtility.fromString(responseEntity.getBody(), ServiceResponse.class);
        } catch (Throwable ex) {
            log.error(String.format("Exception occurred while invoking api for job %s and group %s with message %s",
                    jobExecutionContext.getJobDetail().getKey().getName(),
                    jobExecutionContext.getJobDetail().getKey().getGroup(), ex.getMessage()), ex);
        }
        return null;
    }

    private HttpHeaders getHeaders(JobDetailInfo jobDetailInfo) {
        HttpHeaders headers =new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        if(jobDetailInfo.getParameters().containsKey(Constants.APP_NAME) &&
                jobDetailInfo.getParameters().containsKey(Constants.APP_ENV)) {
            headers.add(Constants.APP_NAME, (String) jobDetailInfo.getParameters().get(Constants.APP_NAME));
            headers.add(Constants.APP_ENV, (String) jobDetailInfo.getParameters().get(Constants.APP_ENV));
        }
        return headers;
    }
}
