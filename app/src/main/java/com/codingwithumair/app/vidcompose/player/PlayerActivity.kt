package com.codingwithumair.app.vidcompose.player

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.UnstableApi
import com.codingwithumair.app.vidcompose.ui.screens.playerScreen.PlayerScreen
import com.codingwithumair.app.vidcompose.ui.theme.VidComposeTheme

@UnstableApi
class PlayerActivity: ComponentActivity() {

	private val playerViewModel by viewModels<PlayerViewModel>(factoryProducer = {PlayerViewModel.factory})

	override fun onCreate(savedInstanceState: Bundle?) {
		handleWindowInsetsAndDecors(window = window)
		super.onCreate(savedInstanceState)

		requestedOrientation = playerViewModel.playerState.value.orientation

		val videoUri = intent.data

		if(videoUri != null){
			Log.d(TAG, "Intent Uri is not null")
			playerViewModel.onIntent(videoUri)
		}

		setContent{
			VidComposeTheme(
				darkTheme = !isSystemInDarkTheme()
			){
				Surface(Modifier.fillMaxSize()) {
					PlayerScreen(
						viewModel = playerViewModel,
						onRotateScreenClick = {
							playerViewModel.onRotateScreen()
							requestedOrientation = playerViewModel.playerState.value.orientation
						},
						onBackClick = {finish()}
					)
				}
			}

		}
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)

		val videoUri = intent?.data

		if(videoUri != null){
			Log.d(TAG, "OnNewIntent Uri is not null")
			playerViewModel.onNewIntent(videoUri)
		}
	}

	override fun onPause() {
		playerViewModel.playPauseOnActivityLifeCycleEvents(shouldPause = true)
		super.onPause()
	}

	override fun onResume() {
		playerViewModel.playPauseOnActivityLifeCycleEvents(shouldPause = false)
		super.onResume()
	}

	companion object{

		const val TAG = "PlayerActivity"

		fun handleWindowInsetsAndDecors(window: Window){

			WindowCompat.setDecorFitsSystemWindows(window, false)

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				window.attributes.layoutInDisplayCutoutMode =
					WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
			}else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
				window.attributes.layoutInDisplayCutoutMode =
					WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
			}

			WindowInsetsControllerCompat(window, window.decorView).let { controller ->
				controller.hide(WindowInsetsCompat.Type.systemBars())
				controller.systemBarsBehavior =
					WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
			}

		}

	}
}