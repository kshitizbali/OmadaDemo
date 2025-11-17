package com.example.omadademo.data.repository

import com.example.omadademo.data.mapper.PhotoMapper
import com.example.omadademo.data.remote.RemoteDataSource
import com.example.omadademo.data.remote.dto.FlickrPhoto
import com.example.omadademo.data.remote.dto.FlickrPhotosResponse
import com.example.omadademo.data.remote.dto.PhotosContainer
import com.example.omadademo.domain.model.Photo
import com.example.omadademo.domain.model.PhotosResult
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class PhotoRepositoryImplTest {

    @Mock
    private lateinit var mockRemoteDataSource: RemoteDataSource

    @Mock
    private lateinit var mockPhotoMapper: PhotoMapper

    private lateinit var photoRepositoryImpl: PhotoRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        photoRepositoryImpl = PhotoRepositoryImpl(mockRemoteDataSource, mockPhotoMapper)
    }

    // --- Tests for getRecentPhotos ---

    @Test
    fun `getRecentPhotos calls remoteDataSource and maps response`() = runTest {
        // Given
        val page = 1
        val perPage = 20
        val mockFlickrPhotos = listOf(
            FlickrPhoto(id = "1", title = "Test Photo 1", owner = "owner1", secret = "secret1", server = "server1", farm = 1),
            FlickrPhoto(id = "2", title = "Test Photo 2", owner = "owner2", secret = "secret2", server = "server2", farm = 2)
        )
        val mockResponse = MockPhotosResponse(
            photosContainer = MockPhotosContainer(photos = mockFlickrPhotos, page = page, pages = 10, total = "100")
        )
        val expectedDomainPhotos = listOf(
            Photo(id = "1", title = "Test Photo 1", owner = "owner1", secret = "secret1", server = "server1", farm = 1),
            Photo(id = "2", title = "Test Photo 2", owner = "owner2", secret = "secret2", server = "server2", farm = 2)
        )

        given(mockRemoteDataSource.getRecentPhotos(page, perPage)).willReturn(mockResponse)
        // Important: Use `any()` for the list if the mapper is complex, or specific list if it's identical.
        // Here, we assume the mapper converts correctly, so we can check the returned domain photos.
        given(mockPhotoMapper.toDomainList(mockFlickrPhotos)).willReturn(expectedDomainPhotos)

        // When
        val result = photoRepositoryImpl.getRecentPhotos(page, perPage)

        // Then
        verify(mockRemoteDataSource).getRecentPhotos(page, perPage)
        verify(mockPhotoMapper).toDomainList(mockFlickrPhotos)

        assert(result is PhotosResult.Success)
        val successResult = result as PhotosResult.Success
        assert(successResult.photos == expectedDomainPhotos)
        assert(successResult.currentPage == page)
        assert(successResult.totalPages == 10)
        assert(successResult.totalPhotos == "100")
    }

    @Test
    fun `getRecentPhotos propagates exception from remoteDataSource`() = runTest {
        // Given
        val page = 1
        val perPage = 20
        val networkException = RuntimeException("Simulated network error")

        given(mockRemoteDataSource.getRecentPhotos(page, perPage)).willThrow(networkException)

        // When & Then
        try {
            photoRepositoryImpl.getRecentPhotos(page, perPage)
            assert(false) { "Expected an exception to be thrown" } // Fail test if no exception
        } catch (e: Exception) {
            assert(e === networkException) // Verify the correct exception is propagated
            verify(mockRemoteDataSource).getRecentPhotos(page, perPage)
            // Ensure mapper is NOT called on error
            verify(mockPhotoMapper, org.mockito.kotlin.never()).toDomainList(any())
        }
    }

    // --- Tests for searchPhotos ---

    @Test
    fun `searchPhotos calls remoteDataSource and maps response`() = runTest {
        // Given
        val query = "test"
        val page = 1
        val perPage = 20
        val mockFlickrPhotos = listOf(
            FlickrPhoto(id = "3", title = "Search Photo 1", owner = "owner3", secret = "secret3", server = "server3", farm = 3)
        )
        val mockResponse = MockPhotosResponse(
            photosContainer = MockPhotosContainer(photos = mockFlickrPhotos, page = page, pages = 5, total = "50")
        )
        val expectedDomainPhotos = listOf(
            Photo(id = "3", title = "Search Photo 1", owner = "owner3", secret = "secret3", server = "server3", farm = 3)
        )

        given(mockRemoteDataSource.searchPhotos(query, page, perPage)).willReturn(mockResponse)
        given(mockPhotoMapper.toDomainList(mockFlickrPhotos)).willReturn(expectedDomainPhotos)

        // When
        val result = photoRepositoryImpl.searchPhotos(query, page, perPage)

        // Then
        verify(mockRemoteDataSource).searchPhotos(query, page, perPage)
        verify(mockPhotoMapper).toDomainList(mockFlickrPhotos)

        assert(result is PhotosResult.Success)
        val successResult = result as PhotosResult.Success
        assert(successResult.photos == expectedDomainPhotos)
        assert(successResult.currentPage == page)
        assert(successResult.totalPages == 5)
        assert(successResult.totalPhotos == "50")
    }

    @Test
    fun `searchPhotos propagates exception from remoteDataSource`() = runTest {
        // Given
        val query = "test"
        val page = 1
        val perPage = 20
        val networkException = RuntimeException("Simulated search network error")

        given(mockRemoteDataSource.searchPhotos(query, page, perPage)).willThrow(networkException)

        // When & Then
        try {
            photoRepositoryImpl.searchPhotos(query, page, perPage)
            assert(false) { "Expected an exception to be thrown" }
        } catch (e: Exception) {
            assert(e === networkException)
            verify(mockRemoteDataSource).searchPhotos(query, page, perPage)
            verify(mockPhotoMapper, org.mockito.kotlin.never()).toDomainList(any())
        }
    }

    // --- Additional tests could include:
    // - Empty lists returned from remoteDataSource
    // - Edge cases for page/perPage values if they are validated in the repository
}
