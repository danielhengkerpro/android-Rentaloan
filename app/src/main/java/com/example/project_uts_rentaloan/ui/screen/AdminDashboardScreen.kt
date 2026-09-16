package com.example.project_uts_rentaloan.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.project_uts_rentaloan.data.local.entity.ItemEntity
import com.example.project_uts_rentaloan.data.local.entity.LoanWithDetails
import com.example.project_uts_rentaloan.ui.components.ImageZoomDialog
import com.example.project_uts_rentaloan.ui.components.StatusBadge
import com.example.project_uts_rentaloan.ui.theme.*
import com.example.project_uts_rentaloan.ui.viewmodel.ItemViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: ItemViewModel,
    onLogout: () -> Unit
) {
    val items by viewModel.allItems.collectAsState()
    val allLoans by viewModel.allLoans.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showItemDialog by remember { mutableStateOf<ItemEntity?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var zoomedImageUri by remember { mutableStateOf<String?>(null) }
    var itemToDelete by remember { mutableStateOf<ItemEntity?>(null) }
    var selectedItemForDetail by remember { mutableStateOf<ItemEntity?>(null) }

    Scaffold(
        modifier = Modifier.background(NeoYellow),
        topBar = {
            Column {
                NeoCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    backgroundColor = NeoWhite,
                    shadowOffset = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ADMIN PANEL",
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(onClick = onLogout, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = NeoBlack)
                        }
                    }
                }
                
                NeoCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    backgroundColor = NeoWhite,
                    shadowOffset = 2.dp
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = NeoBlack,
                                height = 4.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("BARANG", fontWeight = FontWeight.Black) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("RIWAYAT", fontWeight = FontWeight.Black) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                NeoCard(
                    backgroundColor = NeoGreen,
                    shadowOffset = 4.dp
                ) {
                    IconButton(
                        onClick = { 
                            showItemDialog = ItemEntity(nama_barang = "") 
                            isEditing = false
                        },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item", tint = NeoBlack)
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NeoYellow)
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> {
                    if (items.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No items in database.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items) { item ->
                                AdminItemCard(
                                    item = item,
                                    onClick = { selectedItemForDetail = item },
                                    onEdit = { 
                                        showItemDialog = item
                                        isEditing = true
                                    },
                                    onDelete = { itemToDelete = item },
                                    onImageClick = { zoomedImageUri = it }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    LoanHistoryTab(
                        loans = allLoans,
                        onImageClick = { zoomedImageUri = it }
                    )
                }
            }
        }
    }

    if (zoomedImageUri != null) {
        ImageZoomDialog(imgUri = zoomedImageUri!!, onDismiss = { zoomedImageUri = null })
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus Barang") },
            text = { Text("Apakah Anda yakin ingin menghapus '${itemToDelete!!.nama_barang}'? Semua riwayat transaksi barang ini juga akan terhapus.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteItem(itemToDelete!!)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showItemDialog != null) {
        ItemDialog(
            item = showItemDialog!!,
            isEditing = isEditing,
            onDismiss = { showItemDialog = null },
            onConfirm = { updatedItem ->
                if (isEditing) {
                    viewModel.updateItem(updatedItem)
                } else {
                    viewModel.addItem(
                        updatedItem.nama_barang,
                        updatedItem.deskripsi,
                        updatedItem.lokasi,
                        updatedItem.kontak,
                        updatedItem.gambar_uri
                    )
                }
                showItemDialog = null
            }
        )
    }

    if (selectedItemForDetail != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedItemForDetail = null }
        ) {
            AdminItemDetailContent(
                item = selectedItemForDetail!!,
                onImageClick = { zoomedImageUri = it }
            )
        }
    }
}

@Composable
fun AdminItemDetailContent(
    item: ItemEntity,
    onImageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        if (item.gambar_uri != null) {
            AsyncImage(
                model = item.gambar_uri,
                contentDescription = item.nama_barang,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clickable { onImageClick(item.gambar_uri) },
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.nama_barang,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            StatusBadge(status = if (item.status_tersedia) "TERSEDIA" else "DIPINJAM")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Lokasi: ${item.lokasi}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        
        if (item.kontak.isNotEmpty()) {
            Text(
                text = "Kontak Admin: ${item.kontak}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Deskripsi:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = item.deskripsi,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun LoanHistoryTab(
    loans: List<LoanWithDetails>,
    onImageClick: (String) -> Unit
) {
    if (loans.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada riwayat peminjaman.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(loans) { loan ->
                LoanHistoryCard(
                    loan = loan,
                    onImageClick = onImageClick
                )
            }
        }
    }
}

@Composable
fun LoanHistoryCard(
    loan: LoanWithDetails,
    onImageClick: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    
    NeoCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NeoWhite,
        shadowOffset = 6.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (loan.item.gambar_uri != null) {
                    Box(modifier = Modifier.border(2.dp, NeoBlack)) {
                        AsyncImage(
                            model = loan.item.gambar_uri,
                            contentDescription = loan.item.nama_barang,
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { onImageClick(loan.item.gambar_uri) },
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = loan.item.nama_barang.uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                        StatusBadge(status = loan.transaction.status_pinjam)
                    }
                }
            }
            
            // Hard Divider
            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(NeoBlack))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = NeoBlack)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${loan.borrower.nama_penyewa.uppercase()} (${loan.borrower.email})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = NeoBlack)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = loan.borrower.kontak,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeoBlack.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            NeoCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeoWhite,
                shadowOffset = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = "DIPINJAM: ${dateFormat.format(Date(loan.transaction.tgl_pinjam)).uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeoBlack,
                        fontWeight = FontWeight.Black
                    )
                    
                    loan.transaction.tgl_kembali_rencana?.let {
                        Text(
                            text = "RENCANA KEMBALI: ${dateFormat.format(Date(it)).uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeoBlack,
                            fontWeight = FontWeight.Black
                        )
                    }
                    
                    if (loan.transaction.status_pinjam == "DIKEMBALIKAN" && loan.transaction.tgl_kembali != null) {
                        Text(
                            text = "DIKEMBALIKAN: ${dateFormat.format(Date(loan.transaction.tgl_kembali)).uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeoBlue,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminItemCard(
    item: ItemEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onImageClick: (String) -> Unit
) {
    NeoCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundColor = NeoWhite,
        shadowOffset = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.gambar_uri != null) {
                Box(modifier = Modifier.border(2.dp, NeoBlack)) {
                    AsyncImage(
                        model = item.gambar_uri,
                        contentDescription = item.nama_barang,
                        modifier = Modifier
                            .size(72.dp)
                            .clickable { onImageClick(item.gambar_uri) },
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nama_barang.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "LOKASI: ${item.lokasi}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeoBlack.copy(alpha = 0.7f)
                )
                if (item.kontak.isNotBlank()) {
                    Text(
                        text = "TELP: ${item.kontak}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NeoBlack.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                StatusBadge(status = if (item.status_tersedia) "TERSEDIA" else "DIPINJAM")
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NeoBlue)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeoRed)
                }
            }
        }
    }
}

@Composable
fun ItemDialog(
    item: ItemEntity,
    isEditing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (ItemEntity) -> Unit
) {
    var name by remember { mutableStateOf(item.nama_barang) }
    var desc by remember { mutableStateOf(item.deskripsi) }
    var loc by remember { mutableStateOf(item.lokasi) }
    var contact by remember { mutableStateOf(item.kontak) }
    var imgUri by remember { mutableStateOf<android.net.Uri?>(item.gambar_uri?.let { android.net.Uri.parse(it) }) }

    val context = LocalContext.current
    val photoPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            uri?.let {
                imgUri = it
                context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            NeoButton(
                onClick = { 
                    onConfirm(item.copy(
                        nama_barang = name.trim(),
                        deskripsi = desc.trim(),
                        lokasi = loc.trim(),
                        kontak = contact.trim(),
                        gambar_uri = imgUri?.toString()
                    )) 
                },
                text = if (isEditing) "SIMPAN" else "TAMBAH",
                backgroundColor = NeoGreen,
                enabled = name.isNotBlank() && loc.isNotBlank() && desc.isNotBlank() && contact.isNotBlank()
            )
        },
        dismissButton = {
            NeoButton(
                onClick = onDismiss,
                text = "BATAL",
                backgroundColor = NeoWhite
            )
        },
        title = { 
            Text(
                text = if (isEditing) "EDIT BARANG" else "TAMBAH BARANG",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                NeoTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("NAMA BARANG") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    singleLine = true,
                    isError = name.isBlank(),
                    supportingText = { if (name.isBlank()) Text("WAJIB DIISI", color = NeoRed) }
                )
                NeoTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("DESKRIPSI") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Menu, contentDescription = null) },
                    isError = desc.isBlank(),
                    supportingText = { if (desc.isBlank()) Text("WAJIB DIISI", color = NeoRed) }
                )
                NeoTextField(
                    value = loc,
                    onValueChange = { loc = it },
                    label = { Text("LOKASI") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                    singleLine = true,
                    isError = loc.isBlank(),
                    supportingText = { if (loc.isBlank()) Text("WAJIB DIISI", color = NeoRed) }
                )
                NeoTextField(
                    value = contact,
                    onValueChange = { 
                        if (it.all { char -> char.isDigit() }) contact = it 
                    },
                    label = { Text("KONTAK ADMIN") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone),
                    singleLine = true,
                    isError = contact.isBlank(),
                    supportingText = { if (contact.isBlank()) Text("WAJIB DIISI", color = NeoRed) }
                )
                
                NeoButton(
                    onClick = { 
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    text = if (imgUri == null) "PILIH GAMBAR" else "GANTI GAMBAR",
                    backgroundColor = NeoOrange,
                    isFullWidth = true
                )
                
                if (imgUri != null) {
                    NeoCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shadowOffset = 2.dp
                    ) {
                        AsyncImage(
                            model = imgUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        },
        containerColor = NeoWhite,
        shape = RoundedCornerShape(0.dp),
        tonalElevation = 0.dp
    )
}
