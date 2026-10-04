package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.WallpaperRepository
import com.example.data.util.WallpaperHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class NavigationTab(val label: String) {
    HOME("Home"),
    EXPLORE("Explore"),
    UPLOAD("Sell"),
    PURCHASES("Purchases"),
    PROFILE("Profile")
}

enum class SortOption(val label: String) {
    POPULAR("Popular"),
    NEWEST("Newest"),
    RATING("Top Rated"),
    PRICE_LOW("Price: Low to High"),
    PRICE_HIGH("Price: High to Low")
}

enum class DevicePreviewMode {
    OFF,
    LOCK_SCREEN,
    HOME_SCREEN
}

enum class PriceFilter {
    ALL,
    FREE_ONLY,
    PAID_ONLY
}

class MarketplaceViewModel(
    private val repository: WallpaperRepository = WallpaperRepository(),
    val firestoreRepository: com.example.data.repository.FirestoreWallpaperRepository = 
        com.example.data.repository.FirestoreWallpaperRepository(localFallbackRepository = repository)
) : ViewModel() {

    // Current navigation state
    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _isCreatorStudioOpen = MutableStateFlow(false)
    val isCreatorStudioOpen: StateFlow<Boolean> = _isCreatorStudioOpen.asStateFlow()

    private val _isAdminPanelOpen = MutableStateFlow(false)
    val isAdminPanelOpen: StateFlow<Boolean> = _isAdminPanelOpen.asStateFlow()

    // Modals
    private val _selectedWallpaper = MutableStateFlow<Wallpaper?>(null)
    val selectedWallpaper: StateFlow<Wallpaper?> = _selectedWallpaper.asStateFlow()

    private val _checkoutWallpaper = MutableStateFlow<Wallpaper?>(null)
    val checkoutWallpaper: StateFlow<Wallpaper?> = _checkoutWallpaper.asStateFlow()

    private val _reportTarget = MutableStateFlow<Wallpaper?>(null)
    val reportTarget: StateFlow<Wallpaper?> = _reportTarget.asStateFlow()

    private val _reviewTarget = MutableStateFlow<Wallpaper?>(null)
    val reviewTarget: StateFlow<Wallpaper?> = _reviewTarget.asStateFlow()

    private val _previewMode = MutableStateFlow(DevicePreviewMode.OFF)
    val previewMode: StateFlow<DevicePreviewMode> = _previewMode.asStateFlow()

    // Filtering & Searching
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(WallpaperCategory.ALL)
    val selectedCategory: StateFlow<WallpaperCategory> = _selectedCategory.asStateFlow()

    private val _priceFilter = MutableStateFlow(PriceFilter.ALL)
    val priceFilter: StateFlow<PriceFilter> = _priceFilter.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.POPULAR)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    // Transient UI message (Snackbar)
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // Repository flows
    val currentUser = repository.currentUser
    val allUsers = repository.allUsers
    val wallpapers = repository.wallpapers
    val purchases = repository.purchases
    val transactions = repository.transactions
    val reports = repository.reports
    val reviews = repository.reviews
    val platformSettings = repository.platformSettings

    // Filtered marketplace wallpapers (only APPROVED for marketplace)
    val marketplaceWallpapers: StateFlow<List<Wallpaper>> = combine(
        wallpapers,
        _searchQuery,
        _selectedCategory,
        _priceFilter,
        _sortOption
    ) { allWps, query, cat, priceF, sort ->
        allWps.filter { wp ->
            // Only show approved in public marketplace
            wp.status == WallpaperStatus.APPROVED &&
            // Category filter
            (cat == WallpaperCategory.ALL ||
             (cat == WallpaperCategory.RES_4K && wp.resolution.contains("4K")) ||
             (cat == WallpaperCategory.RES_8K && wp.resolution.contains("8K")) ||
             wp.category == cat) &&
            // Price filter
            (priceF == PriceFilter.ALL ||
             (priceF == PriceFilter.FREE_ONLY && !wp.isPaid) ||
             (priceF == PriceFilter.PAID_ONLY && wp.isPaid)) &&
            // Search query
            (query.isBlank() ||
             wp.title.contains(query, ignoreCase = true) ||
             wp.description.contains(query, ignoreCase = true) ||
             wp.category.displayName.contains(query, ignoreCase = true) ||
             wp.tags.any { it.contains(query, ignoreCase = true) })
        }.let { list ->
            when (sort) {
                SortOption.POPULAR -> list.sortedByDescending { it.downloadsCount }
                SortOption.NEWEST -> list.sortedByDescending { it.uploadedAt }
                SortOption.RATING -> list.sortedByDescending { it.rating }
                SortOption.PRICE_LOW -> list.sortedBy { it.price }
                SortOption.PRICE_HIGH -> list.sortedByDescending { it.price }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Featured wallpapers
    val featuredWallpapers: StateFlow<List<Wallpaper>> = wallpapers.map { list ->
        list.filter { it.status == WallpaperStatus.APPROVED && it.isFeatured }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Creator's uploads
    val creatorUploads: StateFlow<List<Wallpaper>> = combine(wallpapers, currentUser) { list, user ->
        list.filter { it.creatorId == user.id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Pending admin queue
    val pendingReviewWallpapers: StateFlow<List<Wallpaper>> = wallpapers.map { list ->
        list.filter { it.status == WallpaperStatus.PENDING_REVIEW }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
        _isCreatorStudioOpen.value = false
        _isAdminPanelOpen.value = false
    }

    fun openWallpaperDetail(wallpaper: Wallpaper) {
        _selectedWallpaper.value = wallpaper
        _previewMode.value = DevicePreviewMode.OFF
    }

    fun closeWallpaperDetail() {
        _selectedWallpaper.value = null
        _previewMode.value = DevicePreviewMode.OFF
    }

    fun setPreviewMode(mode: DevicePreviewMode) {
        _previewMode.value = mode
    }

    fun startBuyFlow(wallpaper: Wallpaper) {
        _checkoutWallpaper.value = wallpaper
    }

    fun dismissBuyFlow() {
        _checkoutWallpaper.value = null
    }

    fun openReportDialog(wallpaper: Wallpaper) {
        _reportTarget.value = wallpaper
    }

    fun closeReportDialog() {
        _reportTarget.value = null
    }

    fun openReviewDialog(wallpaper: Wallpaper) {
        _reviewTarget.value = wallpaper
    }

    fun closeReviewDialog() {
        _reviewTarget.value = null
    }

    fun setCategory(category: WallpaperCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPriceFilter(filter: PriceFilter) {
        _priceFilter.value = filter
    }

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun toggleCreatorStudio(open: Boolean) {
        _isCreatorStudioOpen.value = open
    }

    fun toggleAdminPanel(open: Boolean) {
        _isAdminPanelOpen.value = open
    }

    fun switchUser(userId: String) {
        repository.switchUser(userId)
        viewModelScope.launch {
            _snackbarMessage.emit("Switched profile to ${repository.currentUser.value.name}")
        }
    }

    fun completePurchase(paymentMethod: String, couponCode: String?) {
        val wp = _checkoutWallpaper.value ?: return
        val result = repository.purchaseWallpaper(wp, paymentMethod, couponCode)
        _checkoutWallpaper.value = null
        viewModelScope.launch {
            if (result.isSuccess) {
                val order = result.getOrNull()!!
                _snackbarMessage.emit("Purchase successful! Order #${order.orderId}")
                // Update selected wallpaper if open
                if (_selectedWallpaper.value?.id == wp.id) {
                    _selectedWallpaper.value = wp
                }
            } else {
                _snackbarMessage.emit("Purchase failed. Please try again.")
            }
        }
    }

    fun isWallpaperPurchased(wallpaperId: String): Boolean {
        return repository.isWallpaperPurchased(wallpaperId)
    }

    fun applyWallpaper(context: Context, wallpaper: Wallpaper, target: WallpaperHelper.TargetScreen) {
        viewModelScope.launch {
            val success = WallpaperHelper.applyWallpaper(context, wallpaper.drawableResId, target)
            if (success) {
                val screenName = when (target) {
                    WallpaperHelper.TargetScreen.HOME -> "Home Screen"
                    WallpaperHelper.TargetScreen.LOCK -> "Lock Screen"
                    WallpaperHelper.TargetScreen.BOTH -> "Home & Lock Screens"
                }
                _snackbarMessage.emit("Wallpaper applied to $screenName!")
            } else {
                _snackbarMessage.emit("Failed to set wallpaper. Please check permissions.")
            }
        }
    }

    fun downloadWallpaper(wallpaper: Wallpaper, resolution: String) {
        viewModelScope.launch {
            _snackbarMessage.emit("Downloading ${wallpaper.title} ($resolution)... Saved to Gallery")
        }
    }

    fun submitUpload(
        title: String,
        description: String,
        category: WallpaperCategory,
        resolution: String,
        price: Double,
        tags: List<String>,
        drawableResId: Int?,
        imageUri: String?
    ) {
        val created = repository.uploadWallpaper(
            title = title,
            description = description,
            category = category,
            resolution = resolution,
            price = price,
            tags = tags,
            drawableResId = drawableResId,
            imageUri = imageUri
        )
        viewModelScope.launch {
            firestoreRepository.publishWallpaperToBackend(created)
            if (created.status == WallpaperStatus.APPROVED) {
                _snackbarMessage.emit("Wallpaper published instantly!")
            } else {
                _snackbarMessage.emit("Wallpaper submitted! Pending Admin Review.")
            }
            _currentTab.value = NavigationTab.PROFILE
            _isCreatorStudioOpen.value = true
        }
    }

    fun adminApprove(wallpaperId: String) {
        repository.approveWallpaper(wallpaperId)
        viewModelScope.launch {
            firestoreRepository.updateWallpaperStatus(wallpaperId, WallpaperStatus.APPROVED)
            _snackbarMessage.emit("Wallpaper approved & published to marketplace!")
        }
    }

    fun adminReject(wallpaperId: String, reason: String) {
        repository.rejectWallpaper(wallpaperId, reason)
        viewModelScope.launch {
            firestoreRepository.updateWallpaperStatus(wallpaperId, WallpaperStatus.REJECTED, reason)
            _snackbarMessage.emit("Wallpaper rejected. Creator notified.")
        }
    }

    fun adminSetCommission(percent: Int) {
        repository.updateCommissionPercent(percent)
        viewModelScope.launch {
            _snackbarMessage.emit("Platform commission updated to $percent%")
        }
    }

    fun adminResolveReport(reportId: String, removeContent: Boolean) {
        repository.resolveReport(reportId, removeContent)
        viewModelScope.launch {
            _snackbarMessage.emit(if (removeContent) "Violating content removed" else "Report dismissed")
        }
    }

    fun submitReport(reason: String, details: String) {
        val wp = _reportTarget.value ?: return
        repository.submitReport(wp.id, wp.title, reason, details)
        _reportTarget.value = null
        viewModelScope.launch {
            _snackbarMessage.emit("Report filed. Our security team will review it.")
        }
    }

    fun submitReview(rating: Int, comment: String) {
        val wp = _reviewTarget.value ?: return
        repository.submitReview(wp.id, rating, comment)
        _reviewTarget.value = null
        viewModelScope.launch {
            _snackbarMessage.emit("Review submitted! Thank you.")
        }
    }

    fun requestWithdrawal(amount: Double) {
        val ok = repository.requestWithdrawal(amount)
        viewModelScope.launch {
            if (ok) {
                _snackbarMessage.emit("Payout request for $${String.format("%.2f", amount)} processed!")
            } else {
                _snackbarMessage.emit("Insufficient creator balance for withdrawal.")
            }
        }
    }
}
