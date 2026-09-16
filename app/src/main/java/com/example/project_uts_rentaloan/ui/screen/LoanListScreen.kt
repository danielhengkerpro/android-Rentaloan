package com.example.project_uts_rentaloan.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.project_uts_rentaloan.data.local.entity.BorrowerEntity
import com.example.project_uts_rentaloan.data.local.entity.ItemEntity
import com.example.project_uts_rentaloan.data.local.entity.LoanTransactionEntity
import com.example.project_uts_rentaloan.data.local.entity.LoanWithDetails
import com.example.project_uts_rentaloan.ui.feature_loan.components.LoanItemCard
import com.example.project_uts_rentaloan.ui.state.LoanUiState
import com.example.project_uts_rentaloan.ui.viewmodel.LoanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanListScreen(
    viewModel: LoanViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LoanListContent(
        uiState = uiState, 
        onReturnClick = viewModel::returnItem, 
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanListContent(
    uiState: LoanUiState,
    onReturnClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Rental Tracker",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (uiState) {
                is LoanUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is LoanUiState.Success -> {
                    val loans = (uiState as LoanUiState.Success).loans
                    if (loans.isEmpty()) {
                        LoanEmptyState(modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(loans) { loan ->
                                LoanItemCard(
                                    loan = loan,
                                    onActionClick = { onReturnClick(loan.transaction.id) }
                                )
                            }
                        }
                    }
                }
                is LoanUiState.Error -> {
                    Text(
                        text = (uiState as LoanUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun LoanEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Assignment,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Belum ada transaksi",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Semua riwayat peminjaman barang akan muncul di sini.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun LoanListPreview() {
    val mockLoans = listOf(
        LoanWithDetails(
            transaction = LoanTransactionEntity(
                id = 1,
                item_id = 1,
                borrower_id = 1,
                tgl_pinjam = System.currentTimeMillis() - 86400000,
                status_pinjam = "DIPINJAM"
            ),
            item = ItemEntity(1, "Kamera Sony A7III"),
            borrower = BorrowerEntity(1, "Budi Sudarsono", "08123456789")
        ),
        LoanWithDetails(
            transaction = LoanTransactionEntity(
                id = 2,
                item_id = 2,
                borrower_id = 2,
                tgl_pinjam = System.currentTimeMillis() - 172800000,
                tgl_kembali = System.currentTimeMillis() - 86400000,
                status_pinjam = "DIKEMBALIKAN"
            ),
            item = ItemEntity(2, "Lensa 50mm f1.8"),
            borrower = BorrowerEntity(2, "Siti Aminah", "08987654321")
        )
    )
    MaterialTheme {
        LoanListContent(
            uiState = LoanUiState.Success(mockLoans),
            onReturnClick = {}
        )
    }
}
