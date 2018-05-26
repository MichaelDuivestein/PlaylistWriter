package za.co.k0ma.playlistWriter

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.tag.FieldKey
import java.io.File


/**
 * Attempts to read any tag in the file
 */
fun readTag(file: File) = when (file.extension.toLowerCase()) {
	"flac" -> readTagWithAudioFileIO(file)
	"mp3" -> readTagWithAudioFileIO(file)
	"mp4" -> readTagWithAudioFileIO(file)
	"m4a" -> readTagWithAudioFileIO(file)
	"ogg" -> readTagWithAudioFileIO(file)
	"wav" -> readTagWithAudioFileIO(file)
	"wma" -> readTagWithAudioFileIO(file)
	else -> {
		file.nameWithoutExtension
	}
}

//TODO: AudioFileIO is very slow. In future maybe look at faster libraries for reading tags.
fun readTagWithAudioFileIO(file: File): String {
	var artist = "Unknown Artist"
	var title = "Unknown Title"
	val unknownData = "Unknown Artist - Unknown Title"
	try {
		val audioFile = AudioFileIO.read(file)
		//sometimes a file will not have a tag
		if (audioFile == null || audioFile.tag == null) {
			return file.name
		}
		artist = audioFile.tag.getFirst(FieldKey.ARTIST).takeIf { (it != null) && it.isNotBlank() } ?: artist
		title = audioFile.tag.getFirst(FieldKey.TITLE).takeIf { (it != null) && it.isNotBlank() } ?: title
		
	} catch (ex: CannotReadException) {
		//sometimes files don't contain valid tag data. Log and continue.
		println("Invalid tag data, CannotReadException: $ex")
	} catch (ex: InvalidAudioFrameException) {
		//sometimes files don't contain valid tag data. Log and continue.
		println("Invalid tag data, InvalidAudioFrameException: $ex")
	} catch (ex: Exception) {
		println(ex)
	}
	return "$artist - $title".takeIf { ! it.equals(unknownData) } ?: file.name
}
