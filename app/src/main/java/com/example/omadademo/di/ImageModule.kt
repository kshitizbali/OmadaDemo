package com.example.omadademo.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import timber.log.Timber
import javax.inject.Singleton

/**
 * Hilt module for providing Coil image loading configuration.
 *
 * Configures:
 * - Memory cache (25% of available heap)
 * - Disk cache (50MB)
 * - Network client (OkHttpClient with logging)
 *
 * Provides fast image loading with intelligent caching for offline support.
 */
@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    /**
     * Provides configured ImageLoader instance for Coil.
     *
     * Memory Cache: Uses 25% of available memory for fast access
     * Disk Cache: 50MB persistent cache for offline support
     * Network: Shares OkHttpClient with API calls for connection pooling
     *
     * @param context Application context
     * @param okHttpClient Shared OkHttpClient for network requests
     * @return Configured ImageLoader singleton
     */
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        Timber.d("Initializing Coil ImageLoader with memory and disk cache")

        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // Use 25% of available memory
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB disk cache
                    .build()
            }
            .allowHardware(false) // Force software bitmaps for compatibility
            .okHttpClient(okHttpClient)
            .build()
    }
}
