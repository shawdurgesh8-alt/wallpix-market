package com.example.data.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object WallpaperHelper {

    enum class TargetScreen {
        HOME, LOCK, BOTH
    }

    suspend fun applyWallpaper(
        context: Context,
        drawableResId: Int?,
        target: TargetScreen
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val wallpaperManager = WallpaperManager.getInstance(context)
            val bitmap = if (drawableResId != null) {
                BitmapFactory.decodeResource(context.resources, drawableResId)
            } else {
                // Generate a rich fallback gradient bitmap if resource not present
                val bmp = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(bmp)
                val paint = android.graphics.Paint()
                val shader = android.graphics.LinearGradient(
                    0f, 0f, 1080f, 1920f,
                    android.graphics.Color.parseColor("#8E52FF"),
                    android.graphics.Color.parseColor("#00E5FF"),
                    android.graphics.Shader.TileMode.CLAMP
                )
                paint.shader = shader
                canvas.drawRect(0f, 0f, 1080f, 1920f, paint)
                bmp
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                when (target) {
                    TargetScreen.HOME -> wallpaperManager.setBitmap(
                        bitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_SYSTEM
                    )
                    TargetScreen.LOCK -> wallpaperManager.setBitmap(
                        bitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_LOCK
                    )
                    TargetScreen.BOTH -> wallpaperManager.setBitmap(
                        bitmap,
                        null,
                        true,
                        WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                    )
                }
            } else {
                wallpaperManager.setBitmap(bitmap)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun generateOrderId(): String {
        val rand = (10000..99999).random()
        return "WPX-2026-$rand"
    }

    fun generateDownloadToken(): String {
        return "SEC_DL_${UUID.randomUUID().toString().take(12).uppercase()}"
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatCurrency(amount: Double): String {
        return if (amount <= 0.0) "Free" else String.format(Locale.US, "$%.2f", amount)
    }
}
