package com.bc230420212.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bc230420212.app.ui.theme.*

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
    modifier: Modifier = Modifier,
    iconColor: androidx.compose.ui.graphics.Color = PrimaryColor
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            iconColor.copy(alpha = 0.08f),
                            SurfaceColor,
                            BackgroundColor.copy(alpha = 0.5f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon with circular gradient background
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        iconColor.copy(alpha = 0.25f),
                                        iconColor.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            modifier = Modifier.size(34.dp),
                            tint = iconColor
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
                
                // Title
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

