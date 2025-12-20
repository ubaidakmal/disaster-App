package com.bc230420212.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bc230420212.app.ui.theme.PrimaryColor
import com.bc230420212.app.ui.theme.SurfaceColor
import com.bc230420212.app.ui.theme.TextPrimary

/**
 * REUSABLE DASHBOARD CARD COMPONENT
 * 
 * This is a card component used in the Home Screen Dashboard.
 * Each card represents a feature/function in the app.
 * 
 * @param title - The title text displayed on the card (e.g., "Report Disaster")
 * @param icon - The icon to display on the card (optional)
 * @param onClick - Function called when card is clicked
 * @param modifier - Extra styling (optional)
 */
@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon (if provided)
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(40.dp),
                    tint = PrimaryColor
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Title
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

