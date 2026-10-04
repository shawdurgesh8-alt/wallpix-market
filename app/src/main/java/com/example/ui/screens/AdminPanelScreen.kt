package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.Wallpaper
import com.example.data.model.WallpaperCategory
import com.example.data.model.WallpaperStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingWallpapers = viewModel.pendingReviewWallpapers.collectAsState().value
    val allWallpapers = viewModel.wallpapers.collectAsState().value
    val allUsers = viewModel.allUsers.collectAsState().value
    val reports = viewModel.reports.collectAsState().value
    val platformSettings = viewModel.platformSettings.collectAsState().value
    val transactions = viewModel.transactions.collectAsState().value

    var currentAdminTab by remember { mutableStateOf(0) } // 0: Review Queue, 1: Commission, 2: Reports, 3: Users
    var rejectTargetWp by remember { mutableStateOf<Wallpaper?>(null) }
    var rejectReasonText by remember { mutableStateOf("Resolution does not meet 4K/8K standards.") }

    var commissionSliderValue by remember { mutableFloatStateOf(platformSettings.platformCommissionPercent.toFloat()) }

    val totalPlatformRevenue = transactions.filter { it.type == "PLATFORM_COMMISSION" }.sumOf { it.amount }
    val totalGrossSales = transactions.filter { it.type == "WALLPAPER_SALE" }.sumOf { it.amount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .testTag("admin_panel_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_button")) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Marketplace Admin Console",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Operations, Approvals & Policy Enforcement",
                        fontSize = 11.sp,
                        color = ErrorRed
                    )
                }
            }
        }

        // Financial Overview Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, BorderDark, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Gross GMV", fontSize = 11.sp, color = TextSecondary)
                        Text("$${String.format("%.2f", totalGrossSales)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Platform Revenue", fontSize = 11.sp, color = TextSecondary)
                        Text("$${String.format("%.2f", totalPlatformRevenue)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pending Queue", fontSize = 11.sp, color = TextSecondary)
                        Text("${pendingWallpapers.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                    }
                }
            }
        }

        // Admin Tab Bar (0: Review Queue, 1: Commission, 2: Reports, 3: Users)
        item {
            TabRow(
                selectedTabIndex = currentAdminTab,
                containerColor = SurfaceDark,
                contentColor = PrimaryNeon,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Tab(
                    selected = currentAdminTab == 0,
                    onClick = { currentAdminTab = 0 },
                    text = { Text("Queue (${pendingWallpapers.size})", fontSize = 11.sp) }
                )
                Tab(
                    selected = currentAdminTab == 1,
                    onClick = { currentAdminTab = 1 },
                    text = { Text("Commission", fontSize = 11.sp) }
                )
                Tab(
                    selected = currentAdminTab == 2,
                    onClick = { currentAdminTab = 2 },
                    text = { Text("Reports (${reports.count { it.status == "PENDING" }})", fontSize = 11.sp) }
                )
                Tab(
                    selected = currentAdminTab == 3,
                    onClick = { currentAdminTab = 3 },
                    text = { Text("Users (${allUsers.size})", fontSize = 11.sp) }
                )
            }
        }

        // TAB 0: PENDING REVIEW QUEUE
        if (currentAdminTab == 0) {
            if (pendingWallpapers.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Queue All Caught Up!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("No pending wallpapers awaiting moderation.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            } else {
                items(pendingWallpapers) { wp ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, BorderDark, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (wp.drawableResId != null) {
                                    Image(
                                        painter = painterResource(id = wp.drawableResId),
                                        contentDescription = wp.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(wp.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("Creator: ${wp.creatorName}", fontSize = 12.sp, color = TextSecondary)
                                    Text("Resolution: ${wp.resolution}", fontSize = 11.sp, color = SecondaryCyan)
                                    Text("Category: ${wp.category.displayName}", fontSize = 11.sp, color = TextTertiary)
                                    Text(
                                        text = if (wp.isPaid) "$${wp.price}" else "Free",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (wp.isPaid) AccentGold else SuccessGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(wp.description, fontSize = 12.sp, color = TextSecondary)

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.adminApprove(wp.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve", fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { rejectTargetWp = wp },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = ErrorRed.copy(alpha = 0.1f),
                                        contentColor = ErrorRed
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reject", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 1: COMMISSION CONFIGURATION
        if (currentAdminTab == 1) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, BorderDark, RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Platform Earning Commission",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Deducted automatically from creator wallpaper sales",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Current Platform Cut:", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                "${commissionSliderValue.toInt()}%",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryNeon
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Creator Cut:", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                "${100 - commissionSliderValue.toInt()}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Slider(
                            value = commissionSliderValue,
                            onValueChange = { commissionSliderValue = it },
                            valueRange = 5f..50f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryNeon,
                                activeTrackColor = PrimaryNeon,
                                inactiveTrackColor = BorderDark
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.adminSetCommission(commissionSliderValue.toInt()) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Platform Commission Rate", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // TAB 2: REPORTS & COPYRIGHT COMPLAINTS
        if (currentAdminTab == 2) {
            if (reports.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Zero Active Copyright Reports", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            } else {
                items(reports) { rep ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, BorderDark, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(rep.reason, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ErrorRed)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (rep.status == "PENDING") AccentGold.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(rep.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (rep.status == "PENDING") AccentGold else SuccessGreen)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Wallpaper: ${rep.wallpaperTitle}", fontSize = 12.sp, color = TextPrimary)
                            Text("Reported by: ${rep.reportedBy}", fontSize = 11.sp, color = TextSecondary)
                            if (rep.details.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Details: ${rep.details}", fontSize = 11.sp, color = TextTertiary)
                            }

                            if (rep.status == "PENDING") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { viewModel.adminResolveReport(rep.id, removeContent = false) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Dismiss Report", fontSize = 11.sp)
                                    }
                                    Button(
                                        onClick = { viewModel.adminResolveReport(rep.id, removeContent = true) },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Remove & Ban", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // TAB 3: USERS & CREATORS DIRECTORY
        if (currentAdminTab == 3) {
            items(allUsers) { u ->
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(u.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                if (u.verifiedCreator) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = SecondaryCyan, modifier = Modifier.size(13.dp))
                                }
                            }
                            Text("${u.email} • ${u.phone}", fontSize = 11.sp, color = TextSecondary)
                            Text("Wallet: $${String.format("%.2f", u.walletBalance)} | Creator Earned: $${String.format("%.2f", u.creatorEarnings)}", fontSize = 10.sp, color = AccentGold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (u.isAdmin) ErrorRed.copy(alpha = 0.2f) else if (u.isCreator) PrimaryNeon.copy(alpha = 0.2f) else SurfaceHigh)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (u.isAdmin) "Admin" else if (u.isCreator) "Creator" else "Buyer",
                                fontSize = 10.sp,
                                color = if (u.isAdmin) ErrorRed else if (u.isCreator) PrimaryNeon else TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }

    // Rejection Reason Modal
    if (rejectTargetWp != null) {
        val wp = rejectTargetWp!!
        AlertDialog(
            onDismissRequest = { rejectTargetWp = null },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Reject '${wp.title}'", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Provide a specific reason to help creator rectify:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReasonText,
                        onValueChange = { rejectReasonText = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ErrorRed,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminReject(wp.id, rejectReasonText)
                        rejectTargetWp = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm Rejection", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectTargetWp = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
