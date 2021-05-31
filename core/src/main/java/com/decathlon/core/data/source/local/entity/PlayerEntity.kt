package com.decathlon.core.data.source.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

@Parcelize
@Entity(tableName = "player")
data class PlayerEntity(

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "uid")
    var uid: Int,

    @ColumnInfo(name = "nickname")
    var nickname: String,

    @ColumnInfo(name = "firstname")
    var firstname: String,

    @ColumnInfo(name = "lastname")
    var lastname: String,

) : Parcelable