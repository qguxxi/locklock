package com.locklock.applock.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.locklock.applock.data.local.db.dao.IntruderLogDao
import com.locklock.applock.data.local.db.dao.LockedAppDao
import com.locklock.applock.data.local.db.dao.VaultMediaDao
import com.locklock.applock.data.local.db.entity.IntruderLogEntity
import com.locklock.applock.data.local.db.entity.LockedAppEntity
import com.locklock.applock.data.local.db.entity.VaultMediaEntity

@Database(
    entities = [
        LockedAppEntity::class,
        VaultMediaEntity::class,
        IntruderLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppLockDatabase : RoomDatabase() {
    abstract fun lockedAppDao(): LockedAppDao
    abstract fun vaultMediaDao(): VaultMediaDao
    abstract fun intruderLogDao(): IntruderLogDao
}
