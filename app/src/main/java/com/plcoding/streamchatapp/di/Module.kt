package com.plcoding.streamchatapp.di

import android.content.Context
import com.plcoding.streamchatapp.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.getstream.chat.android.client.ChatClient
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Module {

    @Provides
    @Singleton
    fun provideSDKClient(@ApplicationContext context: Context): ChatClient =
        ChatClient.Builder(context.getString(R.string.api_key), context).build()



}