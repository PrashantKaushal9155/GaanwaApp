package com.example.musicplayerapp.domain.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.guava.await

class MediaControllerManager(context: Context) {

    private val sessionToken = SessionToken(
        context,
        ComponentName(context, MusicPlaybackService::class.java)
    )

    private val controllerFuture: ListenableFuture<MediaController> =
        MediaController.Builder(context, sessionToken).buildAsync()

    suspend fun getController(): MediaController? {
        return (controllerFuture.await())
    }

    fun release() {
            MediaController.releaseFuture(controllerFuture)
    }
}