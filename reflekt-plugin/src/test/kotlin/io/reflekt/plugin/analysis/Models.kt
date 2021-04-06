package io.reflekt.plugin.analysis

import io.reflekt.plugin.analysis.models.*
import io.reflekt.plugin.analysis.models.ReflektUses.Companion.uses
import org.jetbrains.kotlin.psi.KtNamedDeclaration

typealias TypeUsesTest<K> = Map<K, Set<String>>
typealias ClassOrObjectUsesTest = TypeUsesTest<SubTypesToAnnotations>
typealias FunctionUsesTest = TypeUsesTest<SignatureToAnnotations>

data class ReflektUsesTest(
    val objects: ClassOrObjectUsesTest = HashMap(),
    val classes: ClassOrObjectUsesTest = HashMap(),
    val functions: FunctionUsesTest = HashMap()
)

data class SubTypesToFiltersTest(
    val subTypesToFilters: Set<SubTypesToFilters>
)

fun Set<SubTypesToFilters>.toSubTypesToFiltersTest() = SubTypesToFiltersTest(this)

private fun <K, V: KtNamedDeclaration> fromTypeUses(uses: TypeUses<K, V>) : TypeUsesTest<K> {
    return uses.mapValues { (_, items) ->
        items.map { it.fqName!!.toString() }.toSet()
    }
}

// TODO: add files
fun ReflektUses.toTestUses() = ReflektUsesTest(
    objects = fromTypeUses(objects.uses),
    classes = fromTypeUses(classes.uses),
    functions = fromTypeUses(functions.uses)
)
