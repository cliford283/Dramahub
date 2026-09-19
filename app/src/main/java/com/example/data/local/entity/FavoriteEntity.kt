package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites", primaryKeys = ["dramaId", "userId"])
data class FavoriteEntity(
    val dramaId: String,
    val userId: Long,
    val addedAt: Long = System.currentTimeMillis()
)
