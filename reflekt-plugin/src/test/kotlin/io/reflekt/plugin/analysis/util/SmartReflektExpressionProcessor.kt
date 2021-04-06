package io.reflekt.plugin.analysis.util

import io.reflekt.SmartReflekt
import io.reflekt.plugin.analysis.common.ReflektName
import io.reflekt.plugin.analysis.processor.Processor
import io.reflekt.plugin.analysis.psi.getFqName
import io.reflekt.plugin.utils.enumToRegexOptions
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class SmartReflektExpressionProcessor(override val binding: BindingContext): Processor<MutableList<KtNameReferenceExpression>>(binding) {
    val expressions: MutableMap<String, MutableList<KtNameReferenceExpression>> = HashMap()

    override fun process(filePath: String, element: KtElement): MutableMap<String, MutableList<KtNameReferenceExpression>> {
        (element as? KtNameReferenceExpression)?.let {
            expressions.getOrPut(filePath) { ArrayList() }.add(it)
        }
        return expressions
    }

    private fun isValidExpression(expression: KtNameReferenceExpression): Boolean {
        val names = enumToRegexOptions(ReflektName.values(), ReflektName::reflektName)
        val fqName = expression.getFqName(binding) ?: return false
        Regex("${SmartReflekt::class.qualifiedName}\\.$names").matchEntire(fqName) ?: return false
        return true
    }

    override fun shouldRunOn(element: KtElement): Boolean = (element as? KtNameReferenceExpression)?.let { isValidExpression(it) } ?: false
}
