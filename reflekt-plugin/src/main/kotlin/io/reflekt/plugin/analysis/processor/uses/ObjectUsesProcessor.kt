package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.ClassOrObjectUses
import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.analysis.models.ReflektInvokes.Companion.invokes
import io.reflekt.plugin.analysis.processor.isPublicObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.resolve.BindingContext

class ObjectUsesProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : BaseUsesProcessor<ClassOrObjectUses>(binding) {
    override val fileToUses: MutableMap<String, ClassOrObjectUses> = HashMap()

    override fun process(filePath: String, element: KtElement): MutableMap<String, ClassOrObjectUses> {
        val invokes = reflektInvokes.objects.invokes
        val uses = fileToUses.getOrPut(filePath) { initClassOrObjectUses(invokes) }
        processClassOrObjectUses(element, invokes, uses)
        return fileToUses
    }

    override fun shouldRunOn(element: KtElement) = element.isPublicObject
}
