package com.bc230420212.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bc230420212.app.ui.theme.PrimaryColor
import com.bc230420212.app.ui.theme.TextOnPrimary

/**
 * REUSABLE PRIMARY BUTTON COMPONENT
 * 
 * This is a custom button that we created to use throughout the app.
 * Instead of creating buttons everywhere, we use this one component.
 * 
 * Why we created this:
 * - All buttons look the same (consistent design)
 * - Easy to change button style in one place
 * - Less code to write
 * 
 * @param text - The text to display on the button (e.g., "Login", "Register")
 * @param onClick - Function to call when button is clicked
 * @param modifier - Used to add extra styling (padding, margins, etc.)
 * @param enabled - If false, button is disabled (grayed out, can't click)
 * @param isSecondary - If true, uses secondary color (teal) instead of primary (blue)
 */
@Composable
fun AppButton(
    text: String,              // Button text (e.g., "Login")
    onClick: () -> Unit,       // What happens when button is clicked
    modifier: Modifier = Modifier,  // Extra styling (optional)
    enabled: Boolean = true,   // Can button be clicked? (default: yes)
    isSecondary: Boolean = false  // Use secondary color? (default: no, use primary blue)
) {
    // Material3 Button component
    Button(
        onClick = onClick,  // Call the function when clicked
        modifier = modifier
            .fillMaxWidth()  // Button takes full width of screen
            .height(56.dp),  // Button height is 56dp (standard size)
        enabled = enabled,  // Enable/disable button
        shape = RoundedCornerShape(12.dp),  // Rounded corners (12dp radius)
        colors = ButtonDefaults.buttonColors(
            // If isSecondary is true, use SecondaryColor (teal)
            // Otherwise, use PrimaryColor (blue)
            containerColor = if (isSecondary) 
                com.bc230420212.app.ui.theme.SecondaryColor 
            else 
                PrimaryColor,
            // Color when button is disabled (gray)
            disabledContainerColor = com.bc230420212.app.ui.theme.TextDisabled
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextOnPrimary
        )
    }
}

/**
 * Reusable Outlined Button Component
 */
@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = PrimaryColor
        ),
        border = BorderStroke(2.dp, PrimaryColor)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

