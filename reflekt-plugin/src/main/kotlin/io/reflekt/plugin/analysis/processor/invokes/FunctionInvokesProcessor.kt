package io.reflekt.plugin.analysis.processor.invokes

import io.reflekt.plugin.analysis.common.ReflektName
import io.reflekt.plugin.analysis.common.findReflektFunctionInvokeArgumentsByExpressionPart
import io.reflekt.plugin.analysis.models.FunctionInvokes
import io.reflekt.plugin.analysis.psi.getFqName
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.utils.addIfNotNull

class FunctionInvokesProcessor (override val binding: BindingContext): BaseInvokesProcessor<FunctionInvokes>(binding){
    override val fileToInvokes: MutableMap<String, FunctionInvokes> = HashMap()

    override fun process(filePath: String, element: KtElement): MutableMap<String, FunctionInvokes> {
        (element as? KtReferenceExpression)?.let {
            fileToInvokes.getOrPut(filePath) { HashSet() }.addIfNotNull(findReflektFunctionInvokeArgumentsByExpressionPart(element, binding))
        }
        return fileToInvokes
    }

    override fun isValidExpression(expression: KtReferenceExpression) = expression.getFqName(binding) == ReflektName.FUNCTIONS.fqName
}
