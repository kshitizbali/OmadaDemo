package com.example.omadademo.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for application-wide constants.
 *
 * Verifies that constants have expected values for proper app configuration.
 */
class PhotoGridConstantsTest {

    @Test
    fun `GRID_COLUMNS has correct value`() {
        assertEquals(3, PhotoGridConstants.GRID_COLUMNS)
    }

    @Test
    fun `GRID_ITEM_HEIGHT_DP has reasonable value`() {
        assertEquals(200, PhotoGridConstants.GRID_ITEM_HEIGHT_DP)
        assertTrue(PhotoGridConstants.GRID_ITEM_HEIGHT_DP > 0)
    }

    @Test
    fun `PHOTOS_PER_PAGE is configured`() {
        assertEquals(20, PhotoGridConstants.PHOTOS_PER_PAGE)
        assertTrue(PhotoGridConstants.PHOTOS_PER_PAGE > 0)
    }

    @Test
    fun `PAGINATION_THRESHOLD is less than PHOTOS_PER_PAGE`() {
        assertEquals(6, PhotoGridConstants.PAGINATION_THRESHOLD)
        assertTrue(PhotoGridConstants.PAGINATION_THRESHOLD < PhotoGridConstants.PHOTOS_PER_PAGE)
    }

    @Test
    fun `SEARCH_MIN_LENGTH is at least 1`() {
        assertEquals(2, PhotoGridConstants.SEARCH_MIN_LENGTH)
        assertTrue(PhotoGridConstants.SEARCH_MIN_LENGTH >= 1)
    }

    @Test
    fun `SEARCH_MAX_LENGTH is greater than SEARCH_MIN_LENGTH`() {
        assertEquals(100, PhotoGridConstants.SEARCH_MAX_LENGTH)
        assertTrue(PhotoGridConstants.SEARCH_MAX_LENGTH > PhotoGridConstants.SEARCH_MIN_LENGTH)
    }

    @Test
    fun `FLICKR_BASE_URL is valid`() {
        val url = PhotoGridConstants.FLICKR_BASE_URL
        assertEquals("https://www.flickr.com/services/", url)
        assertTrue(url.startsWith("https"))
        assertTrue(url.contains("flickr"))
    }

    @Test
    fun `SEARCH_MIN_LENGTH validates minimum input`() {
        val minLength = PhotoGridConstants.SEARCH_MIN_LENGTH
        assertTrue("a".length < minLength)
        assertTrue("ab".length >= minLength)
    }

    @Test
    fun `SEARCH_MAX_LENGTH validates maximum input`() {
        val maxLength = PhotoGridConstants.SEARCH_MAX_LENGTH
        assertTrue("a".repeat(100).length <= maxLength)
        assertTrue("a".repeat(101).length > maxLength)
    }
}

/**
 * Tests for ImageCacheConstants.
 */
class ImageCacheConstantsTest {

    @Test
    fun `MEMORY_CACHE_PERCENT is between 0 and 1`() {
        val percent = ImageCacheConstants.MEMORY_CACHE_PERCENT
        assertEquals(0.25f, percent)
        assertTrue(percent > 0)
        assertTrue(percent < 1)
    }

    @Test
    fun `DISK_CACHE_SIZE_MB is positive`() {
        assertEquals(50L, ImageCacheConstants.DISK_CACHE_SIZE_MB)
        assertTrue(ImageCacheConstants.DISK_CACHE_SIZE_MB > 0)
    }

    @Test
    fun `DISK_CACHE_SIZE_BYTES is calculated correctly`() {
        val expected = 50L * 1024 * 1024
        assertEquals(expected, ImageCacheConstants.DISK_CACHE_SIZE_BYTES)
    }

    @Test
    fun `DISK_CACHE_SIZE_BYTES is 50 megabytes`() {
        val bytes = ImageCacheConstants.DISK_CACHE_SIZE_BYTES
        val megabytes = bytes / (1024 * 1024)
        assertEquals(50, megabytes)
    }

    @Test
    fun `DISK_CACHE_DIR is not empty`() {
        assertTrue(ImageCacheConstants.DISK_CACHE_DIR.isNotEmpty())
        assertEquals("image_cache", ImageCacheConstants.DISK_CACHE_DIR)
    }

    @Test
    fun `DISK_CACHE_DIR is a valid directory name`() {
        val dirName = ImageCacheConstants.DISK_CACHE_DIR
        assertTrue(dirName.all { c -> c.isLetterOrDigit() || c == '_' })
    }
}

/**
 * Tests for ApiConstants.
 */
class ApiConstantsTest {

    @Test
    fun `FLICKR_API_METHOD_RECENT has correct value`() {
        assertEquals("flickr.photos.getRecent", ApiConstants.FLICKR_API_METHOD_RECENT)
    }

    @Test
    fun `FLICKR_API_METHOD_SEARCH has correct value`() {
        assertEquals("flickr.photos.search", ApiConstants.FLICKR_API_METHOD_SEARCH)
    }

    @Test
    fun `API_FORMAT is json`() {
        assertEquals("json", ApiConstants.API_FORMAT)
    }

    @Test
    fun `API_NO_JSON_CALLBACK is 1`() {
        assertEquals(1, ApiConstants.API_NO_JSON_CALLBACK)
    }

    @Test
    fun `FLICKR_API_METHOD_RECENT is not empty`() {
        assertTrue(ApiConstants.FLICKR_API_METHOD_RECENT.isNotEmpty())
    }

    @Test
    fun `FLICKR_API_METHOD_SEARCH is not empty`() {
        assertTrue(ApiConstants.FLICKR_API_METHOD_SEARCH.isNotEmpty())
    }

    @Test
    fun `API methods follow flickr naming convention`() {
        assertTrue(ApiConstants.FLICKR_API_METHOD_RECENT.startsWith("flickr."))
        assertTrue(ApiConstants.FLICKR_API_METHOD_SEARCH.startsWith("flickr."))
    }

    @Test
    fun `API_FORMAT is supported format`() {
        val supportedFormats = listOf("json", "xml", "rss")
        assertTrue(ApiConstants.API_FORMAT in supportedFormats)
    }
}
