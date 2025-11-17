// app/src/main/java/com/example/omadaplus/data/repository/PhotoRepositoryImpl.kt
package com.example.omadademo.data.repository

import com.example.omadademo.data.mapper.PhotoMapper
import com.example.omadademo.data.remote.RemoteDataSource
import com.example.omadademo.domain.model.PhotosResult
import com.example.omadaplus.domain.repository.PhotoRepository
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val photoMapper: PhotoMapper
) : PhotoRepository {
    override suspend fun getRecentPhotos(page: Int, perPage: Int): PhotosResult {
        val response = remoteDataSource.getRecentPhotos(page = page, perPage = perPage)
        return PhotosResult(
            photos = photoMapper.toDomainList(response.photosContainer.photos),
            currentPage = response.photosContainer.page,
            totalPages = response.photosContainer.pages,
            totalPhotos = response.photosContainer.total
        )
    }

    override suspend fun searchPhotos(
        query: String,
        page: Int,
        perPage: Int
    ): PhotosResult {
        val response = remoteDataSource.searchPhotos(
            query = query,
            page = page,
            perPage = perPage
        )
        return PhotosResult(
            photos = photoMapper.toDomainList(response.photosContainer.photos),
            currentPage = response.photosContainer.page,
            totalPages = response.photosContainer.pages,
            totalPhotos = response.photosContainer.total
        )
    }
}
