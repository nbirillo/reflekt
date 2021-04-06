package io.reflekt.plugin.analysis.processor.uses

import io.reflekt.plugin.analysis.models.FunctionUses
import io.reflekt.plugin.analysis.models.ReflektInvokes
import io.reflekt.plugin.analysis.models.ReflektInvokes.Companion.invokes
import io.reflekt.plugin.analysis.models.SignatureToAnnotations
import io.reflekt.plugin.analysis.processor.isPublicFunction
import io.reflekt.plugin.analysis.psi.annotation.getAnnotations
import io.reflekt.plugin.analysis.psi.function.argumentTypesWithReceiver
import io.reflekt.plugin.analysis.psi.function.equalTo
import io.reflekt.plugin.analysis.psi.function.returnType
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext

class FunctionUsesProcessor(override val binding: BindingContext, private val reflektInvokes: ReflektInvokes) : BaseUsesProcessor<FunctionUses>(binding) {
    override val fileToUses: MutableMap<String, FunctionUses> = HashMap()

    override fun process(filePath: String, element: KtElement): MutableMap<String, FunctionUses> {
        (element as? KtNamedFunction)?.let {
            val invokes = reflektInvokes.functions.invokes
            invokes.forEach {
                if (it.covers(element)) {
                    fileToUses.getOrPut(filePath) { invokes.map { signatureToAnnotation ->
                        signatureToAnnotation to ArrayList<KtNamedFunction>()
                    }.toMap() }.getValue(it).add(element)
                }
            }
        }
        return fileToUses
    }

    override fun shouldRunOn(element: KtElement) = element.isPublicFunction

    private fun SignatureToAnnotations.covers(function: KtNamedFunction): Boolean =
        (annotations.isEmpty() || function.getAnnotations(binding, annotations).isNotEmpty()) && checkSignature(function)

    private fun SignatureToAnnotations.checkSignature(function: KtNamedFunction): Boolean {
        val argumentTypes = function.argumentTypesWithReceiver(binding)
        val returnType = function.returnType(binding) ?: return false
        val functionNParameters = argumentTypes.plus(returnType)
        if (functionNParameters.size != signature.parameters.size) {
            return false
        }
        return (functionNParameters zip signature.parameters).all { (k, p) -> k.equalTo(p, binding) }
    }
}
