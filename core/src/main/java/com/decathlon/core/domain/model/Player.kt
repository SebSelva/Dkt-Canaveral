package com.decathlon.core.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Player (

    var id: Int,
    var nickname: String,
    var firstname: String,
    var lastname: String,
    var image: String

) : Parcelable