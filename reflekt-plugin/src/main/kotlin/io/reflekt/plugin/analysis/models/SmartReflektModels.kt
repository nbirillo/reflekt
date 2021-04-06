package io.reflekt.plugin.analysis.models

import io.reflekt.plugin.analysis.processor.instances.*
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

/*
 * Store a set of qualified names that exist in the project and additional libraries
 */
data class ReflektInstances(
    val objects: Map<String, List<KtClassOrObject>> = HashMap(),
    val classes: Map<String, List<KtClassOrObject>> = HashMap(),
    val functions: Map<String, List<KtNamedFunction>> = HashMap()
) {
    companion object{
        fun createByProcessors(processors: Set<BaseInstancesProcessor<*>>) = ReflektInstances(
            objects = processors.mapNotNull { it as? ObjectInstancesProcessor }.first().fileToInstances,
            classes = processors.mapNotNull { it as? ClassInstancesProcessor }.first().fileToInstances,
            functions = processors.mapNotNull { it as? FunctionInstancesProcessor }.first().fileToInstances
        )
    }
}

data class SubTypesToFilters(
    val subType: String? = null,
    val filters: Set<Lambda> = emptySet()
)

data class Lambda(
    val body: String,
    val parameters: List<String> = listOf("it")
)
