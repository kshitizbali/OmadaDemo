package com.example.omadademo.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FlickrPhotosResponse(
    @Json(name = "photos")
    val photosContainer: PhotosContainer,
    @Json(name = "stat")
    val stat: String
)

@JsonClass(generateAdapter = true)
data class PhotosContainer(
    @Json(name = "page")
    val page: Int,
    @Json(name = "pages")
    val pages: Int,
    @Json(name = "perpage")
    val perPage: Int,
    @Json(name = "total")
    val total: Int,
    @Json(name = "photo")
    val photos: List<FlickrPhoto>
)
