# Tasks and the back stack

- Task is a collection of activities and these activities are put in a LIFO manner, which is called back-stack.
- When the user clicks on the app icon or shortcut, if the back stack is not there in the system, a new stack comes to the foreground with the main activity in the root of the stack. When a new activity is launched from the current activity, the new activity comes at the top of the stack. When the user presses the back button, the current activity pops up from the stack. Activity in the stack is never rearranged, it is always pushed into or popped from the stack, and it is in the "Last In First out" manner. So it is called a back stack.
- When the user is on root activity in the stack, and back pressed, before Android 11, the system is used to kill that activity. But in Android 12 and above, the system stops the activity and puts the task and activity in the background, so when the user reopens the app, it will be easy to restore the previous state.
- We can interrupt and change the default behavior of the task and can do it by adding some attributes in the <Activit> tag in the manifest or flag in the internet, which passes to the startActivity function.

### Attributes for <activity> that you can use to manage tasks:

- [taskAffinity](https://developer.android.com/guide/topics/manifest/activity-element#aff)
    Define which task, the launched activity will belong.[Explaned here](https://chat.openai.com/share/cc7ca8dc-cfe5-4120-986e-9d6add87dbc3)
- launchMode
    Define how the launches will be handled. 
- [alwaysRetainTaskState](https://developer.android.com/guide/topics/manifest/activity-element#always)
    The default behavior of keeping the state of all activity in the stack and returning to the top activity, when the user returns to the task. If is it set to false, the task will be reset on return.
- [allowTaskReparenting](https://developer.android.com/guide/topics/manifest/activity-element#reparent)
    By setting it to true, when an activity with a different affinity is launched and is placed in the current back stack, and then if its affinity's task comes in the foreground, the activity moves from the old affinity's task to the same affinity's task, and then again old task come back to the foreground, it won't have that activity in the task. That means activity moves from one task to another based on affinity. For example, In an email, the link opens, and it launches within this email's task, then if you open Chrome, it will be moved to Chrome's task, and when you come back to the email, it won't have that launched link activity.
- [clearTaskOnLaunch](https://developer.android.com/guide/topics/manifest/activity-element#clear)
    Suppose the user launches activity P from the home screen, and from there goes to activity Q. The user next taps Home, and then returns to activity P. Normally, the user sees activity Q, since that is what they were last doing in P's task. However, if P sets this flag to "true", all of the activities on top of it—in this case, Q—are removed when the user launches activity P from the home screen. So, the user sees only P when returning to the task. If this attribute and allowTaskReparenting are both "true", any activities that can be re-parented are moved to the task they share an affinity with. The remaining activities are then dropped.
- [finishOnTaskLaunch](https://developer.android.com/guide/topics/manifest/activity-element#finish)
    All activity except for root activity instance when task launched by clicking on the app icon. ie, in the task we have a->b>c activity. and it is in the background. when the user launches a task by app icon, all activity except the root activity instance will be shutdown.



## Launch mode can be handled either by flagging in intent or setting modes to activity tag in the manifest.

### Flags for intent

-- FLAG_ACTIVITY_NEW_TASK = singleTask in launch mode
-- FLAG_ACTIVITY_CLEAR_TOP = (Clears all the activities that are on top of the launched activity, if they already exist in the task)
-- FLAG_ACTIVITY_SINGLE_TOP = singleTop in launch mode

# Launch Modes

## Standard

- in <activity> launchMode:"standard"
- default mode
- new activity added at top of the stack, and pop foreground activity on backpressure
- example: **A>B>A>A>B**.(A and B are activity launched from one another and how they arranged in stack )
- Example code can be seen [here](https://github.com/sribanavasi/android_lauch_modes)

## SingleTOP

- in <activity> launchMode:"singleTop" or in intent, FLAG_ACTIVITY_SINGLE_TOP
- If the activity is at the top of the stack, and the activity starts the same activity, instead of creating a new instance, call **onNewIntent()** callback in the same activity.
- example: **A>B>C** is current stack. if C launches any activity other than c, then we will have a stack like **A>B>C>newActivity**. if from stack **A>B>C**, activity C starts C itself, then Stack will be still **A>B>C**. But In activity C, onNewIntent() callback gets triggered.
- When an existing activity handles a new intent and changes the ui state, it will not go to the previous ui state on backpress. Because of this, using the back button, you can't get the previous state of activity.
- Example code can be seen [here](https://github.com/sribanavasi/android_lauch_modes/tree/single_top)

## SingleTask

- in <activity> launchMode:"singleTask" or in intent, FLAG_ACTIVITY_NEW_TASK
- The system creates the activity at the root of a new task or locates the activity on an existing task with the same affinity. If an instance of the activity already exists, the system routes the intent to the existing instance through a call to its onNewIntent() method, rather than creating a new instance. Meanwhile, all of the other activities on top of it are destroyed.
- If the system finds an activity with the same affinity, instead of creating a new activity, it roots the intent to the existing activity in the task by calling **onNewIntent**, and destroying all activity on top of it are destroyed. Example: **A** is a task and A launches B with singleTask, a new task will be **A>B>**, and any activity launched by B will be on top of B in the stack. If **A>B>C>D** is the stack, and D launches B with singleTask, then we will have a stack as **A>B**. 
- Example code for SingleTask with same affinity can be seen [here](https://github.com/sribanavasi/android_lauch_modes/tree/single_task_same_affinity)
- When you launch an Activity with singleTask mode and have a different affinity to the activity it launching, we have two cases that can be seen. 
    - In one case there is no task with launching activity's affinity, it creates a new task and makes this activity as root activity in a new task example, **A>>C** is a stack, and C launches B with singleTask, then we will have two tasks whereas 1st task **A>>C** is in background and new task **B** at the foreground. if any new activity is launched from B will be stacked in the new task.
    - The second case is There is another task in the background with the launching task's affinity, then it pops all the activity if there is other's activity instance on top of the launching activity and routes to that activity through calling **onNewIntent**. Example: if we already have two tasks, where **D>B>C** is in the background and **A>C** is in the foreground, and if C launches B with singleTask, Then task **A>C** will go to the background and **D>B** come to the foreground by poping C which was on top of B and in that task, we get **onNewIntent** called in B.
- Example code for SingleTask with same different affinity can be seen [here](https://github.com/sribanavasi/android_lauch_modes/tree/single_task_different_affinity)

## singleInstance

- in <activity> launchMode:"singleInstance" or in intent, FLAG_ACTIVITY_NEW_TASK
- The behavior is the same as for "singleTask", except that the system doesn't launch any other activities into the task holding the instance. The activity is always the single and only member of its task. Any activities started by this one open in a separate task.
- That means, in this launch mode, if it is with the same affinity or without the same affinity, a new activity will be launched in the new task(if another task with this lurching activity alone is not there), and if we start any other activity in that task with that activity, it will be launched in old activity only(Assuming launching activity have a same affinity as an existing background task).
- Code Example for singleInstance with same affinity is [here](https://github.com/sribanavasi/android_lauch_modes/tree/single_instance_same_affinity), with different affinity is [here](https://github.com/sribanavasi/android_lauch_modes/tree/single_instance_different_affinity)
