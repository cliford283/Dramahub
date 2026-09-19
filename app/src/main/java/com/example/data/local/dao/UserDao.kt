package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY id ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM users WHERE status = 'ACTIVE'")
    fun getActiveUserCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM users WHERE subscriptionTier LIKE 'VIP%'")
    fun getVipUserCount(): Flow<Int>

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET status = :newStatus WHERE id = :userId")
    suspend fun updateUserStatus(userId: Long, newStatus: String)

    @Query("UPDATE users SET role = :newRole WHERE id = :userId")
    suspend fun updateUserRole(userId: Long, newRole: String)

    @Query("UPDATE users SET subscriptionTier = :newTier WHERE id = :userId")
    suspend fun updateUserTier(userId: Long, newTier: String)

    @Query("UPDATE users SET passwordHash = :newPasswordHash, salt = :newSalt WHERE id = :userId")
    suspend fun resetPassword(userId: Long, newPasswordHash: String, newSalt: String)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}
