package com.example.musicplayerapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.domain.player.MusicPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val playerManager = MusicPlayerManager(application)

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    fun play(song: Song) {
        _currentSong.value = song
        playerManager.playSong(song)
        _isPlaying.value = true
    }

    fun pause() {
        playerManager.pause()
        _isPlaying.value = false
    }

    fun resume() {
        playerManager.resume()
        _isPlaying.value = true
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}