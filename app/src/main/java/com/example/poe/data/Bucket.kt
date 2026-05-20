package com.example.poe.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Data class representing a financial "Bucket" entity in the Room database.
 * Buckets can be used to group transactions or categories into specific savings or budget goals.
 * Linked to a [User] through a foreign key.
 */
@Entity(
    tableName = "buckets",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Bucket(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val name: String
)
