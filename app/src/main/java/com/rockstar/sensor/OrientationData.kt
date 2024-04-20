package com.rockstar.sensor

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "orientation_data")
data class OrientationData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "Pitch")
    val pitch: Double,
    @ColumnInfo(name = "Roll")
    val roll: Double,
    @ColumnInfo(name = "Yaw")
    val yaw: Double,

    val timestamp: Long = System.currentTimeMillis()
)

