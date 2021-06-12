package com.willendless.nes.framework

interface Game {
    fun getInput(): Input
    fun getFileIO(): FIleIO
    fun getGraphics(): Graphics
    fun getAudio(): Audio
    fun setScreen(screen: Screen)
    fun getCurrentScreen(): Screen
    fun getStartScreen(): Screen
    fun getJoypadEvents(): List<Input.JoypadEvent>
}