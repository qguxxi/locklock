package com.locklock.applock.di

import android.content.Context
import androidx.room.Room
import com.locklock.applock.data.local.db.AppLockDatabase
import com.locklock.applock.data.local.db.dao.IntruderLogDao
import com.locklock.applock.data.local.db.dao.LockedAppDao
import com.locklock.applock.data.local.db.dao.VaultMediaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppLockDatabase(
        @ApplicationContext context: Context
    ): AppLockDatabase {
        return Room.databaseBuilder(
            context,
            AppLockDatabase::class.java,
            "serene_vault_applock.db"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    fun provideLockedAppDao(db: AppLockDatabase): LockedAppDao = db.lockedAppDao()

    @Provides
    fun provideVaultMediaDao(db: AppLockDatabase): VaultMediaDao = db.vaultMediaDao()

    @Provides
    fun provideIntruderLogDao(db: AppLockDatabase): IntruderLogDao = db.intruderLogDao()
}
