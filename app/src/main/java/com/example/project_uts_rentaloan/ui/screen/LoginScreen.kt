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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.project_uts_rentaloan.ui.theme.*
import com.example.project_uts_rentaloan.ui.viewmodel.AuthUiState
import com.example.project_uts_rentaloan.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val isEmailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6
    val canLogin = isEmailValid && isPasswordValid && authState !is AuthUiState.Loading

    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success && currentUser != null) {
            onLoginSuccess(currentUser!!.role)
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
            modifier = Modifier.padding(bottom = 24.dp),
            backgroundColor = NeoWhite,
            shadowOffset = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineLarge,
                    color = NeoBlack
                )
                Text(
                    text = "Login to your account",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeoBlack.copy(alpha = 0.7f)
                )
            }
        }

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
        Spacer(modifier = Modifier.height(16.dp))

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
        Spacer(modifier = Modifier.height(32.dp))

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
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            text = "Login",
            backgroundColor = NeoGreen,
            enabled = canLogin,
            isFullWidth = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = onRegisterClick,
            modifier = Modifier.border(2.dp, NeoBlack, RoundedCornerShape(8.dp))
        ) {
            Text("Don't have an account? Register", color = NeoBlack, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CircularProgressIndicator(size: androidx.compose.ui.unit.Dp, color: androidx.compose.ui.graphics.Color) {
    androidx.compose.material3.CircularProgressIndicator(
        modifier = Modifier.size(size),
        color = color,
        strokeWidth = 2.dp
    )
}
