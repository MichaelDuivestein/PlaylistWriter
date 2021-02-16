package za.co.k0ma.playlistwriter

import za.co.k0ma.playlistwriter.config.Config
import java.io.File
import java.util.*
import kotlin.random.Random


/**
 * Output file looks like this:
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
    fun shuffle(files: MutableList<File>): Iterable<File> {
        // start from beginning of the list
        for (currentPosition in 0 until files.size - 1) {
            // get a random index j such that i <= j <= n
            val newPosition = currentPosition + Random.nextInt(files.size - currentPosition)

            files[currentPosition] = files[newPosition]
                .also { files[newPosition] = files[currentPosition] }
        }
        return files
    }

    fun writePlaylist(config: Config, files: MutableList<File>) {
        println("------ Writing playlists. ------")

        val pathFolder = File(config.outputPath!!)
        if (!pathFolder.exists()) pathFolder.mkdirs()

        //chop playlist into a bunch of smaller playlists
        val fileSet = files.chunked(config.chunkSize.takeIf { it > 2 } ?: files.size)

        println("writing ${fileSet.size} files containing up to ${config.chunkSize} entries.")

        for ((playlistCounter, fileList) in fileSet.withIndex()) {
            val outputFile = File(
                config.outputPath
                        + config.playlistName
                        + "_"
                        + "$playlistCounter".format("00") + ".m3u8"
            )

            outputFile.writeText("$PLAYLIST_HEADER\n")
            writeEntry(fileList, outputFile, config.readTags)
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
        return if (readTags) {
            readTag(file)
        } else {
            file.nameWithoutExtension
        }
    }
}
