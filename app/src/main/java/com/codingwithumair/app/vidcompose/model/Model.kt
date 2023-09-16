package com.codingwithumair.app.vidcompose.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.media3.common.MediaItem

data class VideoItem(
	val name: String,
	val absolutePath: String,
	val id: Long,
	val uri: Uri,
	val size: Long,
	val width: Int,
	val height: Int,
	val duration: Long,
	val dateModified: Long
){
	fun Long.toHhMmSs(): String {
		val seconds = (this / 1000).toInt()
		val hours = seconds / 3600
		val minutes = (seconds % 3600) / 60
		val remainingSeconds = seconds % 60
		return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
	}
}

data class FolderItem(
	val name: String,
	val videoItems: List<VideoItem>
)
