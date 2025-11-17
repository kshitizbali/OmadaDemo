package com.example.omadademo.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO for Flickr photo data from API response.
 *
 * Fields (from flickr.photos.getRecent/search API):
 * - id: Unique photo identifier
 * - owner: Owner ID (numeric)
 * - secret: Used for constructing image URLs
 * - server: Server ID for image URL construction
 * - farm: Farm ID for image URL construction
 * - title: Photo title
 * - ispublic: Public visibility flag (1 = public)
 * - ownerName: Real name of photographer (requires extras parameter)
 * - dateUpload: Unix timestamp of upload date (requires extras parameter)
 * - views: Number of times photo has been viewed (requires extras parameter)
 */
@JsonClass(generateAdapter = true)
data class FlickrPhoto(
    @Json(name = "id")
    val id: String,
    @Json(name = "owner")
    val owner: String,
    @Json(name = "secret")
    val secret: String,
    @Json(name = "server")
    val server: String,
    @Json(name = "farm")
    val farm: Int,
    @Json(name = "title")
    val title: String,
    @Json(name = "ispublic")
    val isPublic: Int,
    @Json(name = "ownername")
    val ownerName: String? = null,
    @Json(name = "dateupload")
    val dateUpload: String? = null,
    @Json(name = "views")
    val views: String? = null
)
