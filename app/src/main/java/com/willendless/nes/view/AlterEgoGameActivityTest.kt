package com.willendless.nes.view

import android.os.Build
import androidx.annotation.RequiresApi
import com.willendless.nes.framework.Input
import com.willendless.nes.framework.Screen
import com.willendless.nes.framework.impl.AndroidGame
import com.willendless.nes.game.mock.LoadingAlterEgoScreen

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class AlterEgoGameActivityTest: AndroidGame() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getStartScreen(): Screen = LoadingAlterEgoScreen(this)
    override fun getJoypadEvents(): List<Input.JoypadEvent> {
        TODO("Not yet implemented")
    }
}