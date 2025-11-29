package com.daromi.echo.uds

import java.util.concurrent.atomic.AtomicBoolean

class Server private constructor() {
    private val running: AtomicBoolean = AtomicBoolean(false)

    companion object {
        @JvmStatic
        fun create(): Server = Server()
    }

    fun start() {
        running.set(true)

        while (running.get()) {
            println("busy waiting")
        }
    }

    fun stop() {
        running.set(false)
    }
}
