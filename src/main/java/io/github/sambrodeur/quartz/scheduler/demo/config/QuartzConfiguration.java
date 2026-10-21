package io.github.sambrodeur.quartz.scheduler.demo.config;

import io.github.sambrodeur.quartz.scheduler.demo.config.properties.QuartzSchedulerDemoProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class QuartzConfiguration {

  private final Scheduler scheduler;
  private final QuartzSchedulerDemoProperties quartzSchedulerDemoProperties;

  @PostConstruct
  public void scheduleJobs() {
    log.info("Initializing scheduler with job details and trigger");

    quartzSchedulerDemoProperties.getJobs().forEach((jobName, quartzJobProperties) -> {
      String jobClassName = quartzJobProperties.getClassName();
      String jobCron = quartzJobProperties.getCron();

      Class<? extends Job> jobClass = null;
      try {
        Class<?> loadedClass = Class.forName(jobClassName);
        if (Job.class.isAssignableFrom(loadedClass)) {
          jobClass = loadedClass.asSubclass(Job.class);
        } else {
          throw new ClassNotFoundException("Class " + jobClassName + " does not implement Job interface");
        }
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }


      if(jobClass != null) {
        log.info("Creation of job detail : {}", jobName);

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
          .withIdentity(jobName)
          .storeDurably()
          .build();

        log.info("Creation of job trigger : {}", jobName);

        Trigger jobTrigger = TriggerBuilder.newTrigger()
          .forJob(jobDetail)
          .withIdentity(jobName + "Trigger")
          .withSchedule(CronScheduleBuilder.cronSchedule(jobCron))
          .build();

        try {
          scheduler.scheduleJob(jobDetail, jobTrigger);
        } catch (SchedulerException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }
}
