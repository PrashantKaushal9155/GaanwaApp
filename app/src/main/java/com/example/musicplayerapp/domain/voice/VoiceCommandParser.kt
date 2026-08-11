package com.example.musicplayerapp.domain.voice

object VoiceCommandParser {

    fun parse(text: String): VoiceCommand {

        val command = text.lowercase().trim()

        return when {

            command == "play" ->
                VoiceCommand.Play

            command == "pause" ->
                VoiceCommand.Pause

            command == "resume" ->
                VoiceCommand.Resume

            command == "next" ->
                VoiceCommand.Next

            command == "previous" ->
                VoiceCommand.Previous

            command == "shuffle on" ->
                VoiceCommand.ShuffleOn

            command == "shuffle off" ->
                VoiceCommand.ShuffleOff

            command == "repeat on" ->
                VoiceCommand.RepeatOn

            command == "repeat off" ->
                VoiceCommand.RepeatOff

            command.startsWith("play ") ->
                VoiceCommand.PlaySong(
                    command.removePrefix("play ").trim()
                )

            else ->
                VoiceCommand.Unknown
        }
    }
}