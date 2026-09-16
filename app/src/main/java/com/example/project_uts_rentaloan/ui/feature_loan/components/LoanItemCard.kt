package com.example.project_uts_rentaloan.ui.feature_loan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.project_uts_rentaloan.data.local.entity.LoanWithDetails
import com.example.project_uts_rentaloan.ui.components.StatusBadge
import com.example.project_uts_rentaloan.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LoanItemCard(
    loan: LoanWithDetails,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    actionButtonText: String? = null,
    onImageClick: ((String) -> Unit)? = null
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val tglPinjam = dateFormat.format(Date(loan.transaction.tgl_pinjam))
    val isBorrowed = loan.transaction.status_pinjam == "DIPINJAM"

    NeoCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = NeoWhite,
        shadowOffset = 6.dp
    ) {
        Column {
            if (loan.item.gambar_uri != null) {
                Box {
                    AsyncImage(
                        model = loan.item.gambar_uri,
                        contentDescription = loan.item.nama_barang,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .then(
                                if (onImageClick != null) Modifier.clickable { onImageClick(loan.item.gambar_uri) }
                                else Modifier
                            ),
                        contentScale = ContentScale.Crop
                    )
                    // Bottom border for image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(NeoBlack)
                            .align(Alignment.BottomCenter)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = loan.item.nama_barang.uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            color = NeoBlack,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "PENYEWA: ${loan.borrower.nama_penyewa.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeoBlack.copy(alpha = 0.7f)
                        )
                    }
                    
                    StatusBadge(status = loan.transaction.status_pinjam)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NeoYellow.copy(alpha = 0.2f))
                        .border(1.dp, NeoBlack)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (loan.item.kontak.isNotEmpty()) {
                        InfoRow(label = "KONTAK ADMIN", value = loan.item.kontak)
                    }
                    InfoRow(label = "TANGGAL PINJAM", value = tglPinjam.uppercase())
                    
                    if (isBorrowed && loan.transaction.tgl_kembali_rencana != null) {
                        val tglRencana = dateFormat.format(Date(loan.transaction.tgl_kembali_rencana))
                        InfoRow(label = "RENCANA KEMBALI", value = tglRencana.uppercase())
                    }
                    
                    if (!isBorrowed && loan.transaction.tgl_kembali != null) {
                        val tglKembali = dateFormat.format(Date(loan.transaction.tgl_kembali))
                        InfoRow(label = "DIKEMBALIKAN PADA", value = tglKembali.uppercase())
                    }
                }

                if (isBorrowed && actionButtonText != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    NeoButton(
                        onClick = onActionClick,
                        modifier = Modifier.fillMaxWidth(),
                        text = actionButtonText.uppercase(),
                        backgroundColor = NeoOrange,
                        isFullWidth = true
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = NeoBlack,
            fontWeight = FontWeight.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = NeoBlack
        )
    }
}
