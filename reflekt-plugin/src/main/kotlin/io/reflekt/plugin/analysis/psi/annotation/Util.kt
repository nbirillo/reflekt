package io.reflekt.plugin.analysis.psi.annotation

import io.reflekt.plugin.analysis.psi.fqName
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.ForceResolveUtil

// FIXME problems with external libraries
fun KtAnnotationEntry.getDescriptor(context: BindingContext) = context[BindingContext.ANNOTATION, this]!!.forced()

/** Forcefully resolves all contents inside KtElement or Descriptor */
fun <T> T.forced(): T = ForceResolveUtil.forceResolveAllContents(this)

internal val AnnotationDescriptor.qualifiedName: String?
    get() = this.fqName?.asString()

fun KtAnnotated.getAnnotations(context: BindingContext, annotations: Set<String>) = annotationEntries.filter {
    it.fqName(context) in annotations }.toSet()

