package com.willendless.nes.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.willendless.nes.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val tests = arrayListOf("LifeCycleTest", "SingleTouchTest", "MultiTouchTest",
                        "KeyTest", "AccelerometerTest", "AssetsTest",
                        "SoundPoolTest", "MediaPlayerTest", "FullScreenTest",
                        "RenderViewTest", "ShapeTest", "BitmapTest", "FontTest", "SurfaceViewTest",
                        "NotificationTest", "SnakeGameActivityTest")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layoutManager = LinearLayoutManager(this)
        recycleView.layoutManager = layoutManager
        val adapter = TestAdapter(tests, this)
        recycleView.adapter = adapter
    }
}