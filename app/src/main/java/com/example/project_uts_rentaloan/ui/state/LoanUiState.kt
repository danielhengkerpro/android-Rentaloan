package com.example.project_uts_rentaloan.ui.state

import com.example.project_uts_rentaloan.data.local.entity.LoanWithDetails

sealed interface LoanUiState {
    object Loading : LoanUiState
    data class Success(val loans: List<LoanWithDetails>) : LoanUiState
    data class Error(val message: String) : LoanUiState
}
