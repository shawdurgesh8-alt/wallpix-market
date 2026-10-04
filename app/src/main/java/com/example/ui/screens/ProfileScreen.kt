package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun ProfileScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToCreatorDashboard: () -> Unit = {},
    onNavigateToAdminPanel: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser = viewModel.currentUser.collectAsState().value
    val allUsers = viewModel.allUsers.collectAsState().value
    val purchases = viewModel.purchases.collectAsState().value
    val creatorUploads = viewModel.creatorUploads.collectAsState().value

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Top Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Account & Creator Hub",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Profile Card
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar circle with initials
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(PrimaryNeon),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                if (currentUser.verifiedCreator) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Creator",
                                        tint = SecondaryCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = currentUser.email,
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = currentUser.phone,
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                        }

                        // Role badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (currentUser.isAdmin) ErrorRed.copy(alpha = 0.2f)
                                    else if (currentUser.isCreator) PrimaryNeon.copy(alpha = 0.2f)
                                    else SecondaryCyan.copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (currentUser.isAdmin) "ADMIN" else if (currentUser.isCreator) "CREATOR" else "BUYER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentUser.isAdmin) ErrorRed else if (currentUser.isCreator) PrimaryNeon else SecondaryCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentUser.bio,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = BorderDark
                    )

                    // Financial metrics row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Vault Items", fontSize = 11.sp, color = TextSecondary)
                            Text("${purchases.size}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Wallet Bal", fontSize = 11.sp, color = TextSecondary)
                            Text("$${String.format("%.2f", currentUser.walletBalance)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SecondaryCyan)
                        }
                        if (currentUser.isCreator) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Creator Earned", fontSize = 11.sp, color = TextSecondary)
                                Text("$${String.format("%.2f", currentUser.creatorEarnings)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AccentGold)
                            }
                        }
                    }
                }
            }
        }

        // Fast Persona Switcher (For live demonstration of Buyer, Creator, Admin)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Demo Role Switcher",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allUsers.forEach { user ->
                        val isCurrent = user.id == currentUser.id
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isCurrent) SurfaceHigh else SurfaceCard)
                                .border(
                                    1.dp,
                                    if (isCurrent) PrimaryNeon else BorderDark,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.switchUser(user.id) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when {
                                    user.isAdmin -> "Admin"
                                    user.id == "user_creator_alex" -> "Creator Alex"
                                    else -> "Buyer"
                                },
                                fontSize = 11.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrent) PrimaryNeon else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Creator Studio Portal Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToCreatorDashboard() }
                    .border(1.dp, BorderDark, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryNeon.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Analytics, contentDescription = null, tint = PrimaryNeon)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Creator Dashboard",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${creatorUploads.size} uploaded • 80% revenue split",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                }
            }
        }

        // Admin Panel Portal Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToAdminPanel() }
                    .border(
                        1.dp,
                        if (currentUser.isAdmin) ErrorRed.copy(alpha = 0.5f) else BorderDark,
                        RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ErrorRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = ErrorRed)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Marketplace Admin Panel",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Review queue, commissions & copyright moderation",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                }
            }
        }
    }
}
