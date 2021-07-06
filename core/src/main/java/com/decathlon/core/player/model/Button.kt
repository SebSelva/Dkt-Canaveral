package com.decathlon.core.player.model

import com.decathlon.core.common.model.BaseItem
import kotlinx.parcelize.Parcelize

@Parcelize
class Button (

        override val id: Int,
        var text: String

) : BaseItem()
