package com.decathlon.canaveral.stats.model

data class StatItem(
    val title: Int,
    val value: Any
) {
    override fun equals(other: Any?): Boolean {
        if (other !is StatItem) return false
        if (other.title != this.title) return false
        if (other.value != this.value) return false
        return true
    }
}
