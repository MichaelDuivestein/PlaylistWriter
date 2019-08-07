package za.co.k0ma.playlistwriter

import java.io.File
import java.util.TreeSet
import kotlin.collections.ArrayList

class FileData(val uniqueExtensions: MutableSet<String> = TreeSet(),
               var files: ArrayList<File> = ArrayList()) {

    fun listFiles(limitListSize: Int?) {
        println("----- List start ------")
        files.take(limitListSize ?: files.size).forEach(::println)

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