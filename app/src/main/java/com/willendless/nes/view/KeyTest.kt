package com.willendless.nes.view

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.StringBuilder

class KeyTest: AppCompatActivity(), View.OnKeyListener  {
    private val builder = StringBuilder()
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textView = TextView(this)
        textView.text = "Press keys (if you have some)"
        textView.setOnKeyListener(this)
        textView.isFocusableInTouchMode = true
        textView.requestFocus()
        setContentView(textView)
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            builder.setLength(0)
            when (event.action) {
                KeyEvent.ACTION_UP -> return false
                KeyEvent.ACTION_DOWN -> builder.append("down, ")
            }
            builder.append(event.keyCode)
            builder.append(", ")
            builder.append(event.unicodeChar)
            val text = builder.toString()
            Log.d("KeyTest", text)
            textView.text = text
            return true
        }
        return false
    }
}