package com.example.musicplayerapp.domain.voice

sealed class VoiceCommand {
    data object Play : VoiceCommand()

    data object Pause : VoiceCommand()

    data object Resume : VoiceCommand()

    data object Next : VoiceCommand()

    data object Previous : VoiceCommand()

    data class PlaySong(
        val query : String
    ) : VoiceCommand()

    data class PlayArtist(
        val artist: String
    ) : VoiceCommand()

    data class Search(
        val query: String
    ) : VoiceCommand()

    data object ShuffleOn : VoiceCommand()

    data object ShuffleOff : VoiceCommand()

    data object RepeatOn : VoiceCommand()

    data object RepeatOff : VoiceCommand()

    data object Unknown : VoiceCommand()
}