package com.example.musicplayerapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.musicplayerapp.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.example.musicplayerapp.domain.player.MediaControllerManager
import com.example.musicplayerapp.domain.player.toMediaItems
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val controllerManager =
        MediaControllerManager(application)

    private var controller: MediaController? = null

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()
    private var playlist: List<Song> = emptyList()
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()
    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled = _isShuffleEnabled.asStateFlow()
    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled = _isRepeatEnabled.asStateFlow()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _currentSong.value = playlist.firstOrNull() {
                it.id.toString() == mediaItem?.mediaId
            }
        }
    }
    init {

        viewModelScope.launch {

            controller = controllerManager.getController()

            controller?.addListener(playerListener)
            while (true) {
                _currentPosition.value = controller?.currentPosition ?: 0L
                _duration.value = maxOf(0L, controller?.duration ?: 0L)
                delay(500)
            }
        }
    }

    fun play(song: Song, songs: List<Song>) {
        playlist = songs

        _currentSong.value = song
        val mediaItems = songs.toMediaItems()

        controller?.apply {
            setMediaItems(
                mediaItems,
                songs.indexOf(song),
                0L
            )
            prepare()
            play()
        }
    }

    fun pause() {
        controller?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        controller?.play()
        _isPlaying.value = true
    }

    fun playNext() {
        controller?.seekToNextMediaItem()
        controller?.play()
    }

    fun playPrevious() {
            if ((controller?.currentPosition ?: 0) > 3000)
                controller?.seekTo(0)
            else
                controller?.seekToPreviousMediaItem()

            controller?.play()
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }

//    fun getCurrentPosition(): Long {
//        return playerManager.getCurrentPosition()
//    }
//
//    fun getDuration(): Long {
//        return playerManager.getDuration()
//    }

    fun toggleShuffle() {
        controller?.let {
            it.shuffleModeEnabled = !it.shuffleModeEnabled
            _isShuffleEnabled.value = it.shuffleModeEnabled
        }
    }

    fun toggleRepeat() {
        controller?.let {
            it.repeatMode =
                if (controller?.repeatMode == Player.REPEAT_MODE_ONE)
                    Player.REPEAT_MODE_OFF
                else
                    Player.REPEAT_MODE_ONE

            _isRepeatEnabled.value = it.repeatMode == Player.REPEAT_MODE_ONE
        }
    }

    override fun onCleared() {
        super.onCleared()
        controller?.removeListener(playerListener)
        controllerManager.release()
    }
}