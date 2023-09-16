package com.codingwithumair.app.vidcompose.utils


fun Long.toHhMmSs(): String {
	val seconds = (this / 1000).toInt()
	val hours = seconds / 3600
	val minutes = (seconds % 3600) / 60
	val remainingSeconds = seconds % 60
	return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

