package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyCheckoutBottomSheet(
    wallpaper: Wallpaper,
    walletBalance: Double,
    platformCommissionPercent: Int,
    onDismiss: () -> Unit,
    onPurchaseConfirmed: (paymentMethod: String, couponCode: String?) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("UPI / Google Pay") }
    var couponText by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf<String?>(null) }
    var couponError by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val discount = if (appliedCoupon == "WALLPIX20") 0.20 else 0.0
    val rawPrice = wallpaper.price
    val discountAmount = rawPrice * discount
    val finalPrice = (rawPrice - discountAmount).coerceAtLeast(0.0)
    val platformCut = finalPrice * (platformCommissionPercent / 100.0)
    val creatorCut = finalPrice - platformCut

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        dragHandle = { BottomSheetDefaults.DragHandle(color = BorderDark) },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("checkout_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Secure Checkout",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_checkout_button")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wallpaper Item Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (wallpaper.drawableResId != null) {
                        Image(
                            painter = painterResource(id = wallpaper.drawableResId),
                            contentDescription = wallpaper.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryVariant)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = wallpaper.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "by ${wallpaper.creatorName}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Master Resolution: ${wallpaper.resolution}",
                            fontSize = 11.sp,
                            color = SecondaryCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "$${String.format("%.2f", rawPrice)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Methods
            Text(
                text = "Select Payment Method",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            val methods = listOf(
                "UPI / Google Pay",
                "Credit / Debit Card",
                "Wallpix Wallet (Bal: $${String.format("%.2f", walletBalance)})"
            )

            methods.forEach { method ->
                val isSelected = selectedMethod.startsWith(method.take(10))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) SurfaceHigh else SurfaceCard)
                        .border(
                            1.dp,
                            if (isSelected) PrimaryNeon else BorderDark,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedMethod = method }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { selectedMethod = method },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = PrimaryNeon,
                            unselectedColor = TextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = method,
                        fontSize = 14.sp,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Promo code coupon
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = couponText,
                    onValueChange = {
                        couponText = it
                        couponError = null
                    },
                    placeholder = { Text("Have coupon? (try WALLPIX20)", fontSize = 12.sp, color = TextTertiary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryNeon,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("coupon_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (couponText.trim().equals("WALLPIX20", ignoreCase = true)) {
                            appliedCoupon = "WALLPIX20"
                            couponError = null
                        } else {
                            couponError = "Invalid code"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("apply_coupon_button")
                ) {
                    Text("Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (appliedCoupon != null) {
                Text(
                    text = "Coupon WALLPIX20 applied (20% OFF)!",
                    fontSize = 11.sp,
                    color = SuccessGreen,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else if (couponError != null) {
                Text(
                    text = couponError ?: "",
                    fontSize = 11.sp,
                    color = ErrorRed,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price Breakdown Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", color = TextSecondary, fontSize = 13.sp)
                        Text("$${String.format("%.2f", rawPrice)}", color = TextPrimary, fontSize = 13.sp)
                    }

                    if (discountAmount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Discount (20%)", color = SuccessGreen, fontSize = 13.sp)
                            Text("-$${String.format("%.2f", discountAmount)}", color = SuccessGreen, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Creator Share (${100 - platformCommissionPercent}%)",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                        Text(
                            "$${String.format("%.2f", creatorCut)}",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = BorderDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Due", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "$${String.format("%.2f", finalPrice)}",
                            color = AccentGold,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pay Button
            Button(
                onClick = {
                    isProcessing = true
                    coroutineScope.launch {
                        delay(1200) // Realistic secure gateway verification
                        isProcessing = false
                        onPurchaseConfirmed(selectedMethod, appliedCoupon)
                    }
                },
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("confirm_pay_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Securing License & Unlocking...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Secure",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pay $${String.format("%.2f", finalPrice)} & Download",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
