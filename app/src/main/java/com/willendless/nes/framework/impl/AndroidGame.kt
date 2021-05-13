package com.willendless.nes.framework.impl

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.willendless.nes.framework.*

abstract class AndroidGame: AppCompatActivity(), Game {
    lateinit var renderView: AndroidFastRenderView
    private lateinit var graphics: Graphics
    private lateinit var audio: Audio
    private lateinit var input: Input
    private lateinit var fileIO: FIleIO
    private lateinit var screen: Screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Game", "create")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.decorView.windowInsetsController!!.hide(
                WindowInsets.Type.statusBars()
                        or WindowInsets.Type.navigationBars())

        val frameBufferWidth = 32
        val frameBufferHeight = 32
        val frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Bitmap.Config.RGB_565)

        // scaleX
        val scaleX = 1F
        // scaleY
        val scaleY = 1F

        renderView = AndroidFastRenderView(this, frameBuffer)
        graphics = AndroidGraphics(assets, frameBuffer)
        fileIO = AndroidFileIO(this)
        audio = AndroidAudio(this)
        input = AndroidInput(this, renderView, scaleX, scaleY)
        screen = getStartScreen()
        setContentView(renderView)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        screen.resume()
        renderView.resume()
    }

    override fun onPause() {
        super.onPause()
        renderView.pause()
        screen.pause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (isFinishing)
            screen.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        screen.destroy()
    }

    override fun getFileIO(): FIleIO = fileIO

    override fun getGraphics(): Graphics = graphics

    override fun getAudio(): Audio = audio

    override fun getInput(): Input = input

    override fun setScreen(screen: Screen) {
        this.screen.pause()
        this.screen.dispose()
        screen.resume()
        screen.update(0F)
        this.screen = screen
    }

    override fun getCurrentScreen(): Screen = screen
}