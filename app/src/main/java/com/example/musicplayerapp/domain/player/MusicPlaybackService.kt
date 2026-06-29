package com.example.musicplayerapp.domain.player

import android.content.Intent
import android.util.Log
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class MusicPlaybackService : MediaSessionService() {

    companion object {
        var instance: MusicPlaybackService? = null
            private set
    }

    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d("MusicService", "Service Created")
        player = ExoPlayer.Builder(this).build()

        mediaSession = MediaSession.Builder(this, player)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    fun getPlayer(): ExoPlayer = player

    override fun onDestroy() {
        Log.d("MusicService", "Service Destroyed")

        mediaSession?.release()
        player.release()

        instance = null
        mediaSession = null

        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d("MusicService", "Task Removed")
        super.onTaskRemoved(rootIntent)
    }
}