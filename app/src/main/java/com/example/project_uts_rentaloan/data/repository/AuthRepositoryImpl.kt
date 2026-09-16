package com.example.project_uts_rentaloan.data.repository

import com.example.project_uts_rentaloan.data.local.SessionManager
import com.example.project_uts_rentaloan.data.local.dao.UserDao
import com.example.project_uts_rentaloan.data.local.entity.UserEntity
import com.example.project_uts_rentaloan.data.util.PasswordHasher
import com.example.project_uts_rentaloan.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<UserEntity> {
        // Built-in Admin bypass
        if (email == "admin@gmail.com" && password == "admin123") {
            val admin = UserEntity(
                email = "admin@gmail.com",
                password = "admin123",
                nama = "Administrator",
                kontak = "081122334455",
                role = "ADMIN"
            )
            sessionManager.saveSession(admin.email, admin.role)
            return Result.success(admin)
        }

        val user = userDao.getUserByEmail(email)
        return if (user != null && PasswordHasher.check(password, user.password)) {
            sessionManager.saveSession(user.email, user.role)
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid email or password"))
        }
    }

    override suspend fun register(user: UserEntity): Result<Unit> {
        return try {
            val hashedUser = user.copy(password = PasswordHasher.hash(user.password))
            userDao.registerUser(hashedUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        sessionManager.clearSession()
    }

    override fun getSessionEmail(): Flow<String?> = sessionManager.userEmail

    override fun getSessionRole(): Flow<String?> = sessionManager.userRole

    override suspend fun getUserByEmail(email: String): UserEntity? {
        if (email == "admin@gmail.com") {
            return UserEntity(
                email = "admin@gmail.com",
                password = "admin123",
                nama = "Administrator",
                kontak = "081122334455",
                role = "ADMIN"
            )
        }
        return userDao.getUserByEmail(email)
    }
}
