package com.example.omadademo.data.mapper

import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

// Assuming FlickrPhoto DTO is available in this package
// If not, you might need to create a mock DTO or adjust imports.

@RunWith(JUnit4::class)
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
            ownerName = "Test Owner",
            views = "12345",
            dateUpload = "1678886400" // Example Unix timestamp
        )

        // When
        val photo = photoMapper.toDomain(flickrPhoto)

        // Then
        assert(photo.id == "1")
        assert(photo.title == "Test Title")
        assert(photo.owner == "owner1")
        assert(photo.secret == "secret1")
        assert(photo.server == "server1")
        assert(photo.farm == 1)
        assert(photo.ownerName == "Test Owner")
        assert(photo.views == 12345L)
        assert(photo.dateUpload == 1678886400L)
    }

    @Test
    fun `toDomainList maps multiple FlickrPhotos to Photo list`() {
        // Given
        val flickrPhotos = listOf(
            FlickrPhoto(
                id = "1", title = "Title 1", owner = "o1", secret = "s1", server = "sv1", farm = 1,
                ownerName = "Owner 1", views = "100", dateUpload = "1678886400"
            ),
            FlickrPhoto(
                id = "2", title = "Title 2", owner = "o2", secret = "s2", server = "sv2", farm = 2,
                ownerName = "Owner 2", views = "200", dateUpload = "1678886500"
            )
        )

        // When
        val photos = photoMapper.toDomainList(flickrPhotos)

        // Then
        assert(photos.size == 2)
        assert(photos[0].id == "1")
        assert(photos[1].id == "2")
        assert(photos[0].title == "Title 1")
        assert(photos[1].title == "Title 2")
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
            ownerName = null, // Null ownerName
            views = null,     // Null views
            dateUpload = null // Null dateUpload
        )

        // When
        val photo = photoMapper.toDomain(flickrPhoto)

        // Then
        assert(photo.id == "2")
        assert(photo.ownerName == null)
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
            ownerName = "Owner 3",
            views = "not a number", // Malformed views
            dateUpload = "another bad string" // Malformed dateUpload
        )

        // When
        val photo = photoMapper.toDomain(flickrPhoto)

        // Then
        assert(photo.id == "3")
        assertNull(photo.views) // Should be null due to parsing error
        assertNull(photo.dateUpload) // Should be null due to parsing error
    }
}

// Dummy FlickrPhoto class for testing purposes if it's not available in the test classpath
// In a real project, this would be imported from data.remote.dto
data class FlickrPhoto(
    val id: String,
    val title: String,
    val owner: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val ownerName: String?,
    val views: String?,
    val dateUpload: String?
)
