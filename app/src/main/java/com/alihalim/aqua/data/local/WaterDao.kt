package com.alihalim.aqua.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {

    @Insert
    suspend fun insert(entry: WaterEntry): Long

    @Delete
    suspend fun delete(entry: WaterEntry)

    @Query("DELETE FROM water_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM water_entries WHERE timestampMillis >= :start AND timestampMillis < :end ORDER BY timestampMillis DESC")
    fun getEntriesBetween(start: Long, end: Long): Flow<List<WaterEntry>>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM water_entries WHERE timestampMillis >= :start AND timestampMillis < :end")
    fun getTotalBetween(start: Long, end: Long): Flow<Int>

    @Query("SELECT * FROM water_entries WHERE timestampMillis >= :start AND timestampMillis < :end ORDER BY timestampMillis ASC")
    suspend fun getEntriesBetweenOnce(start: Long, end: Long): List<WaterEntry>
}
