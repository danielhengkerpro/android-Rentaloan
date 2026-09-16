package com.example.project_uts_rentaloan.domain.repository

import com.example.project_uts_rentaloan.data.local.entity.ItemEntity
import com.example.project_uts_rentaloan.data.local.entity.LoanWithDetails
import kotlinx.coroutines.flow.Flow

interface LoanRepository {
    fun getAllLoans(): Flow<List<LoanWithDetails>>
    fun getLoansByUser(email: String): Flow<List<LoanWithDetails>>
    fun getAllItems(): Flow<List<ItemEntity>>
    fun getAvailableItems(): Flow<List<ItemEntity>>
    suspend fun addItem(item: ItemEntity)
    suspend fun updateItem(item: ItemEntity)
    suspend fun deleteItem(item: ItemEntity)
    suspend fun borrowItem(
        item: ItemEntity, 
        borrowerEmail: String, 
        borrowerName: String, 
        borrowerPhone: String,
        startDate: Long,
        plannedReturnDate: Long
    )
    suspend fun returnItem(transactionId: Long, returnDate: Long)
}
