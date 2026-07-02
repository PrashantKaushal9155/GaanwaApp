package com.example.musicplayerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.ui.screens.HomeScreen
import com.example.musicplayerapp.ui.screens.PlayerScreen
import kotlin.time.Duration

@Composable
fun AppNavGraph(
    navController: NavHostController,
    songs: List<Song>,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    currentSong: Song?,
    onSongClick: (Song) -> Unit,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    isShuffleEnabled: Boolean,
    onShuffleClick: () -> Unit,
    isRepeatEnabled: Boolean,
    onRepeatClick: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                songs = songs,
                currentSong = currentSong,
                isPlaying = isPlaying,
                onSongClick = { song ->
                    onSongClick(song)
                    navController.navigate("player")
                },
                onPlayPause = onPlayPause,
                onMiniPlayerClick = {
                    navController.navigate("player")
                }
            )
        }
        composable("player") {
            PlayerScreen(
                song = currentSong,
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                onSeek = onSeek,
                onPlayPause = onPlayPause,
                onPrevious = onPrevious,
                isShuffleEnabled = isShuffleEnabled,
                onShuffleClick = onShuffleClick,
                isRepeatEnabled = isRepeatEnabled,
                onRepeatClick = onRepeatClick,
                onNext = onNext,
                onBack = {
                    navController.popBackStack()
                    onBack()
                }
            )
        }
    }
}