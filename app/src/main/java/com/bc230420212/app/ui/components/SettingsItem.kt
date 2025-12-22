package com.bc230420212.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bc230420212.app.ui.theme.TextPrimary
import com.bc230420212.app.ui.theme.TextSecondary

/**
 * SETTINGS ITEM COMPONENT
 * 
 * A reusable component for displaying settings options in the Profile screen.
 * Shows an icon, title, optional subtitle, and a chevron for navigation.
 * 
 * @param title - Main text of the setting
 * @param subtitle - Optional subtitle text
 * @param icon - Optional icon to display
 * @param onClick - Function called when item is clicked
 * @param trailingContent - Optional trailing content (e.g., switch, badge)
 */
@Composable
fun SettingsItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = com.bc230420212.app.ui.theme.SurfaceColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = com.bc230420212.app.ui.theme.PrimaryColor,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 16.dp)
                    )
                }
                
                // Title and Subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    subtitle?.let {
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            // Trailing content or chevron
            if (trailingContent != null) {
                trailingContent()
            } else {
                Text(
                    text = ">",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }
        }
    }
}

