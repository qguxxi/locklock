package com.locklock.applock.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locked_apps")
data class LockedAppEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val isLocked: Boolean = true,
    val lockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vault_media")
data class VaultMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val originalName: String,
    val encryptedFileName: String,
    val mediaType: String,
    val fileSizeBytes: Long,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "intruder_logs")
data class IntruderLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetPackage: String,
    val targetAppName: String,
    val photoPath: String? = null,
    val failedAttempts: Int,
    val timestamp: Long = System.currentTimeMillis()
)
