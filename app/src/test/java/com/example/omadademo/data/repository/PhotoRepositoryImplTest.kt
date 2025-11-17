package com.example.omadademo.data.repository

import com.example.omadademo.data.mapper.PhotoMapper
import com.example.omadademo.data.remote.IRemoteDataSource
import com.example.omadademo.data.remote.dto.FlickrPhoto
import com.example.omadademo.data.remote.dto.FlickrPhotosResponse
import com.example.omadademo.data.remote.dto.PhotosContainer
import com.example.omadademo.domain.model.Photo
import com.example.omadademo.domain.model.PhotosResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    private lateinit var mockRemoteDataSource: IRemoteDataSource

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
            FlickrPhoto(
                id = "1",
                title = "Test Photo 1",
                owner = "owner1",
                secret = "secret1",
                server = "server1",
                farm = 1,
                isPublic = 1
            ),
            FlickrPhoto(
                id = "2",
                title = "Test Photo 2",
                owner = "owner2",
                secret = "secret2",
                server = "server2",
                farm = 2,
                isPublic = 1
            )
        )
        val mockPhotosContainer = PhotosContainer(
            page = page,
            pages = 10,
            perPage = perPage,
            total = 100,
            photos = mockFlickrPhotos
        )
        val mockResponse = FlickrPhotosResponse(
            photosContainer = mockPhotosContainer,
            stat = "ok"
        )
        val expectedDomainPhotos = listOf(
            Photo(
                id = "1",
                title = "Test Photo 1",
                owner = "owner1",
                secret = "secret1",
                server = "server1",
                farm = 1,
                imageUrl = "url1",
                thumbnailUrl = "thumb1"
            ),
            Photo(
                id = "2",
                title = "Test Photo 2",
                owner = "owner2",
                secret = "secret2",
                server = "server2",
                farm = 2,
                imageUrl = "url2",
                thumbnailUrl = "thumb2"
            )
        )

        given(mockRemoteDataSource.getRecentPhotos(page, perPage)).willReturn(mockResponse)
        given(mockPhotoMapper.toDomainList(mockFlickrPhotos)).willReturn(expectedDomainPhotos)

        // When
        val result = photoRepositoryImpl.getRecentPhotos(page, perPage)

        // Then
        verify(mockRemoteDataSource).getRecentPhotos(page, perPage)
        verify(mockPhotoMapper).toDomainList(mockFlickrPhotos)

        assertEquals(expectedDomainPhotos, result.photos)
        assertEquals(page, result.currentPage)
        assertEquals(10, result.totalPages)
        assertEquals(100, result.totalPhotos)
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
            assertTrue("Expected an exception to be thrown", false)
        } catch (e: Exception) {
            assertEquals(networkException, e) // Verify the correct exception is propagated
            verify(mockRemoteDataSource).getRecentPhotos(page, perPage)
            // Ensure mapper is NOT called on error
            verify(mockPhotoMapper, never()).toDomainList(any())
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
            FlickrPhoto(
                id = "3",
                title = "Search Photo 1",
                owner = "owner3",
                secret = "secret3",
                server = "server3",
                farm = 3,
                isPublic = 1
            )
        )
        val mockPhotosContainer = PhotosContainer(
            page = page,
            pages = 5,
            perPage = perPage,
            total = 50,
            photos = mockFlickrPhotos
        )
        val mockResponse = FlickrPhotosResponse(
            photosContainer = mockPhotosContainer,
            stat = "ok"
        )
        val expectedDomainPhotos = listOf(
            Photo(
                id = "3",
                title = "Search Photo 1",
                owner = "owner3",
                secret = "secret3",
                server = "server3",
                farm = 3,
                imageUrl = "url3",
                thumbnailUrl = "thumb3"
            )
        )

        given(mockRemoteDataSource.searchPhotos(query, page, perPage)).willReturn(mockResponse)
        given(mockPhotoMapper.toDomainList(mockFlickrPhotos)).willReturn(expectedDomainPhotos)

        // When
        val result = photoRepositoryImpl.searchPhotos(query, page, perPage)

        // Then
        verify(mockRemoteDataSource).searchPhotos(query, page, perPage)
        verify(mockPhotoMapper).toDomainList(mockFlickrPhotos)

        assertEquals(expectedDomainPhotos, result.photos)
        assertEquals(page, result.currentPage)
        assertEquals(5, result.totalPages)
        assertEquals(50, result.totalPhotos)
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
            assertTrue("Expected an exception to be thrown", false)
        } catch (e: Exception) {
            assertEquals(networkException, e)
            verify(mockRemoteDataSource).searchPhotos(query, page, perPage)
            verify(mockPhotoMapper, never()).toDomainList(any())
        }
    }

    @Test
    fun `getRecentPhotos with empty results returns empty list`() = runTest {
        // Given
        val page = 1
        val perPage = 20
        val mockPhotosContainer = PhotosContainer(
            page = page,
            pages = 0,
            perPage = perPage,
            total = 0,
            photos = emptyList()
        )
        val mockResponse = FlickrPhotosResponse(
            photosContainer = mockPhotosContainer,
            stat = "ok"
        )

        given(mockRemoteDataSource.getRecentPhotos(page, perPage)).willReturn(mockResponse)
        given(mockPhotoMapper.toDomainList(emptyList())).willReturn(emptyList())

        // When
        val result = photoRepositoryImpl.getRecentPhotos(page, perPage)

        // Then
        assertEquals(emptyList<Photo>(), result.photos)
        assertEquals(0, result.totalPages)
        assertEquals(0, result.totalPhotos)
    }
}
