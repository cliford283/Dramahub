package com.example.ui.screens.admin

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.DramaEntity
import com.example.data.local.entity.UserEntity
import com.example.data.security.AdminProfile
import com.example.data.sync.SyncState
import com.example.data.sync.SyncSummary
import com.example.ui.theme.AdminDark
import com.example.ui.theme.AdminPurple
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DramaAmber
import com.example.ui.theme.DramaCard
import com.example.ui.theme.DramaCardElevated
import com.example.ui.theme.DramaGold
import com.example.ui.theme.DramaRed
import com.example.ui.theme.StatusActive
import com.example.ui.theme.StatusBanned
import com.example.ui.theme.StatusSuspended
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AdminTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
    currentAdminTab: AdminTab,
    allUsers: List<UserEntity>,
    allDramas: List<DramaEntity>,
    auditLogs: List<AuditLogEntity>,
    userSearchQuery: String,
    userRoleFilter: String,
    userStatusFilter: String,
    showAddUserDialog: Boolean,
    notificationMessage: String?,
    onSelectTab: (AdminTab) -> Unit,
    onUserSearchQueryChange: (String) -> Unit,
    onUserRoleFilterChange: (String) -> Unit,
    onUserStatusFilterChange: (String) -> Unit,
    onToggleAddUserDialog: (Boolean) -> Unit,
    onCreateUser: (String, String, String, String, String) -> Unit,
    onUpdateUserStatus: (UserEntity, String) -> Unit,
    onUpdateUserRole: (UserEntity, String) -> Unit,
    onUpdateUserTier: (UserEntity, String) -> Unit,
    onResetPassword: (UserEntity, String) -> Unit,
    onDeleteUser: (UserEntity) -> Unit,
    onExitDashboard: () -> Unit,
    onLogout: () -> Unit,
    syncState: SyncState = SyncState.Idle,
    syncSummary: SyncSummary = SyncSummary(),
    adminProfile: AdminProfile? = null,
    isAppCheckActive: Boolean = true,
    appCheckStatus: String = "App Check Active",
    onTriggerSync: () -> Unit = {},
    onPullUpdates: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var userToResetPassword by remember { mutableStateOf<UserEntity?>(null) }
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }

    val filteredUsers = allUsers.filter { user ->
        val matchesQuery = userSearchQuery.isBlank() ||
                user.name.contains(userSearchQuery, ignoreCase = true) ||
                user.email.contains(userSearchQuery, ignoreCase = true)
        val matchesRole = userRoleFilter == "ALL" || user.role == userRoleFilter
        val matchesStatus = userStatusFilter == "ALL" || user.status == userStatusFilter
        matchesQuery && matchesRole && matchesStatus
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AdminDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Admin Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onExitDashboard) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to App",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ADMIN CONSOLE",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AdminPurple)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SECURE",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Full Access User & Platform Management",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("admin_logout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout Admin",
                            tint = DramaRed
                        )
                    }
                }
            }

            // Notification pill if present
            if (notificationMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AdminPurple.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = notificationMessage,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Tabs Row
            TabRow(
                selectedTabIndex = currentAdminTab.ordinal,
                containerColor = DramaCard,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[currentAdminTab.ordinal]),
                        color = AdminPurple
                    )
                }
            ) {
                Tab(
                    selected = currentAdminTab == AdminTab.METRICS,
                    onClick = { onSelectTab(AdminTab.METRICS) },
                    text = { Text("Overview", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = currentAdminTab == AdminTab.USERS,
                    onClick = { onSelectTab(AdminTab.USERS) },
                    text = { Text("Users (${allUsers.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = currentAdminTab == AdminTab.DRAMAS,
                    onClick = { onSelectTab(AdminTab.DRAMAS) },
                    text = { Text("Catalog (${allDramas.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = currentAdminTab == AdminTab.AUDIT_LOGS,
                    onClick = { onSelectTab(AdminTab.AUDIT_LOGS) },
                    text = { Text("Audit", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }

            // Tab Content
            when (currentAdminTab) {
                AdminTab.METRICS -> {
                    AdminMetricsOverview(
                        allUsers = allUsers,
                        allDramas = allDramas,
                        syncState = syncState,
                        syncSummary = syncSummary,
                        adminProfile = adminProfile,
                        isAppCheckActive = isAppCheckActive,
                        appCheckStatus = appCheckStatus,
                        onTriggerSync = onTriggerSync,
                        onPullUpdates = onPullUpdates,
                        onNavigateToUsers = { onSelectTab(AdminTab.USERS) }
                    )
                }
                AdminTab.USERS -> {
                    AdminUserManagement(
                        users = filteredUsers,
                        searchQuery = userSearchQuery,
                        roleFilter = userRoleFilter,
                        statusFilter = userStatusFilter,
                        onSearchQueryChange = onUserSearchQueryChange,
                        onRoleFilterChange = onUserRoleFilterChange,
                        onStatusFilterChange = onUserStatusFilterChange,
                        onOpenAddUserDialog = { onToggleAddUserDialog(true) },
                        onUpdateStatus = onUpdateUserStatus,
                        onUpdateRole = onUpdateUserRole,
                        onUpdateTier = onUpdateUserTier,
                        onRequestResetPassword = { userToResetPassword = it },
                        onRequestDeleteUser = { userToDelete = it }
                    )
                }
                AdminTab.DRAMAS -> {
                    AdminDramasCatalog(dramas = allDramas)
                }
                AdminTab.AUDIT_LOGS -> {
                    AdminAuditLogsView(logs = auditLogs)
                }
            }
        }

        // Add User Dialog
        if (showAddUserDialog) {
            AddUserDialog(
                onDismiss = { onToggleAddUserDialog(false) },
                onConfirm = onCreateUser
            )
        }

        // Reset Password Dialog
        if (userToResetPassword != null) {
            ResetPasswordDialog(
                user = userToResetPassword!!,
                onDismiss = { userToResetPassword = null },
                onConfirm = { newPass ->
                    onResetPassword(userToResetPassword!!, newPass)
                    userToResetPassword = null
                }
            )
        }

        // Delete User Confirmation Dialog
        if (userToDelete != null) {
            AlertDialog(
                onDismissRequest = { userToDelete = null },
                containerColor = DramaCardElevated,
                title = { Text("Delete User?", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you sure you want to permanently delete user '${userToDelete?.email}'? This action cannot be undone.",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteUser(userToDelete!!)
                            userToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DramaRed)
                    ) {
                        Text("Delete Permanently")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { userToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun AdminMetricsOverview(
    allUsers: List<UserEntity>,
    allDramas: List<DramaEntity>,
    syncState: SyncState,
    syncSummary: SyncSummary,
    adminProfile: AdminProfile?,
    isAppCheckActive: Boolean,
    appCheckStatus: String,
    onTriggerSync: () -> Unit,
    onPullUpdates: () -> Unit,
    onNavigateToUsers: () -> Unit
) {
    val totalUsers = allUsers.size
    val activeUsers = allUsers.count { it.status == "ACTIVE" }
    val vipUsers = allUsers.count { it.subscriptionTier != "FREE" }
    val totalWatchTimeHours = allUsers.sumOf { it.watchTimeMinutes } / 60
    val totalWatchedEpisodes = allUsers.sumOf { it.episodesWatched }

    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sync_spin"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Authenticated Admin Security & App Check Status Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DramaCard),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AdminPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = AdminPurple,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = adminProfile?.displayName ?: "Administrator",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(DramaRed.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = adminProfile?.authProvider ?: "FIREBASE",
                                        color = DramaRed,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = adminProfile?.email ?: "admin@shortdrama.tv",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // App Check Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF00E676).copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "App Check OK",
                            color = Color(0xFF00E676),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "Key Performance Indicators",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Live telemetry and viewer engagement metrics",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        // Metrics 2x2 Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Users",
                    value = "$totalUsers",
                    subtitle = "$activeUsers active viewers",
                    icon = Icons.Default.SupervisorAccount,
                    accentColor = AdminPurple,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "VIP Subscribers",
                    value = "$vipUsers",
                    subtitle = "Monthly & Annual",
                    icon = Icons.Default.Star,
                    accentColor = DramaGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Dramas",
                    value = "${allDramas.size}",
                    subtitle = "Catalog titles",
                    icon = Icons.Default.Movie,
                    accentColor = DramaAmber,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Stream Time",
                    value = "${totalWatchTimeHours}h",
                    subtitle = "$totalWatchedEpisodes episodes viewed",
                    icon = Icons.Default.TrendingUp,
                    accentColor = StatusActive,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Data Visualization Component (Recharts-style Area & Bar Charts)
        item {
            AdminMetricsChart(
                totalUsersCount = totalUsers,
                vipCount = vipUsers
            )
        }

        // Room-to-Firestore Cloud Synchronization Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DramaCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("firestore_sync_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isSyncing = syncState is SyncState.Syncing
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E5FF).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSyncing) Icons.Default.Sync else Icons.Default.CloudDone,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .then(if (isSyncing) Modifier.rotate(rotation) else Modifier)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Cloud Firestore Synchronization",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = when (syncState) {
                                        is SyncState.Syncing -> syncState.stage
                                        is SyncState.Success -> syncState.message
                                        is SyncState.Error -> "Sync warning: ${syncState.message}"
                                        else -> "Two-way Room DB to Firestore replication"
                                    },
                                    color = when (syncState) {
                                        is SyncState.Error -> DramaRed
                                        is SyncState.Success -> Color(0xFF00E676)
                                        else -> TextSecondary
                                    },
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Status Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DramaCardElevated)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = syncSummary.cloudStatus,
                                color = Color(0xFF00E5FF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sync Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DramaCardElevated)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Synced Entities",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "${syncSummary.totalRecordsSynced} Records",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Last Synchronized",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = if (syncSummary.lastSyncTimestamp > 0) {
                                    SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date(syncSummary.lastSyncTimestamp))
                                } else {
                                    "Pending trigger"
                                },
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onTriggerSync,
                            enabled = syncState !is SyncState.Syncing,
                            colors = ButtonDefaults.buttonColors(containerColor = DramaRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sync_to_firestore_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (syncState is SyncState.Syncing) "Syncing..." else "Sync to Firestore",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onPullUpdates,
                            enabled = syncState !is SyncState.Syncing,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E5FF)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Pull Updates", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ReCaptcha & App Check security note
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Requests verified via Firebase App Check & ReCaptcha Enterprise",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Quick User Management CTA Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DramaCardElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "User Access & Authentication",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Grant admin roles, ban offending accounts, create new accounts, or change subscription tiers.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Button(
                        onClick = onNavigateToUsers,
                        colors = ButtonDefaults.buttonColors(containerColor = AdminPurple),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Manage")
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DramaCard),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextSecondary, fontSize = 12.sp)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AdminUserManagement(
    users: List<UserEntity>,
    searchQuery: String,
    roleFilter: String,
    statusFilter: String,
    onSearchQueryChange: (String) -> Unit,
    onRoleFilterChange: (String) -> Unit,
    onStatusFilterChange: (String) -> Unit,
    onOpenAddUserDialog: () -> Unit,
    onUpdateStatus: (UserEntity, String) -> Unit,
    onUpdateRole: (UserEntity, String) -> Unit,
    onUpdateTier: (UserEntity, String) -> Unit,
    onRequestResetPassword: (UserEntity) -> Unit,
    onRequestDeleteUser: (UserEntity) -> Unit
) {
    val roles = listOf("ALL", "ADMIN", "VIP", "USER")
    val statuses = listOf("ALL", "ACTIVE", "SUSPENDED", "BANNED")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & Add User Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search users by name or email...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextMuted
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DramaCard,
                    unfocusedContainerColor = DramaCard,
                    focusedBorderColor = AdminPurple,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("admin_user_search_input")
            )

            Button(
                onClick = onOpenAddUserDialog,
                colors = ButtonDefaults.buttonColors(containerColor = AdminPurple),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("admin_add_user_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filters row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(roles) { r ->
                val isSel = roleFilter == r
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSel) AdminPurple else DramaCard)
                        .clickable { onRoleFilterChange(r) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (r == "ALL") "All Roles" else r,
                        color = if (isSel) Color.White else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Users List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(users) { user ->
                UserManagementCard(
                    user = user,
                    onUpdateStatus = { s -> onUpdateStatus(user, s) },
                    onUpdateRole = { r -> onUpdateRole(user, r) },
                    onUpdateTier = { t -> onUpdateTier(user, t) },
                    onResetPassword = { onRequestResetPassword(user) },
                    onDelete = { onRequestDeleteUser(user) }
                )
            }
        }
    }
}

@Composable
private fun UserManagementCard(
    user: UserEntity,
    onUpdateStatus: (String) -> Unit,
    onUpdateRole: (String) -> Unit,
    onUpdateTier: (String) -> Unit,
    onResetPassword: () -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = DramaCard),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("admin_user_item_${user.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when (user.role) {
                                    "SUPER_ADMIN", "ADMIN" -> Brush.linearGradient(listOf(AdminPurple, Color(0xFF6D28D9)))
                                    "VIP" -> Brush.linearGradient(listOf(DramaAmber, DramaGold))
                                    else -> Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)))
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = user.name,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = user.email,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Status & Role Badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Role Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (user.role) {
                                    "SUPER_ADMIN", "ADMIN" -> AdminPurple.copy(alpha = 0.2f)
                                    "VIP" -> DramaGold.copy(alpha = 0.2f)
                                    else -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = user.role,
                            color = when (user.role) {
                                "SUPER_ADMIN", "ADMIN" -> AdminPurple
                                "VIP" -> DramaGold
                                else -> Color(0xFF60A5FA)
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (user.status) {
                                    "ACTIVE" -> StatusActive.copy(alpha = 0.2f)
                                    "SUSPENDED" -> StatusSuspended.copy(alpha = 0.2f)
                                    else -> StatusBanned.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = user.status,
                            color = when (user.status) {
                                "ACTIVE" -> StatusActive
                                "SUSPENDED" -> StatusSuspended
                                else -> StatusBanned
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Expanded Admin Action Panel
            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(BorderSubtle)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Watched: ${user.episodesWatched} episodes (${user.watchTimeMinutes}m) • Plan: ${user.subscriptionTier}",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status Action (Ban / Unban / Suspend)
                    OutlinedButton(
                        onClick = {
                            val newStatus = when (user.status) {
                                "ACTIVE" -> "SUSPENDED"
                                "SUSPENDED" -> "BANNED"
                                else -> "ACTIVE"
                            }
                            onUpdateStatus(newStatus)
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = when (user.status) {
                                "ACTIVE" -> "Suspend"
                                "SUSPENDED" -> "Ban"
                                else -> "Activate"
                            },
                            fontSize = 11.sp
                        )
                    }

                    // Role Switcher Action (User <-> VIP <-> Admin)
                    OutlinedButton(
                        onClick = {
                            val newRole = when (user.role) {
                                "USER" -> "VIP"
                                "VIP" -> "ADMIN"
                                else -> "USER"
                            }
                            onUpdateRole(newRole)
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Set: " + when (user.role) {
                                "USER" -> "VIP"
                                "VIP" -> "Admin"
                                else -> "User"
                            },
                            fontSize = 11.sp
                        )
                    }

                    // Reset Password Button
                    OutlinedButton(
                        onClick = onResetPassword,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset Pass", fontSize = 11.sp)
                    }

                    // Delete User Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete User",
                            tint = DramaRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminDramasCatalog(dramas: List<DramaEntity>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Series Catalog (${dramas.size} Dramas)",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        items(dramas) { drama ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DramaCard),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = drama.title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${drama.totalEpisodes} Episodes • ${drama.category} • Rating: ${drama.rating}★",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    if (drama.isFeaturedCover) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DramaAmber)
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "HERO FEATURED",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAuditLogsView(logs: List<AuditLogEntity>) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Security Audit Trail (${logs.size} Events)",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tamper-resistant security log of all admin operations and access events",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        items(logs) { log ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DramaCard),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = log.action,
                            color = AdminPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dateFormat.format(Date(log.timestamp)),
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = "Target: ${log.targetUser} • By: ${log.adminEmail}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = log.details,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddUserDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("USER") }
    var tier by remember { mutableStateOf("FREE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DramaCardElevated,
        shape = RoundedCornerShape(12.dp),
        title = {
            Text(
                text = "Create New User Account",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_user_name_input")
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_user_email_input")
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_user_password_input")
                )

                // Role selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("USER", "VIP", "ADMIN").forEach { r ->
                        val isSel = role == r
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AdminPurple else DramaCard)
                                .clickable { role = r }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = r,
                                color = if (isSel) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        onConfirm(name, email, password, role, tier)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AdminPurple),
                modifier = Modifier.testTag("confirm_create_user_btn")
            ) {
                Text("Create User")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ResetPasswordDialog(
    user: UserEntity,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DramaCardElevated,
        shape = RoundedCornerShape(12.dp),
        title = { Text("Reset Password for ${user.name}", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Enter new secure password for ${user.email}:", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reset_pass_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.isNotBlank()) {
                        onConfirm(newPassword)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AdminPurple)
            ) {
                Text("Update Password")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
