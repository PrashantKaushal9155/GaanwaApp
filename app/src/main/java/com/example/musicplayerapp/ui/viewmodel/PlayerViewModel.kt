package com.example.musicplayerapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.domain.player.MusicPlayerManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val playerManager = MusicPlayerManager(application)

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()
    private var playlist: List<Song> = emptyList()
    private var currentIndex: Int = -1
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    init {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = playerManager.getCurrentPosition()
                _duration.value = playerManager.getDuration()
                delay(1000)
            }
        }
    }

    init {
        playerManager.initializePlayer()
        playerManager.setOnSongCompletedListener {
            playNext()
        }

        viewModelScope.launch {
            while (true) {
                _currentPosition.value = playerManager.getCurrentPosition()
                _duration.value = playerManager.getDuration()
                delay(1000)
            }
        }
    }

    fun play(song: Song, songs: List<Song>) {
        playlist = songs
        currentIndex = songs.indexOfFirst { it.id == song.id }

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

    fun playNext() {
        if (playlist.isEmpty()) return

        if (currentIndex < playlist.lastIndex) {
            currentIndex++
            val nextSong = playlist[currentIndex]

            _currentSong.value = nextSong
            playerManager.playSong(nextSong)
            _isPlaying.value = true
        }
    }

    fun playPrevious() {
        if (playlist.isEmpty()) return

        if (currentIndex > 0) {
            currentIndex--
            val previousSong = playlist[currentIndex]

            _currentSong.value = previousSong
            playerManager.playSong(previousSong)
            _isPlaying.value = true
        }
    }

    fun seekTo(position: Long) {
        playerManager.seekTo(position)
        _currentPosition.value = position
    }

    fun getCurrentPosition(): Long {
        return playerManager.getCurrentPosition()
    }

    fun getDuration(): Long {
        return playerManager.getDuration()
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}