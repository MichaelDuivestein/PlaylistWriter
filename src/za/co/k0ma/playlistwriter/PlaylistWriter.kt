package za.co.k0ma.playlistwriter

import java.io.File
import java.util.Random

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

class PlaylistWriter {
    companion object TagReader

    /**
     * Basic shuffle. Can do something better later
     */
    fun shuffle(files: Iterable<File>) = files.shuffled(Random())

    fun writePlaylist(outputPath: String,
                      outputName: String,
                      files: MutableList<File>,
                      chunkSize: Int,
                      readTags: Boolean) {


        println("------ Writing playlists. ------")

        val pathFolder = File(outputPath)
        if (!pathFolder.exists()) pathFolder.mkdirs()

        //chop playlist into a bunch of smaller playlists
        val fileSet = files.chunked(chunkSize.takeIf { it > 2 } ?: files.size)

        println("writing ${fileSet.size} files containing up to $chunkSize entries.")

        for ((playlistCounter, fileList) in fileSet.withIndex()) {
            val outputFile = File(
                    outputPath
                            + outputName
                            + "_"
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

    private fun createTitle(file: File, readTags: Boolean): String {
        if (readTags)
            return readTag(file)
        else
            return file.nameWithoutExtension

    }
}

