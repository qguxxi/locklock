package com.locklock.applock.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.locklock.applock.core.crypto.PasscodeHasher
import com.locklock.applock.domain.model.DisguiseIconMode
import com.locklock.applock.domain.model.LockMode
import com.locklock.applock.domain.model.RelockPolicy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.securityDataStore: DataStore<Preferences> by preferencesDataStore(name = "serene_vault_security_prefs")

data class SecuritySettingsState(
    val isMasterProtectionEnabled: Boolean = true,
    val lockMode: LockMode = LockMode.PIN,
    val passcodeHash: String = PasscodeHasher.sha256("1234"), // Default initial PIN 1234
    val isBiometricEnabled: Boolean = true,
    val isRandomKeypadEnabled: Boolean = false,
    val isInvisiblePatternEnabled: Boolean = false,
    val isIntruderSelfieEnabled: Boolean = true,
    val intruderAttemptThreshold: Int = 3,
    val relockPolicy: RelockPolicy = RelockPolicy.IMMEDIATE,
    val disguiseMode: DisguiseIconMode = DisguiseIconMode.DEFAULT
)

@Singleton
class SecurityPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val MASTER_ENABLED = booleanPreferencesKey("master_enabled")
        val LOCK_MODE = stringPreferencesKey("lock_mode")
        val PASSCODE_HASH = stringPreferencesKey("passcode_hash")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val RANDOM_KEYPAD = booleanPreferencesKey("random_keypad")
        val INVISIBLE_PATTERN = booleanPreferencesKey("invisible_pattern")
        val INTRUDER_SELFIE = booleanPreferencesKey("intruder_selfie")
        val INTRUDER_THRESHOLD = intPreferencesKey("intruder_threshold")
        val RELOCK_POLICY = stringPreferencesKey("relock_policy")
        val DISGUISE_MODE = stringPreferencesKey("disguise_mode")
    }

    val settingsFlow: Flow<SecuritySettingsState> = context.securityDataStore.data.map { prefs ->
        SecuritySettingsState(
            isMasterProtectionEnabled = prefs[Keys.MASTER_ENABLED] ?: true,
            lockMode = runCatching { LockMode.valueOf(prefs[Keys.LOCK_MODE] ?: LockMode.PIN.name) }
                .getOrDefault(LockMode.PIN),
            passcodeHash = prefs[Keys.PASSCODE_HASH] ?: PasscodeHasher.sha256("1234"),
            isBiometricEnabled = prefs[Keys.BIOMETRIC_ENABLED] ?: true,
            isRandomKeypadEnabled = prefs[Keys.RANDOM_KEYPAD] ?: false,
            isInvisiblePatternEnabled = prefs[Keys.INVISIBLE_PATTERN] ?: false,
            isIntruderSelfieEnabled = prefs[Keys.INTRUDER_SELFIE] ?: true,
            intruderAttemptThreshold = prefs[Keys.INTRUDER_THRESHOLD] ?: 3,
            relockPolicy = runCatching {
                RelockPolicy.valueOf(prefs[Keys.RELOCK_POLICY] ?: RelockPolicy.IMMEDIATE.name)
            }.getOrDefault(RelockPolicy.IMMEDIATE),
            disguiseMode = runCatching {
                DisguiseIconMode.valueOf(prefs[Keys.DISGUISE_MODE] ?: DisguiseIconMode.DEFAULT.name)
            }.getOrDefault(DisguiseIconMode.DEFAULT)
        )
    }

    suspend fun getSnapshot(): SecuritySettingsState = settingsFlow.first()

    suspend fun verifyPasscode(rawInput: String): Boolean {
        val currentHash = getSnapshot().passcodeHash
        return PasscodeHasher.sha256(rawInput) == currentHash
    }

    suspend fun updatePasscode(newRawPasscode: String, mode: LockMode) {
        context.securityDataStore.edit { prefs ->
            prefs[Keys.PASSCODE_HASH] = PasscodeHasher.sha256(newRawPasscode)
            prefs[Keys.LOCK_MODE] = mode.name
        }
    }

    suspend fun setMasterProtection(enabled: Boolean) {
        context.securityDataStore.edit { it[Keys.MASTER_ENABLED] = enabled }
    }

    suspend fun setLockMode(mode: LockMode) {
        context.securityDataStore.edit { it[Keys.LOCK_MODE] = mode.name }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.securityDataStore.edit { it[Keys.BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun setRandomKeypad(enabled: Boolean) {
        context.securityDataStore.edit { it[Keys.RANDOM_KEYPAD] = enabled }
    }

    suspend fun setInvisiblePattern(enabled: Boolean) {
        context.securityDataStore.edit { it[Keys.INVISIBLE_PATTERN] = enabled }
    }

    suspend fun setIntruderSelfie(enabled: Boolean) {
        context.securityDataStore.edit { it[Keys.INTRUDER_SELFIE] = enabled }
    }

    suspend fun setRelockPolicy(policy: RelockPolicy) {
        context.securityDataStore.edit { it[Keys.RELOCK_POLICY] = policy.name }
    }

    suspend fun setDisguiseMode(mode: DisguiseIconMode) {
        context.securityDataStore.edit { it[Keys.DISGUISE_MODE] = mode.name }
    }
}
