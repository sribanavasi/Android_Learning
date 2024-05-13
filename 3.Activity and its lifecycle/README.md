# Activities

- It Provides a window, on which the app draws its UI.
- One screen in the app, for a given instance.
- Servers as Entry points to an app
- As, all components, we also need to register it in the manifest to use in the app
- unlike, other applications, we don't have a main(Starting point) here. Each screen is an Activity and some callback handles its life cycle based on screen behavior and user interaction triggered by the system to let the activity know its state.

# Activity Lifecycle

![Activity LifeCycle](https://github.com/sribanavasi/Android_Learning/blob/main/3.Activity%20and%20its%20lifecycle/activity_lifecycle.png)

## onCreate

- When the system creates activity, this callback is called. So, You must implement this callback
- In this state, perform basic activity setup logic, that happens, only once for an activity. example: set layout/view of activity (setContentView()). restore state from the bundle, if it ever existed before. initialize member views, if it require later.

```
lateinit var textView: TextView

// Some transient state for the activity instance.
var gameState: String? = null

override fun onCreate(savedInstanceState: Bundle?) {
    // Call the superclass onCreate to complete the creation of
    // the activity, like the view hierarchy.
    super.onCreate(savedInstanceState)

    // Recover the instance state.
    gameState = savedInstanceState?.getString(GAME_STATE_KEY)

    // Set the user interface layout for this activity.
    // The layout is defined in the project res/layout/main_activity.xml file.
    setContentView(R.layout.main_activity)

    // Initialize member TextView so it is available later.
    textView = findViewById(R.id.text_view)
}

// This callback is called only when there is a saved instance previously saved using
// onSaveInstanceState(). Some state is restored in onCreate(). Other state can optionally
// be restored here, possibly usable after onStart() has completed.
// The savedInstanceState Bundle is same as the one used in onCreate().
override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    textView.text = savedInstanceState?.getString(TEXT_VIEW_KEY)
}

// Invoked when the activity might be temporarily destroyed; save the instance state here.
override fun onSaveInstanceState(outState: Bundle?) {
    outState?.run {
        putString(GAME_STATE_KEY, gameState)
        putString(TEXT_VIEW_KEY, textView.text.toString())
    }
    // Call superclass to save any view hierarchy.
    super.onSaveInstanceState(outState)
}
```
- when, onCreate complete, state of activity goes to onStart

## onStart

- This call makes the activity visible(UI is initialized) to the user as the app prepares for the activity to enter the foreground and become interactive. But still not interactive.
- This method completes quickly and follows onStart

## onResume

- This is called, right before the app is active and interactive.
- Stays in this state until it is interrupted by something like, navigating to another activity, a call comes, a dialog comes, etc. Basically, the activity is not interactive.
- Common practice is to start resource-dependent operations like opening cameras or sensors-related operations are done in this state. (You can also start these in on create, then the system denies camera/resource access to other activities that or active)

## onPause

- Common practice is to release system resources in this state.
- Don't use it to save application data/ network calls, because, this function is quick and may not complete those operations.
- from here, the activity state can change to onResume or onStop

## onStop

- Called, when activity no longer visible to user.
- perform cpu intensive, shutdown operation like, ex: saving data to database if not saved before.

## onRestart

- The system invokes this callback when an activity in the Stopped state is about to restar
- After this callback, app always go to on start state.

## onDestory

- called when the app terminates the activity or the system temporarily destroys
- Release and clean up all the resources occupied by activity by activitys.

### Note: System kills activity, when it require RAM and its likelihood is depende on activity state as shown in bellow

Likelihod of being killed | State
:------------ | :------------
Lowest | Foreground(inteactive) or about to Foreground
Low | Visible(no facus and not inetactive)
High | Background
Highest | Empty


### Notes:
-Regardless of which build-up event you choose to perform an initialization operation in, make sure to use the corresponding lifecycle event to release the resource. If you initialize something after the ON_START event, release or terminate it after the ON_STOP event. If you initialize after the ON_RESUME event, release after the ON_PAUSE event.

## Saving and restoring transient UI state
When configuration changes, the screen rotation or switching into muti-window mode stsyem destroys your activity. When the activity is re-created, the user expects to maintain the page states ad before. 
Sometimes the app terminates the activity and the user navigates back to the activity the user expects to have previous state in activity.
In the second case, we have to use the view model and local storage to store the app state and this called **Persistent State**.
In the first case, even actual instance goes, the system remembers its existence and when the user goes to that activity, we can create a new instance of activity with a set of saved data, that describes the state of the activity, when it is destroyed.
The saved data that the system uses to restore the previous state is called the **instance sta**. (Collection of key-value data)
This is suitable for a lightweight UI state. 
**onSaveInstanceState(outBundle: Bundle)** callback is used to save these key values, 
**Note**: onSaveInstanceState() is not called when the user explicitly closes the activity or in other cases when finish() is called.
during on creating or onRestoreInstanceState, we can restore the old state of activity. In the activity you have to check bundle is null or not.
The system calls onRestoreInstanceState() only if there is a saved state to restore, so you do not need to check whether the Bundle is null.

```
override fun onSaveInstanceState(outState: Bundle?) {
    // Save the user's current game state.
    outState?.run {
        putInt(STATE_SCORE, currentScore)
        putInt(STATE_LEVEL, currentLevel)
    }

    // Always call the superclass so it can save the view hierarchy state.
    super.onSaveInstanceState(outState)
}

companion object {
    val STATE_SCORE = "playerScore"
    val STATE_LEVEL = "playerLevel"
}

override fun onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState) // Always call the superclass first

    // Check whether we're recreating a previously destroyed instance.
    if (savedInstanceState != null) {
        with(savedInstanceState) {
            // Restore value of members from saved state.
            currentScore = getInt(STATE_SCORE)
            currentLevel = getInt(STATE_LEVEL)
        }
    } else {
        // Probably initialize members with default values for a new instance.
    }
    // ...
}

override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    // Always call the superclass so it can restore the view hierarchy.
    super.onRestoreInstanceState(savedInstanceState)

    // Restore state members from saved instance.
    savedInstanceState?.run {
        currentScore = getInt(STATE_SCORE)
        currentLevel = getInt(STATE_LEVEL)
    }
}
```

## Starting one activity from another

- Use Intent to start activity and command is startActivity(intnet)
```
val intent = Intent(this,OtherActivity::class.java)
startActivity(intnet)
```
- Sometimes, you may want the same result back from the activity you start, then use the command **startActivityForResult(Intent,requestcode: int)**
- When activity exits, it call setResult(int, INTtent?) with code, RESULT_CANCELED, RESULT_OK, RESULT_FIRST_USER , with opetinal Intent to for any adinatinal data.
- You can receive this data in **onActivityResult(requestCode:int,resultCode:int,intnet)**, 


## Coordinating activities

When we start activity B from activity A, the State changes of activity A and B in order will be as below
Activity A : onPause()
activity B : onCreate(),onStart(),onResume()
Activity A : onStop()
From above case, in activity B, the finish() function called
Activity B : onPause()
Activity A : onRestart(),onStart(),onResume()
Activity B : onStop(),onDestroy()


Note: if on one or more specific configurateing change, you want to bypass restart of activity, add android:configChanges in manifest.You will receive a call to your current activity's onConfigurationChanged(Configuration)



