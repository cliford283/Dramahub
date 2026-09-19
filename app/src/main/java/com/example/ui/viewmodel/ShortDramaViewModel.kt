package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.DramaEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WatchHistoryEntity
import com.example.data.repository.ShortDramaRepository
import com.example.data.security.AdminAuthManager
import com.example.data.security.AdminAuthResult
import com.example.data.security.AdminProfile
import com.example.data.security.FirebaseAppCheckManager
import com.example.data.sync.FirestoreSyncService
import com.example.data.sync.SyncState
import com.example.data.sync.SyncSummary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME, SEARCH, QUICK_PLAY, BROWSE, MY_LIST
}

enum class ScreenState {
    MAIN, DETAIL, PLAYER, ADMIN
}

enum class AdminTab {
    METRICS, USERS, DRAMAS, AUDIT_LOGS
}

class ShortDramaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ShortDramaRepository.getInstance(application)

    // Navigation & Screen states
    private val _currentScreen = MutableStateFlow(ScreenState.MAIN)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Selected drama and episode for detail/player
    private val _selectedDrama = MutableStateFlow<DramaEntity?>(null)
    val selectedDrama: StateFlow<DramaEntity?> = _selectedDrama.asStateFlow()

    private val _currentEpisode = MutableStateFlow(1)
    val currentEpisode: StateFlow<Int> = _currentEpisode.asStateFlow()

    // Player playback state
    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _progressSeconds = MutableStateFlow(12)
    val progressSeconds: StateFlow<Int> = _progressSeconds.asStateFlow()

    private val _totalDurationSeconds = MutableStateFlow(144) // 2:24 duration like video
    val totalDurationSeconds: StateFlow<Int> = _totalDurationSeconds.asStateFlow()

    private val _showEpisodesSheet = MutableStateFlow(false)
    val showEpisodesSheet: StateFlow<Boolean> = _showEpisodesSheet.asStateFlow()

    private val _showSettingsSheet = MutableStateFlow(false)
    val showSettingsSheet: StateFlow<Boolean> = _showSettingsSheet.asStateFlow()

    private var playbackTickerJob: Job? = null

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSearchFilter = MutableStateFlow("All")
    val selectedSearchFilter: StateFlow<String> = _selectedSearchFilter.asStateFlow()

    // Browse Tab
    private val _selectedBrowseTab = MutableStateFlow("COLLECTION")
    val selectedBrowseTab: StateFlow<String> = _selectedBrowseTab.asStateFlow()

    // User & Auth State
    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _showAdminAuthDialog = MutableStateFlow(false)
    val showAdminAuthDialog: StateFlow<Boolean> = _showAdminAuthDialog.asStateFlow()

    private val _adminAuthError = MutableStateFlow<String?>(null)
    val adminAuthError: StateFlow<String?> = _adminAuthError.asStateFlow()

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    // Cloud Firestore Sync & Firebase Auth Services
    val firestoreSyncService = FirestoreSyncService(application, repository)
    val adminAuthManager = AdminAuthManager(application, repository)

    val syncState: StateFlow<SyncState> = firestoreSyncService.syncState
    val syncSummary: StateFlow<SyncSummary> = firestoreSyncService.syncSummary
    val adminProfile: StateFlow<AdminProfile?> = adminAuthManager.currentAdmin
    val isAppCheckActive: StateFlow<Boolean> = FirebaseAppCheckManager.isAppCheckActive
    val appCheckStatus: StateFlow<String> = FirebaseAppCheckManager.tokenStatus

    // Admin Dashboard
    private val _adminTab = MutableStateFlow(AdminTab.METRICS)
    val adminTab: StateFlow<AdminTab> = _adminTab.asStateFlow()

    private val _userSearchQuery = MutableStateFlow("")
    val userSearchQuery: StateFlow<String> = _userSearchQuery.asStateFlow()

    private val _userRoleFilter = MutableStateFlow("ALL")
    val userRoleFilter: StateFlow<String> = _userRoleFilter.asStateFlow()

    private val _userStatusFilter = MutableStateFlow("ALL")
    val userStatusFilter: StateFlow<String> = _userStatusFilter.asStateFlow()

    private val _showAddUserDialog = MutableStateFlow(false)
    val showAddUserDialog: StateFlow<Boolean> = _showAddUserDialog.asStateFlow()

    private val _selectedUserForEdit = MutableStateFlow<UserEntity?>(null)
    val selectedUserForEdit: StateFlow<UserEntity?> = _selectedUserForEdit.asStateFlow()

    private val _adminNotification = MutableStateFlow<String?>(null)
    val adminNotification: StateFlow<String?> = _adminNotification.asStateFlow()

    // Repository Flows
    val allDramas: StateFlow<List<DramaEntity>> = repository.allDramas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredDrama: StateFlow<DramaEntity?> = repository.featuredDrama
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userCount: StateFlow<Int> = repository.userCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeUserCount: StateFlow<Int> = repository.activeUserCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val vipUserCount: StateFlow<Int> = repository.vipUserCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val dramaCount: StateFlow<Int> = repository.dramaCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.recentAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorites & History for current active user
    private val _favoriteIds = MutableStateFlow<List<String>>(listOf("mafia_don_wife", "outcast_queen"))
    val favoriteIds: StateFlow<List<String>> = _favoriteIds.asStateFlow()

    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    init {
        // Set default logged in user
        viewModelScope.launch {
            allUsers.collect { users ->
                if (users.isNotEmpty() && _currentUser.value == null) {
                    val defaultUser = users.find { it.email == "clifordmulumba@gmail.com" } ?: users.first()
                    _currentUser.value = defaultUser
                }
            }
        }
        startPlaybackSimulation()
    }

    private fun startPlaybackSimulation() {
        playbackTickerJob?.cancel()
        playbackTickerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (_isPlaying.value && _currentScreen.value == ScreenState.PLAYER) {
                    val nextSec = _progressSeconds.value + 1
                    if (nextSec >= _totalDurationSeconds.value) {
                        _progressSeconds.value = 0
                        val nextEp = _currentEpisode.value + 1
                        val drama = _selectedDrama.value
                        if (drama != null && nextEp <= drama.totalEpisodes) {
                            _currentEpisode.value = nextEp
                        }
                    } else {
                        _progressSeconds.value = nextSec
                    }
                }
            }
        }
    }

    // Navigation Actions
    fun setScreen(screen: ScreenState) {
        _currentScreen.value = screen
    }

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
        if (_currentScreen.value != ScreenState.MAIN) {
            _currentScreen.value = ScreenState.MAIN
        }
    }

    fun openDramaDetail(drama: DramaEntity) {
        _selectedDrama.value = drama
        _currentScreen.value = ScreenState.DETAIL
    }

    fun playDrama(drama: DramaEntity, episode: Int = 1) {
        _selectedDrama.value = drama
        _currentEpisode.value = episode
        _progressSeconds.value = 0
        _isPlaying.value = true
        _currentScreen.value = ScreenState.PLAYER
    }

    fun closePlayer() {
        _isPlaying.value = false
        _currentScreen.value = ScreenState.MAIN
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    fun seekRelative(seconds: Int) {
        val next = (_progressSeconds.value + seconds).coerceIn(0, _totalDurationSeconds.value)
        _progressSeconds.value = next
    }

    fun seekTo(seconds: Int) {
        _progressSeconds.value = seconds.coerceIn(0, _totalDurationSeconds.value)
    }

    fun nextEpisode() {
        val drama = _selectedDrama.value ?: return
        if (_currentEpisode.value < drama.totalEpisodes) {
            _currentEpisode.value += 1
            _progressSeconds.value = 0
        }
    }

    fun prevEpisode() {
        if (_currentEpisode.value > 1) {
            _currentEpisode.value -= 1
            _progressSeconds.value = 0
        }
    }

    fun selectEpisode(ep: Int) {
        _currentEpisode.value = ep
        _progressSeconds.value = 0
        _showEpisodesSheet.value = false
    }

    fun toggleEpisodesSheet(show: Boolean) {
        _showEpisodesSheet.value = show
    }

    fun toggleSettingsSheet(show: Boolean) {
        _showSettingsSheet.value = show
    }

    // Search and Browse filters
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchFilter(filter: String) {
        _selectedSearchFilter.value = filter
    }

    fun setBrowseTab(tab: String) {
        _selectedBrowseTab.value = tab
    }

    // Favorites
    fun toggleFavorite(dramaId: String) {
        val current = _favoriteIds.value.toMutableList()
        val isFav = current.contains(dramaId)
        if (isFav) {
            current.remove(dramaId)
            _snackMessage.value = "Removed from My List"
        } else {
            current.add(dramaId)
            _snackMessage.value = "Added to My List"
        }
        _favoriteIds.value = current
    }

    fun clearSnackMessage() {
        _snackMessage.value = null
    }

    // Admin Auth
    fun showAdminDialog(show: Boolean) {
        _showAdminAuthDialog.value = show
        _adminAuthError.value = null
        _isAuthenticating.value = false
    }

    fun authenticateAdmin(pinOrPassword: String) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _adminAuthError.value = null
            // Check master PIN (8888) or password check via AdminAuthManager
            if (adminAuthManager.signInWithMasterPin(pinOrPassword) ||
                pinOrPassword == "8888" || pinOrPassword == "Admin@12345" || pinOrPassword == "admin") {
                _isAdminAuthenticated.value = true
                _showAdminAuthDialog.value = false
                _adminAuthError.value = null
                _currentScreen.value = ScreenState.ADMIN
                _adminNotification.value = "Welcome to Private Admin Dashboard"
            } else {
                // Try database credentials
                val admin = repository.authenticateUser("admin@shortdrama.tv", pinOrPassword)
                if (admin != null && (admin.role == "ADMIN" || admin.role == "SUPER_ADMIN")) {
                    adminAuthManager.signInWithMasterPin("8888")
                    _isAdminAuthenticated.value = true
                    _showAdminAuthDialog.value = false
                    _adminAuthError.value = null
                    _currentScreen.value = ScreenState.ADMIN
                    _adminNotification.value = "Welcome, ${admin.name}"
                } else {
                    _adminAuthError.value = "Invalid Admin PIN or Password. (Try PIN: 8888 or Admin@12345)"
                }
            }
            _isAuthenticating.value = false
        }
    }

    fun authenticateAdminWithGoogle(activity: Activity) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _adminAuthError.value = null
            when (val result = adminAuthManager.signInWithGoogle(activity)) {
                is AdminAuthResult.Success -> {
                    _isAdminAuthenticated.value = true
                    _showAdminAuthDialog.value = false
                    _currentScreen.value = ScreenState.ADMIN
                    _adminNotification.value = "Authenticated with Google Admin (${result.profile.email})"
                }
                is AdminAuthResult.Error -> {
                    _adminAuthError.value = result.message
                }
            }
            _isAuthenticating.value = false
        }
    }

    fun authenticateAdminWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _adminAuthError.value = null
            when (val result = adminAuthManager.signInWithEmailPassword(email, pass)) {
                is AdminAuthResult.Success -> {
                    _isAdminAuthenticated.value = true
                    _showAdminAuthDialog.value = false
                    _currentScreen.value = ScreenState.ADMIN
                    _adminNotification.value = "Welcome Admin: ${result.profile.displayName}"
                }
                is AdminAuthResult.Error -> {
                    _adminAuthError.value = result.message
                }
            }
            _isAuthenticating.value = false
        }
    }

    // Firestore Synchronization
    fun triggerFirestoreSync() {
        viewModelScope.launch {
            firestoreSyncService.syncAll()
        }
    }

    fun pullRemoteFirestoreUpdates() {
        viewModelScope.launch {
            firestoreSyncService.pullRemoteUpdates()
        }
    }

    fun exitAdminDashboard() {
        _currentScreen.value = ScreenState.MAIN
    }

    fun logoutAdmin() {
        adminAuthManager.signOut()
        _isAdminAuthenticated.value = false
        _currentScreen.value = ScreenState.MAIN
        _adminNotification.value = "Admin session closed."
    }

    // Admin Operations
    fun setAdminTab(tab: AdminTab) {
        _adminTab.value = tab
    }

    fun setUserSearchQuery(query: String) {
        _userSearchQuery.value = query
    }

    fun setUserRoleFilter(role: String) {
        _userRoleFilter.value = role
    }

    fun setUserStatusFilter(status: String) {
        _userStatusFilter.value = status
    }

    fun setShowAddUserDialog(show: Boolean) {
        _showAddUserDialog.value = show
    }

    fun setSelectedUserForEdit(user: UserEntity?) {
        _selectedUserForEdit.value = user
    }

    fun createNewUser(name: String, email: String, rawPass: String, role: String, tier: String) {
        viewModelScope.launch {
            val adminEmail = _currentUser.value?.email ?: "admin@shortdrama.tv"
            repository.createUser(name, email, rawPass, role, tier, adminEmail)
            _showAddUserDialog.value = false
            _adminNotification.value = "User '$email' created successfully."
        }
    }

    fun updateUserStatus(user: UserEntity, newStatus: String) {
        viewModelScope.launch {
            val adminEmail = _currentUser.value?.email ?: "admin@shortdrama.tv"
            repository.updateUserStatus(user.id, user.email, newStatus, adminEmail)
            _adminNotification.value = "User ${user.email} status set to $newStatus"
        }
    }

    fun updateUserRole(user: UserEntity, newRole: String) {
        viewModelScope.launch {
            val adminEmail = _currentUser.value?.email ?: "admin@shortdrama.tv"
            repository.updateUserRole(user.id, user.email, newRole, adminEmail)
            _adminNotification.value = "User ${user.email} role updated to $newRole"
        }
    }

    fun updateUserTier(user: UserEntity, newTier: String) {
        viewModelScope.launch {
            val adminEmail = _currentUser.value?.email ?: "admin@shortdrama.tv"
            repository.updateUserTier(user.id, user.email, newTier, adminEmail)
            _adminNotification.value = "Subscription tier for ${user.email} set to $newTier"
        }
    }

    fun resetUserPassword(user: UserEntity, newPass: String) {
        viewModelScope.launch {
            val adminEmail = _currentUser.value?.email ?: "admin@shortdrama.tv"
            repository.resetUserPassword(user.id, user.email, newPass, adminEmail)
            _adminNotification.value = "Password reset for ${user.email}"
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch {
            val adminEmail = _currentUser.value?.email ?: "admin@shortdrama.tv"
            repository.deleteUser(user, adminEmail)
            _selectedUserForEdit.value = null
            _adminNotification.value = "User ${user.email} deleted permanently."
        }
    }

    fun clearAdminNotification() {
        _adminNotification.value = null
    }
}
