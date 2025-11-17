package com.example.omadademo.presentation.detail

import com.example.omadademo.domain.model.Photo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
}
