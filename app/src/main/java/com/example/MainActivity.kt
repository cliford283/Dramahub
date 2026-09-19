package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.security.FirebaseAppCheckManager
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.BrowseScreen
import com.example.ui.screens.DramaDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyListScreen
import com.example.ui.screens.QuickPlayReelScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VideoPlayerScreen
import com.example.ui.screens.admin.AdminAuthDialog
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.theme.DramaBlack
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.ScreenState
import com.example.ui.viewmodel.ShortDramaViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ShortDramaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Configure Firebase App Check with ReCaptcha Enterprise verification
        FirebaseAppCheckManager.initialize(applicationContext)

        setContent {
            MyApplicationTheme {
                ShortDramaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ShortDramaApp(viewModel: ShortDramaViewModel) {
    val context = LocalContext.current
    val activity = context as? Activity

    var showSplash by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedDrama by viewModel.selectedDrama.collectAsStateWithLifecycle()
    val currentEpisode by viewModel.currentEpisode.collectAsStateWithLifecycle()

    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val progressSeconds by viewModel.progressSeconds.collectAsStateWithLifecycle()
    val totalDurationSeconds by viewModel.totalDurationSeconds.collectAsStateWithLifecycle()
    val showEpisodesSheet by viewModel.showEpisodesSheet.collectAsStateWithLifecycle()

    val allDramas by viewModel.allDramas.collectAsStateWithLifecycle()
    val featuredDrama by viewModel.featuredDrama.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedSearchFilter by viewModel.selectedSearchFilter.collectAsStateWithLifecycle()
    val selectedBrowseTab by viewModel.selectedBrowseTab.collectAsStateWithLifecycle()

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val isAdminAuthenticated by viewModel.isAdminAuthenticated.collectAsStateWithLifecycle()
    val showAdminAuthDialog by viewModel.showAdminAuthDialog.collectAsStateWithLifecycle()
    val adminAuthError by viewModel.adminAuthError.collectAsStateWithLifecycle()

    // Cloud Firestore Sync & Admin Authentication states
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()
    val syncSummary by viewModel.syncSummary.collectAsStateWithLifecycle()
    val adminProfile by viewModel.adminProfile.collectAsStateWithLifecycle()
    val isAppCheckActive by viewModel.isAppCheckActive.collectAsStateWithLifecycle()
    val appCheckStatus by viewModel.appCheckStatus.collectAsStateWithLifecycle()
    val isAuthenticating by viewModel.isAuthenticating.collectAsStateWithLifecycle()

    val adminTab by viewModel.adminTab.collectAsStateWithLifecycle()
    val userSearchQuery by viewModel.userSearchQuery.collectAsStateWithLifecycle()
    val userRoleFilter by viewModel.userRoleFilter.collectAsStateWithLifecycle()
    val userStatusFilter by viewModel.userStatusFilter.collectAsStateWithLifecycle()
    val showAddUserDialog by viewModel.showAddUserDialog.collectAsStateWithLifecycle()
    val adminNotification by viewModel.adminNotification.collectAsStateWithLifecycle()
    val snackMessage by viewModel.snackMessage.collectAsStateWithLifecycle()

    // Handle toast/snackbar message
    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackMessage()
        }
    }

    // Back handling
    BackHandler(enabled = !showSplash && currentScreen != ScreenState.MAIN) {
        when (currentScreen) {
            ScreenState.PLAYER -> viewModel.closePlayer()
            ScreenState.DETAIL -> viewModel.setScreen(ScreenState.MAIN)
            ScreenState.ADMIN -> viewModel.exitAdminDashboard()
            ScreenState.MAIN -> { /* Default exit */ }
        }
    }

    if (showSplash) {
        SplashScreen(onSplashFinished = { showSplash = false })
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (currentScreen == ScreenState.MAIN) {
                    BottomNavBar(
                        selectedTab = currentTab,
                        onTabSelected = { tab -> viewModel.setTab(tab) }
                    )
                }
            },
            containerColor = DramaBlack
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DramaBlack)
                    .padding(
                        bottom = if (currentScreen == ScreenState.MAIN) innerPadding.calculateBottomPadding() else Color.Transparent.run { androidx.compose.ui.unit.Dp.Hairline }
                    )
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        ScreenState.MAIN -> {
                            when (currentTab) {
                                MainTab.HOME -> {
                                    HomeScreen(
                                        featuredDrama = featuredDrama,
                                        allDramas = allDramas,
                                        favoriteIds = favoriteIds,
                                        isAdminLoggedIn = isAdminAuthenticated,
                                        onDramaClick = { d -> viewModel.openDramaDetail(d) },
                                        onPlayClick = { d -> viewModel.playDrama(d, 1) },
                                        onFavoriteToggle = { id -> viewModel.toggleFavorite(id) },
                                        onSearchClick = { viewModel.setTab(MainTab.SEARCH) },
                                        onAdminClick = {
                                            if (isAdminAuthenticated) {
                                                viewModel.setScreen(ScreenState.ADMIN)
                                            } else {
                                                viewModel.showAdminDialog(true)
                                            }
                                        }
                                    )
                                }
                                MainTab.SEARCH -> {
                                    SearchScreen(
                                        searchQuery = searchQuery,
                                        selectedFilter = selectedSearchFilter,
                                        allDramas = allDramas,
                                        favoriteIds = favoriteIds,
                                        onQueryChange = { q -> viewModel.setSearchQuery(q) },
                                        onFilterSelect = { f -> viewModel.setSearchFilter(f) },
                                        onDramaClick = { d -> viewModel.openDramaDetail(d) },
                                        onFavoriteToggle = { id -> viewModel.toggleFavorite(id) }
                                    )
                                }
                                MainTab.QUICK_PLAY -> {
                                    QuickPlayReelScreen(
                                        dramas = allDramas,
                                        favoriteIds = favoriteIds,
                                        onWatchSeries = { d -> viewModel.openDramaDetail(d) },
                                        onFavoriteToggle = { id -> viewModel.toggleFavorite(id) }
                                    )
                                }
                                MainTab.BROWSE -> {
                                    BrowseScreen(
                                        selectedCategory = selectedBrowseTab,
                                        allDramas = allDramas,
                                        favoriteIds = favoriteIds,
                                        onCategorySelect = { c -> viewModel.setBrowseTab(c) },
                                        onDramaClick = { d -> viewModel.openDramaDetail(d) },
                                        onFavoriteToggle = { id -> viewModel.toggleFavorite(id) }
                                    )
                                }
                                MainTab.MY_LIST -> {
                                    MyListScreen(
                                        currentUser = currentUser,
                                        allDramas = allDramas,
                                        favoriteIds = favoriteIds,
                                        isAdminLoggedIn = isAdminAuthenticated,
                                        onDramaClick = { d -> viewModel.openDramaDetail(d) },
                                        onFavoriteToggle = { id -> viewModel.toggleFavorite(id) },
                                        onAdminDashboardClick = {
                                            if (isAdminAuthenticated) {
                                                viewModel.setScreen(ScreenState.ADMIN)
                                            } else {
                                                viewModel.showAdminDialog(true)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        ScreenState.DETAIL -> {
                            selectedDrama?.let { drama ->
                                DramaDetailScreen(
                                    drama = drama,
                                    isFavorite = favoriteIds.contains(drama.id),
                                    onBackClick = { viewModel.setScreen(ScreenState.MAIN) },
                                    onPlayClick = { ep -> viewModel.playDrama(drama, ep) },
                                    onFavoriteToggle = { id -> viewModel.toggleFavorite(id) }
                                )
                            }
                        }
                        ScreenState.PLAYER -> {
                            selectedDrama?.let { drama ->
                                VideoPlayerScreen(
                                    drama = drama,
                                    currentEpisode = currentEpisode,
                                    isPlaying = isPlaying,
                                    progressSeconds = progressSeconds,
                                    totalDurationSeconds = totalDurationSeconds,
                                    showEpisodesSheet = showEpisodesSheet,
                                    onBackClick = { viewModel.closePlayer() },
                                    onTogglePlay = { viewModel.togglePlayPause() },
                                    onSeekRelative = { s -> viewModel.seekRelative(s) },
                                    onSeekTo = { s -> viewModel.seekTo(s) },
                                    onNextEpisode = { viewModel.nextEpisode() },
                                    onPrevEpisode = { viewModel.prevEpisode() },
                                    onSelectEpisode = { ep -> viewModel.selectEpisode(ep) },
                                    onToggleEpisodesSheet = { show -> viewModel.toggleEpisodesSheet(show) }
                                )
                            }
                        }
                        ScreenState.ADMIN -> {
                            AdminDashboardScreen(
                                currentAdminTab = adminTab,
                                allUsers = allUsers,
                                allDramas = allDramas,
                                auditLogs = auditLogs,
                                userSearchQuery = userSearchQuery,
                                userRoleFilter = userRoleFilter,
                                userStatusFilter = userStatusFilter,
                                showAddUserDialog = showAddUserDialog,
                                notificationMessage = adminNotification,
                                onSelectTab = { t -> viewModel.setAdminTab(t) },
                                onUserSearchQueryChange = { q -> viewModel.setUserSearchQuery(q) },
                                onUserRoleFilterChange = { r -> viewModel.setUserRoleFilter(r) },
                                onUserStatusFilterChange = { s -> viewModel.setUserStatusFilter(s) },
                                onToggleAddUserDialog = { show -> viewModel.setShowAddUserDialog(show) },
                                onCreateUser = { name, email, pass, role, tier ->
                                    viewModel.createNewUser(name, email, pass, role, tier)
                                },
                                onUpdateUserStatus = { u, s -> viewModel.updateUserStatus(u, s) },
                                onUpdateUserRole = { u, r -> viewModel.updateUserRole(u, r) },
                                onUpdateUserTier = { u, t -> viewModel.updateUserTier(u, t) },
                                onResetPassword = { u, p -> viewModel.resetUserPassword(u, p) },
                                onDeleteUser = { u -> viewModel.deleteUser(u) },
                                onExitDashboard = { viewModel.exitAdminDashboard() },
                                onLogout = { viewModel.logoutAdmin() },
                                syncState = syncState,
                                syncSummary = syncSummary,
                                adminProfile = adminProfile,
                                isAppCheckActive = isAppCheckActive,
                                appCheckStatus = appCheckStatus,
                                onTriggerSync = { viewModel.triggerFirestoreSync() },
                                onPullUpdates = { viewModel.pullRemoteFirestoreUpdates() }
                            )
                        }
                    }
                }
            }
        }

        // Secure Admin Auth Dialog
        AdminAuthDialog(
            isOpen = showAdminAuthDialog,
            errorMessage = adminAuthError,
            isAuthenticating = isAuthenticating,
            onDismiss = { viewModel.showAdminDialog(false) },
            onSubmitAuth = { pinOrPass -> viewModel.authenticateAdmin(pinOrPass) },
            onGoogleSignIn = {
                activity?.let { viewModel.authenticateAdminWithGoogle(it) }
            },
            onSubmitEmailPassword = { email, pass ->
                viewModel.authenticateAdminWithEmail(email, pass)
            }
        )
    }
}

// Retained for Screenshot / Unit Tests
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
