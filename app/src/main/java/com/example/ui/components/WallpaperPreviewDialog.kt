package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.Review
import com.example.data.model.Wallpaper
import com.example.data.util.WallpaperHelper
import com.example.ui.theme.*
import com.example.ui.viewmodel.DevicePreviewMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperPreviewDialog(
    wallpaper: Wallpaper,
    isPurchased: Boolean,
    reviews: List<Review>,
    previewMode: DevicePreviewMode,
    onPreviewModeChange: (DevicePreviewMode) -> Unit,
    onClose: () -> Unit,
    onBuyClicked: () -> Unit,
    onApplyWallpaper: (WallpaperHelper.TargetScreen) -> Unit,
    onDownload: (resolution: String) -> Unit,
    onOpenReport: () -> Unit,
    onOpenReview: () -> Unit
) {
    val context = LocalContext.current
    var showApplyMenu by remember { mutableStateOf(false) }
    var showDownloadMenu by remember { mutableStateOf(false) }
    val wpReviews = remember(reviews, wallpaper.id) {
        reviews.filter { it.wallpaperId == wallpaper.id }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .testTag("wallpaper_preview_dialog")
        ) {
            // Main Wallpaper image as background canvas
            if (wallpaper.drawableResId != null) {
                Image(
                    painter = painterResource(id = wallpaper.drawableResId),
                    contentDescription = wallpaper.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (!wallpaper.imageUri.isNullOrEmpty()) {
                AsyncImage(
                    model = wallpaper.imageUri,
                    contentDescription = wallpaper.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Security Watermark if unpaid & not purchased
            if (wallpaper.isPaid && !isPurchased) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .rotate(-28f)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "WALLPIX MARKET",
                            color = Color.White.copy(alpha = 0.45f),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 4.sp
                        )
                        Text(
                            text = "PROTECTED PREVIEW • PURCHASE TO UNLOCK FULL ${wallpaper.resolution.take(2)}",
                            color = Color.White.copy(alpha = 0.40f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Top scrim & bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .testTag("preview_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Mode Switcher Pill (Normal, Lock Screen, Home Screen)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                            .padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(
                            DevicePreviewMode.OFF to "Fit",
                            DevicePreviewMode.LOCK_SCREEN to "Lock",
                            DevicePreviewMode.HOME_SCREEN to "Home"
                        ).forEach { (mode, title) ->
                            val isSelected = previewMode == mode
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) PrimaryNeon else Color.Transparent)
                                    .clickable { onPreviewModeChange(mode) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }

                    Row {
                        IconButton(
                            onClick = onOpenReport,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("report_wallpaper_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Flag,
                                contentDescription = "Report",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Realistic Lock Screen Overlay Mockup
            if (previewMode == DevicePreviewMode.LOCK_SCREEN) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(top = 80.dp, bottom = 40.dp)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sunday, October 4",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "10:48",
                            fontSize = 76.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Dynamic Notification pill
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.45f)),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = SecondaryCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Wallpix Market",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "8K Master unlocked & calibrated for AMOLED",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Lockscreen icons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FlashlightOn, contentDescription = null, tint = Color.White)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = null, tint = Color.White)
                            Text("Swipe up to unlock", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }

            // Realistic Home Screen Overlay Mockup
            if (previewMode == DevicePreviewMode.HOME_SCREEN) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(top = 90.dp, bottom = 40.dp)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Google / Search widget
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(Color.Black.copy(alpha = 0.45f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(25.dp))
                            .padding(horizontal = 18.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Search or type URL", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White)
                        }
                    }

                    // Bottom App Dock
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            Icons.Default.Phone to Color(0xFF00E676),
                            Icons.Default.Message to Color(0xFF2979FF),
                            Icons.Default.Wallpaper to PrimaryNeon,
                            Icons.Default.PhotoCamera to Color(0xFFFF9100)
                        ).forEach { (icon, color) ->
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(color),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }

            // Bottom Action & Details Sheet
            if (previewMode == DevicePreviewMode.OFF) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0xE60A0D14),
                                    Color(0xFF0A0D14)
                                )
                            )
                        )
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        // Title and Price header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = wallpaper.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "by ${wallpaper.creatorName}",
                                        fontSize = 13.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Creator",
                                        tint = SecondaryCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }

                            // Price Tag
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (!wallpaper.isPaid) SuccessGreen.copy(alpha = 0.2f)
                                    else if (isPurchased) SecondaryCyan.copy(alpha = 0.2f)
                                    else AccentGold.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (!wallpaper.isPaid) SuccessGreen
                                    else if (isPurchased) SecondaryCyan
                                    else AccentGold
                                )
                            ) {
                                Text(
                                    text = if (!wallpaper.isPaid) "FREE"
                                    else if (isPurchased) "PURCHASED"
                                    else "$${String.format("%.2f", wallpaper.price)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!wallpaper.isPaid) SuccessGreen
                                    else if (isPurchased) SecondaryCyan
                                    else AccentGold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Specs pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AssistChip(
                                onClick = {},
                                label = { Text(wallpaper.resolution, fontSize = 11.sp, color = TextPrimary) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Hd,
                                        contentDescription = null,
                                        tint = SecondaryCyan,
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(containerColor = SurfaceCard),
                                border = BorderStroke(1.dp, BorderDark)
                            )
                            AssistChip(
                                onClick = {},
                                label = { Text("${wallpaper.fileSizeMb} MB", fontSize = 11.sp, color = TextPrimary) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Storage,
                                        contentDescription = null,
                                        tint = AccentPink,
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(containerColor = SurfaceCard),
                                border = BorderStroke(1.dp, BorderDark)
                            )
                            AssistChip(
                                onClick = onOpenReview,
                                label = {
                                    Text(
                                        "${String.format("%.1f", wallpaper.rating)} (${wallpaper.reviewCount})",
                                        fontSize = 11.sp,
                                        color = TextPrimary
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AccentGold,
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(containerColor = SurfaceCard),
                                border = BorderStroke(1.dp, BorderDark)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Description
                        Text(
                            text = wallpaper.description,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Primary Action Buttons
                        if (wallpaper.isPaid && !isPurchased) {
                            Button(
                                onClick = onBuyClicked,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("buy_now_preview_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Buy Now • $${String.format("%.2f", wallpaper.price)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Apply as Wallpaper
                                Box(modifier = Modifier.weight(1f)) {
                                    Button(
                                        onClick = { showApplyMenu = true },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("apply_wallpaper_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Icon(Icons.Default.Wallpaper, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Set Wallpaper", fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
                                                onApplyWallpaper(WallpaperHelper.TargetScreen.HOME)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Lock Screen", color = TextPrimary) },
                                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AccentPink) },
                                            onClick = {
                                                showApplyMenu = false
                                                onApplyWallpaper(WallpaperHelper.TargetScreen.LOCK)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Both Screens", color = TextPrimary) },
                                            leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = SuccessGreen) },
                                            onClick = {
                                                showApplyMenu = false
                                                onApplyWallpaper(WallpaperHelper.TargetScreen.BOTH)
                                            }
                                        )
                                    }
                                }

                                // Download Button
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedButton(
                                        onClick = { showDownloadMenu = true },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .testTag("download_button"),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = SurfaceCard,
                                            contentColor = TextPrimary
                                        ),
                                        border = BorderStroke(1.dp, BorderDark)
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Download", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    DropdownMenu(
                                        expanded = showDownloadMenu,
                                        onDismissRequest = { showDownloadMenu = false },
                                        modifier = Modifier
                                            .background(SurfaceDark)
                                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Original Master (${wallpaper.resolution.take(2)})", color = TextPrimary) },
                                            onClick = {
                                                showDownloadMenu = false
                                                onDownload(wallpaper.resolution)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Optimized Mobile 1080p (Fast)", color = TextSecondary) },
                                            onClick = {
                                                showDownloadMenu = false
                                                onDownload("1080p FHD")
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
