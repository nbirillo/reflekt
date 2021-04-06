package io.reflekt.plugin.analysis.psi

import io.reflekt.plugin.analysis.processor.Processor
import org.jetbrains.kotlin.psi.*


// TODO: rename??
fun KtElement.visit(filePath: String, processors: Set<Processor<*>>) {
    acceptChildren(object : KtDefaultVisitor() {
        override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
            processors.filter { it.shouldRunOn(declaration) }.forEach { it.process(filePath, declaration) }
            super.visitObjectDeclaration(declaration)
        }

        override fun visitClass(klass: KtClass) {
            processors.filter { it.shouldRunOn(klass) }.forEach { it.process(filePath, klass) }
            super.visitClass(klass)
        }

        override fun visitNamedFunction(function: KtNamedFunction) {
            processors.filter { it.shouldRunOn(function) }.forEach { it.process(filePath, function) }
            super.visitNamedFunction(function)
        }

        override fun visitReferenceExpression(expression: KtReferenceExpression) {
            processors.filter { it.shouldRunOn(expression) }.forEach { it.process(filePath, expression) }
            super.visitReferenceExpression(expression)
        }
    })
}
