package com.willendless.nes.view

import android.content.Context
import android.graphics.Canvas
import android.view.View

class RenderView(context: Context): View(context) {
    override fun onDraw(canvas: Canvas?) {
        invalidate()
    }
}