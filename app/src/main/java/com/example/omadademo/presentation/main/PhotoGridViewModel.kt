package com.example.omadademo.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.omadademo.presentation.common.PhotoGridState
import com.example.omadademo.util.PhotoGridConstants
import com.example.omadademo.domain.usecase.GetRecentPhotosUseCase
import com.example.omadademo.domain.usecase.SearchPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the photo grid screen.
 *
 * Responsibilities:
 * - Manage UI state (photos, loading, errors, pagination)
 * - Handle user interactions (search, pagination)
 * - Validate user input before API calls
 * - Log operations for debugging
 *
 * State Management:
 * - Uses StateFlow for reactive UI updates
 * - Immutable PhotoGridState data class
 * - Proper lifecycle awareness via viewModelScope
 *
 * Input Validation:
 * - Search: 2-100 characters, dangerous characters removed
 * - Prevents SQL injection, XSS, and other injection attacks
 *
 * Pagination:
 * - Maintains search context when paginating
 * - Prevents concurrent page loads
 * - Appends new photos to existing list
 */
@HiltViewModel
class PhotoGridViewModel @Inject constructor(
    private val getRecentPhotosUseCase: GetRecentPhotosUseCase,
    private val searchPhotosUseCase: SearchPhotosUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PhotoGridState())
    val state: StateFlow<PhotoGridState> = _state.asStateFlow()

    private var isSearchActive = false

    init {
        loadRecentPhotos()
    }

    /**
     * Loads the most recent photos from Flickr.
     *
     * Resets search context and loads page 1 of recent photos.
     * Sets loading state during request.
     *
     * Error Handling:
     * - Catches exceptions and sets error state with user-friendly message
     * - Logs errors for debugging
     * - Disables retry logic if already loading
     */
    fun loadRecentPhotos() {
        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                Timber.d("Loading recent photos...")
                val result = getRecentPhotosUseCase(page = 1)
                isSearchActive = false
                _state.value = PhotoGridState(
                    photos = result.photos,
                    isLoading = false,
                    currentPage = result.currentPage,
                    totalPages = result.totalPages,
                    searchQuery = ""
                )
                Timber.d("Loaded ${result.photos.size} recent photos")
            } catch (e: Exception) {
                Timber.e(e, "Error loading recent photos")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    /**
     * Searches for photos matching the given query with comprehensive validation.
     *
     * Validation Steps (in order):
     * 1. Trim whitespace
     * 2. Check if empty (loads recent photos instead)
     * 3. Verify minimum length (2 characters)
     * 4. Verify maximum length (100 characters to prevent abuse)
     * 5. Sanitize dangerous characters (", ', <, >, ;)
     * 6. Verify sanitized result is not empty
     *
     * Security Features:
     * - Prevents SQL injection via sanitization
     * - Prevents XSS attacks via character filtering
     * - Prevents API abuse via length restrictions
     * - Provides user-friendly error messages
     *
     * @param query The search query string (will be trimmed and sanitized)
     *
     * Example:
     * ```
     * search("cats")              // Valid: searches for cats
     * search("a")                 // Invalid: too short, shows error
     * search("test<script>")      // Valid: sanitized to "testscript"
     * search("")                  // Loads recent photos instead
     * ```
     */
    fun search(query: String) {
        val trimmedQuery = query.trim()

        // Validation: Check if empty
        if (trimmedQuery.isBlank()) {
            Timber.d("Empty search query, loading recent photos")
            loadRecentPhotos()
            return
        }

        // Validation: Check minimum length
        if (trimmedQuery.length < PhotoGridConstants.SEARCH_MIN_LENGTH) {
            val error = "Search query must be at least ${PhotoGridConstants.SEARCH_MIN_LENGTH} characters"
            Timber.w(error)
            _state.value = _state.value.copy(error = error)
            return
        }

        // Validation: Check maximum length to prevent API abuse
        if (trimmedQuery.length > PhotoGridConstants.SEARCH_MAX_LENGTH) {
            val error = "Search query must be less than ${PhotoGridConstants.SEARCH_MAX_LENGTH} characters"
            Timber.w(error)
            _state.value = _state.value.copy(error = error)
            return
        }

        // Sanitize: Remove dangerous characters (XSS/injection prevention)
        val sanitized = trimmedQuery.replace(Regex("[\"'<>;]"), "")
        Timber.d("Sanitized search query: '$trimmedQuery' -> '$sanitized'")

        if (sanitized.isEmpty()) {
            _state.value = _state.value.copy(
                error = "Search query contains invalid characters"
            )
            return
        }

        if (_state.value.isLoading) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val result = searchPhotosUseCase(query = sanitized, page = 1)
                isSearchActive = true
                _state.value = PhotoGridState(
                    photos = result.photos,
                    isLoading = false,
                    currentPage = result.currentPage,
                    totalPages = result.totalPages,
                    searchQuery = sanitized
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    /**
     * Loads the next page of photos (pagination).
     *
     * Behavior:
     * - Only loads if not already loading (prevents concurrent requests)
     * - Only loads if more pages available (currentPage < totalPages)
     * - Maintains search context (continues search or recent results)
     * - Appends new photos to existing list
     * - Updates pagination state (currentPage, totalPages)
     *
     * Smart Pagination:
     * - If search is active: calls [searchPhotosUseCase] with current search query
     * - If recent is active: calls [getRecentPhotosUseCase]
     * - Seamlessly handles transition between search and recent
     *
     * Error Handling:
     * - Catches exceptions and sets error state
     * - Maintains existing photo list on error
     * - Logs errors for debugging
     *
     * Example:
     * ```
     * // User scrolls to bottom with 6 items remaining
     * // Fragment calls loadNextPage()
     * // If currentPage=1, totalPages=5: loads page 2, appends photos
     * // If currentPage=5, totalPages=5: does nothing (already at last page)
     * ```
     */
    fun loadNextPage() {
        val currentState = _state.value

        // Prevent concurrent page loads and loading past last page
        if (currentState.isLoading || currentState.currentPage >= currentState.totalPages) {
            return
        }

        val nextPage = currentState.currentPage + 1

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                Timber.d("Loading page $nextPage...")

                // Use appropriate use case based on current context
                val result = if (isSearchActive) {
                    Timber.d("Continuing search: '${currentState.searchQuery}' on page $nextPage")
                    searchPhotosUseCase(query = currentState.searchQuery, page = nextPage)
                } else {
                    Timber.d("Loading recent photos page $nextPage")
                    getRecentPhotosUseCase(page = nextPage)
                }

                // Append new photos to existing list, preserving previous results
                _state.value = PhotoGridState(
                    photos = currentState.photos + result.photos,
                    isLoading = false,
                    currentPage = result.currentPage,
                    totalPages = result.totalPages,
                    searchQuery = currentState.searchQuery
                )
                Timber.d("Loaded page $nextPage with ${result.photos.size} photos (total: ${currentState.photos.size + result.photos.size})")
            } catch (e: Exception) {
                Timber.e(e, "Error loading page $nextPage")
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    /**
     * Clears the current error message from UI state.
     *
     * Called after user dismisses error snackbar to prevent stale error messages.
     * This allows the UI to recover and show normal state after an error.
     *
     * Example:
     * ```
     * // When user taps "Retry" or error snackbar expires
     * viewModel.clearError()
     * // Error message is removed from state
     * ```
     */
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
