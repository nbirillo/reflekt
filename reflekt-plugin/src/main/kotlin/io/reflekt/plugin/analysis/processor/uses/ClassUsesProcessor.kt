package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.ClassOrObjectUses
import io.reflekt.plugin.analysis.ReflektInvokes
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class ClassUsesProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : BaseUsesProcessor<ClassOrObjectUses>(binding) {
    override val uses: ClassOrObjectUses = HashMap()

    override fun process(element: KtElement): ClassOrObjectUses {
        // TODO: Not yet implemented
        return uses
    }

    // TODO: Not yet implemented
    private fun isValidClass(klass: KtClass): Boolean = false

    override fun shouldRunOn(element: KtElement) = (element as? KtClass)?.let{ isValidClass(it) } ?: false
}