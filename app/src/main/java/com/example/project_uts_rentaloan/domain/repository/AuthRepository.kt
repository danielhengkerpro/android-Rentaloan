package com.example.project_uts_rentaloan.domain.repository

import com.example.project_uts_rentaloan.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<UserEntity>
    suspend fun register(user: UserEntity): Result<Unit>
    suspend fun logout()
    fun getSessionEmail(): Flow<String?>
    fun getSessionRole(): Flow<String?>
    suspend fun getUserByEmail(email: String): UserEntity?
}
