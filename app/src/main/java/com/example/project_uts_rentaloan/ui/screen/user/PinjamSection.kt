package com.example.project_uts_rentaloan.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project_uts_rentaloan.data.local.entity.ItemEntity
import com.example.project_uts_rentaloan.ui.components.ImageZoomDialog
import com.example.project_uts_rentaloan.ui.components.StatusBadge
import com.example.project_uts_rentaloan.ui.theme.*
import com.example.project_uts_rentaloan.ui.viewmodel.AuthViewModel
import com.example.project_uts_rentaloan.ui.viewmodel.ItemViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinjamSection(
    viewModel: ItemViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.searchableAllItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val itemReturnDates by viewModel.itemReturnDates.collectAsState()

    var selectedItemForDetail by remember { mutableStateOf<ItemEntity?>(null) }
    var showConfirmDialog by remember { mutableStateOf<ItemEntity?>(null) }
    var showDatePicker by remember { mutableStateOf<ItemEntity?>(null) }
    var zoomedImageUri by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by remember { mutableStateOf(false) }

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
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "PINJAM BARANG",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        
        NeoTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Cari barang...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                BorrowItemCard(
                    item = item,
                    returnDate = itemReturnDates[item.id],
                    onSeeMoreClick = {
                        selectedItemForDetail = item
                        isSheetOpen = true
                    },
                    onImageClick = { zoomedImageUri = it }
                )
            }
        }
    }

    if (zoomedImageUri != null) {
        ImageZoomDialog(imgUri = zoomedImageUri!!, onDismiss = { zoomedImageUri = null })
    }

    if (isSheetOpen && selectedItemForDetail != null) {
        ModalBottomSheet(
            onDismissRequest = { isSheetOpen = false },
            sheetState = sheetState,
            containerColor = NeoWhite,
            shape = RoundedCornerShape(0.dp),
            tonalElevation = 0.dp
        ) {
            ItemDetailContent(
                item = selectedItemForDetail!!,
                onPinjamClick = {
                    isSheetOpen = false
                    showConfirmDialog = selectedItemForDetail
                },
                onImageClick = { zoomedImageUri = it }
            )
        }
    }

    if (showConfirmDialog != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = null },
            confirmButton = {
                NeoButton(
                    onClick = {
                        val item = showConfirmDialog
                        showConfirmDialog = null
                        showDatePicker = item
                    },
                    text = "YAKIN",
                    backgroundColor = NeoGreen
                )
            },
            dismissButton = {
                NeoButton(
                    onClick = { showConfirmDialog = null },
                    text = "BATAL",
                    backgroundColor = NeoWhite
                )
            },
            title = { 
                Text(
                    "KONFIRMASI PINJAM",
                    fontWeight = FontWeight.Black
                ) 
            },
            text = { 
                Text(
                    "Apakah kamu yakin mau meminjam ${showConfirmDialog!!.nama_barang.uppercase()}?",
                    fontWeight = FontWeight.Bold
                ) 
            },
            containerColor = NeoWhite,
            shape = RoundedCornerShape(0.dp)
        )
    }

    if (showDatePicker != null) {
        BorrowDateRangeDialog(
            onDismiss = { showDatePicker = null },
            onConfirm = { start, end ->
                currentUser?.let { user ->
                    viewModel.borrowItem(showDatePicker!!, user.email, user.nama, user.kontak, start, end)
                }
                showDatePicker = null
            }
        )
    }
}

@Composable
fun ItemDetailContent(
    item: ItemEntity,
    onPinjamClick: () -> Unit,
    onImageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        if (item.gambar_uri != null) {
            NeoCard(
                modifier = Modifier.fillMaxWidth(),
                shadowOffset = 8.dp
            ) {
                AsyncImage(
                    model = item.gambar_uri,
                    contentDescription = item.nama_barang,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clickable { onImageClick(item.gambar_uri) },
                    contentScale = ContentScale.Crop
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.nama_barang.uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )
            StatusBadge(status = if (item.status_tersedia) "TERSEDIA" else "DIPINJAM")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NeoYellow.copy(alpha = 0.2f),
            shadowOffset = 4.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "LOKASI: ${item.lokasi.uppercase()}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Black
                )
                if (item.kontak.isNotEmpty()) {
                    Text(
                        text = "KONTAK ADMIN: ${item.kontak}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "DESKRIPSI:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black
        )
        NeoCard(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            backgroundColor = NeoWhite,
            shadowOffset = 4.dp
        ) {
            Text(
                text = item.deskripsi,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        NeoButton(
            onClick = onPinjamClick,
            text = "PINJAM SEKARANG",
            backgroundColor = NeoGreen,
            enabled = item.status_tersedia,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowDateRangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (Long, Long) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            NeoButton(
                onClick = {
                    val start = dateRangePickerState.selectedStartDateMillis
                    val end = dateRangePickerState.selectedEndDateMillis
                    if (start != null && end != null) {
                        onConfirm(start, end)
                    }
                },
                text = "KONFIRMASI",
                backgroundColor = NeoGreen,
                enabled = dateRangePickerState.selectedStartDateMillis != null && 
                          dateRangePickerState.selectedEndDateMillis != null
            )
        },
        dismissButton = {
            NeoButton(
                onClick = onDismiss,
                text = "BATAL",
                backgroundColor = NeoWhite
            )
        },
        shape = RoundedCornerShape(0.dp)
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = { Text("PILIH DURASI PINJAM", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Black) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BorrowItemCard(
    item: ItemEntity,
    returnDate: Long?,
    onSeeMoreClick: () -> Unit,
    onImageClick: (String) -> Unit
) {
    val isBorrowed = !item.status_tersedia
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (isBorrowed) Color.LightGray else NeoWhite,
        shadowOffset = 6.dp
    ) {
        Column {
            Box {
                if (item.gambar_uri != null) {
                    AsyncImage(
                        model = item.gambar_uri,
                        contentDescription = item.nama_barang,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clickable { onImageClick(item.gambar_uri) },
                        contentScale = ContentScale.Crop,
                        alpha = if (isBorrowed) 0.6f else 1f
                    )
                }
                
                // Bottom border for image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(NeoBlack)
                        .align(Alignment.BottomCenter)
                )
                
                StatusBadge(
                    status = if (item.status_tersedia) "TERSEDIA" else "DIPINJAM",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                )
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = item.nama_barang.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = NeoBlack,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "LOKASI: ${item.lokasi}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeoBlack.copy(alpha = 0.7f)
                )
                
                if (isBorrowed && returnDate != null && returnDate > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "TERSEDIA PADA ${dateFormat.format(Date(returnDate)).uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeoRed,
                        fontWeight = FontWeight.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                NeoButton(
                    onClick = onSeeMoreClick,
                    modifier = Modifier.fillMaxWidth(),
                    text = "LIHAT DETAIL",
                    backgroundColor = NeoYellow,
                    isFullWidth = true
                )
            }
        }
    }
}
