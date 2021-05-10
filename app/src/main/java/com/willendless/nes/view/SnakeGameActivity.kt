package com.willendless.nes.view

import com.willendless.nes.framework.Screen
import com.willendless.nes.framework.impl.AndroidGame
import com.willendless.nes.game.LoadingScreen

class SnakeGameActivity: AndroidGame() {
    override fun getStartScreen(): Screen = LoadingScreen(this)
}