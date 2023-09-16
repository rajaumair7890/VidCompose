package com.codingwithumair.app.vidcompose

import android.app.Application
import com.codingwithumair.app.vidcompose.data.AppContainer


class VidComposeApplication: Application(){
	lateinit var container: AppContainer

	override fun onCreate() {
		super.onCreate()
		container = AppContainer(this)
	}

}