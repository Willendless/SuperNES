package com.willendless.nes.framework.impl

import android.app.Activity
import android.media.SoundPool
import android.os.Build
import androidx.annotation.RequiresApi
import com.willendless.nes.framework.Audio
import com.willendless.nes.framework.Music
import com.willendless.nes.framework.Sound

class AndroidAudio(private val activity: Activity): Audio {
    private val assets = activity.assets

    override fun newMusic(filename: String): Music {
        TODO("Not yet implemented")
    }

    override fun newSound(filename: String): Sound {
        TODO("Not yet implemented")
    }
}