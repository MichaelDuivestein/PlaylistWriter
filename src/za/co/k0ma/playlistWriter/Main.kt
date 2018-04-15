fun main(args: Array<String>) {
	println("Starting main")
	val inputPath = args[0]
	val config = Config.loadConfig(inputPath)
	
	if (config == null) {
		println("config is null. Aborting")
		return
	} else if (!config.isInputAndOutputPathLoaded()) {
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
		fileData.files = writer.shuffle(fileData.files)
	}
	writer.writePlaylist(fileData.files, config.chunkSize, config.readTags)
}