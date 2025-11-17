// app/src/main/java/com/example/omadaplus/domain/repository/PhotoRepository.kt
package com.example.omadaplus.domain.repository

import com.example.omadademo.domain.model.PhotosResult

interface PhotoRepository {
    suspend fun getRecentPhotos(page: Int = 1, perPage: Int = 20): PhotosResult
    suspend fun searchPhotos(query: String, page: Int = 1, perPage: Int = 20): PhotosResult
}
