package com.example.project_uts_rentaloan.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * LoanTransactionEntity links an item to a borrower with rental details.
 */
@Entity(
    tableName = "loan_transactions",
    foreignKeys = [
        ForeignKey(
            entity = ItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["item_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BorrowerEntity::class,
            parentColumns = ["id"],
            childColumns = ["borrower_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("item_id"), Index("borrower_id")]
)
data class LoanTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val item_id: Long,
    val borrower_id: Long,
    val tgl_pinjam: Long, // Epoch milliseconds
    val tgl_kembali_rencana: Long? = null, // Planned return date
    val tgl_kembali: Long? = null, // Epoch milliseconds, null if still borrowed
    val status_pinjam: String // "DIPINJAM", "DIKEMBALIKAN"
)
