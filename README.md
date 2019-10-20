# Distributed Scheduler Service

Distributed Scheduler Service - A distributed scheduler service provide API based scheduling job. It provides an API to schedule a job for given cron expression.
It also provide an API to reschedule or delete existing scheduled job. It is distributed and runs in cluster mode to divide the jobs between its running instances.
It is built with springboot framework, quartz scheduler with mysql as persistent store.
In job detail info, client can pass webhook url which will be triggered at mentioned cron schedule. i.e. At the time of execution of job, scheduler-service will call webhook url with all job meta information. 

## Getting Started

Clone the github repository in your workspace
```
$ cd $WORK_SPACE/
$ git clone https://github.com/girnara/scheduler-service.git
$ cd scheduler-service
$ sudo mkdir -p /App/log/core/scheduler-service
$ sudo chmod -R 777 /App/log/core/scheduler-service
$ ./mvnw clean package
$ java -jar target/scheduler-service-1.0.0.jar
```

### Prerequisites

You need to install java 1.8 on your system before starting this project.

```
Install JAVA 1.8 on your development environment
```

### Scheduling a job using APIs

Once application is up and running

You can see the APIs using swagger on following link

```
http://localhost:18080/v2/api-docs
http://localhost:18080/swagger-ui.html
```

Schedule a job with Test Webhook Endpoint(TestJob - to run every minute at 0 seconds. Example json is available in `src/main/resource/data/schedule_job_request.json`)

```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'X-ACCESS-TOKEN: TEST' -d '
{
  "cronExpression": "0 * * * * ?",
  "group": "TEST_GROUP",
  "name": "TEST_JOB",
  "parameters": {
    "WEBHOOK_URL": "http://localhost:18080/v1/test/call",
    "APP_NAME": "TEST_APP",
    "APP_ENV": "DEV"
  },
  "startNow": true,
  "status": true
}' 'http://localhost:18080/v1/schedule'                                                                                                                               }' 'http://localhost:28080/v1/admin/WeWork'
```

Response Json:

```
{
  "statusCode": "SUCCESS",
  "statusMessage": "Job scheduled successfully",
  "payload": {
    "name": "TEST_JOB",
    "group": "TEST_GROUP",
    "cronExpression": "0 * * * * ?",
    "status": true,
    "scheduled": true,
    "parameters": {
      "WEBHOOK_URL": "http://localhost:18080/v1/test/call",
      "APP_NAME": "TEST_APP",
      "APP_ENV": "DEV"
    },
    "startNow": true,
    "startAt": 0
  }
}
```

### Reschedule Existing Job

Goto http://localhost:18080/swagger-ui.html#!/job-controller/rescheduleUsingPUT and changing the cron expression from every minutes to every two minutes. i.e. `src/main/resources/data/reschedule_job_request.json`
```
curl -X PUT --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'X-ACCESS-TOKEN: TEST' -d '
{
  "cronExpression": "0 /2 * * * ?",
  "group": "TEST_GROUP",
  "name": "TEST_JOB",
  "parameters": {
    "WEBHOOK_URL": "http://localhost:18080/v1/test/call",
    "APP_NAME": "TEST_APP",
    "APP_ENV": "DEV"
  },
  "startNow": true,
  "status": true
}' 'http://localhost:18080/v1/reschedule/TEST_JOB/TEST_GROUP'
```

Response Json:
```
{
  "statusCode": "SUCCESS",
  "statusMessage": "Job rescheduled successfully",
  "payload": {
    "name": "TEST_JOB",
    "group": "TEST_GROUP",
    "cronExpression": "0 /2 * * * ?",
    "status": true,
    "scheduled": true,
    "parameters": {
      "WEBHOOK_URL": "http://localhost:18080/v1/test/call",
      "APP_NAME": "TEST_APP",
      "APP_ENV": "DEV"
    },
    "startNow": true,
    "startAt": 0
  }
}
```

### Delete existing scheduled job
Goto hhttp://localhost:18080/swagger-ui.html#!/job-controller/rescheduleUsingPUT and provide the name,group in path param and request body as `src/main/resources/data/delete_existing_scheduled_request.json`

```
curl -X PUT --header 'Content-Type: application/json' --header 'Accept: application/json' --header 'X-ACCESS-TOKEN: TEST' -d '{
"status": false
}' 'http://localhost:18080/v1/reschedule/TEST_JOB/TEST_GROUP'
```

Response Json
```
{
  "status": "SUCCESS",
  "statusMessage": "Successfully processed your request",
  "payload": {
    "id": "8c4f1313-39e3-4fee-85de-431275ab0e55",
    "parkingSpotId": "1",
    "timestamp": 1553986160345,
    "issueAt": 1553986160304,
    "payedAmount": 10.5,
    "status": "PAID"
  }
}
```

### Test Webhook Controller i.e. TestController
Created A test job webhook handler. i.e. Client Need to implement the HTTP POST webhook with JobDetailInfo as json body. We have used TestController as Test Job Webhook request handler. You can implement your custom webhook in your application.
API Contract are available on swagger docs. i.e. `http://localhost:18080/swagger-ui.html#!/test-app-controller/webhookUsingPOST`
 
### Deployment

Deployment folder contain the ansible role for deployment on ubuntu 16.04 as systemctl service.
Export your build number as 100(It can be your jenkins build number). You need to add the ssh public key on target hosts. We uniquely identify the application by its name(parking), env(dev/qa/uat/prod)
You need to create manual symbolic link first time to avoid ansible role failure in service.yml line number 23(You can comment out that task for first build and uncomment for subsequent build).
```
$ export BUILD_NUMBER=100
$ mkdir -p deployment/application/$BUILD_NUMBER
$ cp target/scheduler-service-1.0.0.jar deployment/application/$BUILD_NUMBER/scheduler-service-1.0.0.jar
$ cd deployment
$ sudo ansible-playbook -e "env=dev"  -e "BUILD_NUMBER=100" -i inventory/awsdev-hosts site.yml
```


### Logging
Logs are available for different environment in different file. Each file as size cap of 200MB and purged automatically after the number of days limit.
```
LOCAL: /App/log/core/scheduler-service/service-local.log
DEV  : /App/log/core/scheduler-service/service-dev.log
QA   : /App/log/core/scheduler-service/service-qa.log
PROD : /App/log/core/scheduler-service/service-prod.log
```

### Package structure
As a service we have created multiple package with each having different responsibilities.
```
in.ind.core.configs     : Initialize application configs.
in.ind.core.controllers : All the API endpoint are available in this package. i.e. JobController for schedule and reschedule APIs.
in.ind.core.exceptions  : Recoverable and NonRecoverable Exception definition.
in.ind.core.helpers     : All the Application helpers used for different services.
in.ind.core.jobs        : All the jobs defined for scheduler service. i.e. InvokeApiJob for calling the client webhook at speficified cron trigger.
in.ind.core.models      : All the domain model service objects are available in this package.
in.ind.core.services    : All the application services and its definition available in that. i.e. RestApiService, SchedulerService.
in.ind.core.utls        : All the utility class and methods are available in this package.
```

### Authors

* **Abhay Girnara** - *Initial work* - [girnara](https://github.com/girnara)