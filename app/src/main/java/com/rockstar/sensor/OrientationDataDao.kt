package com.rockstar.sensor

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow




@Dao
interface OrientationDataDao {
    @Insert
    suspend fun insert(data: OrientationData): Long


    @Query("SELECT * FROM orientation_data ORDER BY timestamp DESC")
    fun getAllOrientationData(): Flow<List<OrientationData>>
}
