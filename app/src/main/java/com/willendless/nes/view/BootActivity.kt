package com.willendless.nes.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.willendless.nes.R
import kotlinx.android.synthetic.main.activity_boot.*
import kotlin.system.exitProcess

class BootActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boot)
        supportActionBar?.hide()
        bootStart.setOnClickListener(this)
        bootExit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v) {
                bootStart -> HomeActivity.actionStart(this)
                bootExit -> finish()
            }
        }
    }
}