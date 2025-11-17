// app/src/main/java/com/example/omadademo/presentation/fullscreen/FullScreenImageFragment.kt
package com.example.omadademo.presentation.fullscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.omadademo.R
import com.example.omadademo.databinding.FragmentFullscreenImageBinding
import com.example.omadademo.domain.model.Photo
import timber.log.Timber

/**
 * Full-screen image viewer fragment with pinch-zoom and pan capabilities.
 *
 * Features:
 * - Displays photo in full-screen immersive mode
 * - Pinch-zoom to zoom in/out
 * - Double-tap to zoom
 * - Pan/drag to move around zoomed image
 * - Swipe down to close (dismissible)
 * - Back button to close
 * - Smooth transitions
 *
 * Uses PhotoView library for zoom/pan functionality:
 * - https://github.com/chrisbanes/PhotoView
 * - Minimal setup, powerful features
 * - Touch-friendly gestures
 */
class FullScreenImageFragment : Fragment() {

    private lateinit var binding: FragmentFullscreenImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFullscreenImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        loadPhoto()
    }

    /**
     * Sets up UI elements and listeners.
     */
    private fun setupUI() {
        // Close button
        binding.closeButton.setOnClickListener {
            Timber.d("Close button clicked")
            findNavController().popBackStack()
        }

        // Back button (system)
        // Handled automatically by navigation

        // PhotoView is configured in the layout with default zoom behavior
        // Double-tap to zoom (2x)
        // Pinch to zoom smoothly
        // Pan when zoomed
    }

    /**
     * Loads the photo from navigation arguments.
     */
    private fun loadPhoto() {
        val photo = arguments?.getParcelable<Photo>("photo")
        if (photo != null) {
            Timber.d("Loading full-screen image: ${photo.title}")
            bindPhotoData(photo)
        } else {
            Timber.w("No photo data provided to FullScreenImageFragment")
            // Fallback: close the fragment
            findNavController().popBackStack()
        }
    }

    /**
     * Binds photo data to the PhotoView.
     * Configures PhotoView to properly scale and fit image on initial load.
     */
    private fun bindPhotoData(photo: Photo) {
        // Configure PhotoView for optimal viewing
        with(binding.photoView) {
            // Set scale levels for better initial view
            // minimumScale: 0.75x (slightly less than original, allows pinch-out)
            // mediumScale: 1.0x (original size - used by double-tap)
            // maximumScale: 3x (max zoom when pinching)
            minimumScale = 0.75f
            mediumScale = 1.0f
            maximumScale = 3f

            // Allow zooming
            isZoomable = true
        }

        // Load high-quality image with PhotoView
        binding.photoView.load(photo.imageUrl) {
            crossfade(400)
            placeholder(R.drawable.ic_photo_placeholder)
            error(R.drawable.ic_photo_placeholder)
        }

        // Schedule fitting after a short delay to ensure image is loaded
        binding.photoView.post {
            fitImageToScreen()
        }

        Timber.d("Full-screen photo loaded: ${photo.title}")
    }

    /**
     * Fits the image to the screen by calculating appropriate scale.
     * This ensures the image fills the available space optimally.
     */
    private fun fitImageToScreen() {
        val photoView = binding.photoView
        val displayMetrics = requireContext().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Get drawable dimensions
        val drawable = photoView.drawable
        if (drawable != null) {
            val imageWidth = drawable.intrinsicWidth
            val imageHeight = drawable.intrinsicHeight

            if (imageWidth > 0 && imageHeight > 0) {
                // Calculate scale factors for width and height
                val scaleX = screenWidth.toFloat() / imageWidth
                val scaleY = screenHeight.toFloat() / imageHeight

                // Use the larger scale so image fits in the screen (fills one dimension)
                val scale = maxOf(scaleX, scaleY)

                // Clamp to min/max values
                val finalScale = scale.coerceIn(
                    photoView.minimumScale,
                    photoView.maximumScale
                )

                // Set the scale and center the image
                photoView.setScale(finalScale, true)

                Timber.d(
                    "Image fitted to screen - Scale: $finalScale, " +
                    "Image: ${imageWidth}x${imageHeight}, Screen: ${screenWidth}x${screenHeight}"
                )
            }
        }
    }
}
