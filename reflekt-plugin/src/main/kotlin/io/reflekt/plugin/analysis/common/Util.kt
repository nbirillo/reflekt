package io.reflekt.plugin.analysis.common

import io.reflekt.plugin.analysis.*
import io.reflekt.plugin.analysis.models.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext

// [1]Reflekt.[2]|objects()/classes() or so on|
// [dotQualifiedExpressionNode] is [1]
fun findReflektInvokeArguments(dotQualifiedExpressionNode: ASTNode, binding: BindingContext): SubTypesToAnnotations? {
    val filteredChildren = dotQualifiedExpressionNode.filterChildren { n: ASTNode -> n.text in ReflektFunction.values().map { it.functionName } }

    val subtypes = HashSet<String>()
    val annotations = HashSet<String>()

    for (node in filteredChildren) {
        val callExpressionRoot = node.parents().firstOrNull { it.hasType(ElementType.CallExpression) } ?: continue
        when (node.text) {
            ReflektFunction.WITH_SUBTYPE.functionName -> callExpressionRoot.getFqNamesOfTypeArgument(binding).let { subtypes.addAll(it) }
            ReflektFunction.WITH_SUBTYPES.functionName -> callExpressionRoot.getFqNamesOfValueArguments(binding).let { subtypes.addAll(it) }
            ReflektFunction.WITH_ANNOTATIONS.functionName -> {
                callExpressionRoot.getFqNamesOfTypeArgument(binding).let { subtypes.addAll(it) }
                callExpressionRoot.getFqNamesOfValueArguments(binding).let { annotations.addAll(it) }
            }
            else -> error("Found an unexpected node text: ${node.text}")
        }
    }
    if (subtypes.isEmpty()) {
        return null
    }
    return SubTypesToAnnotations(subtypes, annotations)
}

fun findReflektInvokeArgumentsByExpressionPart(expression: KtExpression, binding: BindingContext): SubTypesToAnnotations? {
    // We use this function only for REFERENCE_EXPRESSION nodes. Any vertex of this type has the following structure:
    // CALL_EXPRESSION -> REFERENCE_EXPRESSION
    // We want to get the root of this expression (CALL_EXPRESSION)
    // For example, in our case we have the following expression: Reflekt.objects()
    // The root of objects() part is CALL_EXPRESSION
    val callExpressionRoot = expression.node.parents().first()

    // Any Reflekt invoke is something like this: ... [1]Reflekt.[2]|objects()/classes() or so on|....
    // We can find the [2] - callExpressionRoot
    // To find the [1] place we should go to the 1 level above from [2]
    // by finding the root of the nested DOT_QUALIFIED_EXPRESSION nodes
    return callExpressionRoot.findLastParentByType(ElementType.DotQualifiedExpression)?.let { node ->
        findReflektInvokeArguments(node, binding)
    }
}

fun findReflektFunctionInvokeArguments(dotQualifiedExpressionNode: ASTNode, binding: BindingContext): SignatureToAnnotations? {
    val filteredChildren = dotQualifiedExpressionNode.filterChildren { n: ASTNode -> n.text in ReflektFunction.values().map { it.functionName } }

    var signature: ParameterizedType? = null
    val annotations = HashSet<String>()

    for (node in filteredChildren) {
        val callExpressionRoot = node.parents().firstOrNull { it.hasType(ElementType.CallExpression) } ?: continue
        when (node.text) {
            ReflektFunction.WITH_ANNOTATIONS.functionName -> {
                callExpressionRoot.getFqNamesOfValueArguments(binding).let { annotations.addAll(it) }
                signature = callExpressionRoot.getTypeArguments().first().getParameterizedType(binding)
            }
            else -> error("Found an unexpected node text: ${node.text}")
        }
    }
    if (signature == null) {
        error("Failed to find function signature.")
    }
    return SignatureToAnnotations(signature, annotations)
}

fun findReflektFunctionInvokeArgumentsByExpressionPart(expression: KtExpression, binding: BindingContext): SignatureToAnnotations? {
    val callExpressionRoot = expression.node.parents().first()
    return callExpressionRoot.findLastParentByType(ElementType.DotQualifiedExpression)?.let { node ->
        findReflektFunctionInvokeArguments(node, binding)
    }
}

// [1]SmartReflekt.[2]|objects()/classes() or so on|
// [dotQualifiedExpressionNode] is [1]
fun findSmartReflektInvokeArguments(dotQualifiedExpressionNode: ASTNode, binding: BindingContext): SubTypesToFilters? {
    val filteredChildren = dotQualifiedExpressionNode.filterChildren { n: ASTNode ->
        (n.text in SmartReflektFunction.values().map { it.functionName } || n.text in ReflektEntity.values().map { it.entityType }) &&
            n.hasType(ElementType.ReferenceExpression)
    }
    var subtype: ParameterizedType? = null
    val filters = ArrayList<Lambda>()
    for (node in filteredChildren) {
        val childCallExpressionRoot = node.parents().firstOrNull { it.elementType.toString() == ElementType.CallExpression.value } ?: continue
        when (node.text) {
            SmartReflektFunction.FILTER.functionName -> {
                val body = childCallExpressionRoot.getLambdaBody()
                val parameters = childCallExpressionRoot.getLambdaParameters()
                filters.add(Lambda(body, parameters))
            }
            in ReflektEntity.values().map { it.entityType } -> {
                subtype = childCallExpressionRoot.getTypeArguments().first().getParameterizedType(binding)
            }
            else -> error("Found an unexpected node text: ${node.text}")
        }
    }
    if (filters.isEmpty()) {
        return null
    }
    val imports = (dotQualifiedExpressionNode.parents().first { it.hasType(ElementType.File) }.psi as KtFile).importDirectives.map {
        Import(it.importedFqName.toString(), it.text)
    }

    return SubTypesToFilters(subtype, filters, imports)
}

fun findSmartReflektInvokeArgumentsByExpressionPart(expression: KtExpression, binding: BindingContext): SubTypesToFilters? {
    val callExpressionRoot = expression.node.parents().first()
    // TODO: should we change it for functions? Or string (fqName) is ok?
    return callExpressionRoot.findLastParentByType(ElementType.DotQualifiedExpression)?.let { node ->
        findSmartReflektInvokeArguments(node, binding)
    }
}
