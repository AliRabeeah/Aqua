package com.alihalim.aqua.data

import com.alihalim.aqua.data.local.WaterDao
import com.alihalim.aqua.data.local.WaterEntry
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class WaterRepository(private val dao: WaterDao) {

    suspend fun logWater(amountMl: Int): Long {
        return dao.insert(WaterEntry(amountMl = amountMl, timestampMillis = System.currentTimeMillis()))
    }

    suspend fun deleteEntry(entry: WaterEntry) = dao.delete(entry)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    fun todaysEntries(): Flow<List<WaterEntry>> {
        val (start, end) = dayRange(0)
        return dao.getEntriesBetween(start, end)
    }

    fun todaysTotal(): Flow<Int> {
        val (start, end) = dayRange(0)
        return dao.getTotalBetween(start, end)
    }

    suspend fun totalForDayOffset(daysAgo: Int): Int {
        val (start, end) = dayRange(daysAgo)
        return dao.getEntriesBetweenOnce(start, end).sumOf { it.amountMl }
    }

    private fun dayRange(daysAgo: Int): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val end = cal.timeInMillis
        return start to end
    }
}
