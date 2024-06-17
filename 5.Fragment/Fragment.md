# Fragment

A fragment represents a modular portion of the user interface within an activity. A fragment has its own lifecycle, receives its own input events, and you can add or remove fragments while the containing activity is running. You can combine multiple fragments in a single activity to build a multi-pane UI and reuse a fragment in multiple activities.

### Add a fragment to an activity
Can add in two ways.
1. Declare the fragment inside the activity's layout XML file. You need to use android:name attribute to specify the fragment class.
Example:
```xml
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/fragment_container_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:name="com.example.ExampleFragment" />
```
2. Programmatically add the fragment to an existing ViewGroup. You can do this by creating an instance of the fragment and then use FragmentManager to add it to the activity.
- You need to use FragmentContainerView to add a fragment to an activity. Because it fixes the known issue related to the fragment.
```xml
<androidx.fragment.app.FragmentContainerView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/fragment_container_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
- Note that the fragment transaction is only created when savedInstanceState is null. This is to ensure that the fragment is added only once, when the activity is first created.
```kotlin
class ExampleActivity : AppCompatActivity(R.layout.example_activity) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val bundle = bundleOf("some_int" to 0)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<ExampleFragment>(R.id.fragment_container_view,bundle)
            }
        }
    }
}
```
- You can use the FragmentManager to add, remove, replace, and perform other fragment transactions in the activity.
- If your fragment requires some initial data, arguments can be passed to your fragment by providing a Bundle in the setArguments() method before adding the fragment to the activity. And it is optional to use the arguments in the fragment add.
- The arguments Bundle can then be retrieved from within your fragment by calling requireArguments(), and the appropriate Bundle getter methods can be used to retrieve each argument.
```kotlin
class ExampleFragment : Fragment(R.layout.example_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val someInt = requireArguments().getInt("some_int")
        
    }
}
```

## Working 

- FragmentManager manages backstack and At runtime, it perform back stack operation like, add, remove, replace. Each transaction commited together as single unit called **Fragment Transaction**
- When, user click on back button or **FragmentManager.popBackStack()** called, top most **Fragment Transaction** will get popped.
- If all transaction in that stack get popped, back event pointed to its parent fragment's backstack. if it have activity as parent, then back event point to activity. so in next back event, activity get destroyed. 


## Fragment manager
- The FragmentManager class is responsible for managing fragments in an activity. 
- It is used to perform fragment transactions, such as adding, removing, replacing, and hiding fragments. 
- The FragmentManager also manages the back stack of fragments, which allows users to navigate back to the previous fragment state.

### Access the FragmentManager
- **In Activity**: getSupportFragmentManager() 
- **In Fragment**: get it's host fragment manager -> getParentFragmentManager()
- **In Fragment**: to manage child fragment get fragment manager -> getChildFragmentManager()

### Use of FragmentManager

#### Perform a transaction

- To perform a fragment transaction, you need to create a FragmentTransaction object using the beginTransaction() method of the FragmentManager.
- You can perform transaction like add, replace and delete
```kotlin
supportFragmentManager.commit {
    replace<ExampleFragment>(R.id.fragment_container)
    setReorderingAllowed(true)
    addToBackStack("name") // Name can be null
}
```
- In above code, we are replacing old fragment in container **R.id.fragment_container** by new fragment instance **ExampleFragment** by commiting it. 
- **setReorderingAllowed()** optimize the fragment state changes so that animation and transaction work correctly.
- **addToBackStack()** add transaction to back stack so that when back button click or popBackstack call happens, revers the transaction and previous state come to foreground. If you don't add this method, as soon as transaction happened, old fragment instance will get destroyed and can't access them reversing transaction. 

#### Find an existing fragment

- use **findFragmentById()**  to find the fragment in given container by container id.
- use **findFragmentByTag()** to find the fragment by tag used in transaction commited.
```kotlin
val fragment: ExampleFragment =
        supportFragmentManager.findFragmentById(R.id.fragment_container) as ExampleFragment
//or
val fragment: ExampleFragment =
    supportFragmentManager.findFragmentByTag("tag") as ExampleFragment
```
#### Special considerations for child and sibling fragments

- Only one FragmentManager can control the fragment back stack at any given time.
- When there is multiple siblings or child at same time, then once fragment manger is designed to handle back stack.
- **setPrimaryNavigationFragment()** is call on transaction to define primary navigation fragment.
- Consider the navigation structure as a series of layers, with the activity as the outermost layer, wrapping each layer of child fragments underneath. Each layer has a single primary navigation fragment.
- When the Back event occurs, the innermost layer controls navigation behavior. Once the innermost layer has no more fragment transactions from which to pop back, control returns to the next layer out, and this process repeats until you reach the activity.
- When two or more fragments are displayed at the same time, only one of them is the primary navigation fragment. Setting a fragment as the primary navigation fragment removes the designation from the previous fragment.

#### Support multiple back stacks

- In some case like bottom navigation, app need to support multiple backstack, where each tab will have its own fragment stack. In this case we save one backstack and restore other backstack using **saveBackStack()** and **restoreBackStack()** methods.
- **saveBackStack()** is similar to **popBacksStack()**, it pop all specified transaction and all bellow transaction in back stack.But store all of them state and can load back it to backstack afterword by **restoreBackStack()** method.
- You can use saveBackStack() only with transactions that call setReorderingAllowed(true) so that the transactions can be restored as a single, atomic operation.
- You can't use saveBackStack() and restoreBackStack() unless you pass an optional name for your fragment transactions with addToBackStack().
```kotlin
supportFragmentManager.commit {
  replace<ExampleFragment>(R.id.fragment_container)
  setReorderingAllowed(true)
  addToBackStack("replacement")
}

supportFragmentManager.saveBackStack("replacement") // pop given transaction & all bellow transaction in backstack and stores its state.

supportFragmentManager.restoreBackStack("replacement") // reload all transaction into backstack and state of fragment
```
#### Provide dependencies to your fragments

- When you do add fragment, you can use custom constructor of fragment and give the instance of fragment to be added. other wise Fragment manager use Fragment factory which uses no-Argument constructor generate fragment instance.
- Also, when configuration changes happens, fragment manager user this fragment factory to get new instance of fragment. 
- So, if you want your fragment to use custom constructor in this situation, you need to change the fragment manager's default factory to custom factory you carte by extending FragmentFactory class . code example is bellow
```kotlin
class DessertsFragment(val dessertsRepository: DessertsRepository) : Fragment() {
    ...
}

class MyFragmentFactory(val repository: DessertsRepository) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =
        when (loadFragmentClass(classLoader, className)) {
            DessertsFragment::class.java -> DessertsFragment(repository)
            else -> super.instantiate(classLoader, className)
        }
}

class MealActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = MyFragmentFactory(DessertsRepository.getInstance())
        super.onCreate(savedInstanceState)
    }
}
```

## Fragment transactions

- Fragment Manager can add,remove and replace the fragment based on user interaction and each set of operation you commit is called transaction.
- To do this operation we use API provided by **FragmentTransaction** class
- You can group multiple this operation in single transaction and is help-full when you want to change all sibling fragment state at once.

#### Allow reordering of fragment state changes

- Each FragmentTransaction should use **setReorderingAllowed(true)**
- Flag is not enabled by default.
- Optimize the fragment state changes so that animation and transaction work correctly.
- Multiple transactions are executed together, intermediate fragment don't go through its lifecycle changes or transaction changes.
- Note that this flag affects both the initial execution of the transaction and reversing the transaction with popBackStack().

#### Commit is asynchronous

- **commit()** doesn't commit the transaction immediately, but do as soon when UI thread available. **commitNow()** function do commit immediately in main thread.
- But **commitNow()** function incompatible with **addToBackStack()**
- SO recommended way is to call **executePendingTransaction()** after **commit** which execute all transaction pending and also compatible with **addToBackStack()**

#### Operation ordering is significant

- Once you begin transaction and commit multiple transaction, transaction will be executed in which order you implemented them.
- This is important in some case  where multiple screens transaction or animation need to be done in required order.

```kotlin
supportFragmentManager.commit {
    setCustomAnimations(enter1, exit1, popEnter1, popExit1)
    add<ExampleFragment>(R.id.container) // gets the first animations
    setCustomAnimations(enter2, exit2, popEnter2, popExit2)
    add<ExampleFragment>(R.id.container) // gets the second animations
}
```

#### Showing and hiding fragment's views

- FragmentTransaction methods **show() and hide()** to show and hide the view of fragments that have been added to a container.
- This won't affect lifecycle of the fragment

#### Attaching and detaching fragments

- FragmentTransaction method **detach()** detaches the fragment from the UI, so it's view get destroyed. Fragment go to **stoped** state and still state managed by Fragment manager.
- When we use **attach()** method to attach this fragment, state get resumed and ony UI hierarchy get recreated.
- If in single transaction we detach and attached same fragment, each operation get canceled out and avoid the life cycle change or UI hierarchy destroy and re-create.

Also Read:
- [Fragment Lifecycle](./FragmentLifeCycle/Fragment_Lifecycle.md)
- [Fragment Communication](./FragmentLifeCycle/Fragment_Communication.md)