package com.example.musicplayerapp

import android.content.Intent
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
import com.example.musicplayerapp.domain.player.MusicPlaybackService
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
            if (allGranted) {
                viewModel.scanAllSongs {  }
            } else {
                viewModel.stopScanning()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent(this, MusicPlaybackService::class.java)
        startService(serviceIntent)
        permissionLauncher.launch(permissions)

        setContent {
            val currentPosition by playerViewModel.currentPosition.collectAsState()
            val duration by playerViewModel.duration.collectAsState()
            val isShuffleEnabled by playerViewModel.isShuffleEnabled.collectAsState()
            val isScanning by viewModel.isScanning.collectAsState()
            val scanCount by viewModel.scanCount.collectAsState()

            MusicPlayerAppTheme {
                Surface {
                    val navController = rememberNavController()

                    val songs by viewModel.songs.collectAsState()
                    val currentSong by playerViewModel.currentSong.collectAsState()
                    val isPlaying by playerViewModel.isPlaying.collectAsState()
                    val isRepeatEnabled by playerViewModel.isRepeatEnabled.collectAsState()

                    AppNavGraph(
                        navController = navController,
                        songs = songs,
                        currentSong = currentSong,
                        isPlaying = isPlaying,
                        isScanning = isScanning,
                        scanCount = scanCount,
                        currentPosition = currentPosition,
                        duration = duration,
                        onSeek = { playerViewModel.seekTo(it) },
                        onSongClick = { song ->
                            playerViewModel.play(song, songs)
                        },
                        onPlayPause = {
                            if (isPlaying) playerViewModel.pause() else playerViewModel.resume()
                        },
                        onPrevious = { playerViewModel.playPrevious()},
                        isShuffleEnabled = isShuffleEnabled,
                        onShuffleClick = { playerViewModel.toggleShuffle() },
                        isRepeatEnabled = isRepeatEnabled,
                        onRepeatClick = { playerViewModel.toggleRepeat() },
                        onNext = { playerViewModel.playNext()},
                        onBack = { /* Nothing extra yet. */}
                    )
                }
            }
        }
    }
}