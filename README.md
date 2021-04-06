[![JetBrains Research](https://jb.gg/badges/research.svg)](https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub)
![Gradle Build](https://github.com/nbirillo/reflekt/workflows/Gradle%20Build/badge.svg?branch=master)

# Reflekt

Reflekt is a compile-time reflection library that leverages the flows of the standard reflection approach.

Instead of relying on JVM reflection, Reflekt performs compile-time resolution of reflection
queries using Kotlin compiler analysis, providing a convenient reflection API without actually
using reflection.

Reflekt is a joint project of [JetBrains Research](https://research.jetbrains.org/) and the [Kotless](https://github.com/JetBrains/kotless) team. 
The main reason for its creation was the necessity of GraalVM support in modern Java applications, 
especially on Serverless workloads. With the help of the Reflekt project, Kotless will be able to provide access to GraalVM to 
users of historically reflection-based frameworks such as Spring or their own Kotless DSL.

Restrictions. Reflekt analyses only `.kt` files in the project and in the libraries, 
uses Kotlin `1.4.20`.
___

## Table of contents

- [Getting started](#getting-started)
- [Local start](#local-start)
- [Supported features](#supported-features)
- [Examples](#examples)
- [Want to know more?](#want-to-know-more?)

## Getting started

Reflekt uses Gradle. If you have a Gradle project, you only need to do three things.

Firstly, set up the Reflekt plugin. You need to apply the plugin. In the `build.gradle.kts` file, add the following lines in the `plugins` section:

```kotlin
plugins {
    // Version of Kotlin should be 1.4.20+
    kotlin("jvm") version "1.4.20" apply true

    id("io.reflekt") version "0.1.0" apply true
}
```

Secondly, add the Reflekt DSL as a library to your application. In the `build.gradle.kts` file, add the following lines in the `dependencies` section:

```kotlin
dependencies {
    // The version here and the version in the plugins sections should be equal
    implementation("io.reflekt", "io.reflekt.dsl", "0.1.0")
    
    // Necessary for this example
    compileOnly("io.kotless", "kotless-dsl", "0.1.6")
}
```

Thirdly, customize the Reflekt plugin. In the `build.gradle.kts` file, add the `reflekt` object:

```kotlin
reflekt {
    // Enable or disable Reflekt plugin
    enabled = true
    // List of external libraries for dependencies search
    // Use only DependencyHandlers which have canBeResolve = True
    // Note: Reflekt works only with kt files from libraries
    librariesToIntrospect = listOf("io.kotless:kotless-dsl:0.1.6")
}
```

_Please note that the `librariesToIntrospect` argument should contain only the dependencies 
that you use in the `dependencies` section. These dependencies have to be
---implemented in Kotlin language._ 

To avoid some bugs, please add the following compilation settings for Java and Kotlin in the `build.gradle.kts` file:

```kotlin
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
    languageVersion = "1.4"
    apiVersion = "1.4"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
    languageVersion = "1.4"
    apiVersion = "1.4"
}
```

This gives you access to [Reflekt DSL](./reflekt-dsl/src/main/kotlin/io/reflekt/Reflekt.kt) interfaces.

Please note that the current version of Reflekt does not support 
incremental compilation process. Please disable incremental compilation in your 
project by changing the `gradle.properties` file:

```kotlin
kotlin.incremental=false
```

Now you can use the Reflekt plugin to find objects, classes, and functions in your project:

```kotlin
val objects = Reflekt.objects().withSubType<AInterface>().withAnnotations<AInterface>(FirstAnnotation::class, SecondAnnotation::class).toList()

val classes = Reflekt.classes().withSubType<BInterface>().toSet()

val functions = Reflekt.functions().withAnnotations<() -> Unit>().toList()
```

## Local start

You can use `any` unpublished Reflekt version. You should do the following steps:

- Clone the Reflekt project (the official repo, any fork, branch, etc.).
- Build the project `./gradlew build`
- Publish the project to maven local `./gradlew publishToMavenLocal`
- Add `mavenLocal()` in the repositories section in the `build.gradle.kts` file:

```kotlin
repositories {
    mavenLocal()
}
```

Please note that if you build a Reflekt version with a customized 
[version](https://github.com/JetBrains-Research/reflekt/blob/master/build.gradle.kts#L4) number,
write this version in the plugins and dependencies sections.

## Supported features

- [x] Compile-time reflection by Reflekt DSL 
  for `multi-module` projects:
    - [x] project's files
    - [x] external libraries
- [ ] Incremental compilation process
- [ ] Compile-time reflection by custom users' filters for `one-module` projects (for objects, classes, and functions)
- [ ] Code generation

## Examples

Any explanation becomes much better with a proper example.

In the repository's [examples folder](./examples), you can find an example project 
that uses the Reflekt plugin --by DSL.

You can also find many examples of objects, functions, and classes ---search in 
the [test](./reflekt-plugin/src/test) folder.

## Want to know more?

The Reflekt code itself is widely documented, 
and you can take a look into its interfaces to get to know Reflekt better.

You may ask questions and participate in discussions on repository [issues](https://github.com/JetBrains-Research/reflekt/issues).
