package com.xunfos.graphqldemo.server

val delayRange = (100..300)

fun trace(msg: Any) {
    println("[${Thread.currentThread().name}] $msg")
}

fun <T> simulateNetwork(block: () -> T): T {
    val timing = delayRange.random()
    trace("simulating network call with ${timing}ms latency")
    return block().also {
        trace("call finished")
    }
}
