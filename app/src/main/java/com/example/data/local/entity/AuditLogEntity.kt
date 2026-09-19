package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val adminEmail: String,
    val action: String,
    val targetUser: String = "",
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
