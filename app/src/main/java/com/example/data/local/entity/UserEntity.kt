package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val role: String, // "SUPER_ADMIN", "ADMIN", "VIP", "USER"
    val status: String, // "ACTIVE", "SUSPENDED", "BANNED"
    val subscriptionTier: String, // "FREE", "VIP_MONTHLY", "VIP_ANNUAL"
    val episodesWatched: Int = 0,
    val watchTimeMinutes: Int = 0,
    val avatarUrl: String = "",
    val lastActiveTime: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
