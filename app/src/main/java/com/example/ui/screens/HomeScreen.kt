package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.Wallpaper
import com.example.data.model.WallpaperCategory
import com.example.ui.components.WallpaperCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun HomeScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToExplore: () -> Unit = {},
    onNavigateToPurchases: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val featuredList = viewModel.featuredWallpapers.value
    val allApproved = viewModel.marketplaceWallpapers.value
    val selectedCategory = viewModel.selectedCategory.value
    val purchases = viewModel.purchases.value

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "WALLPIX",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = PrimaryNeon
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MARKET",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = SecondaryCyan
                        )
                    }
                    Text(
                        text = "Curated 4K & 8K Creator Wallpapers",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Balance / Cart badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceCard)
                        .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                        .clickable { onNavigateToPurchases() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = "Purchases",
                            tint = SecondaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${purchases.size}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Hero Featured Carousel
        if (featuredList.isNotEmpty()) {
            item {
                val hero = featuredList.first()
                val isPurchased = viewModel.isWallpaperPurchased(hero.id)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                        .height(220.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.5.dp, PrimaryNeon.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .clickable { viewModel.openWallpaperDetail(hero) }
                        .testTag("hero_banner_card"),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (hero.drawableResId != null) {
                            Image(
                                painter = painterResource(id = hero.drawableResId),
                                contentDescription = hero.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Gradient Scrim
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0x800A0D14),
                                            Color(0xF20A0D14)
                                        )
                                    )
                                )
                        )

                        // Hero Content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Brush.horizontalGradient(listOf(PrimaryNeon, AccentPink)))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "EDITOR'S CHOICE • 8K",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AccentGold)
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = "$${String.format("%.2f", hero.price)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = hero.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "By ${hero.creatorName} • ${hero.category.displayName}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = { viewModel.openWallpaperDetail(hero) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.height(38.dp)
                                    ) {
                                        Text("Preview 8K", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    if (hero.isPaid && !isPurchased) {
                                        OutlinedButton(
                                            onClick = { viewModel.startBuyFlow(hero) },
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                containerColor = Color.Black.copy(alpha = 0.5f),
                                                contentColor = Color.White
                                            ),
                                            border = BorderStroke(1.dp, BorderDark),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.height(38.dp)
                                        ) {
                                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Buy Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Horizontal Chips
        item {
            Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categories",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "See all",
                        fontSize = 12.sp,
                        color = SecondaryCyan,
                        modifier = Modifier.clickable {
                            onNavigateToExplore()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(WallpaperCategory.values()) { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) PrimaryNeon else SurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) PrimaryNeon else BorderDark,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.setCategory(cat) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cat.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Section: Trending & Popular Wallpapers
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trending Wallpapers",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${allApproved.size} items",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 2-Column Grid of Wallpapers
        val chunked = allApproved.chunked(2)
        items(chunked) { rowWps ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                rowWps.forEach { wp ->
                    WallpaperCard(
                        wallpaper = wp,
                        isPurchased = viewModel.isWallpaperPurchased(wp.id),
                        onClick = { viewModel.openWallpaperDetail(wp) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowWps.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
