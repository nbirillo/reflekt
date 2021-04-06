package io.reflekt.plugin.analysis.processor.instances

import io.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.kotlin.resolve.BindingContext

/*
* Base processor to get instances (e.g. all classes, objects or functions) in a project.
* */
abstract class BaseInstancesProcessor<Output : Any>(override val binding: BindingContext): Processor<Output>(binding) {
    // Map represents instances for each file. String in the map is a path to the file.
    abstract val fileToInstances: MutableMap<String, Output>
}
