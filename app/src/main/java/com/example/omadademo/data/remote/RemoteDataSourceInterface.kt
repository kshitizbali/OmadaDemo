package com.example.omadademo.data.remote

import com.example.omadademo.data.remote.dto.FlickrPhotosResponse

/**
 * Interface for remote data source operations.
 *
 * Separates contract from implementation, making the code:
 * - More testable (interfaces are always mockable)
 * - More flexible (can have multiple implementations)
 * - More maintainable (clear API contract)
 *
 * This interface pattern eliminates Mockito's byte-buddy issues entirely
 * because interfaces don't require inline mocks.
 */
interface IRemoteDataSource {
    /**
     * Fetches recent photos from Flickr API.
     *
     * @param page Page number for pagination (default: 1)
     * @param perPage Photos per page (default: 20)
     * @return FlickrPhotosResponse containing photos and pagination info
     * @throws PhotoException.NetworkException if network request fails
     * @throws PhotoException.ParseException if response parsing fails
     */
    suspend fun getRecentPhotos(page: Int = 1, perPage: Int = 20): FlickrPhotosResponse

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
    ): FlickrPhotosResponse
}
