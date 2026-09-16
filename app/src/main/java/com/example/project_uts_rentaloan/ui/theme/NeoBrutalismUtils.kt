package com.example.project_uts_rentaloan.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neoShadow(
    offset: Dp = 4.dp,
    color: Color = NeoBlack,
    shape: Shape = RoundedCornerShape(0.dp)
): Modifier = this.then(
    Modifier
        .offset(offset, offset)
        .background(color, shape)
)

@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeoWhite,
    shadowColor: Color = NeoBlack,
    borderColor: Color = NeoBlack,
    borderWidth: Dp = 2.dp,
    shadowOffset: Dp = 4.dp,
    shape: Shape = RoundedCornerShape(0.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = modifier) {
        // Shadow layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(shadowOffset, shadowOffset)
                .background(shadowColor, shape)
                .border(borderWidth, borderColor, shape)
        )
        // Main content layer (Background and Border)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(backgroundColor, shape)
                .border(borderWidth, borderColor, shape)
        )
        // Content
        Column(
            modifier = Modifier.padding(borderWidth)
        ) {
            content()
        }
    }
}

@Composable
fun NeoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = NeoPink,
    enabled: Boolean = true,
    isFullWidth: Boolean = false
) {
    val currentBgColor = if (enabled) backgroundColor else Color.Gray
    val shadowOffset = if (enabled) 4.dp else 0.dp

    Box(
        modifier = modifier
            .clickable(enabled = enabled) { onClick() }
    ) {
        if (enabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(shadowOffset, shadowOffset)
                    .background(NeoBlack)
                    .border(2.dp, NeoBlack)
            )
        }
        Box(
            modifier = Modifier
                .then(if (isFullWidth) Modifier.fillMaxWidth() else Modifier)
                .background(currentBgColor)
                .border(2.dp, NeoBlack)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) NeoBlack else Color.DarkGray,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Black
            )
        }
    }
}

@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true
) {
    NeoCard(
        modifier = modifier,
        shadowOffset = 2.dp,
        borderWidth = 2.dp
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            isError = isError,
            supportingText = supportingText,
            singleLine = singleLine,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = NeoBlack,
                focusedLabelColor = NeoBlack,
                unfocusedLabelColor = NeoBlack
            )
        )
    }
}
