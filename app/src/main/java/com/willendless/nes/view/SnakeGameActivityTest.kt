package com.willendless.nes.view

import com.willendless.nes.framework.Input
import com.willendless.nes.framework.Screen
import com.willendless.nes.framework.impl.AndroidGame
import com.willendless.nes.game.LoadingScreen
import com.willendless.nes.game.mock.LoadingSnakeScreen

@ExperimentalStdlibApi
class SnakeGameActivityTest: AndroidGame() {
    @ExperimentalUnsignedTypes
    override fun getStartScreen(): Screen = LoadingSnakeScreen(this)
    override fun getJoypadEvents(): List<Input.JoypadEvent> {
        TODO("Not yet implemented")
    }
}