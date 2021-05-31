package com.decathlon.core.domain.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class Player (
        override var id: Int,
        var nickname: String,
//    var firstname: String,
//    var lastname: String,
        var image: String,
        var visibleIcon: Boolean

) : BaseItem()