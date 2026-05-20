package com.example.poe.data

import com.example.poe.util.AppLogger   // ← ADDED IMPORT

class UserRepository(private val userDao: UserDao) {
    suspend fun registerUser(user: User) {
        AppLogger.d("Inserting new user into DB: ${user.username}", "DB")
        userDao.insertUser(user)
    }

    suspend fun login(username: String, password: String): User? {
        AppLogger.i("Login attempt for username: $username", "DB")
        val user = userDao.getUserByUsername(username)
        return if (user?.password == password) {
            AppLogger.i("Login successful for user ID: ${user.id}", "DB")
            user
        } else {
            AppLogger.e("Login failed for username: $username", subTag = "DB")
            null
        }
    }

    suspend fun updateUser(user: User) {
        AppLogger.d("Updating user goals for user ID: ${user.id}", "DB")
        userDao.updateUser(user)
    }
}