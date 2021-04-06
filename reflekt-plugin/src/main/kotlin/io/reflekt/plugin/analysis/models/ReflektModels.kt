package io.reflekt.plugin.analysis.models

import io.reflekt.plugin.analysis.processor.invokes.*
import io.reflekt.plugin.analysis.processor.uses.*
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

/*
 * If the function [withAnnotations] is called without subtypes then [subTypes] is [setOf(Any::class::qualifiedName)]
 * If the function [withSubTypes] is called without annotations then [annotations] is empty
 */
data class SubTypesToAnnotations(
    val subTypes: Set<String> = emptySet(),
    val annotations: Set<String> = emptySet()
)

/* Recursive structure representing type that may have parameters.
 * For example, Map<Pair<Int, String>, Int> is represented in the following way:
 * ParameterizedType(
 *     "kotlin.collections.Map",
 *     listOf(
 *         ParameterizedType(
 *             "kotlin.Pair",
 *             listOf(
 *                 ParameterizedType("kotlin.Int", emptyList()),
 *                 ParameterizedType("kotlin.String", emptyList())
 *             )
 *         ),
 *         ParameterizedType("kotlin.Int", emptyList())
 *     )
 * )
 */
data class ParameterizedType(
    val fqName: String,
    val parameters: List<ParameterizedType> = emptyList()
)

data class SignatureToAnnotations(
    val signature: ParameterizedType, // kotlin.FunctionN< ... >
    val annotations: Set<String> = emptySet()
)

typealias ClassOrObjectInvokes = MutableSet<SubTypesToAnnotations>
typealias FunctionInvokes = MutableSet<SignatureToAnnotations>

data class ReflektInvokes(
    val objects: Map<String, ClassOrObjectInvokes> = HashMap(),
    val classes: Map<String, ClassOrObjectInvokes> = HashMap(),
    val functions: Map<String, FunctionInvokes> = HashMap()
) {
    companion object{
        fun createByProcessors(processors: Set<BaseInvokesProcessor<*>>) = ReflektInvokes(
            objects = processors.mapNotNull { it as? ObjectInvokesProcessor }.first().fileToInvokes,
            classes = processors.mapNotNull { it as? ClassInvokesProcessor }.first().fileToInvokes,
            functions = processors.mapNotNull { it as? FunctionInvokesProcessor }.first().fileToInvokes
        )

        val <T> Map<String, MutableSet<T>>.invokes: MutableSet<T>
            get() = this.values.flatten().toMutableSet()
    }
}

typealias TypeUses<K, V> = Map<K, MutableList<V>>
typealias ClassOrObjectUses = TypeUses<SubTypesToAnnotations, KtClassOrObject>
typealias FunctionUses = TypeUses<SignatureToAnnotations, KtNamedFunction>

fun ClassOrObjectUses.toSubTypesToFqNamesMap(): Map<Set<String>, MutableList<KtClassOrObject>> {
    return this.map { it.key.subTypes to it.value }.toMap()
}

/*
 * Store a set of qualified names that match the conditions for each item from [ReflektInvokes]
 */
data class ReflektUses(
    val objects: Map<String, ClassOrObjectUses> = HashMap(),
    val classes: Map<String, ClassOrObjectUses> = HashMap(),
    val functions: Map<String, FunctionUses> = HashMap()
) {
    companion object{
        private fun <K, V> TypeUses<K, V>.filterEmptyUsesHelper() = this.filter { it.value.isNotEmpty() }

        private fun <K, V> Map<String, TypeUses<K, V>>.filterEmptyUses() = this.filter { it.value.filterEmptyUsesHelper().isNotEmpty() }

        fun createByProcessors(processors: Set<BaseUsesProcessor<*>>) = ReflektUses(
            objects = processors.mapNotNull { it as? ObjectUsesProcessor }.first().fileToUses.filterEmptyUses(),
            classes = processors.mapNotNull { it as? ClassUsesProcessor }.first().fileToUses.filterEmptyUses(),
            functions = processors.mapNotNull { it as? FunctionUsesProcessor }.first().fileToUses.filterEmptyUses()
        )

        // TypeUses<K, V> = Map<K, MutableList<V>>
        val <K, V> Map<String, TypeUses<K, V>>.uses: TypeUses<K, V>
            get() = run {
                val uses: MutableMap<K, MutableList<V>> = HashMap()
                this.values.forEach { u ->
                    u.forEach { (k, v) ->
                        uses.getOrPut(k) { ArrayList() }.addAll(v)
                    }
                }
                uses
            }
    }
}
