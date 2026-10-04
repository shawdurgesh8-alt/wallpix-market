package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WallpaperStatus
import com.example.data.util.WallpaperHelper
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun CreatorDashboardScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser = viewModel.currentUser.collectAsState().value
    val creatorUploads = viewModel.creatorUploads.collectAsState().value
    val transactions = viewModel.transactions.collectAsState().value
    val platformSettings = viewModel.platformSettings.collectAsState().value
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var withdrawAmountText by remember { mutableStateOf("50.00") }

    val totalDownloads = creatorUploads.sumOf { it.downloadsCount }
    val totalViews = creatorUploads.sumOf { it.viewsCount }
    val commissionPercent = platformSettings.platformCommissionPercent
    val creatorPercent = 100 - commissionPercent

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("creator_dashboard_screen"),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        // App bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("creator_back_button")) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Creator Studio Dashboard",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${currentUser.name} • $creatorPercent% Payout Tier",
                        fontSize = 11.sp,
                        color = SecondaryCyan
                    )
                }
            }
        }

        // Revenue Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, BorderDark, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Net Available Earnings",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${String.format("%.2f", currentUser.creatorEarnings)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Platform fee ($commissionPercent%) automatically settled on sales",
                        fontSize = 11.sp,
                        color = TextTertiary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showWithdrawDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("request_payout_button")
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Withdraw Payout", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 4 KPI Metric Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Artworks",
                    value = "${creatorUploads.size}",
                    subtitle = "${creatorUploads.count { it.status == WallpaperStatus.APPROVED }} live",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Downloads",
                    value = "$totalDownloads",
                    subtitle = "All time",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Impressions",
                    value = "$totalViews",
                    subtitle = "Views",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: Uploaded Wallpapers with Status
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Uploaded Wallpapers",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${creatorUploads.size} total",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        items(creatorUploads) { wp ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, BorderDark, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (wp.drawableResId != null) {
                        Image(
                            painter = painterResource(id = wp.drawableResId),
                            contentDescription = wp.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = wp.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${wp.resolution} • ${if (wp.isPaid) "$${wp.price}" else "Free"}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Status Pill
                        val (statusText, statusBg, statusColor) = when (wp.status) {
                            WallpaperStatus.APPROVED -> Triple("Approved & Live", SuccessGreen.copy(alpha = 0.2f), SuccessGreen)
                            WallpaperStatus.PENDING_REVIEW -> Triple("Pending Review", AccentGold.copy(alpha = 0.2f), AccentGold)
                            WallpaperStatus.REJECTED -> Triple("Rejected", ErrorRed.copy(alpha = 0.2f), ErrorRed)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(statusBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                        if (wp.rejectionReason != null) {
                            Text(
                                text = "Reason: ${wp.rejectionReason}",
                                fontSize = 10.sp,
                                color = ErrorRed,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${wp.downloadsCount} dls",
                            fontSize = 11.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${wp.viewsCount} views",
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                    }
                }
            }
        }

        // Section: Transaction History
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Transaction History & Payouts",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }

        items(transactions) { tx ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tx.description,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${WallpaperHelper.formatDate(tx.timestamp)} • ${tx.id}",
                            fontSize = 10.sp,
                            color = TextTertiary
                        )
                    }
                    Text(
                        text = "${if (tx.type.contains("COMMISSION")) "-" else "+"}$${String.format("%.2f", tx.amount)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (tx.type.contains("COMMISSION")) AccentPink else SuccessGreen
                    )
                }
            }
        }
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Request Earnings Payout", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Available Balance: $${String.format("%.2f", currentUser.creatorEarnings)}",
                        color = SecondaryCyan,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = { withdrawAmountText = it },
                        label = { Text("Amount ($)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryNeon,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Funds will be credited via direct bank transfer or UPI within 24h.",
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = withdrawAmountText.toDoubleOrNull() ?: 0.0
                        viewModel.requestWithdrawal(amt)
                        showWithdrawDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm Payout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = TextTertiary)
        }
    }
}
