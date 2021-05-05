package com.willendless.nes.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.ViewCompat
import com.willendless.nes.R

class FullScreenTest : SingleTouchTest() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.decorView.windowInsetsController!!.hide(WindowInsets.Type.statusBars()
            or WindowInsets.Type.navigationBars())
        super.onCreate(savedInstanceState)
    }
}