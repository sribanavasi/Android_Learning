# Creating Started Services

- You must implement onStartCommand function in service to make service started.
- If you don't want to allow bounding to your service, you can return null in onBind callback, because you must implement either it is bound or started service. 

### Extending the Service class

**Example Code**
```java
public class HelloService extends Service {
  private Looper serviceLooper;
  private ServiceHandler serviceHandler;

  // Handler that receives messages from the thread
  private final class ServiceHandler extends Handler {
      public ServiceHandler(Looper looper) {
          super(looper);
      }
      @Override
      public void handleMessage(Message msg) {
          // Normally we would do some work here, like download a file.
          // For our sample, we just sleep for 5 seconds.
          try {
              Thread.sleep(5000);
          } catch (InterruptedException e) {
              // Restore interrupt status.
              Thread.currentThread().interrupt();
          }
          // Stop the service using the startId, so that we don't stop
          // the service in the middle of handling another job
          stopSelf(msg.arg1);
      }
  }

  @Override
  public void onCreate() {
    // Start up the thread running the service. Note that we create a
    // separate thread because the service normally runs in the process's
    // main thread, which we don't want to block. We also make it
    // background priority so CPU-intensive work doesn't disrupt our UI.
    HandlerThread thread = new HandlerThread("ServiceStartArguments",
            Process.THREAD_PRIORITY_BACKGROUND);
    thread.start();

    // Get the HandlerThread's Looper and use it for our Handler
    serviceLooper = thread.getLooper();
    serviceHandler = new ServiceHandler(serviceLooper);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
      Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

      // For each start request, send a message to start a job and deliver the
      // start ID so we know which request we're stopping when we finish the job
      Message msg = serviceHandler.obtainMessage();
      msg.arg1 = startId;
      serviceHandler.sendMessage(msg);

      // If we get killed, after returning from here, restart
      return START_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
      // We don't provide binding, so return null
      return null;
  }

  @Override
  public void onDestroy() {
    Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
  }
}
```
- **onStartCommand** handle all incoming call and post work to handler running in background thread
- In example above, It work just like IntentService and processes all requests serially, one after another.
- ou could change the code to run the work on a thread pool, for example, if you'd like to run multiple requests simultaneously as bellow example.
```java
 @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /**
                     * Complete the task and stop the services**/
                }
            }).start();
        return START_STICKY;
    }
```
- onStartCommand() method must return an integer.
- This describe how the system should continue the service in the event that the system kills it.
- The return value can be
#### START_NOT_STICKY
- System will not restart your service if it get killed unless pending intent get delivered. Need to restart it by intent by components.
- use this to avoid running your service when not necessary and when your application can simply restart any unfinished jobs. Like, some database sync
#### START_STICKY
- System will restate your service if it get killed but won't receive last Intent in onStartCommand callback and get null intent in this callback unless pending intent start services.
- use this for service that are not executing commands but are running indefinitely and waiting for a job. Like music player, which can be started by user by clicking start button on notification.
#### START_REDELIVER_INTENT
- System will restate your service if it get killed and receive last intent in onStartCommand callback
- use this for service that are actively performing a job that should be immediately resumed, such as downloading a file.

### Starting a service
- use command **startService() or startForegroundService()** from component using **Intent**
```java
startService(new Intent(this, HelloService.class));
```
- If service don't allow to bind, then starService is only way to start communicate between service and app components. However, if you want the service to send a result back, the client that starts the service can create a PendingIntent for a broadcast (with getBroadcast()) and deliver it to the service in the Intent that starts the service. The service can then use the broadcast to deliver a result.
- Multiple requests to start the service result in multiple calls to the service's onStartCommand(). 

### Stopping a service
- Service won't get destroyed by its own unless service call selfStop() or component call stopService.
- Once requested to stop with stopSelf() or stopService(), the system destroys the service as soon as possible.
- Since you can start Service by multiple components concurrently, you shouldn't stop service when you done processing start request. as you might have new start request. Example : topping at the end of the first request would terminate the second one
- To avoid above problem, you can use **stopSelf(int)** to ensure that your request to stop the service is always based on the most recent start request. int you pass to this function is **Id of start request(startId) received in onStartCommand()**

**Note: [Foreground Service](./FOREGROUNDSERVICES.md) is also a started services.*