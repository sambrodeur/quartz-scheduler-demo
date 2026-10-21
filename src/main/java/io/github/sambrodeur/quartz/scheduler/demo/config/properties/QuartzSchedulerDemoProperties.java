package io.github.sambrodeur.quartz.scheduler.demo.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "qsd")
public class QuartzSchedulerDemoProperties {

  private Map<String, QuartzJobProperties> jobs;

  public QuartzSchedulerDemoProperties() {
    jobs = null;
  }
}
