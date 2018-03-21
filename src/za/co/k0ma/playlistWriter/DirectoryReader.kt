import java.io.File
import java.util.TreeSet

class DirectoryReader(private val config: Config) {
	//this keeps a list of the unique extensions we've added
	private var uniqueExtensions = TreeSet<String>()
	var files = ArrayList<File>()
	
	fun readFiles() {
		val root = File(config.inputPath)
		val whitelist = config.extensionWhitelist?.takeIf { it.isNotEmpty() }?.toSet()
		
		println("----- Starting scan ------")
		
		root.walk()
				.filter { it.isFile }
				.filter { whitelist?.contains(it.extension.toUpperCase()) ?: true }
				.forEach { file ->
					uniqueExtensions.add(file.extension.toUpperCase())
					files.add(file)
				}
		
		println("files found: ${files.size}")
		println("unique extensions found: ${uniqueExtensions.size}")
		println("----- Finished scan ------")
	}
	
	fun listFiles(limitListSize: Int?) {
		println("----- List start ------")
		files.take(limitListSize ?: files.size)
				.forEach(::println)
		
		if (limitListSize != null) {
			println("----- List reached limit ------")
		}
		
		println("----- List end ------")
	}
	
	fun listExtensions() {
		println("----- listing extensions ------")
		uniqueExtensions.forEach(::println)
		println("----- List end ------")
	}
}