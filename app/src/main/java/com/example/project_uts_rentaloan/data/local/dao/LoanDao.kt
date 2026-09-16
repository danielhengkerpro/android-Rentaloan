package com.example.project_uts_rentaloan.data.local.dao

import androidx.room.*
import com.example.project_uts_rentaloan.data.local.entity.BorrowerEntity
import com.example.project_uts_rentaloan.data.local.entity.ItemEntity
import com.example.project_uts_rentaloan.data.local.entity.LoanTransactionEntity
import com.example.project_uts_rentaloan.data.local.entity.LoanWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {

    @Query("SELECT * FROM items WHERE status_tersedia = 1")
    fun getAvailableItems(): Flow<List<ItemEntity>>

    @Transaction
    @Query("SELECT * FROM loan_transactions ORDER BY tgl_pinjam DESC")
    fun getAllLoansWithDetails(): Flow<List<LoanWithDetails>>

    @Query("SELECT * FROM loan_transactions WHERE borrower_id = (SELECT id FROM borrowers WHERE email = :userEmail) ORDER BY tgl_pinjam DESC")
    fun getLoansByUser(userEmail: String): Flow<List<LoanWithDetails>>

    @Query("SELECT * FROM items")
    fun getAllItems(): Flow<List<ItemEntity>>

    @Insert
    suspend fun insertItem(item: ItemEntity)

    @Insert
    suspend fun insertBorrower(borrower: BorrowerEntity): Long

    @Query("SELECT * FROM borrowers WHERE email = :email LIMIT 1")
    suspend fun getBorrowerByEmail(email: String): BorrowerEntity?

    @Insert
    suspend fun insertTransaction(transaction: LoanTransactionEntity)

    @Update
    suspend fun updateItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)

    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItemById(itemId: Long): ItemEntity?

    @Query("SELECT * FROM loan_transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: Long): LoanTransactionEntity?

    /**
     * Atomic transaction to create a new loan.
     * Inserts the transaction and marks the item as unavailable.
     */
    @Transaction
    suspend fun createLoanTransaction(transaction: LoanTransactionEntity) {
        val item = getItemById(transaction.item_id)
        if (item != null && item.status_tersedia) {
            insertTransaction(transaction)
            updateItem(item.copy(status_tersedia = false))
        } else {
            throw IllegalStateException("Item not available for loan")
        }
    }

    /**
     * Atomic transaction to return an item.
     * Updates transaction status, sets return date, and marks item as available.
     */
    @Transaction
    suspend fun returnItemTransaction(transactionId: Long, tglKembali: Long) {
        val transaction = getTransactionById(transactionId)
        if (transaction != null && transaction.status_pinjam == "DIPINJAM") {
            // Update transaction
            val updatedTransaction = transaction.copy(
                tgl_kembali = tglKembali,
                status_pinjam = "DIKEMBALIKAN"
            )
            updateTransaction(updatedTransaction)

            // Update item status
            val item = getItemById(transaction.item_id)
            if (item != null) {
                updateItem(item.copy(status_tersedia = true))
            }
        }
    }

    @Update
    suspend fun updateTransaction(transaction: LoanTransactionEntity)
}
