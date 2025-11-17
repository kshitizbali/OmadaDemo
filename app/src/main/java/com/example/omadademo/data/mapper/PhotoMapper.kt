package com.example.omadademo.data.mapper

import com.example.omadademo.data.remote.dto.FlickrPhoto
import com.example.omadademo.domain.model.Photo
import javax.inject.Inject
import kotlin.collections.map
import timber.log.Timber

/**
 * Maps Flickr API DTOs to domain models.
 *
 * Responsibilities:
 * - Convert FlickrPhoto DTOs to Photo domain models
 * - Construct image URLs from photo metadata
 * - Parse and convert additional metadata (views, dates)
 * - Handle nullable optional fields gracefully
 */
class PhotoMapper @Inject constructor() {

    /**
     * Converts a single FlickrPhoto DTO to domain model.
     *
     * Handles:
     * - Image URL construction from server/id/secret
     * - Thumbnail URL construction
     * - Optional field parsing (views, dates)
     * - Nullable field handling
     */
    fun toDomain(dto: FlickrPhoto): Photo {
        return Photo(
            id = dto.id,
            title = dto.title,
            owner = dto.owner,
            secret = dto.secret,
            server = dto.server,
            farm = dto.farm,
            imageUrl = buildImageUrl(dto),
            thumbnailUrl = buildThumbnailUrl(dto),
            ownerName = dto.ownerName,
            views = parseViews(dto.views),
            dateUpload = parseDateUpload(dto.dateUpload)
        )
    }

    /**
     * Converts a list of DTOs to domain models.
     */
    fun toDomainList(dtos: List<FlickrPhoto>): List<Photo> {
        return dtos.map { toDomain(it) }
    }

    /**
     * Builds the full-size image URL.
     *
     * Format: https://live.staticflickr.com/{server}/{id}_{secret}.jpg
     */
    private fun buildImageUrl(photo: FlickrPhoto): String {
        return "https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg"
    }

    /**
     * Builds the thumbnail image URL.
     *
     * Format: https://live.staticflickr.com/{server}/{id}_{secret}_q.jpg
     * Size code 'q' = 75x75 square thumbnail
     */
    private fun buildThumbnailUrl(photo: FlickrPhoto): String {
        return "https://live.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}_q.jpg"
    }

    /**
     * Parses the views count from the API response.
     *
     * The Flickr API returns views as a string, so we convert to Long.
     * Handles null values gracefully.
     *
     * @param viewsString Views count from API (may be null if extras not requested)
     * @return Parsed long value or null
     */
    private fun parseViews(viewsString: String?): Long? {
        return try {
            viewsString?.toLongOrNull()
        } catch (e: NumberFormatException) {
            Timber.w("Failed to parse views: $viewsString")
            null
        }
    }

    /**
     * Parses the upload date from the API response.
     *
     * The Flickr API returns dateupload as a Unix timestamp string.
     * Converts to Long for use in date formatting.
     *
     * @param dateString Upload date as Unix timestamp string (may be null)
     * @return Parsed long timestamp or null
     */
    private fun parseDateUpload(dateString: String?): Long? {
        return try {
            dateString?.toLongOrNull()
        } catch (e: NumberFormatException) {
            Timber.w("Failed to parse date upload: $dateString")
            null
        }
    }
}
