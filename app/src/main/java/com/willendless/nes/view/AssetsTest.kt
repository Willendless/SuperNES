package com.willendless.nes.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.willendless.nes.R
import kotlinx.android.synthetic.main.activity_assets_test.*
import java.io.InputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class AssetsTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assets_test)
        var inputStream: InputStream? = null
        try {
            inputStream = assets.open("texts/myawesometext.txt")
            val text = loadTextFile(inputStream)
            assetsText.text = text
        } catch (e: IOException) {
            Log.d("load file", "failed")
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    Log.d("close file", "failed")
                }
            }
        }
    }

    @Throws(IOException::class)
    fun loadTextFile(inputStream: InputStream): String {
        val byteStream = ByteArrayOutputStream()
        val bytes = ByteArray(1024)
        do {
            val len = inputStream.read(bytes)
            if (len > 0) {
                byteStream.write(bytes, 0, len)
            } else {
                break
            }
        } while (true)
        return byteStream.toString()
    }
}