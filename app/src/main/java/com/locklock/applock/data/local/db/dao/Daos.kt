package com.locklock.applock.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.locklock.applock.data.local.db.entity.IntruderLogEntity
import com.locklock.applock.data.local.db.entity.LockedAppEntity
import com.locklock.applock.data.local.db.entity.VaultMediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LockedAppDao {
    @Query("SELECT * FROM locked_apps WHERE isLocked = 1")
    fun observeLockedApps(): Flow<List<LockedAppEntity>>

    @Query("SELECT packageName FROM locked_apps WHERE isLocked = 1")
    suspend fun getLockedPackageNames(): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM locked_apps WHERE packageName = :packageName AND isLocked = 1)")
    suspend fun isPackageLocked(packageName: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLockedApp(entity: LockedAppEntity)

    @Query("DELETE FROM locked_apps WHERE packageName = :packageName")
    suspend fun unlockPackage(packageName: String)
}

@Dao
interface VaultMediaDao {
    @Query("SELECT * FROM vault_media ORDER BY addedAt DESC")
    fun observeVaultMedia(): Flow<List<VaultMediaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultMedia(entity: VaultMediaEntity): Long

    @Query("DELETE FROM vault_media WHERE id = :id")
    suspend fun deleteVaultMediaById(id: Long)
}

@Dao
interface IntruderLogDao {
    @Query("SELECT * FROM intruder_logs ORDER BY timestamp DESC")
    fun observeIntruderLogs(): Flow<List<IntruderLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntruderLog(entity: IntruderLogEntity)

    @Query("DELETE FROM intruder_logs")
    suspend fun clearAllLogs()
}
