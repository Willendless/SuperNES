package com.willendless.nes.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import com.willendless.nes.R
import kotlinx.android.synthetic.main.activity_life_cycle_test.*
import java.lang.StringBuilder

class LifeCycleTest : AppCompatActivity() {
    var builder = StringBuilder()

    fun log(text: String) {
        Log.d("LifeCycle Test", text)
        builder.append(text)
        builder.append("\n")
        lifecycleText.text = builder.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_life_cycle_test)
        lifecycleText.text = builder.toString()
        log("created")
    }

    override fun onResume() {
        super.onResume()
        log("resumed")
    }

    override fun onPause() {
        super.onPause()
        log("paused")
        if (isFinishing()) {
            log("finishing")
        }
    }
}