package io.reflekt.plugin.analysis.processor.invokes

import io.reflekt.plugin.analysis.models.ClassOrObjectInvokes
import io.reflekt.plugin.analysis.common.findReflektInvokeArgumentsByExpressionPart
import io.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.utils.addIfNotNull

/*
* Base processor to get invokes by fqNames (e.g. all Reflekt DSL invokes) in a project.
* */
abstract class BaseInvokesProcessor<Output : Any>(override val binding: BindingContext): Processor<Output>(binding) {
    // Map represents invokes for each file. String in the map is a path to the file.
    abstract val fileToInvokes: MutableMap<String, Output>

    protected fun processClassOrObjectInvokes(element: KtElement): ClassOrObjectInvokes {
        val invokes: ClassOrObjectInvokes = HashSet()
        (element as? KtReferenceExpression)?.let { expression ->
            invokes.addIfNotNull(findReflektInvokeArgumentsByExpressionPart(expression, binding))
        }
        return invokes
    }

    protected abstract fun isValidExpression(expression: KtReferenceExpression): Boolean

    override fun shouldRunOn(element: KtElement) = (element as? KtReferenceExpression)?.let { isValidExpression(it) } ?: false
}
