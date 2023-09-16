package com.codingwithumair.app.vidcompose.data

import android.app.Application
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer


class AppContainer(private val context: Application){
	val localMediaProvider by lazy {
		LocalMediaProvider(applicationContext = context)
	}
}