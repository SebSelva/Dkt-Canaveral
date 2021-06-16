package com.decathlon.core.player.model

import kotlinx.android.parcel.Parcelize

@Parcelize
class Button (

        override val id: Int,
        var text: String

) : BaseItem()