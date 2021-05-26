package com.willendless.nes.view

import android.os.Build
import androidx.annotation.RequiresApi
import com.willendless.nes.framework.Screen
import com.willendless.nes.framework.impl.AndroidGame
import com.willendless.nes.game.mock.LoadingAlterEgoScreen

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class AlterEgoGameActivityTest: AndroidGame() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getStartScreen(): Screen = LoadingAlterEgoScreen(this)
}