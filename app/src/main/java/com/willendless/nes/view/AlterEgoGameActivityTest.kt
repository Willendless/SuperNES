package com.willendless.nes.view

import com.willendless.nes.framework.Screen
import com.willendless.nes.framework.impl.AndroidGame
import com.willendless.nes.game.mock.LoadingAlterEgoScreen

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class AlterEgoGameActivityTest: AndroidGame() {
    override fun getStartScreen(): Screen = LoadingAlterEgoScreen(this)
}