package io.github.sambrodeur.quartz.scheduler.demo.config.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class QuartzJobProperties implements Serializable {

  @Serial
  private static final long serialVersionUID = -7955382362932394409L;

  private String className;
  private String cron;

  public QuartzJobProperties() {
    className = null;
    cron = null;
  }
}
