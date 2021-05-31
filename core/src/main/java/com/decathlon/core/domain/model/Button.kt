package com.decathlon.core.domain.model

import android.graphics.drawable.Drawable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Button (

        override val id: Int,
        var text: String

) : BaseItem()