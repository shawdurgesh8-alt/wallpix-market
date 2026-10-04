package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WallpaperCategory
import com.example.ui.components.WallpaperCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel
import com.example.ui.viewmodel.PriceFilter
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery = viewModel.searchQuery.collectAsState().value
    val selectedCategory = viewModel.selectedCategory.collectAsState().value
    val priceFilter = viewModel.priceFilter.collectAsState().value
    val sortOption = viewModel.sortOption.collectAsState().value
    val wallpapers = viewModel.marketplaceWallpapers.collectAsState().value

    var showSortMenu by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Search Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Explore Wallpapers",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Find the perfect 4K & 8K art for your screen",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            "Search Indian Railways, Supercars, Anime, 8K...",
                            fontSize = 13.sp,
                            color = TextTertiary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PrimaryNeon
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = PrimaryNeon,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_input")
                )
            }
        }

        // Category Horizontal Chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
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

        // Filter & Sort Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price filters (All, Free, Paid)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        PriceFilter.ALL to "All",
                        PriceFilter.FREE_ONLY to "Free",
                        PriceFilter.PAID_ONLY to "Premium"
                    ).forEach { (filter, label) ->
                        val isSelected = priceFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SecondaryCyan.copy(alpha = 0.2f) else SurfaceCard)
                                .border(
                                    1.dp,
                                    if (isSelected) SecondaryCyan else BorderDark,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setPriceFilter(filter) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SecondaryCyan else TextSecondary
                            )
                        }
                    }
                }

                // Sort Dropdown
                Box {
                    OutlinedButton(
                        onClick = { showSortMenu = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SurfaceCard,
                            contentColor = TextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = null,
                            tint = AccentGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(sortOption.label, fontSize = 11.sp)
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier
                            .background(SurfaceDark)
                            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
                    ) {
                        SortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label, color = TextPrimary, fontSize = 12.sp) },
                                onClick = {
                                    viewModel.setSortOption(option)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Results count
        item {
            Text(
                text = "${wallpapers.size} Wallpapers found",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }

        // Empty state or Grid
        if (wallpapers.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp, bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Wallpapers Found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Try clearing filters or search for another theme",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.setSearchQuery("")
                            viewModel.setCategory(WallpaperCategory.ALL)
                            viewModel.setPriceFilter(PriceFilter.ALL)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Filters")
                    }
                }
            }
        } else {
            val chunked = wallpapers.chunked(2)
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
}
