package com.example.musicplayerapp.domain.player

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.musicplayerapp.data.model.Song

fun Song.toMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(id.toString())
        .setUri(uri)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(title)
                .setArtist(artist)
                .setAlbumTitle(album)
                .build()
        )
        .build()
}

fun List<Song>.toMediaItems(): List<MediaItem> {
    return map { it.toMediaItem()}
}