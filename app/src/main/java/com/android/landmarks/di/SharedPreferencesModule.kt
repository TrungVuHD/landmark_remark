package com.android.landmarks.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SharedPreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences("user_cache", Context.MODE_PRIVATE);
    }

}