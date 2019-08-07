package za.co.k0ma.playlistwriter

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import java.io.File

data class Config(
        @Json(name = "music folder") val inputPath: String?,
        @Json(name = "output folder") val outputPath: String?,
        @Json(name = "playlist name") val playlistName: String?,
        //This could be a faster HashSet or something, but:
        //1) Klaxon can write HashSets but can't read them for some reason
        //2) There are not very many file extension types for music, so processing time's relatively small.
        //http://infotechgems.blogspot.co.za/2011/11/java-collections-performance-time.html
        @Json(name = "Limit extensions to") val extensionWhitelist: ArrayList<String>?,
        @Json(name = "shuffle playlist") val shufflePlaylist: Boolean = false,
        @Json(name = "Split playlist") val splitPlaylist: Boolean = false,
        @Json(name = "split playlist into chunks of") val chunkSize: Int = 0,
        @Json(name = "print a list of extensions") val listExtensions: Boolean = false,
        @Json(name = "print a list of file names") val listFiles: Boolean = false,
        @Json(name = "limit list file names to") val listLimit: Int?,
        @Json(name = "read tags") val readTags: Boolean = false
) {
    companion object ConfigHelper {
        fun loadConfig(path: String): Config? {
            val configFile = File(path)
            return if (!configFile.exists()) null else return Klaxon().parse<Config>(configFile)
        }

        fun saveConfig(path: String, configData: Config) {
            File(path).takeUnless { !it.isFile }?.writeText(Klaxon().toJsonString(configData))
        }
    }

    /**
     * Determines if the minimum we need to create a playlist is present (inputPath & outputPath)
     */
    val isInputAndOutputPathLoaded
        get() = inputPath != null && outputPath != null && playlistName != null


}