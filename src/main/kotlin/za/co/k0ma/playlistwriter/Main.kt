package za.co.k0ma.playlistwriter

import za.co.k0ma.playlistwriter.config.isInputAndOutputPathLoaded
import za.co.k0ma.playlistwriter.config.loadConfig
import java.io.File

fun main() {
    println("Starting main")

    val config = loadConfig()

    if (config == null) {
        println("config is null. Aborting")
        return
    } else if (!isInputAndOutputPathLoaded(config)) {
        println("Input path and/or output path not set in config. Aborting")
        return
    }

    val fileData = readFiles(config)

    if (fileData == null) {
        println("No files found. Aborting.")
        return
    }

    if (config.listExtensions) {
        fileData.listExtensions()
    }
    if (config.listFiles) {
        fileData.listFiles(config.listLimit)
    }
    val writer = PlaylistWriter()
    if (config.shufflePlaylist) {
        fileData.files = writer.shuffle(fileData.files) as ArrayList<File>
    }
    writer.writePlaylist(config, fileData.files)
}