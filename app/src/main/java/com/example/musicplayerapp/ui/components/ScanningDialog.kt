package com.example.musicplayerapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScanningDialog(count: Int) {

    AlertDialog(
        onDismissRequest = {},
        confirmButton = {},

        title = {
            Text(
                text = "🎵 Preparing your music library",
                style = MaterialTheme.typography.titleLarge
            )
        },

        text = {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                CircularProgressIndicator()

                Spacer(Modifier.height(20.dp))

                Text(
                    "Scanning songs on your device...",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "$count songs found",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    )
}