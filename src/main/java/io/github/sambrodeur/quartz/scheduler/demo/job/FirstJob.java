package io.github.sambrodeur.quartz.scheduler.demo.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
public class FirstJob implements Job {

  @Override
  public void execute(JobExecutionContext context) {
    log.info("First job is running: {}", Instant.now());
    log.info("First job is completed");
  }
}
