package com.example.project_uts_rentaloan.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.project_uts_rentaloan.data.local.entity.UserEntity
import com.example.project_uts_rentaloan.ui.theme.*
import com.example.project_uts_rentaloan.ui.viewmodel.AuthUiState
import com.example.project_uts_rentaloan.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    val isEmailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6
    val isFormValid = name.isNotBlank() && contact.length >= 10 && isEmailValid && isPasswordValid

    LaunchedEffect(authState) {
        if (authState is AuthUiState.RegisterSuccess) {
            onRegisterSuccess()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoYellow)
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        NeoCard(
            modifier = Modifier.padding(bottom = 20.dp),
            backgroundColor = NeoWhite,
            shadowOffset = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.headlineSmall,
                    color = NeoBlack
                )
                Text(
                    text = "Join our lending community",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeoBlack.copy(alpha = 0.7f)
                )
            }
        }

        NeoTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeoBlack) },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        NeoTextField(
            value = contact,
            onValueChange = { 
                if (it.all { char -> char.isDigit() }) contact = it 
            },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = NeoBlack) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            supportingText = {
                if (contact.isNotEmpty() && contact.length < 10) {
                    Text("Min. 10 digits", color = NeoRed)
                }
            },
            isError = contact.isNotEmpty() && contact.length < 10,
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        NeoTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NeoBlack) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = email.isNotEmpty() && !isEmailValid,
            supportingText = {
                if (email.isNotEmpty() && !isEmailValid) {
                    Text("Invalid email format", color = NeoRed)
                }
            },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        NeoTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NeoBlack) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = password.isNotEmpty() && !isPasswordValid,
            supportingText = {
                if (password.isNotEmpty() && !isPasswordValid) {
                    Text("Min. 6 characters", color = NeoRed)
                }
            },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (authState is AuthUiState.Error) {
            NeoCard(
                backgroundColor = NeoRed.copy(alpha = 0.1f),
                borderColor = NeoRed,
                shadowColor = NeoRed.copy(alpha = 0.2f),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = (authState as AuthUiState.Error).message,
                    color = NeoRed,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        NeoButton(
            onClick = { 
                viewModel.register(
                    UserEntity(email, password, name, contact, "USER")
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            text = "Register",
            backgroundColor = NeoBlue,
            enabled = isFormValid && authState !is AuthUiState.Loading,
            isFullWidth = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.border(2.dp, NeoBlack, RoundedCornerShape(8.dp))
        ) {
            Text("Already have an account? Login", color = NeoBlack, fontWeight = FontWeight.Bold)
        }
    }
}
