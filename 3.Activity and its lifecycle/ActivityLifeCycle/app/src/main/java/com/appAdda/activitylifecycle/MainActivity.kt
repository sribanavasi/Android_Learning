package com.appAdda.activitylifecycle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
/**
 * When we start activity B from activity A, the State changes of activity A and B in order will be as below
 * Activity A : onPause()
 * activity B : onCreate(),onStart(),onResume()
 * Activity A : onStop()
 * From above case, in activity B, the finish() function called
 * Activity B : onPause()
 * Activity A : onRestart(),onStart(),onResume()
 * Activity B : onStop(),onDestroy()**/
class MainActivity : AppCompatActivity() {
    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) { //In this state, perform basic activity setup logic, that happens, only once for an activity. example: set layout/view of activity (setContentView()). restore state from the bundle, if it ever existed before. initialize member views, if it require later.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("onCreate Called Activity 1")
        button = this.findViewById(R.id.button)
        button.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }
        //this.finish()//if you call finish here, state goes to  on Destroy
    }

    override fun onStart() {//
        super.onStart()
        println("onStart Called Activity 1")
        //this.finish()//if you call finish here, state goes to  on, on stop then on Destroy

    }

    override fun onRestart() {
        super.onRestart()
        println("onRestart Called Activity 1")
    }

    override fun onResume() { //Common practice is to start resource-dependent operations like opening cameras or sensors-related operations are done in this state. (You can also start these in on create, then the system denies camera/resource access to other activities that or active)
        super.onResume()
        println("onResume Called Activity 1")
//        this.finish()//if you call finish here, state goes to  on, on Pause, on stop then on Destroy

    }

    override fun onPause() {//Common practice is to release system resources in this state. Don't use it to save application data/ network calls, because, this function is quick and may not complete those operations.
        super.onPause()
        println("onPause Called Activity 1")
    }

    override fun onStop() {//perform cpu intensive, shutdown operation like, ex: saving data to database if not saved before.

        super.onStop()
        println("onStop Called Activity 1")
    }

    override fun onDestroy() {//Release and clean up all the resources occupied by activity by activitys.
        super.onDestroy()
        println("onDestroy Called Activity 1")
    }
}
