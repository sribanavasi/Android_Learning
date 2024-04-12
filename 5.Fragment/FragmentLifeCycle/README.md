# [Fragment Lifecycle](https://github.com/sribanavasi/Android_Learning/blob/main/5.Fragment/FragmentLifeCycle/fragment_lifecycle.png)

## onAttach
- Called when the fragment has been associated with the activity.
- The fragment and the activity is not fully initialized yet.

## onCreate
- Called to do initial creation of the fragment.
- Initialize essential components, set up data structures.
- Fragment Not visible to the user yet.

## onCreateView
- Called to create the view hierarchy associated with the fragment.
- In this stage, the fragment creates its UI. 
- You inflate a layout file or construct a view hierarchy programmatically and return the root view.

## onViewCreated
- Called immediately after onCreateView & view has been created.
- It is recommended to do most of the view setup here.

## onActivityCreated
- Called when the activity's onCreate() method has returned.
- Fragment is now associated with the activity and can be used to interact with the activity.
- This is the first place where the fragment can access the Activity instance.

## onStart
- Fragment is visible to the user but not yet interactive.
- Start any animations, or prepare the fragment to interact with the user.

## onResume
- Fragment is visible and interactive.
- Start operation which will take longer time.

## onPause
- Fragment is partially visible but interactive.
- Pause any operations that should not continue when the fragment is not in focus.

## onStop
- Fragment is no longer visible
- Clean up resources that should be released when the fragment is not visible.

## onDestroyView
- Called when the view hierarchy associated with the fragment is being removed.
- Clean up resources related to the view(UI).

## onDestroy
- Called when the fragment is no longer in use.
- Clean up resources held by the fragment.

## onDetach
- Called when the fragment is being disassociated from the activity.
