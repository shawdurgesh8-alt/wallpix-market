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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.util.WallpaperHelper
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun PurchasesScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val purchases = viewModel.purchases.collectAsState().value
    val wallpapers = viewModel.wallpapers.collectAsState().value
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("purchases_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "My Purchases & Vault",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Unlocked Master 8K files, purchase orders & downloads",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Empty state
        if (purchases.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCartCheckout,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No Purchased Wallpapers Yet",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Explore the curated 8K marketplace and unlock master artworks",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 30.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { viewModel.selectTab(com.example.ui.viewmodel.NavigationTab.HOME) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Browse Marketplace")
                    }
                }
            }
        } else {
            items(purchases) { order ->
                val wp = wallpapers.find { it.id == order.wallpaperId }
                var showApplyMenu by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, BorderDark, RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Thumbnail
                            if (wp?.drawableResId != null) {
                                Image(
                                    painter = painterResource(id = wp.drawableResId),
                                    contentDescription = order.wallpaperTitle,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { wp?.let { viewModel.openWallpaperDetail(it) } }
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(PrimaryVariant)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Order Info
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = order.wallpaperTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Order: ${order.orderId}",
                                    fontSize = 11.sp,
                                    color = SecondaryCyan,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Date: ${WallpaperHelper.formatDate(order.purchaseDate)}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Paid: $${String.format("%.2f", order.amountPaid)} via ${order.paymentMethod}",
                                    fontSize = 11.sp,
                                    color = AccentGold
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = BorderDark
                        )

                        // Action Buttons: Apply & Download
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                Button(
                                    onClick = { showApplyMenu = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Wallpaper,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                DropdownMenu(
                                    expanded = showApplyMenu,
                                    onDismissRequest = { showApplyMenu = false },
                                    modifier = Modifier
                                        .background(SurfaceDark)
                                        .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Home Screen", color = TextPrimary) },
                                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = SecondaryCyan) },
                                        onClick = {
                                            showApplyMenu = false
                                            wp?.let { viewModel.applyWallpaper(context, it, WallpaperHelper.TargetScreen.HOME) }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Lock Screen", color = TextPrimary) },
                                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AccentPink) },
                                        onClick = {
                                            showApplyMenu = false
                                            wp?.let { viewModel.applyWallpaper(context, it, WallpaperHelper.TargetScreen.LOCK) }
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Both Screens", color = TextPrimary) },
                                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = SuccessGreen) },
                                        onClick = {
                                            showApplyMenu = false
                                            wp?.let { viewModel.applyWallpaper(context, it, WallpaperHelper.TargetScreen.BOTH) }
                                        }
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    wp?.let { viewModel.downloadWallpaper(it, order.resolutionUnlocked) }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = SurfaceHigh,
                                    contentColor = TextPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Download 8K", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
