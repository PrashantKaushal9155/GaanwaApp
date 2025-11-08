package com.example.musicplayerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.musicplayerapp.core.PermissionHandler
import com.example.musicplayerapp.ui.navigation.AppNavGraph
import com.example.musicplayerapp.ui.theme.MusicPlayerAppTheme
import com.example.musicplayerapp.ui.viewmodel.MainViewModel
import com.example.musicplayerapp.ui.viewmodel.PlayerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by viewModels()
    private val permissions = PermissionHandler.getRequiredPermissions()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val allGranted = result.values.all { it }
            if (allGranted) viewModel.scanAllSongs {}
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher.launch(permissions)

        setContent {
            MusicPlayerAppTheme {
                Surface {
                    val navController = rememberNavController()

                    val songs by viewModel.songs.collectAsState()
                    val currentSong by playerViewModel.currentSong.collectAsState()
                    val isPlaying by playerViewModel.isPlaying.collectAsState()

                    AppNavGraph(
                        navController = navController,
                        songs = songs,
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        onSongClick = { song ->
                            playerViewModel.play(song)
                            navController.navigate("player")
                        },
                        onPlayPause = {
                            if (isPlaying) playerViewModel.pause() else playerViewModel.resume()
                        },
                        onBack = { /* Nothing extra yet. */}
                    )
                }
            }
        }
    }
}