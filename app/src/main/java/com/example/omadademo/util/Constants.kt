package com.example.omadademo.util

/**
 * Application-wide constants
 */
object PhotoGridConstants {
    // Grid configuration
    const val GRID_COLUMNS = 3
    const val GRID_ITEM_HEIGHT_DP = 200

    // Pagination configuration
    const val PHOTOS_PER_PAGE = 20
    const val PAGINATION_THRESHOLD = 6  // Load more when this many items from the bottom

    // Search validation
    const val SEARCH_MIN_LENGTH = 2
    const val SEARCH_MAX_LENGTH = 100

    // Network configuration
    const val FLICKR_BASE_URL = "https://www.flickr.com/services/"
}

/**
 * Image cache configuration
 */
object ImageCacheConstants {
    // Memory cache: 25% of available memory
    const val MEMORY_CACHE_PERCENT = 0.25f

    // Disk cache: 50MB
    const val DISK_CACHE_SIZE_MB = 50L
    const val DISK_CACHE_SIZE_BYTES = DISK_CACHE_SIZE_MB * 1024 * 1024
    const val DISK_CACHE_DIR = "image_cache"
}

/**
 * API configuration
 */
object ApiConstants {
    const val FLICKR_API_METHOD_RECENT = "flickr.photos.getRecent"
    const val FLICKR_API_METHOD_SEARCH = "flickr.photos.search"
    const val API_FORMAT = "json"
    const val API_NO_JSON_CALLBACK = 1
}
