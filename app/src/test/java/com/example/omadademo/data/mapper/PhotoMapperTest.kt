package com.example.omadademo.data.mapper

import com.example.omadademo.data.remote.dto.FlickrPhoto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PhotoMapperTest {

    private lateinit var photoMapper: PhotoMapper

    @Before
    fun setup() {
        photoMapper = PhotoMapper()
    }

    @Test
    fun `toDomain maps FlickrPhoto to Photo correctly`() {
        // Given
        val flickrPhoto = FlickrPhoto(
            id = "1",
            title = "Test Title",
            owner = "owner1",
            secret = "secret1",
            server = "server1",
            farm = 1,
            isPublic = 1,
            ownerName = "Test Owner",
            views = "12345",
            dateUpload = "1678886400" // Example Unix timestamp
        )

        // When
        val photo = photoMapper.toDomain(flickrPhoto)

        // Then
        assertEquals("1", photo.id)
        assertEquals("Test Title", photo.title)
        assertEquals("owner1", photo.owner)
        assertEquals("secret1", photo.secret)
        assertEquals("server1", photo.server)
        assertEquals(1, photo.farm)
        assertEquals("Test Owner", photo.ownerName)
        assertEquals(12345L, photo.views)
        assertEquals(1678886400L, photo.dateUpload)
        // Verify URLs are properly constructed
        assertEquals("https://live.staticflickr.com/server1/1_secret1.jpg", photo.imageUrl)
        assertEquals("https://live.staticflickr.com/server1/1_secret1_q.jpg", photo.thumbnailUrl)
    }

    @Test
    fun `toDomainList maps multiple FlickrPhotos to Photo list`() {
        // Given
        val flickrPhotos = listOf(
            FlickrPhoto(
                id = "1", title = "Title 1", owner = "o1", secret = "s1", server = "sv1", farm = 1,
                isPublic = 1, ownerName = "Owner 1", views = "100", dateUpload = "1678886400"
            ),
            FlickrPhoto(
                id = "2", title = "Title 2", owner = "o2", secret = "s2", server = "sv2", farm = 2,
                isPublic = 1, ownerName = "Owner 2", views = "200", dateUpload = "1678886500"
            )
        )

        // When
        val photos = photoMapper.toDomainList(flickrPhotos)

        // Then
        assertEquals(2, photos.size)
        assertEquals("1", photos[0].id)
        assertEquals("2", photos[1].id)
        assertEquals("Title 1", photos[0].title)
        assertEquals("Title 2", photos[1].title)
    }

    @Test
    fun `toDomain handles nullable fields gracefully`() {
        // Given
        val flickrPhoto = FlickrPhoto(
            id = "2",
            title = "Nullable Fields Test",
            owner = "owner2",
            secret = "secret2",
            server = "server2",
            farm = 2,
            isPublic = 1,
            ownerName = null, // Null ownerName
            views = null,     // Null views
            dateUpload = null // Null dateUpload
        )

        // When
        val photo = photoMapper.toDomain(flickrPhoto)

        // Then
        assertEquals("2", photo.id)
        assertNull(photo.ownerName)
        assertNull(photo.views)
        assertNull(photo.dateUpload)
    }

    @Test
    fun `toDomain handles malformed number strings for views and dateUpload`() {
        // Given
        val flickrPhoto = FlickrPhoto(
            id = "3",
            title = "Malformed Numbers Test",
            owner = "owner3",
            secret = "secret3",
            server = "server3",
            farm = 3,
            isPublic = 1,
            ownerName = "Owner 3",
            views = "not a number", // Malformed views
            dateUpload = "another bad string" // Malformed dateUpload
        )

        // When
        val photo = photoMapper.toDomain(flickrPhoto)

        // Then
        assertEquals("3", photo.id)
        assertNull(photo.views) // Should be null due to parsing error
        assertNull(photo.dateUpload) // Should be null due to parsing error
    }

    @Test
    fun `toDomain constructs correct image URLs`() {
        // Given
        val flickrPhoto = FlickrPhoto(
            id = "12345",
            title = "URL Test",
            owner = "owner",
            secret = "abc123",
            server = "server456",
            farm = 1,
            isPublic = 1
        )

        // When
        val photo = photoMapper.toDomain(flickrPhoto)

        // Then
        assertEquals("https://live.staticflickr.com/server456/12345_abc123.jpg", photo.imageUrl)
        assertEquals("https://live.staticflickr.com/server456/12345_abc123_q.jpg", photo.thumbnailUrl)
    }

    @Test
    fun `toDomainList handles empty list`() {
        // When
        val photos = photoMapper.toDomainList(emptyList())

        // Then
        assertEquals(0, photos.size)
    }
}
