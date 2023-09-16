package com.codingwithumair.app.vidcompose.ui.screens.mainScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.codingwithumair.app.vidcompose.VidComposeApplication
import com.codingwithumair.app.vidcompose.data.LocalMediaProvider
import com.codingwithumair.app.vidcompose.model.FolderItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
	localMediaProvider: LocalMediaProvider
): ViewModel() {

	private val _videoItemsStateFlow = localMediaProvider.getMediaVideosFlow().stateIn(
		viewModelScope,
		SharingStarted.WhileSubscribed(5000),
		emptyList()
	)

	private val _folderItemStateFlow = _videoItemsStateFlow.map{ videoItemsList ->

		videoItemsList.map { videoItem ->
			val splitPath = videoItem.absolutePath.split("/")
			val folderName = splitPath[splitPath.size - 2]
			FolderItem(
				name = folderName,
				videoItemsList.filter {
					val splitPathStrings = it.absolutePath.split("/")
					val name = splitPathStrings[splitPathStrings.size - 2]
					folderName == name
				}
			)
		}.distinct()

	}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

	val videoItemsStateFlow = _videoItemsStateFlow

	val folderItemStateFlow = _folderItemStateFlow

	var currentSelectedFolder by mutableStateOf(
		FolderItem("null", emptyList())
	)

	fun updateCurrentSelectedFolderItem(folderItem: FolderItem){
		currentSelectedFolder = folderItem
	}

	companion object{
		val factory = viewModelFactory {
			initializer {
				val application = (this[APPLICATION_KEY] as VidComposeApplication)
				MainViewModel(
					localMediaProvider = application.container.localMediaProvider
				)
			}
		}
	}
}