package com.willendless.nes.view

import com.willendless.nes.framework.Screen
import com.willendless.nes.framework.impl.AndroidGame
import com.willendless.nes.game.LoadingScreen
import com.willendless.nes.game.mock.LoadingSnakeScreen

class SnakeGameActivity: AndroidGame() {
    override fun getStartScreen(): Screen = LoadingSnakeScreen(this)
}