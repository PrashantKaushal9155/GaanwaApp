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
import com.example.musicplayerapp.data.preferences.PlayerPreferences
import com.example.musicplayerapp.domain.player.MediaControllerManager
import com.example.musicplayerapp.domain.player.toMediaItems
import com.example.musicplayerapp.domain.voice.VoiceCommand
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val controllerManager =
        MediaControllerManager(application)

    private val preferences =
        PlayerPreferences(application)
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
    private var isControllerReady = false
    private var hasInitialized = false

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            val id = mediaItem?.mediaId?.toLongOrNull() ?: return

            playlist.firstOrNull { it.id == id}?.let { song ->
                _currentSong.value = song

                viewModelScope.launch {
                    preferences.saveLastSong(song.id)
                }
            }
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            _isShuffleEnabled.value = shuffleModeEnabled
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            _isRepeatEnabled.value =
                repeatMode == Player.REPEAT_MODE_ONE
        }
    }
    init {

        viewModelScope.launch {

            controller = controllerManager.getController()

            controller?.addListener(playerListener)

            isControllerReady = true

            tryInitialize()
        }

        viewModelScope.launch {
            preferences.shuffleEnabled.collect { enabled ->
                controller?.shuffleModeEnabled = enabled
                _isShuffleEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            preferences.repeatEnabled.collect { enabled ->
                controller?.repeatMode =
                    if (enabled)
                        Player.REPEAT_MODE_ONE
                    else
                        Player.REPEAT_MODE_OFF

                _isRepeatEnabled.value = enabled
            }
        }

        viewModelScope.launch {
            while (true) {
                _currentPosition.value = controller?.currentPosition ?: 0L
                _duration.value = maxOf(0L, controller?.duration ?: 0L)
                delay(500)
            }
        }
    }

    fun play(song: Song, songs: List<Song>) {
        playlist = songs

        controller?.apply {
            setMediaItems(
                songs.toMediaItems(),
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
            val enabled = !it.shuffleModeEnabled
            it.shuffleModeEnabled = enabled

            viewModelScope.launch {
                preferences.saveShuffle(enabled)
            }
        }
    }

    fun toggleRepeat() {
        controller?.let {
            val repeat =
                if(it.repeatMode == Player.REPEAT_MODE_ONE)
                    Player.REPEAT_MODE_OFF
                else
                    Player.REPEAT_MODE_ONE

            it.repeatMode = repeat

            viewModelScope.launch {
                preferences.saveRepeat(
                    repeat == Player.REPEAT_MODE_ONE
                )
            }
        }
    }

    fun onSongsLoaded(songs: List<Song>) {

        playlist = songs

        tryInitialize()
    }

    private fun tryInitialize() {

        if (hasInitialized) return
        if (!isControllerReady) return
        if(playlist.isEmpty()) return

        hasInitialized = true

        controller?.currentMediaItem?.mediaId
            ?.toLongOrNull()
            ?.let { mediaId ->

                playlist.firstOrNull { it.id == mediaId }?.let { song ->
                    _currentSong.value = song
                    _isPlaying.value = controller?.isPlaying ?: false

                    return
                }
            }
        viewModelScope.launch {

            val lastSongId = preferences.getLastSongId() ?: return@launch

            val song = playlist.firstOrNull {
                it.id == lastSongId
            }

            if (song == null) {
                preferences.clearLastSong()
                return@launch
            }

            controller?.apply {

                setMediaItems(
                    playlist.toMediaItems(),
                    playlist.indexOf(song),
                    0L
                )

                prepare()
            }

            _currentSong.value = song
        }
    }

    fun executeCommand(command: VoiceCommand) {

        when (command) {

            VoiceCommand.Play ->
                resume()

            VoiceCommand.Pause ->
                pause()

            VoiceCommand.Next ->
                playNext()

            VoiceCommand.Previous ->
                playPrevious()

            VoiceCommand.ShuffleOn -> {

                if (!controller!!.shuffleModeEnabled)
                    toggleShuffle()
            }

            VoiceCommand.ShuffleOff -> {

                if (controller!!.shuffleModeEnabled)
                    toggleShuffle()
            }

            VoiceCommand.RepeatOn -> {

                if (controller!!.repeatMode == Player.REPEAT_MODE_ONE)
                    toggleRepeat()
            }

            VoiceCommand.RepeatOff -> {

                if (controller!!.repeatMode == Player.REPEAT_MODE_OFF)
                    toggleRepeat()
            }

            is VoiceCommand.PlaySong -> {

                val song =
                    playlist.firstOrNull {

                        it.title.contains(
                            command.query,
                            ignoreCase = true
                        )
                    }

                if (song != null)
                    play(song, playlist)
            }

            else -> {}
        }
    }

    override fun onCleared() {
        super.onCleared()
        controller?.removeListener(playerListener)
        controllerManager.release()
    }
}