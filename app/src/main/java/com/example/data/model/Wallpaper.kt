package com.example.data.model

enum class WallpaperCategory(val displayName: String, val iconName: String) {
    ALL("All", "Dashboard"),
    INDIAN_RAILWAYS("Indian Railways", "DirectionsTransit"),
    TRAINS("Trains", "Train"),
    CARS("Cars", "DirectionsCar"),
    BIKES("Bikes", "TwoWheeler"),
    NATURE("Nature", "Forest"),
    GAMING("Gaming", "SportsEsports"),
    ANIME("Anime", "AutoAwesome"),
    TECHNOLOGY("Technology", "Memory"),
    ABSTRACT("Abstract", "BlurOn"),
    RES_4K("4K UHD", "Hd"),
    RES_8K("8K Ultra", "HighDefinition")
}

enum class WallpaperStatus {
    PENDING_REVIEW,
    APPROVED,
    REJECTED
}

data class Wallpaper(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: WallpaperCategory = WallpaperCategory.ALL,
    val resolution: String = "4K UHD (3840x2160)",
    val isPaid: Boolean = false,
    val price: Double = 0.0,
    val creatorId: String = "",
    val creatorName: String = "",
    val creatorAvatar: String = "",
    val drawableResId: Int? = null,
    val imageUri: String? = null,
    val rating: Float = 4.8f,
    val reviewCount: Int = 12,
    val downloadsCount: Int = 1420,
    val viewsCount: Int = 8900,
    val status: WallpaperStatus = WallpaperStatus.APPROVED,
    val rejectionReason: String? = null,
    val uploadedAt: Long = System.currentTimeMillis(),
    val isFeatured: Boolean = false,
    val tags: List<String> = emptyList(),
    val fileSizeMb: Double = 14.2
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val avatarUrl: String = "",
    val isCreator: Boolean = false,
    val isAdmin: Boolean = false,
    val bio: String = "Digital visual enthusiast & collector",
    val walletBalance: Double = 50.0,
    val creatorEarnings: Double = 348.50,
    val verifiedCreator: Boolean = false
)

data class PurchaseOrder(
    val orderId: String,
    val wallpaperId: String,
    val wallpaperTitle: String,
    val userId: String,
    val purchaseDate: Long,
    val amountPaid: Double,
    val platformFee: Double,
    val creatorShare: Double,
    val paymentMethod: String,
    val resolutionUnlocked: String,
    val downloadToken: String
)

data class Review(
    val id: String,
    val wallpaperId: String,
    val userName: String,
    val rating: Int,
    val comment: String,
    val timestamp: Long
)

data class ReportComplaint(
    val id: String,
    val wallpaperId: String,
    val wallpaperTitle: String,
    val reportedBy: String,
    val reason: String,
    val details: String,
    val timestamp: Long,
    val status: String = "PENDING" // "PENDING", "RESOLVED", "CONTENT_REMOVED"
)

data class TransactionRecord(
    val id: String,
    val type: String, // "WALLPAPER_SALE", "PLATFORM_COMMISSION", "PAYOUT_WITHDRAWAL", "PURCHASE"
    val amount: Double,
    val description: String,
    val timestamp: Long,
    val status: String = "COMPLETED"
)

data class PlatformSettings(
    val platformCommissionPercent: Int = 20, // 20% platform, 80% creator
    val featuredListingPrice: Double = 4.99,
    val allowAutoApproval: Boolean = false
)
