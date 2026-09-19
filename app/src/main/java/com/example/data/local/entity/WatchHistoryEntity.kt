package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val dramaId: String,
    val episodeNumber: Int,
    val progressSeconds: Int,
    val totalDurationSeconds: Int = 0,
    val lastWatchedTime: Long = System.currentTimeMillis()
)
