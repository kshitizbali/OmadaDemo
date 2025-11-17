package com.example.omadademo.domain.usecase

import com.example.omadademo.domain.exception.PhotoException
import com.example.omadademo.domain.model.Photo
import com.example.omadademo.domain.model.PhotosResult
import com.example.omadademo.domain.repository.PhotoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.given
import org.mockito.kotlin.verify

class GetRecentPhotosUseCaseTest {

    @Mock
    private lateinit var photoRepository: PhotoRepository

    private lateinit var getRecentPhotosUseCase: GetRecentPhotosUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getRecentPhotosUseCase = GetRecentPhotosUseCase(photoRepository)
    }

    @Test
    fun `should return recent photos on success`() = runTest {
        // Given
        val page = 1
        val perPage = 20
        val expectedPhotos = listOf(
            Photo(id = "1", title = "Test Photo 1", owner = "owner1", secret = "secret1", server = "server1", farm = 1, imageUrl = "url1", thumbnailUrl = "thumb1"),
            Photo(id = "2", title = "Test Photo 2", owner = "owner2", secret = "secret2", server = "server2", farm = 2, imageUrl = "url2", thumbnailUrl = "thumb2")
        )
        val expectedResult = PhotosResult.Success(expectedPhotos, 1, 2, 50)
        given(photoRepository.getRecentPhotos(page, perPage)).willReturn(expectedResult)

        // When
        val result = getRecentPhotosUseCase(page, perPage)

        // Then
        assert(result is PhotosResult.Success)
        assert((result as PhotosResult.Success).photos == expectedPhotos)
        verify(photoRepository).getRecentPhotos(page, perPage)
    }

    @Test
    fun `should return error on repository failure`() = runTest {
        // Given
        val page = 1
        val perPage = 20
        val expectedError = PhotosResult.Error(PhotoException.NetworkException("Network error"))
        given(photoRepository.getRecentPhotos(page, perPage)).willReturn(expectedError)

        // When
        val result = getRecentPhotosUseCase(page, perPage)

        // Then
        assert(result is PhotosResult.Error)
        assert((result as PhotosResult.Error).exception is PhotoException.NetworkException)
        verify(photoRepository).getRecentPhotos(page, perPage)
    }

    @Test
    fun `should use default page and perPage if not provided`() = runTest {
        // Given
        val defaultPage = 1
        val defaultPerPage = 20
        val expectedPhotos = emptyList<Photo>() // Can be any valid list for this test
        val expectedResult = PhotosResult.Success(expectedPhotos, 1, 2, 0)
        given(photoRepository.getRecentPhotos(defaultPage, defaultPerPage)).willReturn(expectedResult)

        // When
        getRecentPhotosUseCase()

        // Then
        verify(photoRepository).getRecentPhotos(defaultPage, defaultPerPage)
    }
}