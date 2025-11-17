package com.example.omadademo.domain.model

data class PhotosResult(
    val photos: List<Photo>,
    val currentPage: Int,
    val totalPages: Int,
    val totalPhotos: Int
)
