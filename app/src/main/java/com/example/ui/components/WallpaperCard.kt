package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Wallpaper
import com.example.ui.theme.*

@Composable
fun WallpaperCard(
    wallpaper: Wallpaper,
    isPurchased: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.62f) // Elegant vertical 9:16-like ratio
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("wallpaper_card_${wallpaper.id}"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Wallpaper image
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
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(PrimaryVariant, BackgroundDark)
                            )
                        )
                )
            }

            // Unpaid padlock badge watermark overlay in center if paid & not purchased
            if (wallpaper.isPaid && !isPurchased) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.18f))
                )
            }

            // Top bar badges: Resolution & Price
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Resolution Badge
                val is8K = wallpaper.resolution.contains("8K")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (is8K) Brush.horizontalGradient(listOf(PrimaryNeon, AccentPink))
                            else Brush.horizontalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF2563EB)))
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (is8K) "8K ULTRA" else "4K UHD",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Price Tag Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (!wallpaper.isPaid) SuccessGreen.copy(alpha = 0.9f)
                            else if (isPurchased) SecondaryCyan.copy(alpha = 0.9f)
                            else AccentGold
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (wallpaper.isPaid && !isPurchased) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Paid",
                                tint = Color.Black,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$${String.format("%.2f", wallpaper.price)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        } else if (isPurchased) {
                            Text(
                                text = "PURCHASED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BackgroundDark
                            )
                        } else {
                            Text(
                                text = "FREE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BackgroundDark
                            )
                        }
                    }
                }
            }

            // Bottom Gradient Scrim & Info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0x990A0D14),
                                Color(0xF00A0D14)
                            )
                        )
                    )
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Column {
                    Text(
                        text = wallpaper.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = wallpaper.creatorName,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.widthIn(max = 85.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Creator",
                                tint = SecondaryCyan,
                                modifier = Modifier.size(11.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = AccentGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format("%.1f", wallpaper.rating),
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
