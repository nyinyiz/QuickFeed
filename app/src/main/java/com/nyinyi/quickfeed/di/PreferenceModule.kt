package com.nyinyi.quickfeed.di

import android.content.Context
import com.nyinyi.quickfeed.provider.ThemePreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferenceModule {
    @Singleton
    @Provides
    fun provideThemePreferenceManager(
        @ApplicationContext context: Context,
    ): ThemePreferenceManager = ThemePreferenceManager(context)
}
