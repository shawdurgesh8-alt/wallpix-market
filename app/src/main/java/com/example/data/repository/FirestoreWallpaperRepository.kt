package com.example.data.repository

import android.util.Log
import com.example.data.model.Wallpaper
import com.example.data.model.WallpaperCategory
import com.example.data.model.WallpaperStatus
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

/**
 * Repository handling real-time fetching and synchronization of wallpaper
 * listings with the Firestore backend.
 */
class FirestoreWallpaperRepository(
    private val firestoreProvider: () -> FirebaseFirestore? = {
        try {
            val apps = FirebaseApp.getApps(com.google.firebase.FirebaseApp.getInstance().applicationContext)
            if (apps.isNotEmpty()) {
                FirebaseFirestore.getInstance()
            } else null
        } catch (e: Exception) {
            null
        }
    },
    private val localFallbackRepository: WallpaperRepository? = null
) {
    companion object {
        private const val TAG = "FirestoreWallpaperRepo"
        const val COLLECTION_WALLPAPERS = "wallpapers"
    }

    /**
     * Checks if Firebase is initialized and available in the current environment.
     */
    fun isBackendConnected(): Boolean {
        return try {
            firestoreProvider() != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Real-time stream of all public approved wallpapers from the backend.
     * Uses Firestore snapshot listeners for live instant synchronization.
     */
    fun observeWallpapersRealtime(category: WallpaperCategory? = null): Flow<List<Wallpaper>> {
        val firestore = firestoreProvider()
        if (firestore == null) {
            Log.d(TAG, "Firestore not provisioned or offline; serving reactive local stream.")
            return localFallbackRepository?.wallpapers?.map { list ->
                list.filter { wp ->
                    wp.status == WallpaperStatus.APPROVED &&
                    (category == null || category == WallpaperCategory.ALL || wp.category == category)
                }
            } ?: flowOf(emptyList())
        }

        return callbackFlow<List<Wallpaper>> {
            var query: Query = firestore.collection(COLLECTION_WALLPAPERS)
                .whereEqualTo("status", WallpaperStatus.APPROVED.name)

            if (category != null && category != WallpaperCategory.ALL) {
                query = query.whereEqualTo("category", category.name)
            }

            val registration = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error in real-time wallpaper stream: ${error.message}", error)
                    localFallbackRepository?.wallpapers?.value?.let { fallback ->
                        trySend(fallback.filter { it.status == WallpaperStatus.APPROVED })
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val wallpapers = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject<Wallpaper>()?.copy(id = doc.id)
                        } catch (e: Exception) {
                            Log.w(TAG, "Failed to parse wallpaper doc ${doc.id}: ${e.message}")
                            null
                        }
                    }
                    trySend(wallpapers)
                }
            }

            awaitClose {
                registration.remove()
            }
        }.catch { e ->
            Log.e(TAG, "Exception in wallpaper stream flow: ${e.message}", e)
            emit(localFallbackRepository?.wallpapers?.value ?: emptyList())
        }
    }

    /**
     * Real-time observation of featured wallpapers for the home hero carousel.
     */
    fun observeFeaturedWallpapersRealtime(): Flow<List<Wallpaper>> {
        val firestore = firestoreProvider()
        if (firestore == null) {
            return localFallbackRepository?.wallpapers?.map { list ->
                list.filter { it.status == WallpaperStatus.APPROVED && it.isFeatured }
            } ?: flowOf(emptyList())
        }

        return callbackFlow<List<Wallpaper>> {
            val query = firestore.collection(COLLECTION_WALLPAPERS)
                .whereEqualTo("status", WallpaperStatus.APPROVED.name)
                .whereEqualTo("isFeatured", true)

            val registration = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing featured wallpapers: ${error.message}", error)
                    val local = localFallbackRepository?.wallpapers?.value?.filter { it.isFeatured } ?: emptyList()
                    trySend(local)
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject<Wallpaper>()?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(list)
            }

            awaitClose { registration.remove() }
        }.catch {
            val fallback = localFallbackRepository?.wallpapers?.value?.filter { it.isFeatured } ?: emptyList()
            emit(fallback)
        }
    }

    /**
     * Real-time observation of a single wallpaper by ID (e.g. for detail view).
     */
    fun observeWallpaperDetail(wallpaperId: String): Flow<Wallpaper?> {
        val firestore = firestoreProvider()
        if (firestore == null) {
            return localFallbackRepository?.wallpapers?.map { list ->
                list.find { it.id == wallpaperId }
            } ?: flowOf(null)
        }

        return callbackFlow<Wallpaper?> {
            val docRef = firestore.collection(COLLECTION_WALLPAPERS).document(wallpaperId)
            val registration = docRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing wallpaper $wallpaperId: ${error.message}", error)
                    val local = localFallbackRepository?.wallpapers?.value?.find { it.id == wallpaperId }
                    trySend(local)
                    return@addSnapshotListener
                }

                val wp = snapshot?.let { doc ->
                    try {
                        doc.toObject<Wallpaper>()?.copy(id = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(wp)
            }

            awaitClose { registration.remove() }
        }.catch {
            emit(localFallbackRepository?.wallpapers?.value?.find { it.id == wallpaperId })
        }
    }

    /**
     * Publish a new wallpaper to the backend Firestore collection.
     */
    suspend fun publishWallpaperToBackend(wallpaper: Wallpaper): Result<String> {
        val firestore = firestoreProvider()
        return try {
            if (firestore != null) {
                val docRef = firestore.collection(COLLECTION_WALLPAPERS).document()
                val itemToSave = wallpaper.copy(id = docRef.id)
                docRef.set(itemToSave).await()
                Result.success(docRef.id)
            } else {
                localFallbackRepository?.uploadWallpaper(
                    title = wallpaper.title,
                    description = wallpaper.description,
                    category = wallpaper.category,
                    resolution = wallpaper.resolution,
                    price = wallpaper.price,
                    tags = wallpaper.tags,
                    drawableResId = wallpaper.drawableResId,
                    imageUri = wallpaper.imageUri
                )
                Result.success(wallpaper.id)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to publish wallpaper to backend: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Updates wallpaper review status in real-time (Approved / Rejected)
     */
    suspend fun updateWallpaperStatus(wallpaperId: String, status: WallpaperStatus, reason: String? = null): Result<Unit> {
        val firestore = firestoreProvider()
        return try {
            if (firestore != null) {
                val updates = mutableMapOf<String, Any>("status" to status.name)
                if (reason != null) updates["rejectionReason"] = reason
                firestore.collection(COLLECTION_WALLPAPERS).document(wallpaperId).update(updates).await()
            }
            when (status) {
                WallpaperStatus.APPROVED -> localFallbackRepository?.approveWallpaper(wallpaperId)
                WallpaperStatus.REJECTED -> localFallbackRepository?.rejectWallpaper(wallpaperId, reason ?: "")
                WallpaperStatus.PENDING_REVIEW -> Unit
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update status on backend: ${e.message}", e)
            Result.failure(e)
        }
    }
}
