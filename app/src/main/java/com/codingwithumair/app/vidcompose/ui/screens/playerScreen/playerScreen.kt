package com.codingwithumair.app.vidcompose.ui.screens.playerScreen

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.PlayerView
import androidx.media3.ui.TimeBar
import com.codingwithumair.app.vidcompose.R
import com.codingwithumair.app.vidcompose.player.PlayerViewModel
import com.codingwithumair.app.vidcompose.utils.toHhMmSs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@UnstableApi
@Composable
fun PlayerScreen(
	viewModel: PlayerViewModel,
	onRotateScreenClick: () -> Unit,
	onBackClick: () -> Unit,
){

	val playerState by viewModel.playerState.collectAsState()

	var showControls by remember{
		mutableStateOf(false)
	}

	LaunchedEffect(key1 = showControls){
		if(showControls){
			delay(10000)
			showControls = false
		}
	}

	Box(
		modifier = Modifier
			.fillMaxSize()
			.background(Color.Black)
	) {
		AndroidView(
			factory = {
				PlayerView(it).apply {
					player = viewModel.player
					useController = false
					resizeMode = playerState.resizeMode
					keepScreenOn = playerState.isPlaying
				}
			},
			update = {
				it.apply {
					resizeMode = playerState.resizeMode
					keepScreenOn = playerState.isPlaying
				}
			},
			modifier = Modifier
				.fillMaxSize()
				.clickable(
					onClick = { showControls = !showControls }
				)

		)

		AnimatedVisibility(
			showControls,
			enter = fadeIn(),
			exit = fadeOut()
		){
			MiddleControls(
				isPlaying = playerState.isPlaying,
				onClick = {
					showControls = !showControls
				},
				onPlayPauseClick = {
					viewModel.onPlayPauseClick()
				},
				onSeekForwardClick = {
					viewModel.player.seekForward()
				},
				onSeekBackwardClick = {
					viewModel.player.seekBack()
				},
				modifier = Modifier.fillMaxSize()
			)
		}

		AnimatedVisibility(
			visible = showControls,
			enter = fadeIn(),
			exit = fadeOut()
		){
			Column(
				modifier = Modifier.fillMaxSize()
			){
				UpperControls(
					videoTitle = playerState.currentVideoItem?.name.toString(),
					onBackClick = onBackClick
				)
				Spacer(modifier = Modifier.weight(1f))
				BottomControls(
					player = viewModel.player,
					totalTime = playerState.currentVideoItem?.duration ?: 0L,
					onRotateScreenClick = onRotateScreenClick ,
					resizeMode = playerState.resizeMode,
					onResizeModeChange = { viewModel.onResizeClick() }
				)
			}
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpperControls(
	videoTitle: String,
	onBackClick: () -> Unit,
	modifier: Modifier = Modifier
){
	Column(
		modifier = modifier.background(Color.Black.copy(0.7f))
	){
		Box(modifier = Modifier
			.fillMaxWidth()
			.height(24.dp)
			.background(Color.Black.copy(0.7f)))
		TopAppBar(
			title = {
				Text(
					text = videoTitle,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis,
					color = MaterialTheme.colorScheme.primary,
					fontWeight = FontWeight.Bold,
					modifier = Modifier.padding(12.dp)
				)
			},
			navigationIcon = {
				IconButton(onClick = onBackClick) {
					Icon(
						Icons.Rounded.ArrowBack,
						stringResource(id = R.string.go_back),
						tint = MaterialTheme.colorScheme.primary
					)
				}
			},
			colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black.copy(0.7f))
		)
	}

}

@Composable
private fun MiddleControls(
	isPlaying: Boolean,
	onClick: () -> Unit,
	onPlayPauseClick: () -> Unit,
	onSeekForwardClick: () -> Unit,
	onSeekBackwardClick: () -> Unit,
	modifier: Modifier = Modifier
){

	Row(
		modifier = modifier
	){

		MiddleControlsItem(
			icon = R.drawable.round_double_arrow_left_24,
			contentDescription = R.string.seek_backward,
			onIconClick = onSeekBackwardClick,
			onSingleClick = onClick,
			onDoubleClick = onSeekBackwardClick,
			modifier = modifier
				.weight(1f)
		)

		MiddleControlsItem(
			icon = if(isPlaying) R.drawable.round_pause_24 else R.drawable.round_play_arrow_24,
			contentDescription = R.string.play_pause,
			onIconClick = onPlayPauseClick,
			onSingleClick = onClick,
			onDoubleClick = onPlayPauseClick,
			modifier = modifier.weight(1f)
		)

		MiddleControlsItem(
			icon = R.drawable.round_double_arrow_right_24,
			contentDescription = R.string.seek_forward,
			onIconClick = onSeekForwardClick,
			onSingleClick = onClick,
			onDoubleClick = onSeekForwardClick,
			modifier = modifier
				.weight(1f)
		)
	}
}

@UnstableApi
@Composable
private fun BottomControls(
	player: Player,
	totalTime: Long,
	onRotateScreenClick: () -> Unit,
	resizeMode: Int,
	onResizeModeChange: () -> Unit,
	modifier: Modifier = Modifier
){
	var currentTime by remember{
		mutableLongStateOf(player.currentPosition)
	}

	var isSeekInProgress by remember{
		mutableStateOf(false)
	}

	val timerCoroutineScope = rememberCoroutineScope()

	LaunchedEffect(key1 = Unit){
		timerCoroutineScope.launch {
			while(true){
				delay(500)
				if(!isSeekInProgress){
					currentTime = player.currentPosition
					Log.d("PlayerScreen", "timer running $currentTime")
				}
			}
		}
	}

	Column(
		modifier = modifier.background(Color.Black.copy(0.7f))
	){
		Row(
			modifier = Modifier
				.fillMaxWidth(),
			verticalAlignment = Alignment.Bottom
		){
			Text(
				text = "${currentTime.toHhMmSs()}-${totalTime.toHhMmSs()}",
				modifier = Modifier.padding(horizontal = 12.dp),
				color = MaterialTheme.colorScheme.primary,
				fontWeight = FontWeight.SemiBold
			)
			Spacer(modifier = Modifier.weight(1f))

			IconButton(onClick = onResizeModeChange) {
				Icon(
					painterResource(id = if(resizeMode  == AspectRatioFrameLayout.RESIZE_MODE_FIT) R.drawable.round_aspect_ratio_24 else R.drawable.round_fit_screen_24),
					stringResource(id = R.string.toggle_fitScreen),
					tint = MaterialTheme.colorScheme.primary
				)
			}
			IconButton(
				onClick = onRotateScreenClick,
				modifier = Modifier.padding(horizontal = 12.dp)
			) {
				Icon(
					painterResource(id = R.drawable.round_screen_rotation_24),
					stringResource(id = R.string.rotate_screen),
					tint = MaterialTheme.colorScheme.primary
				)
			}
		}

		CustomSeekBar(
			player = player,
			isSeekInProgress = { isInProgress ->
				isSeekInProgress = isInProgress
			},
			onSeekBarMove = { position ->
				currentTime = position
			},
			totalDuration = totalTime,
			currentTime = currentTime,
			modifier = Modifier.padding(12.dp)
		)
		Spacer(modifier = Modifier.size(24.dp))
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MiddleControlsItem(
	@DrawableRes icon: Int,
	@StringRes contentDescription: Int,
	onIconClick: () -> Unit,
	onSingleClick: () -> Unit,
	onDoubleClick: () -> Unit,
	modifier: Modifier = Modifier
){
	Box(
		modifier = modifier
			.combinedClickable(
				onClick = onSingleClick,
				onDoubleClick = onDoubleClick
			),
		contentAlignment = Alignment.Center
	){
		FilledIconButton(onClick = onIconClick) {
			Icon(
				painterResource(id = icon),
				stringResource(id = contentDescription)
			)
		}

	}
}

@UnstableApi
@Composable
fun CustomSeekBar(
	player: Player,
	isSeekInProgress: (Boolean) -> Unit,
	onSeekBarMove: (Long) -> Unit,
	currentTime: Long,
	totalDuration: Long,
	modifier: Modifier = Modifier
){
	val primaryColor = MaterialTheme.colorScheme.primary

	AndroidView(
		factory = { context ->

			val listener = object: TimeBar.OnScrubListener{

				var previousScrubPosition = 0L

				override fun onScrubStart(timeBar: TimeBar, position: Long) {
					isSeekInProgress(true)
					previousScrubPosition = position
				}

				override fun onScrubMove(timeBar: TimeBar, position: Long) {
					onSeekBarMove(position)
				}

				override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
					if(canceled){
						player.seekTo(previousScrubPosition)
					}else{
						player.seekTo(position)
					}
					isSeekInProgress(false)
				}

			}

			DefaultTimeBar(context).apply {
				setScrubberColor(primaryColor.toArgb())
				setPlayedColor(primaryColor.toArgb())
				setUnplayedColor(primaryColor.copy(0.3f).toArgb())
				addListener(listener)
				setDuration(totalDuration)
				setPosition(player.currentPosition)
			}
		},
		update = {
			it.apply {
				setPosition(currentTime)
			}
		},

		modifier = modifier
	)
}
