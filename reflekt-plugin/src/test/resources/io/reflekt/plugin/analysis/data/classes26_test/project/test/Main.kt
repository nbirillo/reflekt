package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withSubTypes(B1::class).withAnnotations<B1>(FirstAnnotationTest::class, SecondAnnotationTest::class)
}
