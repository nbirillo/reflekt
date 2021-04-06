package io.reflekt.plugin.analysis.processor.instances

import io.reflekt.plugin.analysis.processor.isPublicNotAbstractClass
import io.reflekt.plugin.analysis.processor.isPublicObject
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingContext

/*
* Processor to get instances for all classes or objects in a project.
* Note: get only public objects and public not abstract classes.
* */
open class ClassOrObjectInstancesProcessor(override val binding: BindingContext) : BaseInstancesProcessor<MutableList<KtClassOrObject>>(binding) {
    override val fileToInstances: MutableMap<String, MutableList<KtClassOrObject>> = HashMap()

    override fun process(filePath: String, element: KtElement): MutableMap<String, MutableList<KtClassOrObject>> {
        (element as? KtClassOrObject)?.let {
            fileToInstances.getOrPut(filePath) { ArrayList() }.add(it)
        }
        return fileToInstances
    }

    override fun shouldRunOn(element: KtElement) = element.isPublicNotAbstractClass || element.isPublicObject
}

class ClassInstancesProcessor(override val binding: BindingContext) : ClassOrObjectInstancesProcessor(binding) {
    override fun shouldRunOn(element: KtElement) = element.isPublicNotAbstractClass
}

class ObjectInstancesProcessor(override val binding: BindingContext) : ClassOrObjectInstancesProcessor(binding) {
    override fun shouldRunOn(element: KtElement) = element.isPublicObject
}

