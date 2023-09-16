package com.codingwithumair.app.vidcompose

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.media3.common.util.UnstableApi
import com.codingwithumair.app.vidcompose.player.PlayerActivity
import com.codingwithumair.app.vidcompose.ui.screens.mainScreen.MainScreenWithBottomNavigation
import com.codingwithumair.app.vidcompose.ui.theme.VidComposeTheme

class MainActivity : ComponentActivity() {

	@UnstableApi
	override fun onCreate(savedInstanceState: Bundle?) {

		WindowCompat.setDecorFitsSystemWindows(window, false)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			window.attributes.layoutInDisplayCutoutMode =
				WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
		}else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
			window.attributes.layoutInDisplayCutoutMode =
				WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
		}

		super.onCreate(savedInstanceState)

		setContent {

			VidComposeTheme{

				 Surface(
					 modifier = Modifier
						 .fillMaxSize()
						 .navigationBarsPadding(),
					 tonalElevation = 8.dp
				 ){

					val playerActivityLauncher = rememberLauncherForActivityResult(
						contract = ActivityResultContracts.StartActivityForResult(),
						onResult = {}
					)

					MainScreenWithBottomNavigation(
						onVideoItemClick = { videoItem ->
							val playerIntent = Intent(this@MainActivity, PlayerActivity::class.java).apply{
								data = videoItem.uri
							}
							playerActivityLauncher.launch(playerIntent)

						}
					)
				}
			}
		}
	}
}
