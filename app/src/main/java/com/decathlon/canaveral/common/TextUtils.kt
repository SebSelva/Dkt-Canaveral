package com.decathlon.canaveral.common

class TextUtils {

    companion object {
        fun isNumber(s :String) :Boolean {
            return try {
                s.toInt()
                true
            } catch (ex: NumberFormatException) {
                false
            }
        }

        fun getNumber(s: String) :Int? {
            return try {
                s.toInt()
            } catch (ex: NumberFormatException) {
                null
            }
        }
    }

}
