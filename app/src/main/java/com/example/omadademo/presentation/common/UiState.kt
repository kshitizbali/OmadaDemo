// app/src/main/java/com/example/omadaplus/presentation/common/UiState.kt
package com.example.omadademo.presentation.common

import com.example.omadademo.domain.model.Photo

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    data class Empty(val message: String = "No data available") : UiState<Nothing>()
}

data class PhotoGridState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val searchQuery: String = ""
)
