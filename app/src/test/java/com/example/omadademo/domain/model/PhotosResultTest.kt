package com.example.omadademo.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Tests for PhotosResult data class.
 *
 * Verifies pagination information is correctly stored and retrieved.
 */
class PhotosResultTest {

    @Test
    fun `PhotosResult creates with all parameters`() {
        // Given
        val photos = listOf(
            Photo("1", "Photo 1", "owner1", "secret1", "server1", 1, "url1", "thumb1"),
            Photo("2", "Photo 2", "owner2", "secret2", "server2", 2, "url2", "thumb2")
        )
        val currentPage = 1
        val totalPages = 5
        val totalPhotos = 100

        // When
        val result = PhotosResult(photos, currentPage, totalPages, totalPhotos)

        // Then
        assertEquals(photos, result.photos)
        assertEquals(currentPage, result.currentPage)
        assertEquals(totalPages, result.totalPages)
        assertEquals(totalPhotos, result.totalPhotos)
    }

    @Test
    fun `PhotosResult with empty photos list`() {
        // Given
        val photos = emptyList<Photo>()
        val currentPage = 1
        val totalPages = 0
        val totalPhotos = 0

        // When
        val result = PhotosResult(photos, currentPage, totalPages, totalPhotos)

        // Then
        assertEquals(emptyList<Photo>(), result.photos)
        assertEquals(1, result.currentPage)
        assertEquals(0, result.totalPages)
        assertEquals(0, result.totalPhotos)
    }

    @Test
    fun `PhotosResult on first page`() {
        // Given
        val photos = listOf(Photo("1", "Photo", "owner", "secret", "server", 1, "url", "thumb"))

        // When
        val result = PhotosResult(photos, currentPage = 1, totalPages = 10, totalPhotos = 200)

        // Then
        assertEquals(1, result.currentPage)
    }

    @Test
    fun `PhotosResult on last page`() {
        // Given
        val photos = listOf(Photo("100", "Photo", "owner", "secret", "server", 1, "url", "thumb"))

        // When
        val result = PhotosResult(photos, currentPage = 10, totalPages = 10, totalPhotos = 200)

        // Then
        assertEquals(10, result.currentPage)
        assertEquals(10, result.totalPages)
    }

    @Test
    fun `PhotosResult on middle page`() {
        // Given
        val photos = listOf(Photo("50", "Photo", "owner", "secret", "server", 1, "url", "thumb"))

        // When
        val result = PhotosResult(photos, currentPage = 5, totalPages = 10, totalPhotos = 200)

        // Then
        assertEquals(5, result.currentPage)
        assertEquals(10, result.totalPages)
    }

    @Test
    fun `PhotosResult equality`() {
        // Given
        val photos = listOf(Photo("1", "Photo", "owner", "secret", "server", 1, "url", "thumb"))
        val result1 = PhotosResult(photos, 1, 2, 20)
        val result2 = PhotosResult(photos, 1, 2, 20)

        // When & Then
        assertEquals(result1, result2)
    }

    @Test
    fun `PhotosResult inequality with different page`() {
        // Given
        val photos = listOf(Photo("1", "Photo", "owner", "secret", "server", 1, "url", "thumb"))
        val result1 = PhotosResult(photos, 1, 2, 20)
        val result2 = PhotosResult(photos, 2, 2, 20)

        // When & Then
        assert(result1 != result2)
    }

    @Test
    fun `PhotosResult inequality with different total pages`() {
        // Given
        val photos = listOf(Photo("1", "Photo", "owner", "secret", "server", 1, "url", "thumb"))
        val result1 = PhotosResult(photos, 1, 2, 20)
        val result2 = PhotosResult(photos, 1, 3, 20)

        // When & Then
        assert(result1 != result2)
    }

    @Test
    fun `PhotosResult inequality with different photos`() {
        // Given
        val photos1 = listOf(Photo("1", "Photo 1", "owner", "secret", "server", 1, "url", "thumb"))
        val photos2 = listOf(Photo("2", "Photo 2", "owner", "secret", "server", 1, "url", "thumb"))
        val result1 = PhotosResult(photos1, 1, 2, 20)
        val result2 = PhotosResult(photos2, 1, 2, 20)

        // When & Then
        assert(result1 != result2)
    }

    @Test
    fun `PhotosResult hash code consistency`() {
        // Given
        val photos = listOf(Photo("1", "Photo", "owner", "secret", "server", 1, "url", "thumb"))
        val result1 = PhotosResult(photos, 1, 2, 20)
        val result2 = PhotosResult(photos, 1, 2, 20)

        // When & Then
        assertEquals(result1.hashCode(), result2.hashCode())
    }

    @Test
    fun `PhotosResult with multiple photos`() {
        // Given
        val photos = listOf(
            Photo("1", "Photo 1", "owner1", "secret1", "server1", 1, "url1", "thumb1"),
            Photo("2", "Photo 2", "owner2", "secret2", "server2", 2, "url2", "thumb2"),
            Photo("3", "Photo 3", "owner3", "secret3", "server3", 3, "url3", "thumb3")
        )

        // When
        val result = PhotosResult(photos, 1, 1, 3)

        // Then
        assertEquals(3, result.photos.size)
        assertEquals("1", result.photos[0].id)
        assertEquals("2", result.photos[1].id)
        assertEquals("3", result.photos[2].id)
    }

    @Test
    fun `PhotosResult toString contains relevant information`() {
        // Given
        val photos = listOf(Photo("1", "Photo", "owner", "secret", "server", 1, "url", "thumb"))
        val result = PhotosResult(photos, 1, 5, 100)

        // When
        val resultString = result.toString()

        // Then
        assertNotNull(resultString)
        assert(resultString.contains("currentPage") || resultString.contains("1"))
        assert(resultString.contains("totalPages") || resultString.contains("5"))
    }

    @Test
    fun `PhotosResult with large photo count`() {
        // Given
        val photos = listOf(Photo("1", "Photo", "owner", "secret", "server", 1, "url", "thumb"))
        val totalPhotos = 1_000_000

        // When
        val result = PhotosResult(photos, 1, 50000, totalPhotos)

        // Then
        assertEquals(1_000_000, result.totalPhotos)
    }
}
