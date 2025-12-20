package com.bc230420212.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bc230420212.app.ui.theme.PrimaryColor
import com.bc230420212.app.ui.theme.TextSecondary

/**
 * REUSABLE TEXT AREA COMPONENT
 * 
 * This is a multi-line text input component for longer text like descriptions.
 * 
 * @param value - Current text value
 * @param onValueChange - Function called when text changes
 * @param label - Label text for the field
 * @param modifier - Extra styling (optional)
 * @param isError - Whether to show error state
 * @param errorMessage - Error message to display
 * @param enabled - Whether the field is enabled
 */
@Composable
fun AppTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled,
        isError = isError,
        supportingText = if (isError) { { Text(errorMessage) } } else null,
        maxLines = 5,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryColor,
            unfocusedBorderColor = TextSecondary,
            errorBorderColor = com.bc230420212.app.ui.theme.ErrorColor,
            focusedLabelColor = PrimaryColor,
            unfocusedLabelColor = TextSecondary
        )
    )
}

