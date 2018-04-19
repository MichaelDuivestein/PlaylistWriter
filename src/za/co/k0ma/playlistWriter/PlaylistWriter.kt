import java.io.File
import java.io.RandomAccessFile
import java.util.*

/**
 * File looks like this:
 * #EXTM3U
 * #EXTINF:{$TITLE}
 * {$PATH}
 * #EXTINF:{$TITLE}
 * {$PATH}
 * ...
 */
const val PLAYLIST_HEADER = "#EXTM3U"
const val ENTRY_START = "#EXTINF:"

class PlaylistWriter(private val outputPathAndName: String) {
	
	/**
	 * Basic shuffle. Can do something better later
	 */
	fun shuffle(files: Iterable<File>) = files.shuffled(Random())
	
	fun writePlaylist(files: MutableList<File>, chunkSize: Int, readTags: Boolean) {
		println("------ Writing playlists. ------")
		
		//chop playlist into a bunch of smaller playlists
		val fileSet = files.chunked(chunkSize.takeIf { it > 2 } ?: files.size)
		
		println("writing ${fileSet.size} files containing up to $chunkSize entries.")
		
		for ((playlistCounter, fileList) in fileSet.withIndex()) {
			val outputFile = File(outputPathAndName + "_"
					+ "$playlistCounter".format("00") + ".m3u8")
			
			outputFile.writeText("$PLAYLIST_HEADER\n")
			writeEntry(fileList, outputFile, readTags)
		}
		
		println("------ Finished writing files ------")
	}
	
	private fun writeEntry(fileList: List<File>, outputFile: File, readTags: Boolean) {
		fileList.forEach {
			outputFile.appendText(ENTRY_START + createTitle(it, readTags) + "\n")
			outputFile.appendText(it.path + "\n")
		}
	}
	
	private fun createTitle(file: File, readTags: Boolean) =
			if (readTags) {
				readTag(file)
			} else {
				file.nameWithoutExtension
			}
	
	private fun readTag(file: File) = when (file.extension.toLowerCase()) {
		"mp3" -> readID3Tag(file)
		else -> {
			file.nameWithoutExtension
		}
	}
	
	//TODO: Move tag reading stuff to it's own class
	private fun readID3Tag(file: File): String {
		val randomAccessFile = RandomAccessFile(file, "r")
		try {
			return when (getTagType(randomAccessFile)) {
				TagType.NONE -> file.nameWithoutExtension
				TagType.ID3V1 -> readID3V1Tag(randomAccessFile)
				TagType.ID3V1_ENHANCED -> readID3V1EnhancedTag(randomAccessFile)
				TagType.ID3V2 -> readID3V2Tag(randomAccessFile, file.nameWithoutExtension)
			}
		} catch (exception: Exception) {
			println("ERROR: ")
			println(exception.stackTrace)
		} finally {
			randomAccessFile.close()
		}
		
		return file.nameWithoutExtension
	}
	
	private fun readBytes(randomAccessFile: RandomAccessFile, length: Int, stripWhitespace: Boolean): String {
		val tagBytes = ByteArray(length)
		randomAccessFile.read(tagBytes)
		val value = String(tagBytes, Charsets.UTF_8)
		
		return if (stripWhitespace) value.replace("\u0000", "").trim() else value
	}
	
	//Checks for ID3 Tag and returns version.
	/**
	 *  @return NONE if no tag, otherwise tag version
	 */
	private fun getTagType(randomAccessFile: RandomAccessFile): TagType {
		
		//TODO: let's try with the most common: ID3V2. (Some ID3V2 files contain a 'backup' ID3V1)
		
		//perhaps it's an ID3v1 tag
		randomAccessFile.seek(randomAccessFile.length() - 128)
		var tag = readBytes(randomAccessFile, 3, true)
		if ("tag".equals(tag, true)) {
			//There is no ID3 tag
			return TagType.ID3V1
		}
		
		//see if it's an ID3V1Enhanced tag
		//do this last, as these tags are hardly used and often stale.
		randomAccessFile.seek(randomAccessFile.length() - 227)
		tag = readBytes(randomAccessFile, 4, true)
		if ("tag+".equals(tag, true)) {
			//There is no ID3 tag
			return TagType.ID3V1_ENHANCED
		}
		
		return TagType.NONE
	}
	
	private fun readID3V1Tag(randomAccessFile: RandomAccessFile) = readID3V1Tag(randomAccessFile, 125, 30)
	private fun readID3V1EnhancedTag(randomAccessFile: RandomAccessFile) = readID3V1Tag(randomAccessFile, 224, 60)
	
	private fun readID3V1Tag(randomAccessFile: RandomAccessFile, seekDistanceFromEnd: Int, elementLength: Int): String {
		randomAccessFile.seek(randomAccessFile.length() - seekDistanceFromEnd)
		
		//read artist
		var title = readBytes(randomAccessFile, elementLength, true).takeIf { it.isNotBlank() } ?: "Unknown Title"
		
		//read title
		var artist = readBytes(randomAccessFile, elementLength, true).takeIf { it.isNotBlank() } ?: "Unknown Artist"
		
		return "$artist - $title"
	}
	
	private fun readID3V2Tag(randomAccessFile: RandomAccessFile, tempFileName: String): String {
		return tempFileName
		//TODO:
		//https://github.com/mpatric/mp3agic
	}
	
	enum class TagType {
		NONE,
		ID3V1,
		ID3V1_ENHANCED,
		ID3V2
	}
	
	//read ID3 tags: https://revbingo.github.io/kotlin/2017/06/25/kotlin-refactoring-dsl.html
	// ID3 for MP3, WMA (modded ID3), https://en.wikipedia.org/wiki/ID3
	//Vorbis comment for ogg, FLAC: https://en.wikipedia.org/wiki/Vorbis_comment
}

