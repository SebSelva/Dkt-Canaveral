package com.decathlon.core.game.model

open class Point(
    val value: String,
    val isDoubled: Boolean,
    val isTripled: Boolean,
)

class Dot: Point("-", false, false)
