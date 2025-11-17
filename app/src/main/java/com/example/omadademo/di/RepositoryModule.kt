// app/src/main/java/com/example/omadaplus/di/RepositoryModule.kt
package com.example.omadademo.di

import com.example.omadademo.data.repository.PhotoRepositoryImpl
import com.example.omadademo.domain.repository.PhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(
        photoRepositoryImpl: PhotoRepositoryImpl
    ): PhotoRepository
}
