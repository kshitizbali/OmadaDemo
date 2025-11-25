package com.example.omadademo.domain.exception

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Tests for PhotoException sealed class and its subclasses.
 *
 * Ensures exception hierarchy is properly structured and works as expected.
 */
class PhotoExceptionTest {

    @Test
    fun `NetworkException creates with message and null cause`() {
        // Given
        val message = "Connection timeout"

        // When
        val exception = PhotoException.NetworkException(message)

        // Then
        assertEquals(message, exception.message)
        assertNull(exception.cause)
    }

    @Test
    fun `NetworkException creates with message and cause`() {
        // Given
        val message = "Connection timeout"
        val cause = RuntimeException("Socket timeout")

        // When
        val exception = PhotoException.NetworkException(message, cause)

        // Then
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `NetworkException is instance of PhotoException`() {
        // Given
        val exception = PhotoException.NetworkException("Error")

        // When & Then
        assert(exception is PhotoException)
        assert(exception is Exception)
    }

    @Test
    fun `ParseException creates with message and null cause`() {
        // Given
        val message = "Invalid JSON format"

        // When
        val exception = PhotoException.ParseException(message)

        // Then
        assertEquals(message, exception.message)
        assertNull(exception.cause)
    }

    @Test
    fun `ParseException creates with message and cause`() {
        // Given
        val message = "Invalid JSON format"
        val cause = IllegalStateException("No value for key")

        // When
        val exception = PhotoException.ParseException(message, cause)

        // Then
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `ServerException creates with code and message`() {
        // Given
        val code = 500
        val message = "Internal server error"

        // When
        val exception = PhotoException.ServerException(code, message)

        // Then
        assertEquals(code, exception.code)
        assertEquals(message, exception.message)
    }

    @Test
    fun `ServerException with 404 code`() {
        // Given
        val code = 404
        val message = "Not found"

        // When
        val exception = PhotoException.ServerException(code, message)

        // Then
        assertEquals(404, exception.code)
        assertEquals(message, exception.message)
    }

    @Test
    fun `ServerException with 403 code`() {
        // Given
        val code = 403
        val message = "Forbidden"

        // When
        val exception = PhotoException.ServerException(code, message)

        // Then
        assertEquals(403, exception.code)
        assertEquals(message, exception.message)
    }

    @Test
    fun `ValidationException creates with message`() {
        // Given
        val message = "Search query too short"

        // When
        val exception = PhotoException.ValidationException(message)

        // Then
        assertEquals(message, exception.message)
    }

    @Test
    fun `UnknownException creates with message and null cause`() {
        // Given
        val message = "An unexpected error occurred"

        // When
        val exception = PhotoException.UnknownException(message)

        // Then
        assertEquals(message, exception.message)
        assertNull(exception.cause)
    }

    @Test
    fun `UnknownException creates with message and cause`() {
        // Given
        val message = "An unexpected error occurred"
        val cause = Exception("Unexpected error")

        // When
        val exception = PhotoException.UnknownException(message, cause)

        // Then
        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `All exception types are PhotoException instances`() {
        // When & Then
        assert(PhotoException.NetworkException("Error") is PhotoException)
        assert(PhotoException.ParseException("Error") is PhotoException)
        assert(PhotoException.ServerException(500, "Error") is PhotoException)
        assert(PhotoException.ValidationException("Error") is PhotoException)
        assert(PhotoException.UnknownException("Error") is PhotoException)
    }

    @Test
    fun `Exception data class equality`() {
        // Given
        val exception1 = PhotoException.NetworkException("Same message")
        val exception2 = PhotoException.NetworkException("Same message")

        // When & Then
        assertEquals(exception1, exception2)
    }

    @Test
    fun `Exception data class inequality`() {
        // Given
        val exception1 = PhotoException.NetworkException("Message 1")
        val exception2 = PhotoException.NetworkException("Message 2")

        // When & Then
        assert(exception1 != exception2)
    }

    @Test
    fun `Different exception types are not equal`() {
        // Given
        val networkException = PhotoException.NetworkException("Error")
        val parseException = PhotoException.ParseException("Error")

        // When & Then
        assert(networkException != parseException)
    }

    @Test
    fun `ServerException hash code consistent with equality`() {
        // Given
        val exception1 = PhotoException.ServerException(500, "Error")
        val exception2 = PhotoException.ServerException(500, "Error")

        // When & Then
        assertEquals(exception1.hashCode(), exception2.hashCode())
    }

    @Test
    fun `ValidationException is throwable`() {
        // Given
        val exception = PhotoException.ValidationException("Invalid input")

        // When & Then
        assertNotNull(exception)
        assert(exception is Throwable)
    }
}
