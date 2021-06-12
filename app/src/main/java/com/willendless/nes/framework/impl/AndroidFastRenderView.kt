package com.willendless.nes.framework.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.jar.Attributes

class AndroidFastRenderView(context: Context, attrs: AttributeSet):
    SurfaceView(context, attrs), Runnable {
    @Volatile
    var running = false
    private val viewHolder: SurfaceHolder = holder
    lateinit var renderThread: Thread
    lateinit var frameBuffer: Bitmap
    lateinit var game: AndroidGame

    override fun run() {
        val dstRect = Rect()
        var startTime = System.nanoTime()
        while (running) {
            if (!viewHolder.surface.isValid) continue

            val deltaTime = (System.nanoTime() - startTime) / 1000000000.0F
            startTime = System.nanoTime()

            val draw = game.getCurrentScreen().update(deltaTime)

            if (draw) {
                game.getCurrentScreen().present(deltaTime)
                val canvas = viewHolder.lockCanvas()
                canvas.getClipBounds(dstRect)
//                val right = dstRect.right.toFloat()
//                val bottom = dstRect.bottom.toFloat()
                // game panel
//                dstRect.set(0, 0, dstRect.right, dstRect.bottom / 4 * 3)
                canvas.drawBitmap(frameBuffer, null, dstRect, null)
//                 todo: up, down, left, right
//                 todo: A, B
                // start
//                val paint = Paint()
//                paint.color = Color.BLUE
//                canvas.drawRect(right / 4 * 3, bottom / 4 * 3,
//                    right, bottom / 8 * 7, paint)
                // select
//                paint.color = Color.RED
//                canvas.drawRect(right / 4 * 3, bottom / 8 * 7,
//                    right, bottom, paint)
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    fun resume() {
        running = true
        renderThread = Thread(this)
        renderThread.start()
    }

    fun pause() {
        running = false
        while (true) {
           try {
               renderThread.join()
               return
           } catch (e: InterruptedException) {
               // retry
           }
        }
    }
}