package io.reflekt.test

import io.reflekt.Reflekt

fun main() {
    val classes = Reflekt.classes().withAnnotations<BInterfaceTest>(SecondAnnotationTest::class, FirstAnnotationTest::class).withSubTypes(BInterfaceTest::class)
}
