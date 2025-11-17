package com.example.omadademo.di

import android.content.Context
import com.example.omadademo.BuildConfig
import com.example.omadademo.data.remote.FlickrApiService
import com.example.omadademo.data.remote.IRemoteDataSource
import com.example.omadademo.data.remote.RemoteDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val FLICKR_BASE_URL = "https://www.flickr.com/services/"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        // Only add logging interceptor in debug builds to avoid logging sensitive data
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(FLICKR_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideFlickrApiService(retrofit: Retrofit): FlickrApiService =
        retrofit.create(FlickrApiService::class.java)

    @Provides
    @Singleton
    fun provideApiKey(): String = BuildConfig.FLICKR_API_KEY

    /**
     * Provides RemoteDataSource as IRemoteDataSource interface.
     *
     * This binding allows Hilt to inject the interface type (IRemoteDataSource)
     * instead of the concrete implementation (RemoteDataSource).
     *
     * Benefits:
     * - Loose coupling: Consumers depend on interface, not implementation
     * - Testability: Interface is 100% mockable in unit tests
     * - Flexibility: Easy to swap implementations if needed
     *
     * @param flickrApiService API service for making requests
     * @param apiKey Flickr API key from local.properties
     * @return RemoteDataSource instance as IRemoteDataSource interface
     */
    @Provides
    @Singleton
    fun provideRemoteDataSource(
        flickrApiService: FlickrApiService,
        apiKey: String
    ): IRemoteDataSource = RemoteDataSource(flickrApiService, apiKey)
}
