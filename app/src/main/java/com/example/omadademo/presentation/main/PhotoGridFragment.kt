package com.example.omadademo.presentation.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.omadademo.R
import com.example.omadademo.databinding.FragmentPhotoGridBinding
import com.example.omadademo.util.hideKeyboard
import com.example.omadademo.util.PhotoGridConstants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class PhotoGridFragment : Fragment() {

    private val viewModel: PhotoGridViewModel by viewModels()
    private lateinit var binding: FragmentPhotoGridBinding
    private lateinit var adapter: PhotoGridAdapter
    private var isLoadingMore = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchListener()
        observeState()
    }

    private fun setupRecyclerView() {
        adapter = PhotoGridAdapter { photo ->
            val bundle = Bundle().apply {
                putParcelable("photo", photo)
            }
            findNavController().navigate(
                R.id.action_photoGridFragment_to_photoDetailFragment,
                bundle
            )
        }

        binding.recyclerViewPhotos.apply {
            layoutManager = GridLayoutManager(requireContext(), PhotoGridConstants.GRID_COLUMNS)
            adapter = this@PhotoGridFragment.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    if (lastVisibleItem >= totalItemCount - PhotoGridConstants.PAGINATION_THRESHOLD && !isLoadingMore) {
                        isLoadingMore = true
                        viewModel.loadNextPage()
                    }
                }
            })
        }
    }

    private fun setupSearchListener() {
        val searchEditText = binding.editTextSearch
        val searchButton = binding.buttonSearch

        // Search when button is clicked
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                viewModel.search(query)
            } else {
                // Empty search - load recent photos
                viewModel.loadRecentPhotos()
            }
            // Hide keyboard after search
            searchEditText.hideKeyboard()
        }

        // Search on text submission (when user presses search key)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text.toString()
                if (query.isNotEmpty()) {
                    viewModel.search(query)
                } else {
                    // Empty search - load recent photos
                    viewModel.loadRecentPhotos()
                }
                // Hide keyboard after search
                searchEditText.hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Automatically cancel collection when fragment goes to background
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    adapter.submitList(state.photos)
                    isLoadingMore = false

                    if (state.error != null) {
                        Timber.e("Error: ${state.error}")
                        showError(state.error)
                        viewModel.clearError()
                    }

                    // Only show empty state message if user performed a search (searchQuery is not empty)
                    if (state.photos.isEmpty() && !state.isLoading && state.searchQuery.isNotEmpty()) {
                        showEmptyState()
                    }
                }
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Retry") {
                viewModel.loadRecentPhotos()
            }
            .show()
    }

    private fun showEmptyState() {
        // Show empty state message only when a search returns no results
        Snackbar.make(
            binding.root,
            "No photos found for your search",
            Snackbar.LENGTH_SHORT
        ).show()
    }
}
