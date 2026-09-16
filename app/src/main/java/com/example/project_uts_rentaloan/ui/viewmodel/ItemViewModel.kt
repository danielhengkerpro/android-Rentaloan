package com.example.project_uts_rentaloan.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_uts_rentaloan.data.local.entity.ItemEntity
import com.example.project_uts_rentaloan.data.local.entity.LoanWithDetails
import com.example.project_uts_rentaloan.domain.repository.LoanRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ItemViewModel(private val repository: LoanRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val availableItems: StateFlow<List<ItemEntity>> = repository.getAvailableItems()
        .combine(_searchQuery) { items, query ->
            if (query.isEmpty()) items
            else items.filter { it.nama_barang.contains(query, ignoreCase = true) || it.lokasi.contains(query, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allItems: StateFlow<List<ItemEntity>> = repository.getAllItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchableAllItems: StateFlow<List<ItemEntity>> = allItems
        .combine(_searchQuery) { items, query ->
            if (query.isEmpty()) items
            else items.filter { it.nama_barang.contains(query, ignoreCase = true) || it.lokasi.contains(query, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLoans: StateFlow<List<LoanWithDetails>> = repository.getAllLoans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val itemReturnDates: StateFlow<Map<Long, Long>> = repository.getAllLoans()
        .map { loans ->
            loans.filter { it.transaction.status_pinjam == "DIPINJAM" }
                .associate { it.item.id to (it.transaction.tgl_kembali_rencana ?: 0L) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addItem(name: String, description: String, location: String, contact: String, imageUri: String?) {
        viewModelScope.launch {
            repository.addItem(
                ItemEntity(
                    nama_barang = name.trim(),
                    deskripsi = description.trim(),
                    lokasi = location.trim(),
                    kontak = contact.trim(),
                    gambar_uri = imageUri
                )
            )
        }
    }

    fun updateItem(item: ItemEntity) {
        viewModelScope.launch {
            val sanitizedItem = item.copy(
                nama_barang = item.nama_barang.trim(),
                deskripsi = item.deskripsi.trim(),
                lokasi = item.lokasi.trim(),
                kontak = item.kontak.trim()
            )
            repository.updateItem(sanitizedItem)
        }
    }

    fun deleteItem(item: ItemEntity) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun borrowItem(item: ItemEntity, email: String, name: String, phone: String, startDate: Long, endDate: Long) {
        viewModelScope.launch {
            repository.borrowItem(item, email, name, phone, startDate, endDate)
        }
    }

    fun getLoansByUser(email: String): Flow<List<LoanWithDetails>> {
        return repository.getLoansByUser(email)
    }

    fun returnItem(transactionId: Long) {
        viewModelScope.launch {
            repository.returnItem(transactionId, System.currentTimeMillis())
        }
    }
}
