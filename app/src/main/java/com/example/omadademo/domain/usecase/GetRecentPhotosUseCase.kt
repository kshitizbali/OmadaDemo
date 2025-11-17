package com.example.omadademo.domain.usecase

import com.example.omadademo.domain.model.PhotosResult
import com.example.omadademo.domain.repository.PhotoRepository
import javax.inject.Inject

class GetRecentPhotosUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(page: Int = 1, perPage: Int = 20): PhotosResult {
        return photoRepository.getRecentPhotos(page = page, perPage = perPage)
    }
}
