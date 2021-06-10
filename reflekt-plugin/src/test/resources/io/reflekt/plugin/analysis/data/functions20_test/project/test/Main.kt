package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val functions = Reflekt.functions().withAnnotations<(Array<in FunTest1>) -> Array<out FunTest2>>(FunctionsTestAnnotation::class)
}