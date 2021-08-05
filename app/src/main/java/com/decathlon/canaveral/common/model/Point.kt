package com.decathlon.canaveral.common.model

open class Point(
    val value: String,
    val isDoubled: Boolean,
    val isTripled: Boolean,
)

class Dot: Point("-", false, false)

class NullPoint: Point("null", false, false)
