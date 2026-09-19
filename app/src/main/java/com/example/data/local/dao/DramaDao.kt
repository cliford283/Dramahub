package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.DramaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DramaDao {
    @Query("SELECT * FROM dramas")
    fun getAllDramas(): Flow<List<DramaEntity>>

    @Query("SELECT * FROM dramas WHERE isFeaturedCover = 1 LIMIT 1")
    fun getFeaturedDrama(): Flow<DramaEntity?>

    @Query("SELECT COUNT(*) FROM dramas")
    fun getDramaCount(): Flow<Int>

    @Query("SELECT * FROM dramas WHERE id = :id LIMIT 1")
    suspend fun getDramaById(id: String): DramaEntity?

    @Query("SELECT * FROM dramas WHERE title LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchDramas(query: String): Flow<List<DramaEntity>>

    @Query("SELECT * FROM dramas WHERE category = :category")
    fun getDramasByCategory(category: String): Flow<List<DramaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrama(drama: DramaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDramas(dramas: List<DramaEntity>)

    @Update
    suspend fun updateDrama(drama: DramaEntity)

    @Delete
    suspend fun deleteDrama(drama: DramaEntity)
}
