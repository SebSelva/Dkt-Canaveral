package com.decathlon.core.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

abstract class BaseItem : Parcelable {
    abstract val id: Int

}