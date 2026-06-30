package com.example.musicplayerapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.SongScanner
import com.example.musicplayerapp.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    var songs = _songs.asStateFlow()
    private val _isScanning = MutableStateFlow(true)
    val isScanning = _isScanning.asStateFlow()

    private val _scanCount = MutableStateFlow(0)
    val scanCount = _scanCount.asStateFlow()

    fun scanAllSongs(function: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {

            _scanCount.value = 0
            val songs = SongScanner.scanAllSongs(
                getApplication()
            ) {
                count ->
                    _scanCount.value = count
            }

            _songs.value = songs

            _isScanning.value = false
        }
    }

    fun stopScanning() {
        _isScanning.value = false
    }
}