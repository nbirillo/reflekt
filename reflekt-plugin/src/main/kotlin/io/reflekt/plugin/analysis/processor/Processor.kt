package io.reflekt.plugin.analysis.processor

import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

abstract class Processor<Output : Any>(protected open val binding: BindingContext){
    // Map represents <Output> for each file. String in the map is a path to the file.
    abstract fun process(filePath: String, element: KtElement): MutableMap<String, Output>

    abstract fun shouldRunOn(element: KtElement): Boolean
}
