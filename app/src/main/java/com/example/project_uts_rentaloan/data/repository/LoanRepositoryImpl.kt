package com.example.project_uts_rentaloan.data.repository

import com.example.project_uts_rentaloan.data.local.dao.LoanDao
import com.example.project_uts_rentaloan.data.local.entity.*
import com.example.project_uts_rentaloan.domain.repository.LoanRepository
import kotlinx.coroutines.flow.Flow

class LoanRepositoryImpl(private val loanDao: LoanDao) : LoanRepository {
    override fun getAllLoans(): Flow<List<LoanWithDetails>> = loanDao.getAllLoansWithDetails()

    override fun getLoansByUser(email: String): Flow<List<LoanWithDetails>> = loanDao.getLoansByUser(email)

    override fun getAllItems(): Flow<List<ItemEntity>> = loanDao.getAllItems()

    override fun getAvailableItems(): Flow<List<ItemEntity>> = loanDao.getAvailableItems()

    override suspend fun addItem(item: ItemEntity) {
        loanDao.insertItem(item)
    }

    override suspend fun updateItem(item: ItemEntity) {
        loanDao.updateItem(item)
    }

    override suspend fun deleteItem(item: ItemEntity) {
        loanDao.deleteItem(item)
    }

    override suspend fun borrowItem(
        item: ItemEntity,
        borrowerEmail: String,
        borrowerName: String,
        borrowerPhone: String,
        startDate: Long,
        plannedReturnDate: Long
    ) {
        var borrower = loanDao.getBorrowerByEmail(borrowerEmail)
        if (borrower == null) {
            val borrowerId = loanDao.insertBorrower(
                BorrowerEntity(
                    nama_penyewa = borrowerName,
                    kontak = borrowerPhone,
                    email = borrowerEmail
                )
            )
            borrower = BorrowerEntity(id = borrowerId, nama_penyewa = borrowerName, kontak = borrowerPhone, email = borrowerEmail)
        }

        val transaction = LoanTransactionEntity(
            item_id = item.id,
            borrower_id = borrower.id,
            tgl_pinjam = startDate,
            tgl_kembali_rencana = plannedReturnDate,
            status_pinjam = "DIPINJAM"
        )
        loanDao.createLoanTransaction(transaction)
    }

    override suspend fun returnItem(transactionId: Long, returnDate: Long) {
        loanDao.returnItemTransaction(transactionId, returnDate)
    }
}
