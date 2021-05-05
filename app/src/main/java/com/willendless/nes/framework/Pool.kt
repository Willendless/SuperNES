package com.willendless.nes.framework

class Pool<T>(private val maxSize: Int, private val createObject: () -> T) {
    private var freeObjects = ArrayList<T>(maxSize)

    fun newObject(): T = if (freeObjects.isEmpty()) createObject()
                         else freeObjects.removeAt(freeObjects.size - 1)

    fun free(obj: T) {
        if (freeObjects.size < maxSize)
            freeObjects.add(obj)
    }
}