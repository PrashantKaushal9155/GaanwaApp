package com.example.musicplayerapp.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("player_preferences")

class PlayerPreferences(
    private val context: Context
) {

    companion object {
        private val SHUFFLE = booleanPreferencesKey("shuffle")
        private val REPEAT = booleanPreferencesKey("repeat")
        private val LAST_SONG_ID = longPreferencesKey("last_song_id")
    }

    val shuffleEnabled: Flow<Boolean> =
        context.dataStore.data.map {
            it[SHUFFLE] ?: false
        }

    val repeatEnabled: Flow<Boolean> =
        context.dataStore.data.map {
            it[REPEAT] ?: false
        }

    val lastSongId: Flow<Long?> =
        context.dataStore.data.map {
            it[LAST_SONG_ID]
        }

    suspend fun saveShuffle(enabled: Boolean) {
        context.dataStore.edit {
            it[SHUFFLE] = enabled
        }
    }

    suspend fun saveRepeat(enabled: Boolean) {
        context.dataStore.edit {
            it[REPEAT] = enabled
        }
    }

    suspend fun saveLastSong(songId: Long) {
        context.dataStore.edit {
            it[LAST_SONG_ID] = songId
        }
    }

    suspend fun getLastSongId(): Long? {
        return lastSongId.first()
    }

    suspend fun clearLastSong() {
        context.dataStore.edit {
            it.remove(LAST_SONG_ID)
        }
    }
}