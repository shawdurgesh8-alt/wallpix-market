package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.WallpaperCategory
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    viewModel: MarketplaceViewModel,
    modifier: Modifier = Modifier
) {
    val platformSettings = viewModel.platformSettings.collectAsState().value
    val commissionPercent = platformSettings.platformCommissionPercent
    val creatorPercent = 100 - commissionPercent

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(WallpaperCategory.INDIAN_RAILWAYS) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val resolutions = listOf("8K Ultra (7680x4320)", "4K UHD (3840x2160)", "QHD+ (1440x3200)")
    var selectedResolution by remember { mutableStateOf(resolutions[0]) }

    var isFree by remember { mutableStateOf(false) }
    var priceText by remember { mutableStateOf("2.99") }
    var tagsText by remember { mutableStateOf("4K, Indian Railways, Aesthetic") }
    var agreedToCopyright by remember { mutableStateOf(false) }

    // Sample high-res artwork options to select for the upload asset
    val artworkPresets = listOf(
        R.drawable.wp_indian_railway_1791132787481 to "Indian Railways 8K",
        R.drawable.wp_hypercar_1791132810644 to "Hypercar Midnight 8K",
        R.drawable.wp_aurora_1791132829449 to "Aurora Peaks 8K",
        R.drawable.wp_cyber_anime_1791132845884 to "Cyber Samurai 8K"
    )
    var selectedAssetIndex by remember { mutableStateOf(0) }

    val priceVal = if (isFree) 0.0 else priceText.toDoubleOrNull() ?: 0.0
    val creatorCut = priceVal * (creatorPercent / 100.0)
    val platformCut = priceVal * (commissionPercent / 100.0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("upload_screen"),
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
                    text = "Creator Studio • Sell Art",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Upload your high-res wallpapers and earn on every download",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Image Selection Preset Box
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Select Artwork File to Upload",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Large selected preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, PrimaryNeon, RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = artworkPresets[selectedAssetIndex].first),
                        contentDescription = "Selected asset preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Selected: ${artworkPresets[selectedAssetIndex].second}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Preset selector thumbnails
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(artworkPresets.indices.toList()) { index ->
                        val (resId, label) = artworkPresets[index]
                        val isChosen = selectedAssetIndex == index
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    2.dp,
                                    if (isChosen) SecondaryCyan else BorderDark,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedAssetIndex = index }
                        ) {
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        // Wallpaper Details Form
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Title
                Text("Wallpaper Title", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. Vande Bharat Express Golden Hour", fontSize = 13.sp, color = TextTertiary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryNeon,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("upload_title_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text("Description & Camera/Render Details", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Describe location, lighting, camera gear or 3D engine used...", fontSize = 13.sp, color = TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryNeon,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("upload_description_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Dropdown
                Text("Category", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryNeon,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(SurfaceDark)
                    ) {
                        WallpaperCategory.values().filterNot { it == WallpaperCategory.ALL }.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName, color = TextPrimary) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Resolution Choice
                Text("Resolution Package", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    resolutions.forEach { res ->
                        val isSelected = selectedResolution == res
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SurfaceHigh else SurfaceCard)
                                .border(1.dp, if (isSelected) PrimaryNeon else BorderDark, RoundedCornerShape(10.dp))
                                .clickable { selectedResolution = res }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = res.take(8),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PrimaryNeon else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Monetization", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Make Free", fontSize = 12.sp, color = TextSecondary)
                        Switch(
                            checked = isFree,
                            onCheckedChange = { isFree = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = SuccessGreen, checkedTrackColor = SuccessGreen.copy(alpha = 0.4f))
                        )
                    }
                }

                if (!isFree) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price in USD ($)") },
                        leadingIcon = { Text("$", color = AccentGold, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentGold,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_price_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Creator Earning Calculator Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Your Net Payout ($creatorPercent%):",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                                Text(
                                    text = "$${String.format("%.2f", creatorCut)} / sale",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Platform Fee ($commissionPercent%):",
                                    fontSize = 11.sp,
                                    color = TextTertiary
                                )
                                Text(
                                    text = "$${String.format("%.2f", platformCut)}",
                                    fontSize = 11.sp,
                                    color = TextTertiary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Copyright certification
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = agreedToCopyright,
                        onCheckedChange = { agreedToCopyright = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryNeon)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "I certify that I created or hold exclusive commercial distribution rights to this artwork.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        val tagsList = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        viewModel.submitUpload(
                            title = title.ifBlank { "Untitled Artwork" },
                            description = description.ifBlank { "Original 8K wallpaper." },
                            category = selectedCategory,
                            resolution = selectedResolution,
                            price = priceVal,
                            tags = tagsList,
                            drawableResId = artworkPresets[selectedAssetIndex].first,
                            imageUri = null
                        )
                    },
                    enabled = agreedToCopyright && (isFree || priceVal > 0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_upload_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Submit for Admin Review",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
