package com.example.poe.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Insert
    suspend fun insertBadge(badge: Badge)

    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<Badge>>

    @Query("UPDATE badges SET achieved = 1 WHERE id = :badgeId")
    suspend fun unlockBadge(badgeId: Int)
}
