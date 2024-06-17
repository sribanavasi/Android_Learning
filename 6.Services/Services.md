# Services

- Service is Components of Android, started by other component like activity ,other services or broadcast receiver.
- User to perform long running operations in background.
- By default don't have any UI
- Run on main UI thread it's hosting process. so it don't create it's won thread or run on separate process. So please make sure you run blocking operation on separate thread.

### Types

#### Foreground

- Perform operation noticeable to user. Example: Audio track, Navigation.
- When foreground services running, must show **Notification**. And this can't be dismissive unless services stopped.
- Can run even app is in background and stopped.
- Started by **startForegroundService(Intent)** function
- self stopped  by calling **selfStop()** or **stopServices()** to stop service

#### Background

- Perform operation that does not directly noticeable by user. Example: Implementing some logic like storage compact/clean.
- After API 26, restrict its running if app is in background.
- Started by **startService(Intent)** function
- self stopped  by calling **selfStop()** or **stopServices()** to stop service

#### Bound

- Application component binds to services by calling **bindService()**
- Allow component to interact with bound services.
- onBind method of service will return binder.
- Stop services by calling **unbind()** method by components.

***Note: Both Foreground & Background services are started services***

### Creating any Services

- To create service, crate subclass of services by **extending Service** or it's subclass.
- After you extend class you need must override bellow function to handle key aspect of service life-cycle
- Also need to add services in manifest

#### onStartCommand()
- System invoke this function if service started by **startService** function.
- when service started by this method, it runs until service self-stop or stopped by component
- If you only want to allow service to be started, don't implement this method
#### onBind()
- System invoke this function if service started by **bind()** function.
- You must always implement this method.
- If you don't want to allow binding, return null.
- Otherwise return IBinder, which is interface client users to communicate with services.
- When Services Started by this method, services run until all component bound to services are unbound
#### onCreate()
- One time callback used to do services initial setup.
- Called before onBind or onStartCommand callback.
- If the service is already running, this method is not called.
#### onDestroy()
- System invokes this method when the service is no longer used and is being destroyed. 
- Use this to clean Up the resources used in services
#### Declaring a service in the manifest
```manifest
<manifest ... >
  ...
  <application ... >
      <service android:name=".ExampleService" />
      ...
  </application>
</manifest>
```
- You must declare all services in your application's manifest file as above example
- **android:name** is only required attribute
- You you don't want your services to be used by other app, please set **android:exported** attribute to **true**
- optional **android:description** attribute help user to know about service when they check in devices about running services.
- **android:foregroundServiceType** attribute need to be added for foreground services.

***Notes***
- System kill services only when there is need of resources to activity user focuses on.
- If Service is bound to activity user focus has, it less likely get killed.
- If it foreground services, it less likely and rarely get killed.
- If long running started service have more likely get killed over time because  system lowers its position in the list of background tasks over time.
- If started service get killed, you can handle and design its restart and System restarts it as soon as resources become available and it is dependent on value(STICK flag) you return on onStartCommand.
- Always use **Explicit intent** when starting a Service. so don't declare intent-filter. also System throw exception (After API 26), if you start service by Implicit intent.

### lifecycle of a service
![Service Lifecycle](sribanavasi/Android_Learning/6.Services/img.png)

- Service Life cycle can follow t path
```java
public class ExampleService extends Service {
    int startMode;       // indicates how to behave if the service is killed
    IBinder binder;      // interface for clients that bind
    boolean allowRebind; // indicates whether onRebind should be used

    @Override
    public void onCreate() {
        // The service is being created
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        return startMode;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return binder;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return allowRebind;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
    }
}
```

#### 1. Started Service 

- Service Created when other components calls **startService()**.
- on **startService()** result in calling **onStartCommand()** in service and onStartCommand receive intent that passed on startService.
- It runs until **selfStop()** called by service or **stopService()** called by components. and get destroyed afterward.
- stopService or selfStop won't result in calling any callbacks in Services.
- Since you can start Service by multiple components concurrently, you shouldn't stop service when you done processing start request. as you might have new start request. Example : topping at the end of the first request would terminate the second one
- To avoid above problem, you can use **stopSelf(int)** to ensure that your request to stop the service is always based on the most recent start request. int you pass to this function is **Id of start request(startId) received in onStartCommand()**

#### 2. Bound Service

- Service Created when other components/client calls **bindService()**
- on **bindService()** result in calling **onBind()** in service and onBind receive intent that passed on bindService.
- Client can then communicate with service using IBinder interface.
- It runs until all component bound to it unbound by calling **unbindService()**
- unbindService result in calling **onUnbind** callback in services
- Since it allow multiple client to bound, service get destroyed after all client unbound.

**NOTE: These two paths aren't entirely separate, you can bind to service that started also. In cases such as this, stopService() or stopSelf() doesn't actually stop the service until all of the clients unbind.**

- **onCreate()** called only once for service before service stated by either started or bound or both. And called only once per service no matter how many time you called startService or bindService.
- And **onDestroy()** called when service stopped.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**Note: Since Services run on main thread of processor , it suggested to use IntentService subclass of Service that uses a worker thread to handle all the start requests, one at a time.**

**[IntentService](sribanavasi/Android_Learning/6.Services/JobServices and JobScheduler/JobScheduler_JonServices.md) will not work well starting with Android 8 Oreo, due to the introduction of Background execution limits. Moreover, it's deprecated starting with Android 11.**

**[JobIntentService](sribanavasi/Android_Learning/6.Services/JobServices and JobScheduler/JobScheduler_JonServices.md) as a replacement for IntentService that is compatible with newer versions of Android.**

Also Read:
- [Started Services](6.Services/Started Services/Started_Services.md)
- [Bound Services](sribanavasi/Android_Learning/6.Services/Bound Services/Bound_Services.md)
