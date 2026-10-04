package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BuyCheckoutBottomSheet
import com.example.ui.components.ReportDialog
import com.example.ui.components.ReviewDialog
import com.example.ui.components.WallpaperPreviewDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel
import com.example.ui.viewmodel.NavigationTab
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WallpixTheme {
                WallpixMarketApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpixMarketApp(
    viewModel: MarketplaceViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentTab = viewModel.currentTab.collectAsState().value
    val isCreatorStudioOpen = viewModel.isCreatorStudioOpen.collectAsState().value
    val isAdminPanelOpen = viewModel.isAdminPanelOpen.collectAsState().value

    val selectedWallpaper = viewModel.selectedWallpaper.collectAsState().value
    val checkoutWallpaper = viewModel.checkoutWallpaper.collectAsState().value
    val reportTarget = viewModel.reportTarget.collectAsState().value
    val reviewTarget = viewModel.reviewTarget.collectAsState().value
    val previewMode = viewModel.previewMode.collectAsState().value
    val reviews = viewModel.reviews.collectAsState().value
    val currentUser = viewModel.currentUser.collectAsState().value
    val platformSettings = viewModel.platformSettings.collectAsState().value

    val snackbarHostState = remember { SnackbarHostState() }

    // Collect snackbars
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
        }
    }

    // Back handling
    BackHandler(enabled = isCreatorStudioOpen || isAdminPanelOpen || selectedWallpaper != null) {
        when {
            selectedWallpaper != null -> viewModel.closeWallpaperDetail()
            isCreatorStudioOpen -> viewModel.toggleCreatorStudio(false)
            isAdminPanelOpen -> viewModel.toggleAdminPanel(false)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding()
            )
        },
        bottomBar = {
            if (!isCreatorStudioOpen && !isAdminPanelOpen) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .border(1.dp, BorderDark, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .testTag("bottom_nav_bar")
                ) {
                    val tabs = listOf(
                        NavTabItem(NavigationTab.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
                        NavTabItem(NavigationTab.EXPLORE, "Explore", Icons.Filled.Explore, Icons.Outlined.Explore),
                        NavTabItem(NavigationTab.UPLOAD, "Sell", Icons.Filled.AddCircle, Icons.Outlined.AddCircleOutline),
                        NavTabItem(NavigationTab.PURCHASES, "Vault", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
                        NavTabItem(NavigationTab.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
                    )

                    tabs.forEach { item ->
                        val isSelected = currentTab == item.tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(item.tab) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryNeon,
                                selectedTextColor = PrimaryNeon,
                                indicatorColor = PrimaryNeon.copy(alpha = 0.15f),
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_${item.tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when {
                isAdminPanelOpen -> {
                    AdminPanelScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.toggleAdminPanel(false) }
                    )
                }
                isCreatorStudioOpen -> {
                    CreatorDashboardScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.toggleCreatorStudio(false) }
                    )
                }
                else -> {
                    when (currentTab) {
                        NavigationTab.HOME -> HomeScreen(viewModel = viewModel)
                        NavigationTab.EXPLORE -> ExploreScreen(viewModel = viewModel)
                        NavigationTab.UPLOAD -> UploadScreen(viewModel = viewModel)
                        NavigationTab.PURCHASES -> PurchasesScreen(viewModel = viewModel)
                        NavigationTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                    }
                }
            }

            // Wallpaper Detail & Interactive Preview Modal
            if (selectedWallpaper != null) {
                val wp = selectedWallpaper
                val isPurchased = viewModel.isWallpaperPurchased(wp.id)
                WallpaperPreviewDialog(
                    wallpaper = wp,
                    isPurchased = isPurchased,
                    reviews = reviews,
                    previewMode = previewMode,
                    onPreviewModeChange = { viewModel.setPreviewMode(it) },
                    onClose = { viewModel.closeWallpaperDetail() },
                    onBuyClicked = { viewModel.startBuyFlow(wp) },
                    onApplyWallpaper = { target ->
                        viewModel.applyWallpaper(context, wp, target)
                    },
                    onDownload = { res ->
                        viewModel.downloadWallpaper(wp, res)
                    },
                    onOpenReport = { viewModel.openReportDialog(wp) },
                    onOpenReview = { viewModel.openReviewDialog(wp) }
                )
            }

            // Buy Checkout Bottom Sheet
            if (checkoutWallpaper != null) {
                val wp = checkoutWallpaper
                BuyCheckoutBottomSheet(
                    wallpaper = wp,
                    walletBalance = currentUser.walletBalance,
                    platformCommissionPercent = platformSettings.platformCommissionPercent,
                    onDismiss = { viewModel.dismissBuyFlow() },
                    onPurchaseConfirmed = { method, coupon ->
                        viewModel.completePurchase(method, coupon)
                    }
                )
            }

            // Report Dialog
            if (reportTarget != null) {
                val wp = reportTarget
                ReportDialog(
                    wallpaper = wp,
                    onDismiss = { viewModel.closeReportDialog() },
                    onSubmitReport = { reason, details ->
                        viewModel.submitReport(reason, details)
                    }
                )
            }

            // Review Dialog
            if (reviewTarget != null) {
                val wp = reviewTarget
                ReviewDialog(
                    wallpaper = wp,
                    onDismiss = { viewModel.closeReviewDialog() },
                    onSubmitReview = { rating, comment ->
                        viewModel.submitReview(rating, comment)
                    }
                )
            }
        }
    }
}

private data class NavTabItem(
    val tab: NavigationTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
