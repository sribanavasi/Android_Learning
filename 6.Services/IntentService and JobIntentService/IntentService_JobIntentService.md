# IntentService

- IntentService is an extension of the Service component class that handles asynchronous requests on demand. And client request through startService(intent) function.
- IntentService, handle these request in `Worker Theread`  and stops itself when it runs out of work.
- This follow "work queue processor" pattern to offload tasks from an application's main thread. That means, when multiple request comes to Service, it process one after the other in worker thread. and when all request complete, stop the service itself(Must stop by itself).
- Service uses `onHandleIntent(android.content.Intent)` to process these request. And this function will be called, when request arrives. So don't override and implement `onSTartCommand()` in Service.

**Note:**
- Unless you provide Binder, don't override and implement `onBind()`
- Don't override and implement `onSTartCommand()` in Service. We have `onHandleIntent(android.content.Intent)` which start the services on request.


**IntentService is subject to all the background execution limits imposed with Android 8.0 (API level 26). That means, Services will get destroyed when App running in background**
**IntentService departed after API Level 30(Android 11)**

# JobIntentService

```java

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

/**
 * Example implementation of a JobIntentService.
 */
public class SimpleJobIntentService extends JobIntentService {
    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SimpleJobIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // We have received work to do.  The system or framework is already
        // holding a wake lock for us at this point, so we can just go.
        Log.i("SimpleJobIntentService", "Executing work: " + intent);
        String label = intent.getStringExtra("label");
        if (label == null) {
            label = intent.toString();
        }
        toast("Executing: " + label);
        for (int i = 0; i < 5; i++) {
            Log.i("SimpleJobIntentService", "Running service " + (i + 1)
                    + "/5 @ " + SystemClock.elapsedRealtime());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        Log.i("SimpleJobIntentService", "Completed service @ " + SystemClock.elapsedRealtime());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toast("All work complete");
    }

    @SuppressWarnings("deprecation")
    final Handler mHandler = new Handler();

    // Helper for showing tests
    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(SimpleJobIntentService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
```

### enqueueWork

This will either directly start the service (when running on pre-O platforms) or enqueue work for it as a job (when running on O and later).
```java
public static void enqueueWork(
    @NonNull Context context,
    @NonNull Class<Object> cls,
    int jobId, // this should be unique constant for given service in application
    @NonNull Intent work
)
```

### onHandleWork
- Called serially for each work dispatched to and processed by the service. That means, when multiple work enquire done, these will be processed one after the other.
- After work done, selfStop here

### onStopCurrentWork
`public boolean onStopCurrentWork()`
- This will be called if the JobScheduler has decided to stop this job.
- return type true indicate, you would like to reschedule or false indicate not to reschedule when system kill service for some reason.

### isStopped
`public boolean isStopped()`
- Returns true if onStopCurrentWork has been called. 

### onStartCommand
- Processes start commands when running as a pre-O service

**IN pre-O service, you can't enquire the work so, start service in those versions**

***Unlike IntentServices, JobIntentServices will run in background, but won't run once service get killed. But it can get rescheduled, if you return true in  onStopCurrentWork***

***Departed in favor of Work Manager***


Also Check:
- [JobScheduler and JonServices](../JobServices%20and%20JobScheduler/JobScheduler_JonServices.md)