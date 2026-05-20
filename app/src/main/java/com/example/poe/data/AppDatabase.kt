package com.example.poe.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The Room Database for the application.
 * Defines the database configuration and serves as the main access point for the persisted data.
 * Includes entities for Users, Categories, Transactions, and Buckets.
 */
@Database(
    entities = [User::class, Category::class, Transaction::class, Bucket::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // Data Access Objects (DAOs) for various entities
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        /**
         * Singleton pattern to ensure only one instance of the database exists.
         * uses destructive migration to simplify schema changes during development.
         */
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration() // Simple for development, wipes DB on schema change
                .build()
                .also { Instance = it }
            }
        }
    }
}
