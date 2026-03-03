package com.bc230420212.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bc230420212.app.data.model.DisasterReport
import com.bc230420212.app.data.model.DisasterType
import com.bc230420212.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * REPORT ITEM COMPONENT
 * 
 * This component displays a single disaster report in a list.
 * Shows: type, time, short description, status, verification info
 * 
 * @param report - The disaster report to display
 * @param onClick - Function called when item is clicked
 */
@Composable
fun ReportItem(
    report: DisasterReport,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // First Row: Type and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Disaster Type Badge
                Text(
                    text = report.disasterType.displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = getDisasterTypeColor(report.disasterType),
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                // Status Badge
                Text(
                    text = report.status.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = getStatusColor(report.status),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            // Description (shortened)
            Text(
                text = report.description,
                fontSize = 14.sp,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            // Time and Verification Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time
                Text(
                    text = formatTimestamp(report.timestamp),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                
                // Verification Info
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "✓ ${report.confirmations}",
                        fontSize = 12.sp,
                        color = SuccessColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "✗ ${report.dismissals}",
                        fontSize = 12.sp,
                        color = ErrorColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Get color for disaster type
 */
@Composable
private fun getDisasterTypeColor(type: DisasterType): androidx.compose.ui.graphics.Color {
    return when (type) {
        DisasterType.FLOOD -> FloodColor
        DisasterType.FIRE -> FireColor
        DisasterType.EARTHQUAKE -> EarthquakeColor
        DisasterType.ACCIDENT -> AccidentColor
        else -> OtherDisasterColor
    }
}

/**
 * Get color for report status
 */
@Composable
private fun getStatusColor(status: com.bc230420212.app.data.model.ReportStatus): androidx.compose.ui.graphics.Color {
    return when (status) {
        com.bc230420212.app.data.model.ReportStatus.ACTIVE -> WarningColor
        com.bc230420212.app.data.model.ReportStatus.VERIFIED -> SuccessColor
        com.bc230420212.app.data.model.ReportStatus.RESOLVED -> SuccessColor
        com.bc230420212.app.data.model.ReportStatus.FALSE_ALARM -> ErrorColor
    }
}

/**
 * Format timestamp to readable date/time
 */
private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}

