package com.ltsw.animo.data

import com.ltsw.animo.data.database.UserDao
import com.ltsw.animo.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val loggedInUser: Flow<User?> = userDao.getLoggedInUser()

    suspend fun getLoggedInUserOnce(): User? = userDao.getLoggedInUserOnce()

    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)

    suspend fun registerUser(name: String, email: String): Long {
        // Logout all users first
        userDao.logoutAllUsers()

        // Check if user exists
        val existingUser = userDao.getUserByEmail(email)

        return if (existingUser != null) {
            // Login existing user
            userDao.loginUser(existingUser.id)
            existingUser.id
        } else {
            // Create new user
            val newUser = User(
                name = name,
                email = email,
                isLoggedIn = true
            )
            userDao.insertUser(newUser)
        }
    }

    suspend fun loginUser(email: String): Boolean {
        val user = userDao.getUserByEmail(email)
        return if (user != null) {
            userDao.logoutAllUsers()
            userDao.loginUser(user.id)
            true
        } else {
            false
        }
    }

    suspend fun logoutUser() {
        userDao.logoutAllUsers()
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(userId: Long) {
        userDao.deleteUser(userId)
    }
}

