import java.io.File
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

class PlaylistWriter(private val files: MutableList<File>, private val outputPathAndName: String) {
	
	/**
	 * Basic shuffle. Can do something better later
	 */
	fun shuffle() {
		files.shuffle(Random())
	}
	
	fun writePlaylist(chunkSize: Int) {
		
		println("------ Writing playlists. ------")
		
		//chop playlist into a bunch of smaller playlists
		val fileSet = files.chunked(chunkSize.takeIf { it > 2 } ?: files.size)
		
		println("writing ${fileSet.size} files containing up to ${chunkSize} entries.")
		
		for ((playlistCounter, fileList) in fileSet.withIndex()) {
			val outputFile = File(outputPathAndName + "_"
					+ "$playlistCounter".format("00") + ".m3u8")
			
			outputFile.writeText("$PLAYLIST_HEADER\n")
			writeEntry(fileList, outputFile)
		}
		
		println("------ Finished writing files ------");
	}
	
	private fun writeEntry(fileList: List<File>, outputFile: File) {
		fileList.forEach {
			outputFile.appendText("$ENTRY_START" + createTitle(it) + "\n")
			outputFile.appendText(it.path + "\n")
		}
	}
	
	fun createTitle(file: File) = file.nameWithoutExtension
	
	//read ID3 tags: https://revbingo.github.io/kotlin/2017/06/25/kotlin-refactoring-dsl.html
	// ID3 for MP3, WMA (modded), https://en.wikipedia.org/wiki/ID3
	//Vorbis comment for ogg, FLAC: https://en.wikipedia.org/wiki/Vorbis_comment
	//
}

