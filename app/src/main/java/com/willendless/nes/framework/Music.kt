package com.willendless.nes.framework

interface Music {
    fun play()
    fun stop()
    fun pause()
    fun setLooping(looping: Boolean)
    fun setVolume(volume: Float)
    fun isPlaying(): Boolean
    fun isStopped(): Boolean
    fun isLooping(): Boolean
    fun dispose()
}