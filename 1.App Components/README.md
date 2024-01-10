# App Components

These are building Blocks of android app
1. Activity
2. Services
3. Broadcast Receiver
4. Content Providers

## 1. Activity
- Entry point for interacting with user. Responsible for single screen : Keeping tack of What all process is going on, and handling these process.
 ex: user on home screen , that maybe one activity
- To activate this componet:startActivity() or starActivityForResult() with intent

## 2. Services 
- Entry point to for keeping app running in background: This components Runs in Background to perform long-running opertaions or perform Remote operation.
- To activate this componet:startService() and bindService() with intent

### Two type : 
-- Started Services: it tells System to keep them running untill there work compltes.
    Two type in Started Services: Foreground Services(Music player) and Background Services(Downloading the file).
-- Bound Services: it tells system to keep them running, beacause some process(componets) have dependency on these services. untill all process(componets) are unbound, it keep runing.

## 3. Broadcast Receiver
- Used to deliver system events to the apps outside of reguler flow.Ex;Aleram,or calls.
In some case, broadcast reciever can schedule "JobService" to perform some work based on even using "JobScheduler".And used to interact beetween the app.
- To activate this componet:sendBroadcast() or sendOrderedBroadcast() with intent

## 4. COntent Providers
- It Manages a shared set of app data that you can store in file system.Handle Permition to read/write these data.
- To activate this componet:Only Contnet provider are acivated when Requested by Content Resolver with Query


## Activate Components.
Intent is used to activate 3 of 4 componets(activity,service, broadcast receiver). Intent is Like messager that request an action from one componet to other.
Inetnet Object created with Meassage to activate specific componet(Explicit Inetnt) or Type of Components(Implicit Intent)



