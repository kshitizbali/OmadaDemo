// app/src/main/java/com/example/omadademo/presentation/detail/PhotoDetailFragment.kt
package com.example.omadademo.presentation.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.omadademo.R
import com.example.omadademo.databinding.FragmentPhotoDetailBinding
import com.example.omadademo.domain.model.Photo
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Fragment displaying detailed photo information with enhanced Material Design 3 UX.
 *
 * Features:
 * - Collapsing toolbar with parallax effect
 * - Beautiful image header with gradient scrim
 * - Photo information card with photographer details
 * - Statistics card (views, favorites, comments)
 * - Action buttons (share, view on Flickr)
 * - Smooth scrolling behavior with nested scroll view
 *
 * State Management:
 * - Uses ViewModel for photo data persistence
 * - Observes photo changes via Flow
 * - Proper lifecycle awareness via viewLifecycleOwner
 */
@AndroidEntryPoint
class PhotoDetailFragment : Fragment() {

    private val viewModel: PhotoDetailViewModel by viewModels()
    private lateinit var binding: FragmentPhotoDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupImageClickListener()
        setupButtons()
        observePhoto()

        // Retrieve photo from arguments
        val photo = arguments?.getParcelable<Photo>("photo")
        if (photo != null) {
            viewModel.setPhoto(photo)
            Timber.d("Photo detail opened: ${photo.title}")
        }
    }

    /**
     * Configures the collapsing toolbar and back navigation.
     */
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    /**
     * Configures the image click listener to open full-screen viewer.
     */
    private fun setupImageClickListener() {
        binding.imageViewPhotoDetail.setOnClickListener {
            viewModel.photo.value?.let { photo ->
                Timber.d("Photo image clicked - opening full-screen: ${photo.title}")
                val bundle = Bundle().apply {
                    putParcelable("photo", photo)
                }
                findNavController().navigate(
                    R.id.action_photoDetailFragment_to_fullScreenImageFragment,
                    bundle
                )
            }
        }
    }

    /**
     * Configures action buttons and their click listeners.
     */
    private fun setupButtons() {
        binding.btnFavorite.setOnClickListener {
            viewModel.photo.value?.let { photo ->
                Timber.d("Favorite button clicked for: ${photo.title}")
                showSnackbar("Added to favorites")
            }
        }

        binding.btnShare.setOnClickListener {
            viewModel.photo.value?.let { photo ->
                sharePhoto(photo)
            }
        }

        binding.btnViewOnFlickr.setOnClickListener {
            viewModel.photo.value?.let { photo ->
                openOnFlickr(photo)
            }
        }
    }

    /**
     * Observes photo data changes and updates the UI accordingly.
     *
     * Updates:
     * - Hero image with parallax effect
     * - Collapsing toolbar title
     * - Photo information (title, owner)
     * - Statistics (views, favorites, comments)
     */
    private fun observePhoto() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.photo.collect { photo ->
                if (photo != null) {
                    Timber.d("Rendering photo detail: ${photo.title}")
                    bindPhotoData(photo)
                }
            }
        }
    }

    /**
     * Binds photo data to all UI elements.
     */
    private fun bindPhotoData(photo: Photo) {
        // Load hero image with crossfade
        binding.imageViewPhotoDetail.load(photo.imageUrl) {
            crossfade(400)
            placeholder(R.drawable.ic_photo_placeholder)
            error(R.drawable.ic_photo_placeholder)
        }

        // Set collapsing toolbar title
        binding.collapsingToolbar.title = photo.title

        // Set photo information
        binding.textViewPhotoTitle.text = photo.title
        binding.textViewPhotoOwner.text = photo.ownerName ?: photo.owner

        // Set statistics from API data
        binding.textViewViews.text = if (photo.views != null) {
            formatNumber(photo.views)
        } else {
            "N/A"
        }

        // Favorites and comments use placeholder (not available in getRecent API)
        binding.textViewFavorites.text = "—"
        binding.textViewComments.text = "—"

        Timber.d("Photo detail bound successfully with views: ${photo.views}")
    }

    /**
     * Shares the photo via intent chooser.
     */
    private fun sharePhoto(photo: Photo) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, photo.title)
                putExtra(Intent.EXTRA_TEXT, buildShareText(photo))
            }
            startActivity(Intent.createChooser(shareIntent, "Share Photo"))
            Timber.d("Share intent launched for: ${photo.title}")
        } catch (e: Exception) {
            Timber.e(e, "Error sharing photo")
            showSnackbar("Could not share photo")
        }
    }

    /**
     * Opens the photo on Flickr website.
     */
    private fun openOnFlickr(photo: Photo) {
        try {
            val flickrUrl = "https://www.flickr.com/photos/${photo.owner}/${photo.id}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(flickrUrl))
            startActivity(intent)
            Timber.d("Opening on Flickr: $flickrUrl")
        } catch (e: Exception) {
            Timber.e(e, "Error opening Flickr")
            showSnackbar("Could not open Flickr")
        }
    }

    /**
     * Builds shareable text for the photo.
     */
    private fun buildShareText(photo: Photo): String {
        return "Check out this photo: \"${photo.title}\" by ${photo.owner}\n" +
                "View on Flickr: https://www.flickr.com/photos/${photo.owner}/${photo.id}"
    }

    /**
     * Formats large numbers (e.g., 2500 -> "2.5K").
     */
    private fun formatNumber(number: Long): String {
        return when {
            number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
            number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
            else -> number.toString()
        }
    }

    /**
     * Shows a temporary snackbar message.
     */
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
