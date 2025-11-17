package com.example.omadademo.data.remote

import com.example.omadademo.data.remote.dto.FlickrPhotosResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Flickr API Service interface for photo operations.
 *
 * Extras Parameter:
 * - date_upload: Photo upload timestamp
 * - date_taken: When photo was taken
 * - owner_name: Real name of photographer
 * - views: Number of times photo has been viewed
 * - isfavorite: Whether photo is user's favorite (requires authentication)
 * - ispublic, isfriend, isfamily: Privacy settings
 *
 * More info: https://www.flickr.com/services/api/flickr.photos.getRecent.html
 */
interface FlickrApiService {

    /**
     * Gets recent photos from Flickr with enhanced metadata.
     *
     * @param method API method name
     * @param apiKey Flickr API key
     * @param format Response format (JSON)
     * @param noJsonCallback Remove JSON callback wrapper
     * @param extras Additional fields to include in response
     * @param page Page number for pagination
     * @param perPage Photos per page
     */
    @GET("rest/")
    suspend fun getRecentPhotos(
        @Query("method") method: String = "flickr.photos.getRecent",
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noJsonCallback: Int = 1,
        @Query("extras") extras: String = "date_upload,owner_name,views",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): FlickrPhotosResponse

    /**
     * Searches for photos with enhanced metadata.
     *
     * @param method API method name
     * @param apiKey Flickr API key
     * @param text Search query
     * @param format Response format (JSON)
     * @param noJsonCallback Remove JSON callback wrapper
     * @param extras Additional fields to include in response
     * @param page Page number for pagination
     * @param perPage Photos per page
     */
    @GET("rest/")
    suspend fun searchPhotos(
        @Query("method") method: String = "flickr.photos.search",
        @Query("api_key") apiKey: String,
        @Query("text") text: String,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") noJsonCallback: Int = 1,
        @Query("extras") extras: String = "date_upload,owner_name,views",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): FlickrPhotosResponse
}
