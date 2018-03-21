fun main(args: Array<String>) {
	println("Starting main")
	val inputPath = args[0]
	val config = Config.loadConfig(inputPath)
	
	if (config == null) {
		println("config is null. Aborting")
		return
	} else if (! config.isInputAndOutputPathLoaded()) {
		println("Input path and/or output path not set in config. Aborting")
		return
	}
	
	val reader = DirectoryReader(config)
	reader.readFiles()
	if (config.listExtensions) {
		reader.listExtensions()
	}
	if (config.listFiles) {
		reader.listFiles(config.listLimit)
	}
	val writer = PlaylistWriter(reader.files, config.outputPath!!)
	if (config.shufflePlaylist) {
		writer.shuffle()
	}
	writer.writePlaylist(config.chunkSize)
}