package in.ind.core;

import in.ind.core.configs.SchedulerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * The type Scheduler service app.
 *
 * Created by abhay on 20/10/19.
 */
@SpringBootApplication
@Import({SchedulerConfig.class})
public class SchedulerServiceApp {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SchedulerServiceApp.class, args);
    }
}
