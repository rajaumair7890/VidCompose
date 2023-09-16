package com.codingwithumair.app.vidcompose.ui.screens.mainScreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codingwithumair.app.vidcompose.R
import com.codingwithumair.app.vidcompose.model.VideoItem
import com.codingwithumair.app.vidcompose.ui.screens.mainScreenComponents.FolderItemGridLayout
import com.codingwithumair.app.vidcompose.ui.screens.mainScreenComponents.VideoItemGridLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithBottomNavigation(
	onVideoItemClick: (VideoItem) -> Unit
){

	var bottomNavigationScreen by rememberSaveable {
		mutableStateOf( BottomNavigationScreens.VideosView )
	}

	val mainViewModel: MainViewModel = viewModel(factory = MainViewModel.factory)

	val videosViewStateFlow by mainViewModel.videoItemsStateFlow.collectAsState()
	val foldersViewStateFlow by mainViewModel.folderItemStateFlow.collectAsState()

	Scaffold(
		topBar = {

			CenterAlignedTopAppBar(
				title = {
					Text(
						text = stringResource(id = R.string.app_name),
						style = MaterialTheme.typography.titleLarge,
						color = MaterialTheme.colorScheme.primary
					)
				}
				//Todo Add Nested Scroll To Animate TopBar And Background
			)


		},
		bottomBar = {

			NavigationBar(
				tonalElevation = 12.dp
			){

				NavigationBarItem(
					colors = NavigationBarItemDefaults.colors(
						indicatorColor = MaterialTheme.colorScheme.primaryContainer
					),
					selected = bottomNavigationScreen == BottomNavigationScreens.VideosView,
					label = { Text(text = stringResource(id = R.string.videos_layout)) },
					onClick = {
						bottomNavigationScreen = BottomNavigationScreens.VideosView
					},
					icon = {
						Icon(
							painter = painterResource(id = R.drawable.round_video_library_24),
							contentDescription = stringResource(id = R.string.videos_layout)
						)
					}
				)

				NavigationBarItem(
					colors = NavigationBarItemDefaults.colors(
						indicatorColor = MaterialTheme.colorScheme.primaryContainer
					),
					selected = bottomNavigationScreen == BottomNavigationScreens.FoldersView,
					label = { Text(text = stringResource(id = R.string.folders_layout)) },
					onClick = {
						bottomNavigationScreen = BottomNavigationScreens.FoldersView
					},
					icon = {
						Icon(
							painter = painterResource(id = R.drawable.round_folder_copy_24),
							contentDescription = stringResource(id = R.string.folders_layout)
						)
					}
				)
			}
		}
	) { paddingValues ->

		AnimatedContent(
			targetState = bottomNavigationScreen ,
			label = "",
			transitionSpec = {
				when(this.targetState){
					BottomNavigationScreens.VideosView -> slideInHorizontally(){-it}.togetherWith(slideOutHorizontally(){it})

					BottomNavigationScreens.FoldersView -> slideInHorizontally(){it}.togetherWith(slideOutHorizontally(){-it})
				}
			}
		) { navScreen ->

			when(navScreen){

				BottomNavigationScreens.VideosView -> {

					VideoItemGridLayout(
						contentPadding = paddingValues,
						videoList = videosViewStateFlow,
						onVideoItemClick = onVideoItemClick,
					)

				}

				BottomNavigationScreens.FoldersView -> {

					var foldersVideosNavigation by rememberSaveable{
						mutableStateOf(FoldersVideosNavigation.FoldersScreen)
					}

					Crossfade(
						targetState = foldersVideosNavigation, label = "",
						animationSpec = tween(300, easing = LinearEasing)
					) { foldersAndVideosNav ->

						when(foldersAndVideosNav){

							FoldersVideosNavigation.FoldersScreen -> {

								FolderItemGridLayout(
									foldersList = foldersViewStateFlow,
									onFolderItemClick = {
										mainViewModel.updateCurrentSelectedFolderItem(it)
										foldersVideosNavigation = FoldersVideosNavigation.VideosScreen
									},
									contentPadding = paddingValues
								)

							}

							FoldersVideosNavigation.VideosScreen -> {

								BackHandler(true) {
									foldersVideosNavigation = FoldersVideosNavigation.FoldersScreen
								}

								VideoItemGridLayout(
									contentPadding = paddingValues,
									videoList = mainViewModel.currentSelectedFolder.videoItems,
									onVideoItemClick = onVideoItemClick,
								)
							}
						}
					}
				}
			}
		}
	}
}

private enum class BottomNavigationScreens{
	VideosView,
	FoldersView
}

private enum class FoldersVideosNavigation{
	FoldersScreen,
	VideosScreen
}