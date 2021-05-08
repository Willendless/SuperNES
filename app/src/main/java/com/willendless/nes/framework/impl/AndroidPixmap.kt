package com.willendless.nes.framework.impl

import android.graphics.Bitmap
import com.willendless.nes.framework.Graphics
import com.willendless.nes.framework.Pixmap

class AndroidPixmap(val bitmap: Bitmap, val _format: Graphics.PixmapFormat): Pixmap {
    override fun getWidth(): Int = bitmap.width

    override fun getHeight(): Int = bitmap.height

    override fun getFormat(): Graphics.PixmapFormat = _format

    override fun dispose() {
        bitmap.recycle()
    }
}