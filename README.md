# Quartz Scheduler Demo

This project is a minimal Spring Boot application that demonstrates how to configure and run Quartz jobs with either an in-memory job store or a JDBC-backed database job store.

The `spring-boot-starter-quartz` dependency is the core Quartz integration. `spring-boot-starter-jdbc` and H2 are needed when using the JDBC-backed job store, while `spring-boot-starter-web` is included here because this demo is a Spring Boot web app.

## Technologies Used

- **Spring Boot 4.1.1**: Framework for building the application
- **Java 25**: Programming language
- **Quartz Scheduler 2.5.2**: Job scheduling engine
- **Spring Web**: Web application support
- **Spring JDBC**: Database access layer
- **H2 Database**: In-memory relational database for local testing
- **Lombok**: Reduces boilerplate code
- **Maven**: Build and dependency management

## What this app does

The application starts a Quartz `Scheduler` and schedules jobs from the `qsd.jobs` configuration map.

- `QuartzConfiguration` reads each job entry from `application.yml`
- It uses the configured Java class name and cron expression
- It creates a `JobDetail` and a `Trigger`
- It registers both with the Quartz scheduler

The demo job is `FirstJob`, which logs a message every 10 seconds:

- `io.github.sambrodeur.quartz.scheduler.demo.job.FirstJob`
- cron: `*/10 * * * * ?`

## Project structure

- `src/main/java/.../QuartzSchedulerDemoApplication.java` — Spring Boot entry point
- `src/main/java/.../config/QuartzConfiguration.java` — schedules Quartz jobs at startup
- `src/main/java/.../config/properties/QuartzSchedulerDemoProperties.java` — binds `qsd.*`
- `src/main/java/.../job/FirstJob.java` — sample job implementation
- `src/main/resources/application.yml` — default in-memory Quartz configuration
- `src/main/resources/application-db.yml` — database-backed Quartz configuration

## Run the app

From the project root:

```bash
./mvnw spring-boot:run
```

This uses the default configuration in `application.yml`.

To use the database-backed Quartz setup:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=db
```

## Quartz setup explained

### 1) In-memory mode

The default configuration is in `src/main/resources/application.yml`:

```yaml
spring:
  quartz:
    auto-startup: true
    job-store-type: memory
    properties:
      org:
        quartz:
          scheduler:
            instanceId: AUTO
          threadPool:
            class: org.quartz.simpl.SimpleThreadPool
            threadCount: 1

qsd:
  jobs:
    firstJob:
      class-name: io.github.sambrodeur.quartz.scheduler.demo.job.FirstJob
      cron: '*/10 * * * * ?'
```

This mode uses Quartz's `RAMJobStore`, which keeps all scheduler state in memory only.

Benefits:

- Very easy to set up
- No database required
- Good for local development, demos, and simple apps

Limitations:

- Jobs are lost when the application stops
- No persistence across restarts
- Not suitable for clustered or long-lived production scheduling

In this configuration, Quartz startup logs will show:

```text
Using job-store 'org.quartz.simpl.RAMJobStore' - which does not support persistence.
```

### 2) Database mode

The database configuration is in `src/main/resources/application-db.yml`:

```yaml
spring:
  application:
    name: quartz-scheduler-demo
  datasource:
    url: jdbc:h2:mem:quartzdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  sql:
    init:
      mode: always
  quartz:
    auto-startup: true
    job-store-type: jdbc
    jdbc:
      initialize-schema: always
```

This activates Quartz's JDBC job store, which stores jobs, triggers, and scheduler state in a relational database.

Benefits:

- Jobs survive application restarts
- Better for production and operational reliability
- Works with clustered or shared scheduler scenarios when configured appropriately

Limitations:

- Requires a database and schema setup
- Slightly more configuration and operational complexity
- More moving parts than the memory store

This project uses the H2 in-memory database, so the database is created automatically for local testing.

The H2 console is exposed at:

```text
http://localhost:8080/h2-console
```

Use:

- JDBC URL: `jdbc:h2:mem:quartzdb`
- Username: `sa`
- Password: blank

## Job definition format

Jobs are defined in the `qsd.jobs` map. For example:

```yaml
qsd:
  jobs:
    firstJob:
      class-name: io.github.sambrodeur.quartz.scheduler.demo.job.FirstJob
      cron: '*/10 * * * * ?'
```

`QuartzConfiguration` reads each entry, loads the class with reflection, validates that it implements `org.quartz.Job`, and schedules it with the cron expression.

## Choosing the right mode

Use in-memory mode when:

- learning Quartz
- running a demo locally
- no need for job persistence

Use database mode when:

- jobs should survive restarts
- you need durable scheduling state
- you want production-like behavior

## Troubleshooting

- If the app starts but no jobs run, check the cron expression and the `qsd.jobs` map.
- If Quartz is not starting as expected, confirm the active profile: `db` vs default.
- If you use JDBC mode, make sure the database is available and the schema can be initialized.
- For database mode, the H2 console remains useful for checking whether Quartz tables were created.

## Summary

This demo intentionally shows both common Quartz storage strategies:

- `memory` for quick, lightweight local development
- `jdbc` for durable, restart-safe scheduling

The application is structured so you can switch between them by changing the Spring profile and configuration file without changing the job code.

## Note

When using a relational database such as SQL Server, Oracle, PostgreSQL, or another vendor, the Quartz configuration must be adjusted for that database driver and schema. In this demo, the jobs are created the first time the scheduler loads and will be created again on each startup. Using a different database, they do not need to be recreated on every startup. This will require a different configuration and schema setup, which is outside the scope of this demo.