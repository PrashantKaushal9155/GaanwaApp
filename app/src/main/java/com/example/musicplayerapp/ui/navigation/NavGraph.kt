package com.example.musicplayerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicplayerapp.data.model.Song
import com.example.musicplayerapp.ui.screens.HomeScreen
import com.example.musicplayerapp.ui.screens.PlayerScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    songs: List<Song>,
    isPlaying: Boolean,
    currentSong: Song?,
    onSongClick: (Song) -> Unit,
    onPlayPause: () -> Unit,
    onBack: () -> Unit
) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                songs = songs,
                onSongClick = { song ->
                    onSongClick(song)
                    navController.navigate("player")
                }
            )
        }
        composable("player") {
            PlayerScreen(
                song = currentSong,
                isPlaying = isPlaying,
                onPlayPause = onPlayPause,
                onBack = {
                    navController.popBackStack()
                    onBack()
                }
            )
        }
    }
}