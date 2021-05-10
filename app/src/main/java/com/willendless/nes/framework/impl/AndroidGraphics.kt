package com.willendless.nes.framework.impl

import android.content.res.AssetManager
import android.graphics.*
import com.willendless.nes.framework.Graphics
import com.willendless.nes.framework.Pixmap
import java.io.IOException
import java.io.InputStream
import java.lang.RuntimeException

class AndroidGraphics(val assets: AssetManager, val frameBuffer: Bitmap): Graphics {
    val canvas = Canvas(frameBuffer)
    val paint = Paint()
    val srcRect = Rect()
    val dstRect = Rect()

    override fun newPixmap(fileName: String, format: Graphics.PixmapFormat): Pixmap {
        val config: Bitmap.Config = when (format) {
            Graphics.PixmapFormat.ARG565 -> Bitmap.Config.RGB_565
            Graphics.PixmapFormat.ARG4444 -> Bitmap.Config.ARGB_4444
            Graphics.PixmapFormat.ARG8888 -> Bitmap.Config.ARGB_8888
        }
        val options = BitmapFactory.Options()
        options.inPreferredConfig = config

        var inputStream: InputStream? = null
        var bitmap: Bitmap? = null
        try {
            inputStream = assets.open(fileName)
            bitmap = BitmapFactory.decodeStream(inputStream) ?: throw RuntimeException("Couldn't load bitmap" +
                    "from asset '" + fileName + "'")
        } catch (e: IOException) {
            throw RuntimeException("Couldn't load bitmapfrom asset '$fileName'")
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
            }
        }
        val format = when(bitmap!!.config) {
            Bitmap.Config.RGB_565 -> Graphics.PixmapFormat.ARG565
            Bitmap.Config.ARGB_4444 -> Graphics.PixmapFormat.ARG4444
            Bitmap.Config.ARGB_8888 -> Graphics.PixmapFormat.ARG8888
            else -> throw RuntimeException("unknown bitmap format")
        }
        return AndroidPixmap(bitmap, format)
    }

    override fun clear(color: Int) {
        canvas.drawRGB((color and 0xff0000) shr 16, (color and 0x00ff00) shr 8, (color and 0x0000ff))
    }

    override fun drawPixel(x: Int, y: Int, color: Int) {
        paint.color = color
        canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
    }

    override fun drawLine(x: Int, y: Int, x2: Int, y2: Int, color: Int) {
        paint.color = color
        canvas.drawLine(x.toFloat(), y.toFloat(), x2.toFloat(), y2.toFloat(), paint)
    }

    override fun drawRect(x: Int, y: Int, width: Int, height: Int, color: Int) {
        paint.color = color
        paint.style = Paint.Style.FILL
        canvas.drawRect(x.toFloat(), y.toFloat(), (x + width - 1).toFloat(), (y + height - 1).toFloat(), paint)
    }

    override fun drawPixmap(pixmap: Pixmap, x: Int, y: Int, srcX: Int, srcY: Int, srcWidth: Int, srcHeight: Int) {
        srcRect.left = srcX
        srcRect.top = srcY
        srcRect.right = srcX + srcWidth - 1
        srcRect.bottom = srcY + srcHeight - 1

        dstRect.left = x
        dstRect.top = y
        dstRect.right = x + srcWidth - 1
        dstRect.bottom = y + srcHeight - 1
        canvas.drawBitmap((pixmap as AndroidPixmap).bitmap, srcRect, dstRect, null)
    }

    override fun drawPixmap(pixmap: Pixmap, x: Int, y: Int) {
        canvas.drawBitmap((pixmap as AndroidPixmap).bitmap, x.toFloat(), y.toFloat(), null)
    }

    override fun getWidth(): Int = frameBuffer.width

    override fun getHeight(): Int = frameBuffer.height

}