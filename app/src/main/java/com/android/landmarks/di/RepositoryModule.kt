package com.android.landmarks.di

import android.content.SharedPreferences
import com.android.landmarks.domain.repository.NotesRepository
import com.android.landmarks.domain.repository.UserRepository
import com.android.landmarks.repository.NotesRepositoryImp
import com.android.landmarks.repository.UserRepositoryImp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideNotesRepository(
        firebaseFirestore: FirebaseFirestore
    ): NotesRepository {
        return NotesRepositoryImp(firebaseFirestore)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        sharedPreferences: SharedPreferences,
        firebaseFirestore: FirebaseFirestore
    ): UserRepository {
        return UserRepositoryImp(sharedPreferences, firebaseFirestore)
    }
}
