package com.willendless.nes.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets

class ShapeTest : AppCompatActivity() {
    class RenderView(context: Context): View(context) {
        val paint = Paint()

        override fun onDraw(canvas: Canvas?) {
            canvas?.drawRGB(255, 255, 255)
            paint.setColor(Color.RED)
            canvas?.drawLine(0F, 0F, (canvas.width - 1).toFloat(),
                (canvas.height - 1).toFloat(), paint)

            paint.style = Paint.Style.STROKE
            paint.color = 0xff00ff00.toInt()
            canvas?.drawCircle(canvas.width / 2f, canvas.height / 2f, 40F, paint)

            paint.style = Paint.Style.FILL
            paint.color = 0x770000ff
            canvas?.drawRect(100F, 100F, 200F, 200F, paint)
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