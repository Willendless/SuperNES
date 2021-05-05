package com.willendless.nes.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import android.view.Window
import android.view.WindowInsets

class SurfaceViewTest : AppCompatActivity() {
    private lateinit var renderView: FastRenderView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.decorView.windowInsetsController!!.hide(
            WindowInsets.Type.statusBars()
                or WindowInsets.Type.navigationBars())
        renderView = FastRenderView(this)
        setContentView(renderView)
    }

    override fun onResume() {
        super.onResume()
        renderView.onResume()
    }

    override fun onPause() {
        super.onPause()
        renderView.onPause()
    }

    class FastRenderView(context: Context): SurfaceView(context), Runnable {
        @Volatile
        var running: Boolean = false
        private lateinit var runningThread: Thread

        override fun run() {
            while (running) {
                if (!holder.surface.isValid) continue
                // since surface will be destroyed after onPause return
                // therefore it is safe to call lockCanvas() while surface is valid
                val canvas = holder.lockCanvas()
                canvas.drawRGB(255, 0, 0)
                holder.unlockCanvasAndPost(canvas)
            }
        }

        fun onResume() {
            running = true
            runningThread = Thread(this)
            runningThread.start()
        }

        fun onPause() {
            running = false
            while (true) {
                try {
                    runningThread.join()
                    return
                } catch (e: InterruptedException) {
                    // retry
                }
            }
        }
    }
}