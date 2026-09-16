package com.example.project_uts_rentaloan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * BorrowerEntity represents a person who borrows or rents an item.
 */
@Entity(tableName = "borrowers")
data class BorrowerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nama_penyewa: String,
    val kontak: String,
    val email: String = ""
)
