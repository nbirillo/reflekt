package io.reflekt.util

import net.lingala.zip4j.ZipFile
import java.io.File

object FileUtil {
    fun extractAllFiles(zipFile: File): List<File> = getAllNestedFiles(unZipFile(zipFile))

    fun getAllNestedFiles(rootPath: String, files: MutableList<File> = ArrayList(), ignoredDirectories: Set<String> = emptySet()): List<File> {
        val root = File(rootPath)
        root.listFiles()?.forEach {
            if (it.isFile) {
                files.add(it)
            } else if (it.isDirectory && it.name !in ignoredDirectories) {
                getAllNestedFiles(it.absolutePath, files)
            }
        }
        return files
    }

    fun getNestedDirectories(rootPath: String): List<File> {
        return File(rootPath).listFiles()?.filter { it.isDirectory } ?: emptyList()
    }

    val File.extension: String
        get() = this.name.substringAfterLast('.', "")

    private fun unZipFile(file: File, destinationPath: String? = null): String {
        val zipFile = ZipFile(file.path)
        val outputPath = destinationPath ?: file.parent
        zipFile.extractAll(outputPath)
        return outputPath
    }
}
