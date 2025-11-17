package com.example.omadademo.data.remote

import com.example.omadademo.data.remote.dto.FlickrPhotosResponse
import com.example.omadademo.domain.exception.PhotoException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Remote data source for Flickr API calls.
 * Handles network communication and error transformation.
 *
 * Converts API exceptions into domain-level PhotoExceptions for proper error handling.
 */
class RemoteDataSource @Inject constructor(
    private val flickrApiService: FlickrApiService,
    private val apiKey: String
) {
    /**
     * Fetches recent photos from Flickr API.
     *
     * @param page Page number for pagination (default: 1)
     * @param perPage Photos per page (default: 20)
     * @return FlickrPhotosResponse containing photos and pagination info
     * @throws PhotoException.NetworkException if network request fails
     * @throws PhotoException.ParseException if response parsing fails
     */
    suspend fun getRecentPhotos(page: Int = 1, perPage: Int = 20): FlickrPhotosResponse {
        return try {
            Timber.d("Fetching recent photos: page=$page, perPage=$perPage")
            flickrApiService.getRecentPhotos(
                apiKey = apiKey,
                page = page,
                perPage = perPage
            )
        } catch (e: IOException) {
            Timber.e(e, "Network error fetching recent photos")
            throw PhotoException.NetworkException(
                message = "Failed to fetch recent photos: ${e.message}",
                cause = e
            )
        } catch (e: Exception) {
            Timber.e(e, "Error parsing recent photos response")
            throw PhotoException.ParseException(
                message = "Error parsing recent photos: ${e.message}",
                cause = e
            )
        }
    }

    /**
     * Searches for photos matching the given query.
     *
     * @param query Search query string
     * @param page Page number for pagination (default: 1)
     * @param perPage Photos per page (default: 20)
     * @return FlickrPhotosResponse containing search results and pagination info
     * @throws PhotoException.NetworkException if network request fails
     * @throws PhotoException.ParseException if response parsing fails
     */
    suspend fun searchPhotos(
        query: String,
        page: Int = 1,
        perPage: Int = 20
    ): FlickrPhotosResponse {
        return try {
            Timber.d("Searching photos: query=$query, page=$page, perPage=$perPage")
            flickrApiService.searchPhotos(
                apiKey = apiKey,
                text = query,
                page = page,
                perPage = perPage
            )
        } catch (e: IOException) {
            Timber.e(e, "Network error searching photos")
            throw PhotoException.NetworkException(
                message = "Failed to search photos: ${e.message}",
                cause = e
            )
        } catch (e: Exception) {
            Timber.e(e, "Error parsing search response")
            throw PhotoException.ParseException(
                message = "Error parsing search results: ${e.message}",
                cause = e
            )
        }
    }
}
