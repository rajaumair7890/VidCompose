package com.codingwithumair.app.vidcompose.player

import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.C.ContentType
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.AspectRatioFrameLayout
import com.codingwithumair.app.vidcompose.VidComposeApplication
import com.codingwithumair.app.vidcompose.data.LocalMediaProvider
import com.codingwithumair.app.vidcompose.model.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.media.audiofx.LoudnessEnhancer
import androidx.media3.common.Player

@UnstableApi
class PlayerViewModel(
	val player: ExoPlayer,
	private val mediaSession: MediaSession,
	private val loudnessEnhancer: LoudnessEnhancer,
	private val listener: Player.Listener,
	private val localMediaProvider: LocalMediaProvider,
): ViewModel() {

	private val _playerState = MutableStateFlow(PlayerState())
	val playerState = _playerState.asStateFlow()

	init {
		player.prepare().also {
			Log.d(TAG, "viewModel created and player is prepared")
		}
	}

	override fun onCleared() {
		player.removeListener(listener)
		player.release()
		mediaSession.release()
		loudnessEnhancer.release()
		Log.d(TAG, "on Cleared called and player is released")
		super.onCleared()
	}

	private fun updateCurrentVideoItem(videoItem: VideoItem){
		_playerState.update {
			it.copy(
				currentVideoItem = videoItem
			)
		}
		setMediaItem(_playerState.value.currentVideoItem!!.uri)
	}

	private fun setMediaItem(uri: Uri) {
		player.apply{
			addMediaItem(MediaItem.fromUri(uri))
			playWhenReady = true
			if(isPlaying){
				_playerState.update {
					it.copy(isPlaying = true)
				}
			}
		}
	}

	fun onPlayPauseClick(){
		if (player.isPlaying){
			player.pause().also {
				_playerState.update {
					it.copy(isPlaying = false)
				}
			}
		}else {
			player.play().also {
				_playerState.update {
					it.copy(isPlaying = true)
				}
			}
		}
	}

	fun playPauseOnActivityLifeCycleEvents(shouldPause: Boolean){
		if(player.isPlaying && shouldPause){
			player.pause().also{
				_playerState.update { it.copy(isPlaying = false) }
			}
		}else if(!player.isPlaying && !shouldPause){
			player.play().also {
				_playerState.update { it.copy(isPlaying = true) }
			}
		}
	}

	fun onRotateScreen(){
		val orientation = if(_playerState.value.orientation == ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE){
			ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
		}else{
			ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
		}
		_playerState.update {
			it.copy(orientation = orientation)
		}
	}

	fun onResizeClick(){
		_playerState.update {
			it.copy(
				resizeMode = if(_playerState.value.resizeMode == AspectRatioFrameLayout.RESIZE_MODE_FIT){
					AspectRatioFrameLayout.RESIZE_MODE_FILL
				}else{
					AspectRatioFrameLayout.RESIZE_MODE_FIT
				}
			)
		}
	}

	fun onIntent(uri: Uri){
		localMediaProvider.getVideoItemFromContentUri(uri)?.let{
			updateCurrentVideoItem(it)
		}
	}

	fun onNewIntent(uri:Uri){
		player.clearMediaItems()
		localMediaProvider.getVideoItemFromContentUri(uri)?.let{
			updateCurrentVideoItem(it)
		}
	}

	companion object{

		const val TAG = "PlayerViewModel"

		val factory = viewModelFactory {

			initializer {

				val application = (this[APPLICATION_KEY] as VidComposeApplication)

				val player = ExoPlayer
					.Builder(application)
					.setAudioAttributes(
						AudioAttributes.Builder()
							.setUsage(C.USAGE_MEDIA)
							.setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
							.build(),
						true
					)
					.build()

				val mediaSession = MediaSession.Builder(application, player).build()

				var loudnessEnhancer = LoudnessEnhancer(player.audioSessionId)

				val listener = object: Player.Listener{
					override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
						super.onDeviceVolumeChanged(volume, muted)
						player.volume = volume.toFloat()
					}

					override fun onAudioSessionIdChanged(audioSessionId: Int) {
						super.onAudioSessionIdChanged(audioSessionId)
						loudnessEnhancer?.release()

						try {
							loudnessEnhancer = LoudnessEnhancer(audioSessionId)
						} catch (e: Exception) {
							e.printStackTrace()
						}
					}
				}

				player.addListener(listener)

				PlayerViewModel(
					player = player,
					mediaSession = mediaSession,
					loudnessEnhancer = loudnessEnhancer!!,
					listener = listener,
					application.container.localMediaProvider
				)
			}

		}
	}
}

@UnstableApi
data class PlayerState(
	val isPlaying: Boolean = false,
	val currentVideoItem: VideoItem? = null,
	val resizeMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
	val orientation: Int = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
)