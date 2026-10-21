package io.github.sambrodeur.quartz.scheduler.demo;

import io.github.sambrodeur.quartz.scheduler.demo.config.properties.QuartzSchedulerDemoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(QuartzSchedulerDemoProperties.class)
public class QuartzSchedulerDemoApplication {

	static void main(String[] args) {
		SpringApplication.run(QuartzSchedulerDemoApplication.class, args);
	}

}
