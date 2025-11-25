package com.example.omadademo.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests for Photo domain model.
 *
 * Verifies that Photo data class correctly stores and retrieves photo information.
 */
class PhotoTest {

    @Test
    fun `Photo creates with required fields only`() {
        // Given
        val id = "photo123"
        val title = "Mountain Sunrise"
        val owner = "owner456"
        val secret = "abc123"
        val server = "server789"
        val farm = 1
        val imageUrl = "https://example.com/image.jpg"
        val thumbnailUrl = "https://example.com/thumb.jpg"

        // When
        val photo = Photo(
            id = id,
            title = title,
            owner = owner,
            secret = secret,
            server = server,
            farm = farm,
            imageUrl = imageUrl,
            thumbnailUrl = thumbnailUrl
        )

        // Then
        assertEquals(id, photo.id)
        assertEquals(title, photo.title)
        assertEquals(owner, photo.owner)
        assertEquals(secret, photo.secret)
        assertEquals(server, photo.server)
        assertEquals(farm, photo.farm)
        assertEquals(imageUrl, photo.imageUrl)
        assertEquals(thumbnailUrl, photo.thumbnailUrl)
        assertNull(photo.ownerName)
        assertNull(photo.views)
        assertNull(photo.dateUpload)
    }

    @Test
    fun `Photo creates with all fields including optional`() {
        // Given
        val photo = Photo(
            id = "photo123",
            title = "Mountain Sunrise",
            owner = "owner456",
            secret = "abc123",
            server = "server789",
            farm = 1,
            imageUrl = "https://example.com/image.jpg",
            thumbnailUrl = "https://example.com/thumb.jpg",
            ownerName = "John Doe",
            views = 5000L,
            dateUpload = 1678886400L
        )

        // When & Then
        assertEquals("John Doe", photo.ownerName)
        assertEquals(5000L, photo.views)
        assertEquals(1678886400L, photo.dateUpload)
    }

    @Test
    fun `Photo with null optional fields`() {
        // When
        val photo = Photo(
            id = "1",
            title = "Title",
            owner = "owner",
            secret = "secret",
            server = "server",
            farm = 1,
            imageUrl = "url",
            thumbnailUrl = "thumb",
            ownerName = null,
            views = null,
            dateUpload = null
        )

        // Then
        assertNull(photo.ownerName)
        assertNull(photo.views)
        assertNull(photo.dateUpload)
    }

    @Test
    fun `Photo equality with identical data`() {
        // Given
        val photo1 = Photo("1", "Title", "owner", "secret", "server", 1, "url", "thumb")
        val photo2 = Photo("1", "Title", "owner", "secret", "server", 1, "url", "thumb")

        // When & Then
        assertEquals(photo1, photo2)
    }

    @Test
    fun `Photo inequality with different ID`() {
        // Given
        val photo1 = Photo("1", "Title", "owner", "secret", "server", 1, "url", "thumb")
        val photo2 = Photo("2", "Title", "owner", "secret", "server", 1, "url", "thumb")

        // When & Then
        assert(photo1 != photo2)
    }

    @Test
    fun `Photo inequality with different title`() {
        // Given
        val photo1 = Photo("1", "Title 1", "owner", "secret", "server", 1, "url", "thumb")
        val photo2 = Photo("1", "Title 2", "owner", "secret", "server", 1, "url", "thumb")

        // When & Then
        assert(photo1 != photo2)
    }

    @Test
    fun `Photo inequality with different farm`() {
        // Given
        val photo1 = Photo("1", "Title", "owner", "secret", "server", 1, "url", "thumb")
        val photo2 = Photo("1", "Title", "owner", "secret", "server", 2, "url", "thumb")

        // When & Then
        assert(photo1 != photo2)
    }

    @Test
    fun `Photo inequality with different ownerName`() {
        // Given
        val photo1 = Photo("1", "Title", "owner", "secret", "server", 1, "url", "thumb", ownerName = "Alice")
        val photo2 = Photo("1", "Title", "owner", "secret", "server", 1, "url", "thumb", ownerName = "Bob")

        // When & Then
        assert(photo1 != photo2)
    }

    @Test
    fun `Photo hash code consistency`() {
        // Given
        val photo1 = Photo("1", "Title", "owner", "secret", "server", 1, "url", "thumb")
        val photo2 = Photo("1", "Title", "owner", "secret", "server", 1, "url", "thumb")

        // When & Then
        assertEquals(photo1.hashCode(), photo2.hashCode())
    }

    @Test
    fun `Photo with zero views`() {
        // When
        val photo = Photo(
            "1", "Title", "owner", "secret", "server", 1, "url", "thumb",
            views = 0L
        )

        // Then
        assertEquals(0L, photo.views)
    }

    @Test
    fun `Photo with high view count`() {
        // When
        val photo = Photo(
            "1", "Title", "owner", "secret", "server", 1, "url", "thumb",
            views = 999_999_999L
        )

        // Then
        assertEquals(999_999_999L, photo.views)
    }

    @Test
    fun `Photo with long ago timestamp`() {
        // When
        val photo = Photo(
            "1", "Title", "owner", "secret", "server", 1, "url", "thumb",
            dateUpload = 0L // Unix epoch
        )

        // Then
        assertEquals(0L, photo.dateUpload)
    }

    @Test
    fun `Photo with recent timestamp`() {
        // When
        val recentTimestamp = System.currentTimeMillis() / 1000
        val photo = Photo(
            "1", "Title", "owner", "secret", "server", 1, "url", "thumb",
            dateUpload = recentTimestamp
        )

        // Then
        assertEquals(recentTimestamp, photo.dateUpload)
    }

    @Test
    fun `Photo string representation`() {
        // Given
        val photo = Photo("1", "Title", "owner", "secret", "server", 1, "url", "thumb")

        // When
        val result = photo.toString()

        // Then
        assertNotNull(result)
        assert(result.contains("Title"))
        assert(result.contains("1"))
    }

    @Test
    fun `Photo with special characters in title`() {
        // Given
        val title = "Sunset & Beautiful <Sky>"

        // When
        val photo = Photo("1", title, "owner", "secret", "server", 1, "url", "thumb")

        // Then
        assertEquals(title, photo.title)
    }

    @Test
    fun `Photo with empty strings`() {
        // When
        val photo = Photo("", "", "", "", "", 0, "", "")

        // Then
        assertEquals("", photo.id)
        assertEquals("", photo.title)
        assertEquals("", photo.imageUrl)
    }

    @Test
    fun `Photo with URL containing query parameters`() {
        // When
        val imageUrl = "https://example.com/image.jpg?size=large&format=png"
        val photo = Photo(
            "1", "Title", "owner", "secret", "server", 1,
            imageUrl = imageUrl,
            thumbnailUrl = "thumb"
        )

        // Then
        assertEquals(imageUrl, photo.imageUrl)
    }

    @Test
    fun `Photo copy constructor`() {
        // Given
        val original = Photo(
            "1", "Title", "owner", "secret", "server", 1, "url", "thumb",
            ownerName = "Owner", views = 100L, dateUpload = 123456L
        )

        // When
        val copy = original.copy(views = 200L)

        // Then
        assertEquals("1", copy.id)
        assertEquals("Owner", copy.ownerName)
        assertEquals(200L, copy.views) // Changed
        assertEquals(123456L, copy.dateUpload) // Unchanged
    }

    @Test
    fun `Photo is Parcelable`() {
        // When
        val photo = Photo("1", "Title", "owner", "secret", "server", 1, "url", "thumb")

        // Then
        assert(photo is android.os.Parcelable)
    }
}
