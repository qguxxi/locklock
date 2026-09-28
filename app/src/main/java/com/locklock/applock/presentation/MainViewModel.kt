package com.locklock.applock.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.locklock.applock.core.disguise.AppIconDisguiseManager
import com.locklock.applock.core.permission.PermissionHelper
import com.locklock.applock.data.local.datastore.SecurityPreferences
import com.locklock.applock.data.local.datastore.SecuritySettingsState
import com.locklock.applock.data.repository.AppLockRepository
import com.locklock.applock.domain.model.DisguiseIconMode
import com.locklock.applock.domain.model.InstalledAppInfo
import com.locklock.applock.domain.model.IntruderLogItem
import com.locklock.applock.domain.model.LockMode
import com.locklock.applock.domain.model.RelockPolicy
import com.locklock.applock.domain.model.VaultMediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AppFilterTab(val title: String) {
    ALL("Tất cả ứng dụng"),
    LOCKED("Đang khóa"),
    RECOMMENDED("Gợi ý bảo vệ")
}

data class DashboardUiState(
    val isLoadingApps: Boolean = true,
    val allApps: List<InstalledAppInfo> = emptyList(),
    val filteredApps: List<InstalledAppInfo> = emptyList(),
    val lockedCount: Int = 0,
    val selectedFilter: AppFilterTab = AppFilterTab.ALL,
    val searchQuery: String = "",
    val hasOverlayPermission: Boolean = false,
    val hasAccessibilityEnabled: Boolean = false,
    val hasUsageStatsPermission: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: AppLockRepository,
    private val securityPreferences: SecurityPreferences,
    private val disguiseManager: AppIconDisguiseManager
) : ViewModel() {

    private val rawInstalledApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    private val isLoading = MutableStateFlow(true)
    private val selectedFilter = MutableStateFlow(AppFilterTab.ALL)
    private val searchQuery = MutableStateFlow("")
    private val permissionTick = MutableStateFlow(0)

    val securitySettings: StateFlow<SecuritySettingsState> = securityPreferences.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SecuritySettingsState())

    val vaultItems: StateFlow<List<VaultMediaItem>> = repository.observeVaultItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val intruderLogs: StateFlow<List<IntruderLogItem>> = repository.observeIntruderLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashboardState: StateFlow<DashboardUiState> = combine(
        rawInstalledApps,
        repository.observeLockedPackageSet(),
        selectedFilter,
        searchQuery,
        permissionTick
    ) { installed, lockedSet, filter, query, _ ->
        val merged = installed.map { app ->
            app.copy(isLocked = lockedSet.contains(app.packageName))
        }
        val filtered = merged.filter { app ->
            val matchesTab = when (filter) {
                AppFilterTab.ALL -> true
                AppFilterTab.LOCKED -> app.isLocked
                AppFilterTab.RECOMMENDED -> app.isRecommended
            }
            val matchesQuery = query.isBlank() ||
                app.appName.contains(query, ignoreCase = true) ||
                app.packageName.contains(query, ignoreCase = true)
            matchesTab && matchesQuery
        }

        DashboardUiState(
            isLoadingApps = isLoading.value,
            allApps = merged,
            filteredApps = filtered,
            lockedCount = merged.count { it.isLocked },
            selectedFilter = filter,
            searchQuery = query,
            hasOverlayPermission = PermissionHelper.hasOverlayPermission(context),
            hasAccessibilityEnabled = PermissionHelper.isAccessibilityServiceEnabled(context),
            hasUsageStatsPermission = PermissionHelper.hasUsageStatsPermission(context)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    init {
        refreshInstalledApps()
    }

    fun refreshPermissions() {
        permissionTick.value += 1
    }

    fun refreshInstalledApps() {
        viewModelScope.launch {
            isLoading.value = true
            val lockedSet = repository.observeLockedPackageSet()
            val apps = repository.getInstalledApps(emptySet())
            rawInstalledApps.value = apps
            isLoading.value = false
            permissionTick.value += 1
        }
    }

    fun setFilterTab(tab: AppFilterTab) {
        selectedFilter.value = tab
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun toggleAppLock(app: InstalledAppInfo, shouldLock: Boolean) {
        viewModelScope.launch {
            repository.toggleAppLock(app.packageName, app.appName, shouldLock)
        }
    }

    fun lockAllRecommendedApps() {
        viewModelScope.launch {
            val current = dashboardState.value.allApps
            current.filter { it.isRecommended && !it.isLocked }.forEach { app ->
                repository.toggleAppLock(app.packageName, app.appName, true)
            }
        }
    }

    fun importMediaToVault(uris: List<Uri>) {
        viewModelScope.launch {
            uris.forEach { uri ->
                repository.importUriToVault(uri)
            }
        }
    }

    fun deleteVaultItem(item: VaultMediaItem) {
        viewModelScope.launch {
            repository.removeVaultItem(item)
        }
    }

    fun recordDemoIntruderLog() {
        viewModelScope.launch {
            repository.recordIntruderAttempt("com.whatsapp", 3)
        }
    }

    fun clearIntruderLogs() {
        viewModelScope.launch {
            repository.clearIntruderLogs()
        }
    }

    suspend fun verifyPasscode(rawInput: String): Boolean {
        return securityPreferences.verifyPasscode(rawInput)
    }

    fun updatePasscode(newPasscode: String, mode: LockMode) {
        viewModelScope.launch {
            securityPreferences.updatePasscode(newPasscode, mode)
        }
    }

    fun setMasterProtection(enabled: Boolean) {
        viewModelScope.launch {
            securityPreferences.setMasterProtection(enabled)
        }
    }

    fun setLockMode(mode: LockMode) {
        viewModelScope.launch {
            securityPreferences.setLockMode(mode)
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            securityPreferences.setBiometricEnabled(enabled)
        }
    }

    fun setRandomKeypad(enabled: Boolean) {
        viewModelScope.launch {
            securityPreferences.setRandomKeypad(enabled)
        }
    }

    fun setInvisiblePattern(enabled: Boolean) {
        viewModelScope.launch {
            securityPreferences.setInvisiblePattern(enabled)
        }
    }

    fun setIntruderSelfie(enabled: Boolean) {
        viewModelScope.launch {
            securityPreferences.setIntruderSelfie(enabled)
        }
    }

    fun setRelockPolicy(policy: RelockPolicy) {
        viewModelScope.launch {
            securityPreferences.setRelockPolicy(policy)
        }
    }

    fun setDisguiseMode(mode: DisguiseIconMode) {
        viewModelScope.launch {
            securityPreferences.setDisguiseMode(mode)
            disguiseManager.applyDisguise(mode)
        }
    }
}
