package com.willendless.nes.framework

interface Audio {
    fun newMusic(filename: String): Music
    fun newSound(filename: String): Sound
}



