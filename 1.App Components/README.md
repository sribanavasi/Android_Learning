# App Components

These are the building blocks of the Android app
1. Activity
2. Services
3. Broadcast Receiver
4. Content Providers

## 1. Activity
- Entry point for interacting with users. Responsible for single screen: Keeping track of all processes that are going on, and handling these processes.
 ex: user on the home screen, that maybe one activity
- To activate this componet:startActivity() or starActivityForResult() with intent

## 2. Services 
- Entry point for keeping the app running in the background: This component runs in the background to perform long-running operations or perform Remote operations.
- To activate this componet:startService() and bindService() with intent

### Two types: 
- Started Services: it tells the System to keep them running until their work is completed.
    Two types of Started Services: Foreground Services(Music player) and Background Services(Downloading the file).
- Bound Services: it tells the system to keep them running because some processes (components) have a dependency on these services. Until all processes (components) are unbound, it keeps running.

## 3. Broadcast Receiver
- Used to deliver system events to the apps outside of regular flow.Ex; aleram or calls.
In some cases, the broadcast receiver can schedule "JobService" to perform some work based on even using "JobScheduler".And they are used to interact among the apps.
- To activate this componet:sendBroadcast() or sendOrderedBroadcast() with intent

## 4. Content Providers
- It Manages a shared set of app data that you can store in the file system. Handle Permission to read/write these data.
- To activate this component: Only Content providers are activated when Requested by Content Resolver with Query But not by Intent


## Activate Components.
Intent activates 3 of 4 components (activity, service, broadcast receiver). The intent is Like a message that requests an action from one component to another.
Internet Object created with Message to activate specific component(Explicit Inetnt) or Type of Components(Implicit Intent)



