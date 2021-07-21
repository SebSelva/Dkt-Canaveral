package com.decathlon.core.player.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Button (
        val id: Int,
        var text: String

) : Parcelable
