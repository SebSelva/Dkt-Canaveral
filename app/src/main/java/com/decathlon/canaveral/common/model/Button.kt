package com.decathlon.canaveral.common.model

import kotlinx.parcelize.Parcelize

@Parcelize
class Button (

        override val id: Long,
        var text: String

) : BaseItem()
