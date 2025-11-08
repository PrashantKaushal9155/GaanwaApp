package com.example.musicplayerapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.data.Song

@Composable
fun HomeScreen(
    songs: List<Song>,
    onScanAll: () -> Unit,
    onPickFolder: () -> Unit,
    onOpenPickerDirect: () -> Unit // optional: call when permission already granted
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("🎵 Gaanwa Music Player", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Songs", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(10.dp))

        if (songs.isEmpty()) {
            Text("No songs scanned yet", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(songs) { song ->
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* TODO: open player */ }
                        .padding(vertical = 8.dp)) {
                        Text(text = song.title, style = MaterialTheme.typography.bodyLarge)
                        Text(text = song.artist ?: "Unknown", style = MaterialTheme.typography.bodySmall)
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}