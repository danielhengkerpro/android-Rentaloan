package com.example.project_uts_rentaloan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.project_uts_rentaloan.data.local.SessionManager
import com.example.project_uts_rentaloan.data.local.database.AppDatabase
import com.example.project_uts_rentaloan.data.repository.AuthRepositoryImpl
import com.example.project_uts_rentaloan.data.repository.LoanRepositoryImpl
import com.example.project_uts_rentaloan.ui.navigation.NavGraph
import com.example.project_uts_rentaloan.ui.viewmodel.AuthViewModel
import com.example.project_uts_rentaloan.ui.viewmodel.ItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val database = AppDatabase.getDatabase(this)
        val sessionManager = SessionManager(this)
        val authRepository = AuthRepositoryImpl(database.userDao(), sessionManager)
        val loanRepository = LoanRepositoryImpl(database.loanDao())
        
        val authViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(authRepository) as T
            }
        }
        val itemViewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ItemViewModel(loanRepository) as T
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
                    val itemViewModel: ItemViewModel = viewModel(factory = itemViewModelFactory)
                    
                    NavGraph(
                        navController = navController,
                        authViewModel = authViewModel,
                        itemViewModel = itemViewModel
                    )
                }
            }
        }
    }
}
