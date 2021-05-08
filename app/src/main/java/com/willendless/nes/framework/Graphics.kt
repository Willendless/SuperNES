package com.willendless.nes.framework

interface Graphics {
    enum class PixmapFormat {
        ARG8888, ARG4444, ARG565
    }
    // load an image and return corresponding Pixmap
    fun newPixmap(fileName: String, format: PixmapFormat): Pixmap
    // clears the current framebuffer with a color
    fun clear(color: Int)
    // set the pixel at (x,y) in framebuffer to a color
    fun drawPixel(x: Int, y: Int, color: Int)
    fun drawLine(x: Int, y: Int, x2: Int, y2: Int, color: Int)
    fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int)
    // draw a pixmap to (x,y) of the framebuffer
    fun drawPixmap(pixmap: Pixmap, x: Int, y: Int, srcX: Int, srcY: Int, srcWidth: Int, srcHeight: Int)
    fun drawPixmap(pixmap: Pixmap, x: Int, y: Int)
    // get framebuffer width and height
    fun getWidth(): Int
    fun getHeight(): Int
}