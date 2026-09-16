package com.example.project_uts_rentaloan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_uts_rentaloan.domain.repository.LoanRepository
import com.example.project_uts_rentaloan.ui.state.LoanUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the Loan Tracker UI.
 * Handles data flow from the repository and manages the UI state.
 */
class LoanViewModel(private val repository: LoanRepository) : ViewModel() {

    // Main UI State for the Loan screen
    val uiState: StateFlow<LoanUiState> = repository.getAllLoans()
        .map { loans -> 
            LoanUiState.Success(loans) as LoanUiState 
        }
        .onStart { emit(LoanUiState.Loading) }
        .catch { e -> emit(LoanUiState.Error(e.message ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LoanUiState.Loading
        )

    /**
     * Processes the return of a borrowed item.
     */
    fun returnItem(transactionId: Long) {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                repository.returnItem(transactionId, currentTime)
            } catch (e: Exception) {
                // In a real app, we might want to show a Snackbar or similar
                println("Error returning item: ${e.message}")
            }
        }
    }
}
