package com.decathlon.canaveral.common.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class X01PlayerStats (
    val player: Player,
    var remainingPoints: Int,
    var checkout: Int,
    var ppd: Float
    ) : Parcelable