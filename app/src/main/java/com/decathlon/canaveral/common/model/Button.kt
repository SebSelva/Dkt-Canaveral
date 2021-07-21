package com.decathlon.canaveral.common.model

import kotlinx.parcelize.Parcelize

@Parcelize
class Button (

        override val id: Int,
        var text: String

) : BaseItem()
