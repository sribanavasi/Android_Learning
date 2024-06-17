# JobServices and JobScheduler

- JobServices helps you with when to trigger the service, run/trigger service even when app killed, in repeated duration trigger, How to restart work, if it is aborted because of some reason.

### JobInfo

- JobInfo is abstraction which encapsulate the task needed to be done and have constraint(conditions) under which job need to be triggered.
- We can post multiple task to single JobInfo
- We have Builder class , which build the instance of JobInfo

### JobInfo.Builder
- Create the instance of JobInfo
- You need to specify kind of request, and different conditional under which it get triggered or stopped.
- To help you with different inbuilt functions and flags to set this constraint can be seen [here](https://developer.android.com/reference/android/app/job/JobInfo.Builder).
```kotlin
val jobInfo = JobInfo.Builder(123, ComponentName(this,DeepJobService::class.java))  
// Before android Q we need to specify at least one contraint like below.
// After Q JobSchedulers will accepts jobinfos without any constraints.
val job =   jobInfo.setRequiresCharging(false)
            .setMinimumLatency(1)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setOverrideDeadline(3*60*1000).build()
```
- JobInfo.Builder take Unique jobId and piece of work need to be done. 
- Then you can set the different Condition for service to work. example for these condition is like, when network percent/not, when device on charging or not, repeated time period. etc.

### JobScheduler

- It Services running in Device.
- You can access its instance by calling `Context.getSystemService(Context.JOB_SCHEDULER_SERVICE)`
- You build job by JobInfo.Builder and schedule this job using jobScheduler
- JobScheduler call callback of JobScheduler Services to handle the operations 
```kotlin
val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
val jobInfo = JobInfo.Builder(123, ComponentName(this,DeepJobService::class.java))
val job = jobInfo.setRequiresCharging(false)
    .setMinimumLatency(1)
    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
    .setOverrideDeadline(3*60*1000).build()

jobScheduler.schedule(job)
```
### JobService
- Entry point for the callback from the JobScheduler.
- if you are using JobService, add android permission `android.permission.BIND_JOB_SERVICE` inside service tag in manifest 
- Run on main thread. so it's your responsibility to do job in worker third/handler
```manifest
 <service android:name="MyJobService"
              android:permission="android.permission.BIND_JOB_SERVICE" >
         ...
     </service>
```
#### jobFinished
```kotlin
public final void jobFinished (JobParameters params, 
                boolean wantsReschedule)
```
- call this method to tell system that job completed.
- When system receive this method, it release wake lock being held for the job.
- No need to call if `onStopJob(android.app.job.JobParameters)` has been called.
- By passing true as the `wantsReschedule`, system will adjust the back-off policy for the job; That means, reschedule the job. If you pass false, job will reschedule based on periodic policy(if it not periodic, job won't get rescheduled).

#### onStartJob
```kotlin
public abstract boolean onStartJob (JobParameters params)
```
- Called when schedule constraint met and indicate job begin. SO override this method and implement job logic here. 
- Run on main thread. so it's your responsibility to do job in worker third/handler
- If this return true, indicate job is long-running. if you do this, job will be active until you call jobFinished() function. Whenever the constraints for our jobinfo are no longer satisfied, like network not percent, or for any reason System stops jobs, system invoke `onStopJob(android.app.job.JobParameters)` and no need to call jobFinished function

#### onStopJob
```kotlin
public abstract boolean onStopJob (JobParameters params)
```
- System invoke this method, when it decides to stop job becuase of some reason like jobinfo are no longer satisfied.
- System only call this method before 'jobFinished' been called.
- Once this method is called, you no longer need to call jobFinished(android.app.job.JobParameters, boolean)
- `JobParameters#getStopReason()` get reason for system to stop the job
- if you return true to indicate to the JobScheduler whether you'd like to reschedule this job based on the retry criteria provided at job creation-time; or false to end the job entirely (or, for a periodic job, to reschedule it according to its requested periodic criteria). Regardless of the value returned, your job must stop executing.

