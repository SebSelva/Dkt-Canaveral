package com.decathlon.core.gamestats.data.source.room.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.decathlon.core.gamestats.data.source.network.model.StdActivity
import com.decathlon.core.gamestats.data.source.network.model.StdDartsData
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "activities")
data class GameActivityEntity(
    @PrimaryKey
    var id: Long = 0L,
    val name: String,
    var user: String?,
    val sport: String,
    val startDate: String,
    val duration: Long,
    val connector: String,
    @Embedded val dataSummaries: StdDartsData
) : Parcelable


fun StdActivity.toEntity(): GameActivityEntity {
    return GameActivityEntity(
        name = name,
        user = user,
        sport = sport,
        startDate = startDate,
        duration = duration,
        connector = connector,
        dataSummaries = dataSummaries
    )
}

fun GameActivityEntity.toWs(userWs: String): StdActivity {
    return StdActivity(
        name = name,
        user = userWs,
        sport = sport,
        startDate = startDate,
        duration = duration,
        connector = connector,
        dataSummaries = dataSummaries
    )
}
fun List<GameActivityEntity>.toWs(userWs:String): List<StdActivity> {
    return map { it.toWs(userWs) }
}
