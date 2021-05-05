package com.willendless.nes.view

import android.content.Context
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowInsets
import com.willendless.nes.R
import kotlinx.android.synthetic.main.activity_render_view_test.view.*
import java.util.Random

class RenderViewTest : AppCompatActivity() {
    class RenderView(context: Context): View(context) {
        val random = Random()

        override fun onDraw(canvas: Canvas?) {
            Log.d("try", "draw")
            canvas!!.drawRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256))
            invalidate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.decorView.windowInsetsController!!.hide(
            WindowInsets.Type.statusBars()
                or WindowInsets.Type.navigationBars())
        setContentView(RenderView(this))
    }
}