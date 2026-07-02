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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import com.example.musicplayerapp.ui.components.SearchBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
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
    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }

    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    val filteredSongs = remember(songs, searchQuery) {

        if (searchQuery.isBlank()) {
            songs
        } else {
            songs.filter {

                it.title.contains(searchQuery, true) ||

                        (it.artist?.contains(searchQuery, true) == true)
            }
        }
    }

    Scaffold(
        topBar = {
            if (isSearching) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onClose = {
                        isSearching = false
                        searchQuery = ""
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            "🎵 Gaanwa Music",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    actions = {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                HorizontalDivider(thickness = 1.dp)
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                if (filteredSongs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (searchQuery.isBlank())
                                "No songs found"
                            else
                                "No matching songs found"
                        )
                    }
                } else {

                    val availableLetters = remember(songs) {
                        songs.mapNotNull {
                            it.title.firstOrNull()?.uppercaseChar()
                        }.toSet()
                    }

                    val letterIndexMap = remember(filteredSongs) {
                        buildMap {
                            filteredSongs.forEachIndexed { index, song ->
                                song.title.firstOrNull()?.uppercaseChar()?.let { letter ->
                                    if (letter !in this) {
                                        put(letter, index)
                                    }
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
                            items(filteredSongs) { song ->

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
                            modifier = Modifier.verticalScroll(
                                rememberScrollState()
                            )
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
                    onClick = onMiniPlayerClick
                )
            }
        }
    }
}