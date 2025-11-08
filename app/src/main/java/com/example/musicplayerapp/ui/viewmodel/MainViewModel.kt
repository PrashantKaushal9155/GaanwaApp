package com.example.musicplayerapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerapp.data.Song
import com.example.musicplayerapp.data.SongScanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    var songs = _songs.asStateFlow()

    fun scanAllSongs(onScanned: (List<Song>) -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                SongScanner.scanAllSongs(getApplication())
            }
            _songs.value = result
            onScanned(result)
        }
    }
}