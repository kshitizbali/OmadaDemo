package com.example.omadademo.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.omadademo.R
import com.example.omadademo.databinding.ItemPhotoGridBinding
import com.example.omadademo.domain.model.Photo

class PhotoGridAdapter(
    private val onPhotoClick: (Photo) -> Unit
) : ListAdapter<Photo, PhotoGridAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position), onPhotoClick)
    }

    class PhotoViewHolder(private val binding: ItemPhotoGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo, onPhotoClick: (Photo) -> Unit) {
            binding.photoThumbnail.load(photo.thumbnailUrl) {
                crossfade(false) // Disable crossfade to avoid drawing cache issues with hardware bitmaps
                placeholder(R.drawable.ic_photo_placeholder)
                error(R.drawable.ic_photo_placeholder)
            }
            binding.root.setOnClickListener {
                onPhotoClick(photo)
            }
        }
    }

    private class PhotoDiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}
