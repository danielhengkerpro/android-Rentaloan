package com.example.project_uts_rentaloan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val password: String,
    val nama: String,
    val kontak: String,
    val role: String // "ADMIN" or "USER"
)
