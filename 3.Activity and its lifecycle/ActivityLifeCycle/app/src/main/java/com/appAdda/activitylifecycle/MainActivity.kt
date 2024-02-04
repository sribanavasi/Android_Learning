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
    override fun onCreate(savedInstanceState: Bundle?) {
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

    override fun onStart() {
        super.onStart()
        println("onStart Called Activity 1")
        //this.finish()//if you call finish here, state goes to  on, on stop then on Destroy

    }

    override fun onRestart() {
        super.onRestart()
        println("onRestart Called Activity 1")
    }

    override fun onResume() {
        super.onResume()
        println("onResume Called Activity 1")
//        this.finish()//if you call finish here, state goes to  on, on Pause, on stop then on Destroy

    }

    override fun onPause() {
        super.onPause()
        println("onPause Called Activity 1")
    }

    override fun onStop() {
        super.onStop()
        println("onStop Called Activity 1")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy Called Activity 1")
    }
}