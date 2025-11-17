// app/src/main/java/com/example/omadaplus/presentation/detail/PhotoDetailViewModel.kt
package com.example.omadademo.presentation.detail

import androidx.lifecycle.ViewModel
import com.example.omadademo.domain.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor() : ViewModel() {

    private val _photo = MutableStateFlow<Photo?>(null)
    val photo: StateFlow<Photo?> = _photo.asStateFlow()

    fun setPhoto(photo: Photo) {
        _photo.value = photo
    }
}
