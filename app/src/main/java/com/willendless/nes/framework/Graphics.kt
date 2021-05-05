package com.willendless.nes.framework

interface Graphics {
    enum class PixmapFormat {
        ARG8888, ARG4444, ARG565
    }

    fun newPixmap(fileName: String, format: PixmapFormat): Pixmap
    fun clear(color: Int)
    fun drawPixel(x: Int, y: Int, color: Int)
    fun drawLine(x: Int, y: Int, width: Int, height: Int, color: Int)
    fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int)
    fun drawPixmap(pixmap: Pixmap, x: Int, y: Int, srcX: Int, srcY: Int, srcWidth: Int, srcHeight: Int)
    fun drawPixmap(pixmap: Pixmap, x: Int, y: Int)
    fun getWidth(): Int
    fun getHeight(): Int
}