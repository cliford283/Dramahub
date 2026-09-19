package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT dramaId FROM favorites WHERE userId = :userId")
    fun getFavoriteDramaIds(userId: Long): Flow<List<String>>

    @Query("SELECT COUNT(*) > 0 FROM favorites WHERE dramaId = :dramaId AND userId = :userId")
    fun isFavorite(dramaId: String, userId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE dramaId = :dramaId AND userId = :userId")
    suspend fun removeFavorite(dramaId: String, userId: Long)
}
