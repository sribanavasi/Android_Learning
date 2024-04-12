package com.appadda.fragment_lifecycle

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {
    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "onCreate")
        button = findViewById(R.id.button)
        addFragment()
//        button.setOnClickListener({
//            addFragment()
//        })
    }


    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("MainActivity", "onRestart")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("key", "value")
        Log.d("MainActivity", "onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("MainActivity", "onRestoreInstanceState")
        Log.d("MainActivity", savedInstanceState.getString("key").toString())
    }

    fun addFragment() {
        supportFragmentManager.beginTransaction().add(R.id.fragment_container_view, TestFragment())
//            .addToBackStack("addFragment")//if you comment this, on back button, app will close, so fragment will be removed, if you uncomment this, fragment will be added to backstack, so on back button, fragment will be removed from backstack , otherwise, app first remove fragment from back stack, then in next back press, app will close.
            .commit()
    }
}