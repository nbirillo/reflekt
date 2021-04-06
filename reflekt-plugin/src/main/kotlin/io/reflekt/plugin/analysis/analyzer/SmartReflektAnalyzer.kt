package io.reflekt.plugin.analysis.analyzer

import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext

class SmartReflektAnalyzer(override val ktFiles: Set<KtFile>, override val binding: BindingContext) : BaseAnalyzer(ktFiles, binding)
