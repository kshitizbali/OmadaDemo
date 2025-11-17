// app/src/main/java/com/example/omadaplus/domain/usecase/SearchPhotosUseCase.kt
package com.example.omadaplus.domain.usecase

import com.example.omadademo.domain.model.PhotosResult
import com.example.omadaplus.domain.repository.PhotoRepository
import javax.inject.Inject

class SearchPhotosUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(
        query: String,
        page: Int = 1,
        perPage: Int = 20
    ): PhotosResult {
        return photoRepository.searchPhotos(
            query = query,
            page = page,
            perPage = perPage
        )
    }
}
