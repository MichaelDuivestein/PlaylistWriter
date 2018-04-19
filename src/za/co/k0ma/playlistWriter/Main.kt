import java.io.File

fun main(args: Array<String>) {
	println("Starting main")
	val configPath = args.firstOrNull()
	
	if(configPath == null) {
		println("Config path not set in config. Aborting")
		return
	}
	
	val config = Config.loadConfig(configPath)
	
	if (config == null) {
		println("config is null. Aborting")
		return
	} else if (!config.isInputAndOutputPathLoaded) {
		println("Input path and/or output path not set in config. Aborting")
		return
	}
	
	val reader = DirectoryReader(config)
	val fileData = reader.readFiles()
	if (config.listExtensions) {
		fileData.listExtensions()
	}
	if (config.listFiles) {
		fileData.listFiles(config.listLimit)
	}
	val writer = PlaylistWriter(config.outputPath!! + config.playlistName)
	if (config.shufflePlaylist) {
		fileData.files = writer.shuffle(fileData.files) as ArrayList<File>
	}
	writer.writePlaylist(fileData.files, config.chunkSize, config.readTags)
}