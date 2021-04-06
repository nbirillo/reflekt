package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.models.ClassOrObjectUses
import io.reflekt.plugin.analysis.models.SubTypesToAnnotations
import io.reflekt.plugin.analysis.processor.Processor
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import io.reflekt.plugin.analysis.psi.isSubtypeOf
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingContext

/*
* Base processor to get uses which satisfy by invokes in a project.
* For example, concrete classes with set of annotations or subtypes.
* */
abstract class BaseUsesProcessor<Output : Any>(override val binding: BindingContext): Processor<Output>(binding) {
    // Map represents uses for each file. String in the map is a path to the file.
    abstract val fileToUses: MutableMap<String, Output>

    protected fun processClassOrObjectUses(element: KtElement,
                                           invokes: ClassOrObjectInvokes,
                                           uses: ClassOrObjectUses): ClassOrObjectUses {
        (element as? KtClassOrObject)?.let {
            invokes.filter { it.covers(element) }.forEach {
                uses.getValue(it).add(element)
            }
        }
        return uses
    }

    protected fun initClassOrObjectUses(invokes: ClassOrObjectInvokes): ClassOrObjectUses =
        invokes.map { it to ArrayList<KtClassOrObject>() }.toMap()

    private fun SubTypesToAnnotations.covers(element: KtClassOrObject): Boolean =
        // annotations set is empty when withSubTypes() method is called, so we don't need to check annotations in this case
        (annotations.isEmpty() || element.getAnnotations(binding, annotations).isNotEmpty()) && element.isSubtypeOf(subTypes, binding)
}
