package io.reflekt.plugin.analysis.processor.invokes

import io.reflekt.plugin.analysis.common.ReflektName
import io.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import kotlin.collections.HashSet

class ObjectInvokesProcessor (override val binding: BindingContext): BaseInvokesProcessor<ClassOrObjectInvokes>(binding){
    override val fileToInvokes: MutableMap<String, ClassOrObjectInvokes> = HashMap()

    override fun process(filePath: String, element: KtElement): MutableMap<String, ClassOrObjectInvokes> {
        val invokes = processClassOrObjectInvokes(element)
        if (invokes.isNotEmpty()) {
            fileToInvokes.getOrPut(filePath) { HashSet() }.addAll(invokes)
        }
        return fileToInvokes
    }

    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektName.OBJECTS.fqName
}
