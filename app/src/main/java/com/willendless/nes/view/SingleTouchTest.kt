package com.willendless.nes.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.TextView
import com.willendless.nes.R
import kotlinx.android.synthetic.main.activity_single_touch_test.*
import java.lang.StringBuilder

open class SingleTouchTest : AppCompatActivity(), View.OnTouchListener {
    private val builder = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_touch_test)
        singleTouchText.text = "Touch and drag"
        singleTouchText.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        builder.setLength(0)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> builder.append("down")
            MotionEvent.ACTION_MOVE -> builder.append("move")
            MotionEvent.ACTION_CANCEL -> builder.append("cancel")
            MotionEvent.ACTION_UP -> builder.append("up")
            else -> builder.append("not handled")
        }
        builder.append(event?.x)
        builder.append(",")
        builder.append(event?.y)
        val text = builder.toString()
        Log.d("Touch test:", text)
        singleTouchText.text = text
        window.decorView.windowInsetsController!!.hide(
            WindowInsets.Type.statusBars()
                or WindowInsets.Type.navigationBars())
        return true
    }
}