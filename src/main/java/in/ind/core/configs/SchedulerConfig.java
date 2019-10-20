package in.ind.core.configs;

import javax.sql.DataSource;

import in.ind.core.jobs.InvokeApiJob;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Properties;

/**
 * The type Scheduler config.
 *
 * Created by abhay on 20/10/19.
 */
@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class SchedulerConfig {

    /**
     * Job factory job factory.
     *
     * @param applicationContext the application context
     * @param springLiquibase    the spring liquibase
     * @return the job factory
     */
// injecting SpringLiquibase to ensure liquibase is already initialized and created the quartz tables:
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext, SpringLiquibase springLiquibase) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * Scheduler factory bean scheduler factory bean.
     *
     * @param dataSource       the data source
     * @param jobFactory       the job factory
     * @param sampleJobTrigger the sample job trigger
     * @return the scheduler factory bean
     * @throws IOException the io exception
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, JobFactory jobFactory,
                                                     @Qualifier("sampleJobTrigger") Trigger sampleJobTrigger)
            throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        // this allows to update triggers in DB when updating settings in config file:
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);
        factory.setQuartzProperties(quartzProperties());

        return factory;
    }

    /**
     * Quartz properties properties.
     *
     * @return the properties
     * @throws IOException the io exception
     */
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    /**
     * Gets rest template.
     *
     * @param timeout the timeout
     * @return the rest template
     */
    @Bean
    public RestTemplate getRestTemplate(@Value("${rest.api.timeout}") int timeout) {
        RestTemplate restTemplate = new RestTemplate(getHttpFactoryDetails(timeout));
        restTemplate.setErrorHandler(new ResponseErrorHandler() {

            @Override
            public boolean hasError(ClientHttpResponse clienthttpresponse) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse clienthttpresponse) throws IOException {
            }
        });
        return restTemplate;
    }

    private HttpComponentsClientHttpRequestFactory getHttpFactoryDetails(int timeoutPeriod) {
        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).
                setMaxConnPerRoute(20).setMaxConnTotal(100).build();
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(timeoutPeriod);
        httpRequestFactory.setConnectTimeout(timeoutPeriod);
        httpRequestFactory.setReadTimeout(timeoutPeriod);
        httpRequestFactory.setHttpClient(httpClient);
        return httpRequestFactory;
    }

    /**
     * Sample job detail job detail factory bean.
     *
     * @return the job detail factory bean
     */
    @Bean
    public JobDetailFactoryBean sampleJobDetail() {
        return createJobDetail(InvokeApiJob.class);
    }

    /**
     * Sample job trigger simple trigger factory bean.
     *
     * @param jobDetail the job detail
     * @param frequency the frequency
     * @return the simple trigger factory bean
     */
    @Bean(name = "sampleJobTrigger")
    public SimpleTriggerFactoryBean sampleJobTrigger(@Qualifier("sampleJobDetail") JobDetail jobDetail,
                                                     @Value("${samplejob.frequency}") long frequency) {
        return createTrigger(jobDetail, frequency);
    }

    private static JobDetailFactoryBean createJobDetail(Class jobClass) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        // jobs has to be durable to be stored in DB:
        factoryBean.setDurability(true);
        return factoryBean;
    }

    private static SimpleTriggerFactoryBean createTrigger(JobDetail jobDetail, long pollFrequencyMs) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setStartDelay(0L);
        factoryBean.setRepeatInterval(pollFrequencyMs);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        // in case of misfire, ignore all missed triggers and continue :
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
        return factoryBean;
    }

    // Use this method for creating cron triggers instead of simple triggers:
    private static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        return factoryBean;
    }

}
