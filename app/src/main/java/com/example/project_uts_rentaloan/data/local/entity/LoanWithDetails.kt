package com.example.project_uts_rentaloan.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Relation class to fetch joined data of a loan transaction, its item, and its borrower.
 */
data class LoanWithDetails(
    @Embedded
    val transaction: LoanTransactionEntity,
    
    @Relation(
        parentColumn = "item_id",
        entityColumn = "id"
    )
    val item: ItemEntity,
    
    @Relation(
        parentColumn = "borrower_id",
        entityColumn = "id"
    )
    val borrower: BorrowerEntity
)
