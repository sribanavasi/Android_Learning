# Foreground Services

- Foreground services perform operations that are noticeable to the user show a status bar notification, to make users aware that your app is performing a task in the foreground and is consuming system resources.

**Note:User can dismiss notification of Foreground service by default(Android 13 onward). To Make this notification, non-dismissible, set true nto the setOngoing() method when you create your notification using Notification.Builder**

**Note: TO give seamless experience for short-running foreground service, in some devices(Android 12 onward), System won't show notification to for few sec. There are a few exceptions, as several types of services always display a notification immediately**

**Note: Services that show a notification immediately if has at least one of the following characteristics (even in Android 12 onward)**
- if notification has action button
- foreground service type :  mediaPlayback, mediaProjection, or phoneCall.
- Opted to set FOREGROUND_SERVICE_IMMEDIATE to setForegroundServiceBehavior() when setting up the notification.

## Step to set up Foreground Services

### Declare foreground services in your manifest
```manifest
<manifest xmlns:android="http://schemas.android.com/apk/res/android" ...>
  <application ...>

    <service
        android:name=".MyMediaPlaybackService"
        android:foregroundServiceType="mediaPlayback|microphone"
        android:exported="false">
    </service>
  </application>
</manifest>
```

- Need to `android:foregroundServiceType` attribute in service tag
- If service use more then one type, use `|` to separate them
- You get `MissingForegroundServiceTypeException` on onStartForeGround(), if you don't mention the types
- Choose appropriate type based on [Foreground Service Type](https://developer.android.com/develop/background-work/services/fg-service-types) use cases.

### Request the foreground service permissions

```manifest
<manifest xmlns:android="http://schemas.android.com/apk/res/android" ...>

    <uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_CAMERA"/>

    <application ...>
        ...
    </application>
</manifest>
```

- Android 9 onward, need to request `FOREGROUND_SERVICE` if you are using foreground services
- API level 34 or higher, it must request the appropriate permission type based on Foreground Service Type. Otherwise, service throws `SecurityException`.

### Start a foreground service
```java
Context context = getApplicationContext();
Intent intent = new Intent(...); // Build the intent for the service
context.startForegroundService(intent);
```
- use `startForegroundService(intent)` to start foreground services
- Implementation of Foreground service class is simmilear to any other services. But need to Build notification inside onStartCommand function along with other services operations implementations.
```java
public class MyCameraService extends Service {

    private void startForeground() {
        // Before starting the service as foreground check that the app has the
        // appropriate runtime permissions. In this case, verify that the user
        // has granted the CAMERA permission.
        int cameraPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (cameraPermission == PackageManager.PERMISSION_DENIED) {
            // Without camera permissions the service cannot run in the
            // foreground. Consider informing user or updating your app UI if
            // visible.
            stopSelf();
            return;
        }

        try {
            Notification notification =
                new NotificationCompat.Builder(this, "CHANNEL_ID")
                    // Create the notification to display while the service
                    // is running
                    .build();
            int type = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                type = ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA;
            }
            ServiceCompat.startForeground(
                    /* service = */ this,
                    /* id = */ 100, // Cannot be 0
                    /* notification = */ notification,
                    /* foregroundServiceType = */ type
            );
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    e instanceof ForegroundServiceStartNotAllowedException
            ) {
                // App not in a valid state to start foreground service
                // (e.g started from bg)
            }
            // ...
        }
    }

    //...
}
```

### Remove a service from the foreground
- call stopForeground(). This method takes a boolean, which indicates whether to remove the status bar notification as well. Note that the service continues to run only it's notification removed. Stop service as other services.

**Android 12 or higher can't start foreground services while the app is running in the background, except for a few special cases. If an app tries to start a foreground service while the app runs in the background, and the foreground service doesn't satisfy one of the exceptional cases, the system throws a ForegroundServiceStartNotAllowedException.**
