package com.willendless.nes.framework

import com.willendless.nes.framework.Graphics.PixmapFormat

interface Pixmap {
    fun getWidth(): Int
    fun getHeight(): Int
    fun getFormat(): PixmapFormat
    fun dispose()
}