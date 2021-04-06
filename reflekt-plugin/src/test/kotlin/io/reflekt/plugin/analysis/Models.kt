package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.models.ReflektUses.Companion.uses
import org.jetbrains.kotlin.psi.KtNamedDeclaration

typealias TypeUsesTest<K> = Map<K, Set<String>>
typealias ClassOrObjectUsesTest = TypeUsesTest<SubTypesToAnnotations>
typealias FunctionUsesTest = TypeUsesTest<SignatureToAnnotations>

data class ReflektUsesTest(
    val objects: Map<String, ClassOrObjectUsesTest> = HashMap(),
    val classes: Map<String, ClassOrObjectUsesTest> = HashMap(),
    val functions: Map<String, FunctionUsesTest> = HashMap()
)

data class SubTypesToFiltersTest(
    val subTypesToFilters: Set<SubTypesToFilters>
)

fun Set<SubTypesToFilters>.toSubTypesToFiltersTest() = SubTypesToFiltersTest(this)

private fun <K, V: KtNamedDeclaration> fromTypeUses(fileToUses: Map<String, TypeUses<K, V>>) : Map<String, TypeUsesTest<K>> {
    return fileToUses.mapValues {(_, uses) ->
        uses.mapValues { (_, items) ->
            items.map { it.fqName!!.toString() }.toSet()
        }
    }
}

fun ReflektUses.toTestUses() = ReflektUsesTest(
    objects = fromTypeUses(objects),
    classes = fromTypeUses(classes),
    functions = fromTypeUses(functions)
)
