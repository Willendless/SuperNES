package com.willendless.nes.framework.impl

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.willendless.nes.framework.FIleIO
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class AndroidFileIO(private val context: Context): FIleIO {
    private val assets = context.assets

    override fun readAsset(fileName: String): InputStream {
        return assets.open(fileName)
    }

    override fun readFile(fileName: String): InputStream {
        return context.openFileInput(fileName)
    }

    override fun writeFile(fileName: String): OutputStream {
        return context.openFileOutput(fileName, Context.MODE_PRIVATE)
    }

    fun getPreferences(fileName: String): SharedPreferences {
        return context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }
}