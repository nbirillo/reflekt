package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.ClassOrObjectUses
import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.analysis.models.ReflektInvokes.Companion.invokes
import io.reflekt.plugin.analysis.processor.isPublicNotAbstractClass
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingContext

class ClassUsesProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : BaseUsesProcessor<ClassOrObjectUses>(binding) {
    override val fileToUses: MutableMap<String, ClassOrObjectUses> = HashMap()

    override fun process(filePath: String, element: KtElement): MutableMap<String, ClassOrObjectUses> {
        val invokes = reflektInvokes.classes.invokes
        processClassOrObjectUses(filePath, element, invokes, fileToUses)
        return fileToUses
    }

    override fun shouldRunOn(element: KtElement) = element.isPublicNotAbstractClass
}
