package com.example.project_uts_rentaloan.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.project_uts_rentaloan.ui.theme.*
import com.example.project_uts_rentaloan.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by authViewModel.currentUser.collectAsState()

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
                    "PENGATURAN",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            NeoCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeoWhite,
                shadowOffset = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "PROFIL PENGGUNA", 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Black,
                        color = NeoBlack
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileInfoRow("NAMA", currentUser?.nama ?: "UNKNOWN")
                    ProfileInfoRow("EMAIL", currentUser?.email ?: "")
                    ProfileInfoRow("KONTAK", currentUser?.kontak ?: "")
                    ProfileInfoRow("ROLE", currentUser?.role ?: "")
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            NeoButton(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                text = "LOG OUT",
                backgroundColor = NeoRed,
                isFullWidth = true
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = NeoBlack.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value.uppercase(),
            style = MaterialTheme.typography.bodyLarge,
            color = NeoBlack,
            fontWeight = FontWeight.Black
        )
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(NeoBlack.copy(alpha = 0.2f)))
    }
}
