package com.example.project_uts_rentaloan.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ItemEntity represents a physical item available for rent or loan.
 */
@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nama_barang: String,
    val deskripsi: String = "",
    val lokasi: String = "",
    val kontak: String = "",
    val gambar_uri: String? = null,
    val status_tersedia: Boolean = true
)
