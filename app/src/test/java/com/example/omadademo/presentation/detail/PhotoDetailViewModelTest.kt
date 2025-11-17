package com.example.omadademo.presentation.detail

import com.example.omadademo.domain.model.Photo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class PhotoDetailViewModelTest {

    private lateinit var viewModel: PhotoDetailViewModel

    @Before
    fun setup() {
        viewModel = PhotoDetailViewModel()
    }

    @Test
    fun `setPhoto updates the photo StateFlow`() = runTest {
        // Given
        val photo = Photo(
            id = "1",
            title = "Test Photo",
            owner = "owner1",
            secret = "secret1",
            server = "server1",
            farm = 1,
            imageUrl = "url1",
            thumbnailUrl = "thumb1"
        )

        // When
        viewModel.setPhoto(photo)

        // Then
        val emittedPhoto = viewModel.photo.first()
        assertEquals(photo, emittedPhoto)
    }

    @Test
    fun `setPhoto with optional fields updates StateFlow`() = runTest {
        // Given
        val photo = Photo(
            id = "2",
            title = "Photo with Details",
            owner = "owner2",
            secret = "secret2",
            server = "server2",
            farm = 2,
            imageUrl = "url2",
            thumbnailUrl = "thumb2",
            ownerName = "John Doe",
            views = 5000L,
            dateUpload = 1678886400L
        )

        // When
        viewModel.setPhoto(photo)

        // Then
        val emittedPhoto = viewModel.photo.first()
        assertEquals(photo, emittedPhoto)
        assertEquals("John Doe", emittedPhoto?.ownerName)
        assertEquals(5000L, emittedPhoto?.views)
        assertEquals(1678886400L, emittedPhoto?.dateUpload)
    }

    @Test
    fun `multiple setPhoto calls update the StateFlow`() = runTest {
        // Given
        val photo1 = Photo("1", "Photo 1", "owner1", "secret1", "server1", 1, "url1", "thumb1")
        val photo2 = Photo("2", "Photo 2", "owner2", "secret2", "server2", 2, "url2", "thumb2")

        // When
        viewModel.setPhoto(photo1)
        var emittedPhoto = viewModel.photo.first()
        assertEquals(photo1, emittedPhoto)

        viewModel.setPhoto(photo2)
        emittedPhoto = viewModel.photo.first()
        assertEquals(photo2, emittedPhoto)

        // Then
        assertEquals("2", emittedPhoto?.id)
        assertEquals("Photo 2", emittedPhoto?.title)
    }

    @Test
    fun `photo StateFlow is accessible`() = runTest {
        // When
        val photoFlow = viewModel.photo

        // Then
        assertNotNull(photoFlow)
    }

    @Test
    fun `setPhoto with same photo twice`() = runTest {
        // Given
        val photo = Photo("1", "Photo", "owner", "secret", "server", 1, "url", "thumb")

        // When
        viewModel.setPhoto(photo)
        viewModel.setPhoto(photo)

        // Then
        val emittedPhoto = viewModel.photo.first()
        assertEquals(photo, emittedPhoto)
    }
}
