package com.example.omadademo.domain.exception

/**
 * Base sealed class for all photo-related exceptions.
 * Provides type-safe exception handling throughout the application.
 */
sealed class PhotoException : Exception() {

    /**
     * Thrown when a network request fails due to connectivity or timeout issues.
     *
     * @param message Description of the network error
     * @param cause The underlying exception that caused this error
     */
    data class NetworkException(
        override val message: String,
        override val cause: Throwable? = null
    ) : PhotoException()

    /**
     * Thrown when JSON parsing or serialization fails.
     * Indicates API response format is unexpected or corrupted.
     *
     * @param message Description of the parsing error
     * @param cause The underlying exception that caused this error
     */
    data class ParseException(
        override val message: String,
        override val cause: Throwable? = null
    ) : PhotoException()

    /**
     * Thrown when the API server returns an error status code.
     *
     * @param code HTTP status code (e.g., 500, 404)
     * @param message Error message from server or description
     */
    data class ServerException(
        val code: Int,
        override val message: String
    ) : PhotoException()

    /**
     * Thrown when user input validation fails.
     * Examples: search query too short/long, contains invalid characters
     *
     * @param message Description of the validation failure
     */
    data class ValidationException(
        override val message: String
    ) : PhotoException()

    /**
     * Thrown for unexpected or unknown errors that don't fit other categories.
     *
     * @param message Description of the error
     * @param cause The underlying exception that caused this error
     */
    data class UnknownException(
        override val message: String,
        override val cause: Throwable? = null
    ) : PhotoException()
}
