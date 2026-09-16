package com.example.project_uts_rentaloan.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_uts_rentaloan.ui.components.ImageZoomDialog
import com.example.project_uts_rentaloan.ui.feature_loan.components.LoanItemCard
import com.example.project_uts_rentaloan.ui.theme.*
import com.example.project_uts_rentaloan.ui.viewmodel.AuthViewModel
import com.example.project_uts_rentaloan.ui.viewmodel.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSection(
    viewModel: ItemViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val loans by if (currentUser != null) {
        viewModel.getLoansByUser(currentUser!!.email).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }
    var zoomedImageUri by remember { mutableStateOf<String?>(null) }

    val activeLoans = loans.filter { it.transaction.status_pinjam == "DIPINJAM" }
    val historyLoans = loans.filter { it.transaction.status_pinjam == "DIKEMBALIKAN" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeoYellow)
    ) {
        NeoCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            backgroundColor = NeoWhite,
            shadowOffset = 2.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "DASHBOARD SAYA",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeLoans.isNotEmpty()) {
                item {
                    Text(
                        text = "SEDANG DIPINJAM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = NeoBlack,
                        modifier = Modifier
                            .background(NeoOrange)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                items(activeLoans) { loan ->
                    LoanItemCard(
                        loan = loan,
                        onActionClick = { viewModel.returnItem(loan.transaction.id) },
                        actionButtonText = "KEMBALIKAN",
                        onImageClick = { zoomedImageUri = it }
                    )
                }
            }

            if (historyLoans.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "RIWAYAT PEMINJAMAN",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = NeoBlack,
                        modifier = Modifier
                            .background(NeoBlue)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                items(historyLoans) { loan ->
                    LoanItemCard(
                        loan = loan,
                        onActionClick = { /* No action for history */ },
                        onImageClick = { zoomedImageUri = it }
                    )
                }
            }

            if (loans.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada aktivitas peminjaman.")
                    }
                }
            }
        }
    }

    if (zoomedImageUri != null) {
        ImageZoomDialog(imgUri = zoomedImageUri!!, onDismiss = { zoomedImageUri = null })
    }
}
