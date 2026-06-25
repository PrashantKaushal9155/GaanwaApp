package com.example.musicplayerapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.ui.components.MiniPlayer
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongClick: (Song) -> Unit,
    onPlayPause: () -> Unit,
    onMiniPlayerClick: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            Text("🎵 Gaanwa Music Player", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Songs", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(10.dp))

            if (songs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No songs found")
                }
            } else {
                val availableLetters: Set<Char> = remember(songs) {
                    songs.mapNotNull { song ->
                        song.title.firstOrNull()?.uppercaseChar()
                    }.toSet()
                }
                val letterIndexMap = remember(songs) {
                    buildMap {
                        songs.forEachIndexed { index, song ->
                            val firstLetter = song.title
                                .firstOrNull()
                                ?.uppercaseChar()

                            if (firstLetter != null && firstLetter !in this) {
                                put(firstLetter, index)
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(songs) { song ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSongClick(song) }
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Text(
                                    text = song.artist ?: "Unknown",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            HorizontalDivider()
                        }
                    }

                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        ('A'..'Z').forEach { letter ->

                            Text(
                                text = letter.toString(),
                                color =
                                    if (letter in availableLetters)
                                        LocalContentColor.current
                                    else
                                        LocalContentColor.current.copy(alpha = 0.2f),
                                modifier = Modifier.clickable {

                                    val index = letterIndexMap[letter] ?: -1

                                    if (index >= 0) {
                                        scope.launch {
                                            listState.animateScrollToItem(index)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        currentSong?.let { song ->
            MiniPlayer(
                song = song,
                isPlaying = isPlaying,
                onPlayPause = onPlayPause,
                onClick = onMiniPlayerClick,
            )
        }
    }
}