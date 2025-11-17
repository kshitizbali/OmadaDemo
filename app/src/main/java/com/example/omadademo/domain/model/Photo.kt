package com.example.omadademo.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Domain model for photo data.
 *
 * This represents a photo from Flickr with both required and optional metadata.
 *
 * Required fields:
 * - id: Unique photo identifier
 * - title: Photo title
 * - owner: Owner ID
 * - secret, server, farm: Image URL construction
 * - imageUrl: Full URL to photo
 * - thumbnailUrl: URL to thumbnail
 *
 * Optional fields (from API extras):
 * - ownerName: Real name of photographer
 * - views: Number of times viewed
 * - dateUpload: Upload date as Unix timestamp
 *
 * All fields are Parcelable for safe Fragment navigation.
 */
@Parcelize
data class Photo(
    val id: String,
    val title: String,
    val owner: String,
    val secret: String,
    val server: String,
    val farm: Int,
    val imageUrl: String,
    val thumbnailUrl: String,
    val ownerName: String? = null,
    val views: Long? = null,
    val dateUpload: Long? = null
) : Parcelable
