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
    private var playlist: List<Song> = emptyList()
    private var currentIndex: Int = -1

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

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}