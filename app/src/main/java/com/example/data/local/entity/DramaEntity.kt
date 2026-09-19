package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dramas")
data class DramaEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val coverDrawableResName: String,
    val totalEpisodes: Int,
    val tags: String, // comma-separated e.g. "CONTRACT LOVE,FAMILY DRAMA,SWEET ROMANCE"
    val category: String, // "TRENDING", "NEW", "HOTLIST", "FANTASY", etc.
    val isFeaturedCover: Boolean = false,
    val rating: Float = 4.8f,
    val viewCount: Long = 1000000L,
    val isDownloaded: Boolean = false
)
