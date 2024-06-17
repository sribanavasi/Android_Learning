# Creating Bound Services

- You Should return IBinder interface in onBound callback to make service as bound services.
- If you don't want to allow service to be started, you should not implement onServiceCommand function in service

### Create a bound service
3 way we can create IBinder Interface

**1.Extend the Binder class:**
- If Service run in application and available for only your application. use this way
- Crete interface by extending Binder class and return it's instance in onBinder method
- Only scenario when this is not allowed is , when service is accessed by other application

**2. Use a Messenger**
- If your interface need to work access application, you need to create it using Messenger.
- In this way, Service define handler that handle different type of message.
- This handler is base to messenger that can share IBinder with client.
- Client will also have messenger of it's own to get message back from services

**3. Use AIDL**
- use Android Interface Definition Language (AIDL)  for interface creation.
- This way is not preferred. so we won't discuss it here.

### Extend the Binder class

- If your service is targeted to use in same processes(app) and it doesn't need to work across processes, use this method
- In service, create **Binder class instance**, that do one of the follow
    - Prove instance of service, so client can call public functions in services
    - Contains public function, that client can call
    - provide instance of other class, that service hosted, so client can call public function of hosted class
- return **Binder instance** on onBind function
- client receive **Binder instance** in onServiceConnected() callback method and make calls to the services using function provided.

**Service code**
```java
public class LocalService extends Service {
    // Binder given to clients.
    private final IBinder binder = new LocalBinder();
    // Random number generator.
    private final Random mGenerator = new Random();

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            // Return this instance of LocalService so clients can call public methods.
            return LocalService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /** Method for clients. */
    public int getRandomNumber() {
      return mGenerator.nextInt(100);
    }
}
```

**Client code**
```java
public class BindingActivity extends Activity {
    LocalService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService.
        Intent intent = new Intent(this, LocalService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    /** Called when a button is clicked (the button in the layout file attaches to
      * this method with the android:onClick attribute). */
    public void onButtonClick(View v) {
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call is something that might hang, then put this request
            // in a separate thread to avoid slowing down the activity performance.
            int num = mService.getRandomNumber();
            Toast.makeText(this, "number: " + num, Toast.LENGTH_SHORT).show();
        }
    }

    /** Defines callbacks for service binding, passed to bindService(). */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
```

### Use a Messenger

- if your service is being used in other app/processes, then you need to use Messenger to provide your service interface
- Messenger enable IPC(interprocess communication)
- Messenger queses all call to services, where AIDL send simultaneous request to services. So in messenger don't need to do multithreading, where in AIDL , we need to handle multiple request by multithreading, and this is the reason, AIDL is not recommended to use in normal cases.
- In manifest, set service's attribute *export* as true.
- Service implement **Handler** that receive the callback for each client call
- Service uses handler object to create **Messenger** object.
- Messenger create **IBinder**, that services return in onBind function
- Client uses IBinder to instantiate Messenger which it uses to send request as message to services
- Services receive this message in on **handleMessage()** callback of the handler
- in this way, client won't call services message directly, instead, it send message and based on message, services execute different operation 

```java
public class MessengerService extends Service {
    /**
     * Command to the service to display a message.
     */
    static final int MSG_SAY_HELLO = 1;

    /**
     * Handler of incoming messages from clients.
     */
    static class IncomingHandler extends Handler {
        private Context applicationContext;

        IncomingHandler(Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Toast.makeText(applicationContext, "hello!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    Messenger mMessenger;

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        mMessenger = new Messenger(new IncomingHandler(this));
        return mMessenger.getBinder();
    }
}
```

```java
public class ActivityMessenger extends Activity {
    /** Messenger for communicating with the service. */
    Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    boolean bound;

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mService = new Messenger(service);
            bound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected&mdash;that is, its process crashed.
            mService = null;
            bound = false;
        }
    };

    public void sayHello(View v) {
        if (!bound) return;
        // Create and send a message to the service, using a supported 'what' value.
        Message msg = Message.obtain(null, MessengerService.MSG_SAY_HELLO, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to the service.
        bindService(new Intent(this, MessengerService.class), mConnection,
            Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service.
        if (bound) {
            unbindService(mConnection);
            bound = false;
        }
    }
}
```

In this Example, only one way communication is shown. If you want to Services also to send client messages, then you need to create it's **Messenger**. send **message** to service in onServiceConnected callback, and this message include client messenger in **replyTo()** parameter of send(). And Services use this Messenger and send message back.
Example : [services](https://android.googlesource.com/platform/development/+/master/samples/ApiDemos/src/com/example/android/apis/app/MessengerService.java) and [client](https://android.googlesource.com/platform/development/+/master/samples/ApiDemos/src/com/example/android/apis/app/MessengerServiceActivities.java)