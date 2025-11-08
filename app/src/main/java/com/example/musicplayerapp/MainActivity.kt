package com.example.musicplayerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.musicplayerapp.core.PermissionHandler
import com.example.musicplayerapp.ui.screens.HomeScreen
import com.example.musicplayerapp.ui.theme.MusicPlayerAppTheme
import com.example.musicplayerapp.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
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
                val songs by viewModel.songs.collectAsState()
                HomeScreen(
                    songs = songs,
                    onScanAll = { viewModel.scanAllSongs {} },
                    onPickFolder = { /* for later */ },
                    onOpenPickerDirect = {}
                )
            }
        }
    }
}