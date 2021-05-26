package com.willendless.nes.framework

abstract class Screen(val game: Game) {
    /**
     * ENSURES: update screen state and return if state changed (need to present new screen)
     */
    abstract fun update(deltaTime: Float): Boolean
    /**
     * ENSURES: render screen based on screen state
     */
    abstract fun present(deltaTime: Float)
    abstract fun pause()
    abstract fun resume()
    abstract fun dispose()
}
