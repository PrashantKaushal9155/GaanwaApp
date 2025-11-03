package com.example.musicplayerapp.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.data.Song
import com.example.musicplayerapp.data.SongScanner

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎵 Gaanwa Music Player", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
                isLoading = true
                songs = SongScanner.scanAllSongs(context)
                isLoading = false
            }) {
                Text("Scan All Songs")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(onClick = {
                pickFolder(context)
            }) {
                Text("Select Folder")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (songs.isNotEmpty()) {
            SongList(songs)
        }
    }
}

@Composable
fun SongList(songs: List<Song>) {
    LazyColumn {
        items(songs.size) { index ->
            val song = songs[index]
            Text(
                text = "${song.title} - ${song.artist ?: "Unknown"}",
                modifier = Modifier.padding(8.dp)
            )
            Divider()
        }
    }
}

fun pickFolder(context: Context) {
    // Folder picker implementation (next step)
}