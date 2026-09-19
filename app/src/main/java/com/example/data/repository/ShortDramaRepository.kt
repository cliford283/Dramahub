package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.DramaEntity
import com.example.data.local.entity.FavoriteEntity
import com.example.data.local.entity.UserEntity
import com.example.data.local.entity.WatchHistoryEntity
import com.example.data.security.SecurityHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ShortDramaRepository(private val database: AppDatabase) {

    val allDramas: Flow<List<DramaEntity>> = database.dramaDao().getAllDramas()
    val allUsers: Flow<List<UserEntity>> = database.userDao().getAllUsers()
    val featuredDrama: Flow<DramaEntity?> = database.dramaDao().getFeaturedDrama()
    val userCount: Flow<Int> = database.userDao().getUserCount()
    val activeUserCount: Flow<Int> = database.userDao().getActiveUserCount()
    val vipUserCount: Flow<Int> = database.userDao().getVipUserCount()
    val dramaCount: Flow<Int> = database.dramaDao().getDramaCount()
    val recentAuditLogs: Flow<List<AuditLogEntity>> = database.auditLogDao().getRecentLogs()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfNeeded()
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        val existingDramas = database.dramaDao().getAllDramas().firstOrNull()
        if (existingDramas.isNullOrEmpty()) {
            val initialDramas = listOf(
                DramaEntity(
                    id = "mafia_don_wife",
                    title = "The Mafia Don Hired Me As His Wife",
                    description = "Dumped by her ex-husband for her looks and left with little money, Nora responds to a 'step-mother wanted' notice and arrives at Mafia boss Luca's estate. She enters a contractual marriage with him to soothe his traumatized young son Leo. While helping Leo regain security, Nora heals her own low self-worth, and sincere romantic feelings slowly develop between Nora and Luca.",
                    coverDrawableResName = "mafia_don_cover_1789802199526",
                    totalEpisodes = 143,
                    tags = "CONTRACT LOVE,FAMILY DRAMA,SWEET ROMANCE",
                    category = "TRENDING",
                    isFeaturedCover = true,
                    rating = 4.9f,
                    viewCount = 3820000L
                ),
                DramaEntity(
                    id = "she_quit_140m",
                    title = "She Quit and Told the 140M Deal",
                    description = "Dumped by her ungrateful executive fiancé after securing the mega-corporation's landmark $140M takeover, senior strategist Maya hands in her resignation. She joins hands with the enigmatic venture capitalist billionaire Vincent Vance, triggering high-stakes corporate warfare and irresistible mutual attraction.",
                    coverDrawableResName = "business_romance_cover_1789802210713",
                    totalEpisodes = 88,
                    tags = "CEO,BILLIONAIRE,REVENGE,SWEET LOVE",
                    category = "NEW",
                    rating = 4.8f,
                    viewCount = 2100000L
                ),
                DramaEntity(
                    id = "dragon_queen",
                    title = "Kneel Before The Dragon Queen",
                    description = "Cast into exile as a disgraced princess on a misty medieval dock, Valery awakens the legendary silver Storm Dragon from the abyssal ocean. Returning to the imperial high court, she forces the treacherous usurpers to kneel beneath roaring dragon fire.",
                    coverDrawableResName = "dragon_queen_cover_1789802223760",
                    totalEpisodes = 60,
                    tags = "FANTASY,DRAGON,ROYALTY,MAGIC",
                    category = "FANTASY",
                    rating = 4.9f,
                    viewCount = 4500000L
                ),
                DramaEntity(
                    id = "divine_mage",
                    title = "The Exiled Heir Is the Divine Mage",
                    description = "Valdon was stripped of his royal birthright and tossed into the gladiatorial colosseum arena to perish. What his treacherous brothers did not foresee is that the ancient celestial mage dynasty bloodline awoke within him.",
                    coverDrawableResName = "divine_mage_cover_1789802233959",
                    totalEpisodes = 54,
                    tags = "FANTASY,ACTION,MARTIAL ARTS,REBIRTH",
                    category = "HOTLIST",
                    rating = 4.7f,
                    viewCount = 1890000L
                ),
                DramaEntity(
                    id = "weakest_bastard",
                    title = "Weakest Bastard Shakes the World",
                    description = "Born without a spiritual core and mocked by his entire sect as useless trash, a sudden celestial strike awakens an ancient forbidden god realm within his soul.",
                    coverDrawableResName = "divine_mage_cover_1789802233959",
                    totalEpisodes = 45,
                    tags = "MARTIAL ARTS,REVENGE,LEVEL UP",
                    category = "HOTLIST",
                    isDownloaded = true,
                    rating = 4.9f,
                    viewCount = 3100000L
                ),
                DramaEntity(
                    id = "billion_dollar_empire",
                    title = "Billion Dollar Empire: Stolen Heir",
                    description = "Swapped in the maternity ward 25 years ago, a humble delivery driver discovers he is the sole heir to Manhattan's wealthiest family estate.",
                    coverDrawableResName = "business_romance_cover_1789802210713",
                    totalEpisodes = 53,
                    tags = "CEO,FAMILY DRAMA,REVENGE,SWEET ROMANCE",
                    category = "TRENDING",
                    rating = 4.6f,
                    viewCount = 1450000L
                ),
                DramaEntity(
                    id = "outcast_queen",
                    title = "The Rise of the Outcast Queen",
                    description = "A castaway maiden disguised as a royal scribe outsmarts political court assassins to claim her rightful throne and capture the heart of the emperor.",
                    coverDrawableResName = "mafia_don_cover_1789802199526",
                    totalEpisodes = 65,
                    tags = "ROYALTY,PALACE,DRAMA,LOVE",
                    category = "NEW",
                    rating = 4.8f,
                    viewCount = 2800000L
                ),
                DramaEntity(
                    id = "hired_killer_dad",
                    title = "I Hired a Killer to Be My Dad",
                    description = "Tired of being targeted by school bullies, 8-year-old Toby used his entire piggy bank savings to hire the underworld's most feared phantom hitman as his surrogate father.",
                    coverDrawableResName = "mafia_don_cover_1789802199526",
                    totalEpisodes = 32,
                    tags = "FAMILY,COMEDY,SUSPENSE,PROTECTION",
                    category = "HOTLIST",
                    rating = 4.9f,
                    viewCount = 5200000L
                ),
                DramaEntity(
                    id = "boxing_champion",
                    title = "The 7-Year-Old Boxing Champion",
                    description = "Born with legendary martial instincts, a tiny prodigy steps into the underground ring to save his injured father's boxing gym from corrupt syndicate bosses.",
                    coverDrawableResName = "divine_mage_cover_1789802233959",
                    totalEpisodes = 59,
                    tags = "ACTION,INSPIRATIONAL,FAMILY",
                    category = "TRENDING",
                    rating = 4.7f,
                    viewCount = 1950000L
                ),
                DramaEntity(
                    id = "contract_wife_don",
                    title = "The Don's Contract Wife to True Heiress",
                    description = "Bound by a 1-year secret marriage agreement with the fearsome head of the Luciano family, she conceals her true identity as the world's most elusive diamond heiress.",
                    coverDrawableResName = "mafia_don_cover_1789802199526",
                    totalEpisodes = 56,
                    tags = "CONTRACT LOVE,MAFIA,HEIRESS,ROMANCE",
                    category = "NEW",
                    rating = 4.9f,
                    viewCount = 4100000L
                )
            )
            database.dramaDao().insertDramas(initialDramas)
        }

        // Seed initial users if empty
        val existingUsers = database.userDao().getAllUsers().firstOrNull()
        if (existingUsers.isNullOrEmpty()) {
            val adminSalt = SecurityHelper.generateSalt()
            val adminHash = SecurityHelper.hashPassword("Admin@12345", adminSalt)

            val user1Salt = SecurityHelper.generateSalt()
            val user1Hash = SecurityHelper.hashPassword("User@123", user1Salt)

            val initialUsers = listOf(
                UserEntity(
                    name = "Super Admin (Console)",
                    email = "admin@shortdrama.tv",
                    passwordHash = adminHash,
                    salt = adminSalt,
                    role = "SUPER_ADMIN",
                    status = "ACTIVE",
                    subscriptionTier = "VIP_ANNUAL",
                    episodesWatched = 420,
                    watchTimeMinutes = 1840,
                    lastActiveTime = System.currentTimeMillis()
                ),
                UserEntity(
                    name = "Cliford Mulumba",
                    email = "clifordmulumba@gmail.com",
                    passwordHash = user1Hash,
                    salt = user1Salt,
                    role = "ADMIN",
                    status = "ACTIVE",
                    subscriptionTier = "VIP_ANNUAL",
                    episodesWatched = 186,
                    watchTimeMinutes = 890,
                    lastActiveTime = System.currentTimeMillis() - 1000 * 60 * 15
                ),
                UserEntity(
                    name = "Sarah Jenkins",
                    email = "sarah.j@hollywood.com",
                    passwordHash = user1Hash,
                    salt = user1Salt,
                    role = "VIP",
                    status = "ACTIVE",
                    subscriptionTier = "VIP_MONTHLY",
                    episodesWatched = 94,
                    watchTimeMinutes = 430,
                    lastActiveTime = System.currentTimeMillis() - 1000 * 60 * 120
                ),
                UserEntity(
                    name = "David Choi",
                    email = "david.choi@media.net",
                    passwordHash = user1Hash,
                    salt = user1Salt,
                    role = "USER",
                    status = "ACTIVE",
                    subscriptionTier = "FREE",
                    episodesWatched = 23,
                    watchTimeMinutes = 95,
                    lastActiveTime = System.currentTimeMillis() - 1000 * 60 * 600
                ),
                UserEntity(
                    name = "Alex Morgan",
                    email = "alex.morgan@test.com",
                    passwordHash = user1Hash,
                    salt = user1Salt,
                    role = "USER",
                    status = "SUSPENDED",
                    subscriptionTier = "FREE",
                    episodesWatched = 7,
                    watchTimeMinutes = 28,
                    lastActiveTime = System.currentTimeMillis() - 1000 * 60 * 1440
                )
            )
            database.userDao().insertUsers(initialUsers)

            // Seed initial favorites
            database.favoriteDao().addFavorite(FavoriteEntity("mafia_don_wife", 2L))
            database.favoriteDao().addFavorite(FavoriteEntity("outcast_queen", 2L))

            // Seed initial audit log
            database.auditLogDao().insertLog(
                AuditLogEntity(
                    adminEmail = "system@shortdrama.tv",
                    action = "SYSTEM_INITIALIZED",
                    targetUser = "System",
                    details = "Platform initialized with secure auth and seeded user management database."
                )
            )
        }
    }

    // Drama Queries
    suspend fun getDramaById(id: String): DramaEntity? = database.dramaDao().getDramaById(id)

    fun searchDramas(query: String): Flow<List<DramaEntity>> = database.dramaDao().searchDramas(query)

    fun getDramasByCategory(category: String): Flow<List<DramaEntity>> =
        database.dramaDao().getDramasByCategory(category)

    suspend fun insertDrama(drama: DramaEntity) = database.dramaDao().insertDrama(drama)
    suspend fun updateDrama(drama: DramaEntity) = database.dramaDao().updateDrama(drama)
    suspend fun deleteDrama(drama: DramaEntity) = database.dramaDao().deleteDrama(drama)

    // Favorites
    fun getFavoriteIds(userId: Long): Flow<List<String>> = database.favoriteDao().getFavoriteDramaIds(userId)

    fun isFavorite(dramaId: String, userId: Long): Flow<Boolean> =
        database.favoriteDao().isFavorite(dramaId, userId)

    suspend fun toggleFavorite(dramaId: String, userId: Long, currentStatus: Boolean) {
        if (currentStatus) {
            database.favoriteDao().removeFavorite(dramaId, userId)
        } else {
            database.favoriteDao().addFavorite(FavoriteEntity(dramaId, userId))
        }
    }

    // Watch History
    fun getWatchHistory(userId: Long): Flow<List<WatchHistoryEntity>> =
        database.watchHistoryDao().getWatchHistory(userId)

    suspend fun saveWatchProgress(userId: Long, dramaId: String, episodeNumber: Int, progressSec: Int, totalSec: Int) {
        database.watchHistoryDao().saveProgress(
            WatchHistoryEntity(
                userId = userId,
                dramaId = dramaId,
                episodeNumber = episodeNumber,
                progressSeconds = progressSec,
                totalDurationSeconds = totalSec
            )
        )
    }

    // User Management (Admin Dashboard)
    suspend fun getUserByEmail(email: String): UserEntity? = database.userDao().getUserByEmail(email)

    suspend fun authenticateUser(email: String, passwordAttempt: String): UserEntity? {
        val user = database.userDao().getUserByEmail(email.trim().lowercase()) ?: return null
        val verified = SecurityHelper.verifyPassword(passwordAttempt, user.salt, user.passwordHash)
        return if (verified) user else null
    }

    suspend fun createUser(
        name: String,
        email: String,
        rawPassword: String,
        role: String,
        tier: String,
        creatorEmail: String
    ): Long {
        val salt = SecurityHelper.generateSalt()
        val hash = SecurityHelper.hashPassword(rawPassword, salt)
        val user = UserEntity(
            name = name,
            email = email.trim().lowercase(),
            passwordHash = hash,
            salt = salt,
            role = role,
            status = "ACTIVE",
            subscriptionTier = tier
        )
        val newId = database.userDao().insertUser(user)
        database.auditLogDao().insertLog(
            AuditLogEntity(
                adminEmail = creatorEmail,
                action = "USER_CREATED",
                targetUser = email,
                details = "Created user with role $role and tier $tier"
            )
        )
        return newId
    }

    suspend fun updateUser(user: UserEntity, adminEmail: String) {
        database.userDao().updateUser(user)
        database.auditLogDao().insertLog(
            AuditLogEntity(
                adminEmail = adminEmail,
                action = "USER_UPDATED",
                targetUser = user.email,
                details = "Updated user profile: Role=${user.role}, Status=${user.status}, Tier=${user.subscriptionTier}"
            )
        )
    }

    suspend fun updateUserStatus(userId: Long, email: String, newStatus: String, adminEmail: String) {
        database.userDao().updateUserStatus(userId, newStatus)
        database.auditLogDao().insertLog(
            AuditLogEntity(
                adminEmail = adminEmail,
                action = "USER_STATUS_CHANGE",
                targetUser = email,
                details = "Changed user status to $newStatus"
            )
        )
    }

    suspend fun updateUserRole(userId: Long, email: String, newRole: String, adminEmail: String) {
        database.userDao().updateUserRole(userId, newRole)
        database.auditLogDao().insertLog(
            AuditLogEntity(
                adminEmail = adminEmail,
                action = "USER_ROLE_CHANGE",
                targetUser = email,
                details = "Changed user role to $newRole"
            )
        )
    }

    suspend fun updateUserTier(userId: Long, email: String, newTier: String, adminEmail: String) {
        database.userDao().updateUserTier(userId, newTier)
        database.auditLogDao().insertLog(
            AuditLogEntity(
                adminEmail = adminEmail,
                action = "SUBSCRIPTION_TIER_CHANGE",
                targetUser = email,
                details = "Changed subscription tier to $newTier"
            )
        )
    }

    suspend fun resetUserPassword(userId: Long, email: String, newPassword: String, adminEmail: String) {
        val salt = SecurityHelper.generateSalt()
        val hash = SecurityHelper.hashPassword(newPassword, salt)
        database.userDao().resetPassword(userId, hash, salt)
        database.auditLogDao().insertLog(
            AuditLogEntity(
                adminEmail = adminEmail,
                action = "PASSWORD_RESET",
                targetUser = email,
                details = "Admin triggered secure password reset"
            )
        )
    }

    suspend fun deleteUser(user: UserEntity, adminEmail: String) {
        database.userDao().deleteUser(user)
        database.auditLogDao().insertLog(
            AuditLogEntity(
                adminEmail = adminEmail,
                action = "USER_DELETED",
                targetUser = user.email,
                details = "Deleted user account permanently"
            )
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: ShortDramaRepository? = null

        fun getInstance(context: Context): ShortDramaRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val instance = ShortDramaRepository(db)
                INSTANCE = instance
                instance
            }
        }
    }
}
