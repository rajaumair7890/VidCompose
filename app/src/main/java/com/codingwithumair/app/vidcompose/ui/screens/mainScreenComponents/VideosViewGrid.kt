package com.codingwithumair.app.vidcompose.ui.screens.mainScreenComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.codingwithumair.app.vidcompose.model.VideoItem
import com.codingwithumair.app.vidcompose.utils.toHhMmSs
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage


@Composable
fun VideoItemGridLayout(
	videoList: List<VideoItem>,
	onVideoItemClick: (VideoItem) -> Unit,
	contentPadding: PaddingValues,
	modifier: Modifier = Modifier
){
	LazyVerticalStaggeredGrid(
		modifier = modifier,
		contentPadding = contentPadding,
		horizontalArrangement = Arrangement.Center,
		columns = StaggeredGridCells.Fixed(2)
	){
		items(videoList, key = {it.name}){videoItem ->
			VideoGridItem(
				videoItem = videoItem,
				onItemClick = onVideoItemClick,
				modifier = Modifier.padding(6.dp)
			)
		}

	}
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun VideoGridItem(
	videoItem: VideoItem,
	onItemClick: (VideoItem) -> Unit,
	modifier: Modifier = Modifier
){
	OutlinedCard(
		modifier = modifier,
		onClick = {
			onItemClick(videoItem)
		},
		shape = RoundedCornerShape(15.dp)
	){

		Box(
			modifier = Modifier.height(200.dp).fillMaxWidth()
		){
			GlideImage(
				imageModel = { videoItem.uri },
				imageOptions = ImageOptions(
					contentScale = ContentScale.Crop,
					alignment = Alignment.Center
				),
				loading = {
					CircularProgressIndicator(modifier = Modifier
						.matchParentSize()
						.align(Alignment.Center))
				}
			)
		}

		Spacer(modifier = Modifier.size(12.dp))

		Text(
			text = videoItem.name,
			style = MaterialTheme.typography.bodyMedium,
			textAlign = TextAlign.Start,
			maxLines = 3,
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier.padding(10.dp),
			color = MaterialTheme.colorScheme.primary
		)

		FlowRow(modifier = Modifier.padding(6.dp)){
			FlowRowItem(text = "${videoItem.size/1000000} MB")
			FlowRowItem(text = videoItem.duration.toHhMmSs())
			FlowRowItem(text = "${videoItem.height} x ${videoItem.width}")
		}


	}
}

@Composable
private fun FlowRowItem(
	text: String,
	modifier: Modifier = Modifier
){
	ElevatedCard(
		modifier = modifier.padding(4.dp),
		shape = CutCornerShape(2.dp),
		colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
	){
		Text(text, modifier = Modifier.padding(2.dp))
	}
}


