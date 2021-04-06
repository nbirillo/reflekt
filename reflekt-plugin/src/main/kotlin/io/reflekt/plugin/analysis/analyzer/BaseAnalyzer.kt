package io.reflekt.plugin.analysis.analyzer

import io.reflekt.plugin.analysis.models.ReflektInstances
import io.reflekt.plugin.analysis.processor.Processor
import io.reflekt.plugin.analysis.processor.instances.*
import io.reflekt.plugin.analysis.psi.visit
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

open class BaseAnalyzer(open val ktFiles: Set<KtFile>, open val binding: BindingContext) {
    fun instances(): ReflektInstances {
        val processors = setOf(ClassInstancesProcessor(binding), ObjectInstancesProcessor(binding), FunctionInstancesProcessor(binding))
        ktFiles.processFiles(processors)
        return ReflektInstances.createByProcessors(processors)
    }

    protected fun Set<KtFile>.processFiles(processors: Set<Processor<*>>) {
        this.forEach { file ->
            file.visit(file.packageFqName.asString(), processors)
        }
    }
}
