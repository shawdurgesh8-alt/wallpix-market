package com.example.data.repository

import com.example.R
import com.example.data.model.*
import com.example.data.util.WallpaperHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class WallpaperRepository {

    // Preloaded users
    private val defaultUsers = listOf(
        User(
            id = "user_buyer",
            name = "Durgesh Shaw",
            email = "shawdurgesh8@gmail.com",
            phone = "+91 98765 43210",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            isCreator = true,
            isAdmin = false,
            bio = "Digital art enthusiast & mobile customizer",
            walletBalance = 85.00,
            creatorEarnings = 120.40,
            verifiedCreator = true
        ),
        User(
            id = "user_creator_alex",
            name = "Alex Vance",
            email = "alex.vance@studio.art",
            phone = "+1 (555) 234-5678",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            isCreator = true,
            isAdmin = false,
            bio = "Cinematic 8K 3D Render Artist & Railway Photographer",
            walletBalance = 45.00,
            creatorEarnings = 742.60,
            verifiedCreator = true
        ),
        User(
            id = "user_admin",
            name = "Wallpix Master Admin",
            email = "admin@wallpixmarket.io",
            phone = "+1 (800) 555-0199",
            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde",
            isCreator = false,
            isAdmin = true,
            bio = "Platform Integrity & Marketplace Operations Director",
            walletBalance = 999.00,
            creatorEarnings = 0.0,
            verifiedCreator = false
        )
    )

    private val _currentUser = MutableStateFlow(defaultUsers[0])
    val currentUser: StateFlow<User> = _currentUser.asStateFlow()

    private val _allUsers = MutableStateFlow(defaultUsers)
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    private val _platformSettings = MutableStateFlow(PlatformSettings(platformCommissionPercent = 20))
    val platformSettings: StateFlow<PlatformSettings> = _platformSettings.asStateFlow()

    // Initial marketplace catalog
    private val initialWallpapers = listOf(
        Wallpaper(
            id = "wp_1",
            title = "Vande Bharat Golden Sunset",
            description = "Cinematic 8K capture of India's flagship high-speed semi-bullet train cruising through the Western Ghats during golden hour.",
            category = WallpaperCategory.INDIAN_RAILWAYS,
            resolution = "8K Ultra (7680x4320)",
            isPaid = true,
            price = 2.99,
            creatorId = "user_creator_alex",
            creatorName = "Alex Vance",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            drawableResId = R.drawable.wp_indian_railway_1791132787481,
            rating = 4.9f,
            reviewCount = 48,
            downloadsCount = 2840,
            viewsCount = 14200,
            status = WallpaperStatus.APPROVED,
            isFeatured = true,
            tags = listOf("Vande Bharat", "Indian Railways", "8K", "Sunset", "Speed"),
            fileSizeMb = 18.4
        ),
        Wallpaper(
            id = "wp_2",
            title = "Cyber Hypercar Midnight Drift",
            description = "Futuristic aerodynamic hypercar drenched in rain and glowing neon city reflections in dark neo-Tokyo streets.",
            category = WallpaperCategory.CARS,
            resolution = "8K Ultra (7680x4320)",
            isPaid = true,
            price = 3.49,
            creatorId = "user_creator_alex",
            creatorName = "Alex Vance",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            drawableResId = R.drawable.wp_hypercar_1791132810644,
            rating = 5.0f,
            reviewCount = 92,
            downloadsCount = 5120,
            viewsCount = 28900,
            status = WallpaperStatus.APPROVED,
            isFeatured = true,
            tags = listOf("Supercar", "Cyberpunk", "Neon", "Rain", "8K"),
            fileSizeMb = 21.0
        ),
        Wallpaper(
            id = "wp_3",
            title = "Celestial Aurora Mountain Lake",
            description = "Spectacular polar light curtains reflecting over undisturbed glacial waters beneath towering snow-capped summits.",
            category = WallpaperCategory.NATURE,
            resolution = "8K Ultra (7680x4320)",
            isPaid = false,
            price = 0.0,
            creatorId = "user_creator_alex",
            creatorName = "Alex Vance",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            drawableResId = R.drawable.wp_aurora_1791132829449,
            rating = 4.8f,
            reviewCount = 134,
            downloadsCount = 8900,
            viewsCount = 34500,
            status = WallpaperStatus.APPROVED,
            isFeatured = true,
            tags = listOf("Aurora", "Nature", "Mountains", "Free", "8K"),
            fileSizeMb = 16.8
        ),
        Wallpaper(
            id = "wp_4",
            title = "Neo Tokyo Cyber Samurai",
            description = "Futuristic blade warrior overlooking a rain-drenched cyberpunk metropolis from skyscraper heights with glowing holographic katana.",
            category = WallpaperCategory.ANIME,
            resolution = "8K Ultra (7680x4320)",
            isPaid = true,
            price = 2.49,
            creatorId = "user_buyer",
            creatorName = "Durgesh Shaw",
            creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            drawableResId = R.drawable.wp_cyber_anime_1791132845884,
            rating = 4.9f,
            reviewCount = 64,
            downloadsCount = 3710,
            viewsCount = 19400,
            status = WallpaperStatus.APPROVED,
            isFeatured = true,
            tags = listOf("Anime", "Samurai", "Cyberpunk", "Gaming", "4K"),
            fileSizeMb = 19.5
        ),
        Wallpaper(
            id = "wp_5",
            title = "WAP-7 High Voltage Express",
            description = "The mighty electric locomotive WAP-7 roaring down the grand trunk route in monsoon thunder.",
            category = WallpaperCategory.INDIAN_RAILWAYS,
            resolution = "4K UHD (3840x2160)",
            isPaid = false,
            price = 0.0,
            creatorId = "user_creator_alex",
            creatorName = "Alex Vance",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            drawableResId = R.drawable.wp_indian_railway_1791132787481,
            rating = 4.7f,
            reviewCount = 31,
            downloadsCount = 1980,
            viewsCount = 9200,
            status = WallpaperStatus.APPROVED,
            isFeatured = false,
            tags = listOf("Indian Railways", "WAP7", "Locomotive", "Free"),
            fileSizeMb = 11.2
        ),
        Wallpaper(
            id = "wp_6",
            title = "Shinkansen Bullet Train Sunset",
            description = "Japanese series N700S high-speed train cutting across Mount Fuji foothills at dusk.",
            category = WallpaperCategory.TRAINS,
            resolution = "4K UHD (3840x2160)",
            isPaid = true,
            price = 1.99,
            creatorId = "user_creator_alex",
            creatorName = "Alex Vance",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            drawableResId = R.drawable.wp_indian_railway_1791132787481,
            rating = 4.8f,
            reviewCount = 27,
            downloadsCount = 1450,
            viewsCount = 7600,
            status = WallpaperStatus.APPROVED,
            isFeatured = false,
            tags = listOf("Trains", "Bullet Train", "Japan", "Sunset"),
            fileSizeMb = 12.8
        ),
        Wallpaper(
            id = "wp_7",
            title = "Ducati Panigale Neon Beast",
            description = "Carbon fiber Italian superbike standing idle on a rooftop above neon-soaked streets.",
            category = WallpaperCategory.BIKES,
            resolution = "8K Ultra (7680x4320)",
            isPaid = true,
            price = 2.99,
            creatorId = "user_buyer",
            creatorName = "Durgesh Shaw",
            creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            drawableResId = R.drawable.wp_hypercar_1791132810644,
            rating = 4.9f,
            reviewCount = 39,
            downloadsCount = 2210,
            viewsCount = 11200,
            status = WallpaperStatus.APPROVED,
            isFeatured = false,
            tags = listOf("Bikes", "Superbike", "Ducati", "Carbon", "8K"),
            fileSizeMb = 17.5
        ),
        Wallpaper(
            id = "wp_8",
            title = "Quantum Core Neural Processor",
            description = "3D crystalline superconducting quantum processing unit glowing with optic light circuits.",
            category = WallpaperCategory.TECHNOLOGY,
            resolution = "8K Ultra (7680x4320)",
            isPaid = true,
            price = 3.99,
            creatorId = "user_creator_alex",
            creatorName = "Alex Vance",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            drawableResId = R.drawable.wp_cyber_anime_1791132845884,
            rating = 5.0f,
            reviewCount = 55,
            downloadsCount = 4120,
            viewsCount = 18700,
            status = WallpaperStatus.APPROVED,
            isFeatured = false,
            tags = listOf("Technology", "Quantum", "AI", "Hardware", "8K"),
            fileSizeMb = 22.1
        ),
        Wallpaper(
            id = "wp_9",
            title = "Liquid Chrome Prismatic Void",
            description = "Organic flowing metallic chrome fluid distorting vivid holographic prisms in zero gravity.",
            category = WallpaperCategory.ABSTRACT,
            resolution = "4K UHD (3840x2160)",
            isPaid = false,
            price = 0.0,
            creatorId = "user_buyer",
            creatorName = "Durgesh Shaw",
            creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb",
            drawableResId = R.drawable.wp_aurora_1791132829449,
            rating = 4.6f,
            reviewCount = 19,
            downloadsCount = 3300,
            viewsCount = 14000,
            status = WallpaperStatus.APPROVED,
            isFeatured = false,
            tags = listOf("Abstract", "Liquid", "Chrome", "Free", "4K"),
            fileSizeMb = 13.9
        ),
        Wallpaper(
            id = "wp_10",
            title = "Synthwave Arcade 1989",
            description = "Retro-futuristic neon grid horizon with wireframe mountains and glowing magenta sun.",
            category = WallpaperCategory.GAMING,
            resolution = "4K UHD (3840x2160)",
            isPaid = true,
            price = 1.49,
            creatorId = "user_creator_alex",
            creatorName = "Alex Vance",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            drawableResId = R.drawable.wp_cyber_anime_1791132845884,
            rating = 4.7f,
            reviewCount = 42,
            downloadsCount = 2890,
            viewsCount = 13200,
            status = WallpaperStatus.APPROVED,
            isFeatured = false,
            tags = listOf("Gaming", "Retro", "Synthwave", "Arcade"),
            fileSizeMb = 14.5
        ),
        // One wallpaper submitted for review to demonstrate Admin approval flow
        Wallpaper(
            id = "wp_pending_1",
            title = "Kalka Shimla Heritage Toy Train",
            description = "A UNESCO world heritage steam narrow-gauge train traversing high viaduct bridges through lush pine hills.",
            category = WallpaperCategory.INDIAN_RAILWAYS,
            resolution = "4K UHD (3840x2160)",
            isPaid = true,
            price = 2.49,
            creatorId = "user_creator_alex",
            creatorName = "Alex Vance",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            drawableResId = R.drawable.wp_indian_railway_1791132787481,
            rating = 0.0f,
            reviewCount = 0,
            downloadsCount = 0,
            viewsCount = 0,
            status = WallpaperStatus.PENDING_REVIEW,
            isFeatured = false,
            tags = listOf("Indian Railways", "Shimla", "Heritage", "Steam"),
            fileSizeMb = 13.1
        )
    )

    private val _wallpapers = MutableStateFlow(initialWallpapers)
    val wallpapers: StateFlow<List<Wallpaper>> = _wallpapers.asStateFlow()

    // Purchases
    private val initialPurchases = listOf(
        PurchaseOrder(
            orderId = "WPX-2026-78412",
            wallpaperId = "wp_2",
            wallpaperTitle = "Cyber Hypercar Midnight Drift",
            userId = "user_buyer",
            purchaseDate = System.currentTimeMillis() - 86400000L * 2,
            amountPaid = 3.49,
            platformFee = 0.70,
            creatorShare = 2.79,
            paymentMethod = "Google Pay",
            resolutionUnlocked = "8K Ultra (7680x4320)",
            downloadToken = WallpaperHelper.generateDownloadToken()
        )
    )

    private val _purchases = MutableStateFlow(initialPurchases)
    val purchases: StateFlow<List<PurchaseOrder>> = _purchases.asStateFlow()

    // Transactions log
    private val initialTransactions = listOf(
        TransactionRecord(
            id = "TX_9901",
            type = "WALLPAPER_SALE",
            amount = 3.49,
            description = "Sale: Cyber Hypercar Midnight Drift (80% to Alex Vance)",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            status = "COMPLETED"
        ),
        TransactionRecord(
            id = "TX_9902",
            type = "PLATFORM_COMMISSION",
            amount = 0.70,
            description = "Platform fee (20%) on Order WPX-2026-78412",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            status = "COMPLETED"
        )
    )

    private val _transactions = MutableStateFlow(initialTransactions)
    val transactions: StateFlow<List<TransactionRecord>> = _transactions.asStateFlow()

    // User reviews
    private val initialReviews = listOf(
        Review(
            id = "rev_1",
            wallpaperId = "wp_1",
            userName = "Rohit Verma",
            rating = 5,
            comment = "Absolute masterpiece! The lighting on the Vande Bharat front nose looks unreal on my AMOLED screen.",
            timestamp = System.currentTimeMillis() - 3600000L * 12
        ),
        Review(
            id = "rev_2",
            wallpaperId = "wp_1",
            userName = "Sarah Jenkins",
            rating = 5,
            comment = "Crisp 8K detail. Well worth the price!",
            timestamp = System.currentTimeMillis() - 3600000L * 24
        ),
        Review(
            id = "rev_3",
            wallpaperId = "wp_2",
            userName = "Kenji Sato",
            rating = 5,
            comment = "Best lock screen wallpaper I've bought this year. Rain reflections are insane.",
            timestamp = System.currentTimeMillis() - 3600000L * 18
        )
    )

    private val _reviews = MutableStateFlow(initialReviews)
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    // Reports / Copyright complaints
    private val initialReports = listOf(
        ReportComplaint(
            id = "REP_101",
            wallpaperId = "wp_6",
            wallpaperTitle = "Shinkansen Bullet Train Sunset",
            reportedBy = "user_buyer",
            reason = "Copyright & Attribution Inquiry",
            details = "Photographer signature matches external stock gallery. Verification requested.",
            timestamp = System.currentTimeMillis() - 86400000L,
            status = "PENDING"
        )
    )

    private val _reports = MutableStateFlow(initialReports)
    val reports: StateFlow<List<ReportComplaint>> = _reports.asStateFlow()

    // Switch active user
    fun switchUser(userId: String) {
        val found = _allUsers.value.find { it.id == userId } ?: return
        _currentUser.value = found
    }

    // Purchase Wallpaper
    fun purchaseWallpaper(
        wallpaper: Wallpaper,
        paymentMethod: String,
        couponCode: String?
    ): Result<PurchaseOrder> {
        val user = _currentUser.value
        val discount = if (couponCode.equals("WALLPIX20", ignoreCase = true)) 0.20 else 0.0
        val finalPrice = (wallpaper.price * (1.0 - discount)).coerceAtLeast(0.0)
        val commissionRate = _platformSettings.value.platformCommissionPercent / 100.0
        val platformFee = finalPrice * commissionRate
        val creatorCut = finalPrice - platformFee

        val order = PurchaseOrder(
            orderId = WallpaperHelper.generateOrderId(),
            wallpaperId = wallpaper.id,
            wallpaperTitle = wallpaper.title,
            userId = user.id,
            purchaseDate = System.currentTimeMillis(),
            amountPaid = finalPrice,
            platformFee = platformFee,
            creatorShare = creatorCut,
            paymentMethod = paymentMethod,
            resolutionUnlocked = wallpaper.resolution,
            downloadToken = WallpaperHelper.generateDownloadToken()
        )

        // Update purchase list
        _purchases.update { listOf(order) + it }

        // Update downloads & views on wallpaper
        _wallpapers.update { list ->
            list.map {
                if (it.id == wallpaper.id) it.copy(downloadsCount = it.downloadsCount + 1) else it
            }
        }

        // Credit creator earnings
        _allUsers.update { users ->
            users.map { u ->
                if (u.id == wallpaper.creatorId) {
                    u.copy(creatorEarnings = u.creatorEarnings + creatorCut)
                } else if (u.id == user.id && paymentMethod.contains("Wallet", ignoreCase = true)) {
                    u.copy(walletBalance = (u.walletBalance - finalPrice).coerceAtLeast(0.0))
                } else {
                    u
                }
            }
        }
        // Update current user copy
        _currentUser.value = _allUsers.value.find { it.id == _currentUser.value.id } ?: _currentUser.value

        // Log transactions
        val saleTx = TransactionRecord(
            id = "TX_" + UUID.randomUUID().toString().take(6).uppercase(),
            type = "WALLPAPER_SALE",
            amount = finalPrice,
            description = "Sale of '${wallpaper.title}' by ${wallpaper.creatorName}",
            timestamp = System.currentTimeMillis()
        )
        val feeTx = TransactionRecord(
            id = "TX_" + UUID.randomUUID().toString().take(6).uppercase(),
            type = "PLATFORM_COMMISSION",
            amount = platformFee,
            description = "Commission (${_platformSettings.value.platformCommissionPercent}%) on order ${order.orderId}",
            timestamp = System.currentTimeMillis()
        )
        _transactions.update { listOf(saleTx, feeTx) + it }

        return Result.success(order)
    }

    // Creator Upload Wallpaper
    fun uploadWallpaper(
        title: String,
        description: String,
        category: WallpaperCategory,
        resolution: String,
        price: Double,
        tags: List<String>,
        drawableResId: Int?,
        imageUri: String?
    ): Wallpaper {
        val user = _currentUser.value
        val isAutoApproved = _platformSettings.value.allowAutoApproval || user.isAdmin

        val newWp = Wallpaper(
            id = "wp_" + UUID.randomUUID().toString().take(8),
            title = title,
            description = description,
            category = category,
            resolution = resolution,
            isPaid = price > 0.0,
            price = price,
            creatorId = user.id,
            creatorName = user.name,
            creatorAvatar = user.avatarUrl,
            drawableResId = drawableResId ?: R.drawable.wp_indian_railway_1791132787481,
            imageUri = imageUri,
            rating = 5.0f,
            reviewCount = 0,
            downloadsCount = 0,
            viewsCount = 1,
            status = if (isAutoApproved) WallpaperStatus.APPROVED else WallpaperStatus.PENDING_REVIEW,
            uploadedAt = System.currentTimeMillis(),
            isFeatured = false,
            tags = tags,
            fileSizeMb = if (resolution.contains("8K")) 19.8 else 12.4
        )

        _wallpapers.update { listOf(newWp) + it }
        return newWp
    }

    // Admin approve wallpaper
    fun approveWallpaper(wallpaperId: String) {
        _wallpapers.update { list ->
            list.map {
                if (it.id == wallpaperId) it.copy(status = WallpaperStatus.APPROVED, rejectionReason = null) else it
            }
        }
    }

    // Admin reject wallpaper
    fun rejectWallpaper(wallpaperId: String, reason: String) {
        _wallpapers.update { list ->
            list.map {
                if (it.id == wallpaperId) it.copy(status = WallpaperStatus.REJECTED, rejectionReason = reason) else it
            }
        }
    }

    // Admin update commission rate
    fun updateCommissionPercent(newPercent: Int) {
        _platformSettings.update { it.copy(platformCommissionPercent = newPercent.coerceIn(5, 50)) }
    }

    // File report
    fun submitReport(wallpaperId: String, wallpaperTitle: String, reason: String, details: String) {
        val user = _currentUser.value
        val report = ReportComplaint(
            id = "REP_" + UUID.randomUUID().toString().take(6).uppercase(),
            wallpaperId = wallpaperId,
            wallpaperTitle = wallpaperTitle,
            reportedBy = user.name,
            reason = reason,
            details = details,
            timestamp = System.currentTimeMillis(),
            status = "PENDING"
        )
        _reports.update { listOf(report) + it }
    }

    // Admin resolve report
    fun resolveReport(reportId: String, removeContent: Boolean) {
        val rep = _reports.value.find { it.id == reportId }
        _reports.update { list ->
            list.map {
                if (it.id == reportId) {
                    it.copy(status = if (removeContent) "CONTENT_REMOVED" else "RESOLVED")
                } else it
            }
        }
        if (removeContent && rep != null) {
            _wallpapers.update { list ->
                list.filterNot { it.id == rep.wallpaperId }
            }
        }
    }

    // Submit review
    fun submitReview(wallpaperId: String, rating: Int, comment: String) {
        val user = _currentUser.value
        val review = Review(
            id = "rev_" + UUID.randomUUID().toString().take(6),
            wallpaperId = wallpaperId,
            userName = user.name,
            rating = rating,
            comment = comment,
            timestamp = System.currentTimeMillis()
        )
        _reviews.update { listOf(review) + it }

        // Recalculate average rating
        val wpReviews = _reviews.value.filter { it.wallpaperId == wallpaperId }
        val newAvg = if (wpReviews.isNotEmpty()) {
            wpReviews.map { it.rating }.average().toFloat()
        } else rating.toFloat()

        _wallpapers.update { list ->
            list.map {
                if (it.id == wallpaperId) {
                    it.copy(rating = newAvg, reviewCount = wpReviews.size)
                } else it
            }
        }
    }

    // Creator withdraw request
    fun requestWithdrawal(amount: Double): Boolean {
        val user = _currentUser.value
        if (user.creatorEarnings < amount || amount <= 0.0) return false

        _allUsers.update { users ->
            users.map {
                if (it.id == user.id) it.copy(creatorEarnings = it.creatorEarnings - amount) else it
            }
        }
        _currentUser.value = _allUsers.value.find { it.id == user.id } ?: _currentUser.value

        val tx = TransactionRecord(
            id = "TX_" + UUID.randomUUID().toString().take(6).uppercase(),
            type = "PAYOUT_WITHDRAWAL",
            amount = amount,
            description = "Payout to bank account for creator ${user.name}",
            timestamp = System.currentTimeMillis(),
            status = "COMPLETED"
        )
        _transactions.update { listOf(tx) + it }
        return true
    }

    fun isWallpaperPurchased(wallpaperId: String): Boolean {
        val userId = _currentUser.value.id
        return _purchases.value.any { it.wallpaperId == wallpaperId && it.userId == userId }
    }
}
