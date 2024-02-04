package com.appAdda.activitylifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.TextView

class MainActivity2 : AppCompatActivity() {
    lateinit var button: Button
    lateinit var text: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        println("onCreate Called Activity 2")
        button = this.findViewById(R.id.button)
        text = this.findViewById(R.id.text)
        button.setOnClickListener { finish() }
//        if (savedInstanceState != null) {  //ned to check savedInstanceState if it is not null. since if app don;t have old state, it will be null
//            with(savedInstanceState) {
//                // Restore value of members from saved state.
//                text.text = getString("TEXTValue")
//            }
//        }
    }

    override fun onStart() {
        super.onStart()
        println("onStart Called Activity 2")
    }

    override fun onRestart() {
        super.onRestart()
        println("onRestart Called Activity 2")
    }

    //called after onStart, and only called if Bundle is not null, mean, activity previously closed by system
    override fun onRestoreInstanceState(
        savedInstanceState: Bundle
    ) {
        super.onRestoreInstanceState(savedInstanceState)

        // Restore state members from saved instance.
        savedInstanceState?.run {
            text.text = getString("TEXTValue")
        }
    }

    override fun onResume() {
        super.onResume()
        println("onResume Called Activity 2")
    }

    override fun onPause() {
        super.onPause()
        println("onPause Called Activity 2")
    }

    override fun onStop() {
        super.onStop()
        println("onStop Called Activity 2")
    }

    //This is called after on stop, if system destroying activity on configuration change or screen rotate
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState?.run {
            putString("TEXTValue", "Abcs")//roteate screen to see data being saved
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy Called Activity 2")
    }


}