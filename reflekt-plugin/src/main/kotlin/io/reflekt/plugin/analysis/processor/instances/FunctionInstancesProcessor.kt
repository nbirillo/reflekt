package io.reflekt.plugin.analysis.processor.instances

import io.reflekt.plugin.analysis.processor.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

/*
* Processor to get instances for all functions in a project.
* Note: get only public functions.
* */
class FunctionInstancesProcessor(override val binding: BindingContext) : BaseInstancesProcessor<MutableList<KtNamedFunction>>(binding) {
    override val fileToInstances: MutableMap<String, MutableList<KtNamedFunction>> = HashMap()

    override fun process(filePath: String, element: KtElement): MutableMap<String, MutableList<KtNamedFunction>> {
        (element as? KtNamedFunction)?.let {
            fileToInstances.getOrPut(filePath) { ArrayList() }.add(it)
        }
        return fileToInstances
    }

    override fun shouldRunOn(element: KtElement) = element.isPublicFunction
}
