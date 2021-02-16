package za.co.k0ma.playlistwriter

import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.tag.FieldKey
import java.io.File


/**
 * Attempts to read any tag in the file
 */
fun readTag(file: File) = when (file.extension.toLowerCase()) {
    "flac", "mp3", "mp4", "m4a", "ogg", "wav", "wma" -> createPlaylistEntryNameWithAudioFileIO(file)
    else -> file.nameWithoutExtension
}

//TODO: AudioFileIO is very slow. In future maybe look at faster libraries for reading tags.
fun createPlaylistEntryNameWithAudioFileIO(file: File): String {
    try {
        val audioFile = AudioFileIO.read(file)
        //sometimes a file will not have a tag
        if (audioFile != null && audioFile.tag != null) {
            "${getArtist(audioFile)} - ${getTitle(audioFile)}"
                .also { entryName -> if (entryName != "Unknown Artist - Unknown Title") return entryName }
        }
    } catch (ex: CannotReadException) {
        //sometimes files don't contain valid tag data. Log and continue.
        println("Invalid tag data, CannotReadException: $ex")
    } catch (ex: InvalidAudioFrameException) {
        //sometimes files don't contain valid tag data. Log and continue.
        println("Invalid tag data, InvalidAudioFrameException: $ex")
    } catch (ex: Exception) {
        println(ex)
    }
    return file.name
}

private fun getTitle(audioFile: AudioFile) =
    audioFile.tag.getFirst(FieldKey.TITLE).takeIf { (it != null) && it.isNotBlank() } ?: "Unknown Title"

private fun getArtist(audioFile: AudioFile) =
    audioFile.tag.getFirst(FieldKey.ARTIST).takeIf { (it != null) && it.isNotBlank() } ?: "Unknown Artist"


