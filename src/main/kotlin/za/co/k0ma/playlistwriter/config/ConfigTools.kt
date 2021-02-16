package za.co.k0ma.playlistwriter.config

import com.beust.klaxon.Klaxon
import java.io.File

fun loadConfig(): Config? {

    Config::class.java.classLoader.getResource("config.json")
        ?.path
        ?.let { path ->
            File(path).takeIf { maybeFile ->
                maybeFile.exists()
            }
                ?.let { file ->
                    return Klaxon().parse<Config>(file)
                }
        }
    return null
}

fun saveConfig(path: String, configData: Config) {
    File(path).takeUnless { !it.isFile }?.writeText(Klaxon().toJsonString(configData))
}

/**
 * Determines if the minimum we need to create a playlist is present (inputPath & outputPath)
 */
fun isInputAndOutputPathLoaded(config: Config) =
    config.inputPath != null && config.outputPath != null && config.playlistName != null
