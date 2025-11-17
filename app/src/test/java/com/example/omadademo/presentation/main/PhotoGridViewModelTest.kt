package com.example.omadademo.presentation.main

import com.example.omadademo.domain.model.Photo
import com.example.omadademo.domain.model.PhotosResult
import com.example.omadademo.domain.usecase.GetRecentPhotosUseCase
import com.example.omadademo.domain.usecase.SearchPhotosUseCase
import com.example.omadademo.util.PhotoGridConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.never
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class PhotoGridViewModelTest {

    @Mock
    private lateinit var getRecentPhotosUseCase: GetRecentPhotosUseCase

    @Mock
    private lateinit var searchPhotosUseCase: SearchPhotosUseCase

    private lateinit var viewModel: PhotoGridViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = PhotoGridViewModel(getRecentPhotosUseCase, searchPhotosUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadRecentPhotos success updates state`() = runTest {
        // Given
        val photos = listOf(Photo("1", "title", "owner", "secret", "server", 1, "url", "thumb"))
        val successResult = PhotosResult.Success(photos, 1, 2, 1)
        given(getRecentPhotosUseCase(any(), any())).willReturn(successResult)

        // When
        viewModel.loadRecentPhotos()

        // Then
        val state = viewModel.state.first()
        assertEquals(photos, state.photos)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
        assertEquals(1, state.currentPage)
        assertEquals(2, state.totalPages)
    }

    @Test
    fun `loadRecentPhotos error updates state`() = runTest {
        // Given
        val errorMessage = "Network error"
        given(getRecentPhotosUseCase(any(), any())).willThrow(RuntimeException(errorMessage))

        // When
        viewModel.loadRecentPhotos()

        // Then
        val state = viewModel.state.first()
        assertEquals(emptyList<Photo>(), state.photos)
        assertEquals(false, state.isLoading)
        assertEquals(errorMessage, state.error)
    }

    @Test
    fun `search success updates state`() = runTest {
        // Given
        val query = "cats"
        val photos = listOf(Photo("1", "cat photo", "owner", "secret", "server", 1, "url", "thumb"))
        val successResult = PhotosResult.Success(photos, 1, 1, 1)
        given(searchPhotosUseCase(any(), any(), any())).willReturn(successResult)

        // When
        viewModel.search(query)

        // Then
        val state = viewModel.state.first()
        assertEquals(photos, state.photos)
        assertEquals(false, state.isLoading)
        assertNull(state.error)
        assertEquals(query, state.searchQuery)
    }

    @Test
    fun `search with blank query loads recent photos`() = runTest {
        // When
        viewModel.search("   ")

        // Then
        verify(getRecentPhotosUseCase)(any(), any())
        verify(searchPhotosUseCase, never())(any(), any(), any())
    }

    @Test
    fun `search with short query sets error`() = runTest {
        // When
        viewModel.search("a")

        // Then
        val state = viewModel.state.first()
        assertEquals("Search query must be at least ${PhotoGridConstants.SEARCH_MIN_LENGTH} characters", state.error)
        verify(searchPhotosUseCase, never())(any(), any(), any())
    }

    @Test
    fun `search with long query sets error`() = runTest {
        // When
        viewModel.search("a".repeat(101))

        // Then
        val state = viewModel.state.first()
        assertEquals("Search query must be less than ${PhotoGridConstants.SEARCH_MAX_LENGTH} characters", state.error)
        verify(searchPhotosUseCase, never())(any(), any(), any())
    }

    @Test
    fun `search with invalid characters sanitizes query`() = runTest {
        // Given
        val query = "cats<script>"
        val sanitizedQuery = "catsscript"
        val photos = listOf(Photo("1", "cat photo", "owner", "secret", "server", 1, "url", "thumb"))
        val successResult = PhotosResult.Success(photos, 1, 1, 1)
        given(searchPhotosUseCase(sanitizedQuery, 1, 20)).willReturn(successResult)

        // When
        viewModel.search(query)

        // Then
        verify(searchPhotosUseCase)(sanitizedQuery, 1, 20)
    }

    @Test
    fun `loadNextPage for recent photos success`() = runTest {
        // Given
        val initialPhotos = listOf(Photo("1", "title", "owner", "secret", "server", 1, "url", "thumb"))
        val initialResult = PhotosResult.Success(initialPhotos, 1, 3, 2)
        given(getRecentPhotosUseCase(1, 20)).willReturn(initialResult)
        viewModel.loadRecentPhotos() // Initial load

        val nextPhotos = listOf(Photo("2", "title2", "owner2", "secret2", "server2", 2, "url2", "thumb2"))
        val nextResult = PhotosResult.Success(nextPhotos, 2, 3, 2)
        given(getRecentPhotosUseCase(2, 20)).willReturn(nextResult)

        // When
        viewModel.loadNextPage()

        // Then
        val state = viewModel.state.first()
        assertEquals(initialPhotos + nextPhotos, state.photos)
        assertEquals(2, state.currentPage)
    }

    @Test
    fun `loadNextPage for search results success`() = runTest {
        // Given
        val query = "cats"
        val initialPhotos = listOf(Photo("1", "cat photo", "owner", "secret", "server", 1, "url", "thumb"))
        val initialResult = PhotosResult.Success(initialPhotos, 1, 3, 2)
        given(searchPhotosUseCase(query, 1, 20)).willReturn(initialResult)
        viewModel.search(query) // Initial search

        val nextPhotos = listOf(Photo("2", "cat photo 2", "owner2", "secret2", "server2", 2, "url2", "thumb2"))
        val nextResult = PhotosResult.Success(nextPhotos, 2, 3, 2)
        given(searchPhotosUseCase(query, 2, 20)).willReturn(nextResult)

        // When
        viewModel.loadNextPage()

        // Then
        val state = viewModel.state.first()
        assertEquals(initialPhotos + nextPhotos, state.photos)
        assertEquals(2, state.currentPage)
    }

    @Test
    fun `clearError clears error from state`() = runTest {
        // Given
        viewModel.search("a") // This will set an error

        // When
        viewModel.clearError()

        // Then
        val state = viewModel.state.first()
        assertNull(state.error)
    }
}
