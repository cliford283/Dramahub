package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history WHERE userId = :userId ORDER BY lastWatchedTime DESC")
    fun getWatchHistory(userId: Long): Flow<List<WatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(history: WatchHistoryEntity)
}
