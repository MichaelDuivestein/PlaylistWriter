package za.co.k0ma.playlistwriter

import za.co.k0ma.playlistwriter.config.Config
import java.io.File

//this keeps a list of the unique extensions we've added
fun readFiles(config: Config): FileData? {
    if (config.inputPath == null) {
        println("inputPath is null in config. Aborting")
        return null
    }

    val root = File(config.inputPath)
    val whitelist = config.extensionWhitelist?.takeIf { it.isNotEmpty() }?.toSet()
    val fileData = FileData()

    println("----- Starting scan ------")

    root.walk()
        .filter { it.isFile }
        .filter { whitelist?.contains(it.extension.toUpperCase()) ?: true }
        .forEach { file ->
            fileData.uniqueExtensions.add(file.extension.toUpperCase())
            fileData.files.add(file)
        }

    println("files found: ${fileData.files.size}")
    println("unique extensions found: ${fileData.uniqueExtensions.size}")
    println("----- Finished scan ------")

    return fileData
}
