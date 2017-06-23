# Recipes for Multi-Platform Kotlin Modules

This project is a testbed for multi-platform Kotlin modules. It shows how you could implement modules that have code that is common for all platforms, and that may need platform-specific code for (some of) its types and functions.

Recipes for unit tests for common and platform-specific code are available. To run the Java tests:

    ./gradlew :jvm:test
    
To run the JavaScript tests:

    ./gradlew :js:compileTestKotlin2Js
    
and open `js/JsTest.html`.

## Current Issues

Not everything is working as it should as multi-platform support is still being developed. This is the list of known issues:

* (Generic) extension functions on header types is not working in Java yet. An [issue](https://youtrack.jetbrains.com/issue/KT-18257) has been created for it.
* IntelliJ IDEA support using stable plug-ins is broken. The experience should be better with an [EAP](https://discuss.kotlinlang.org/c/eap) or [development](https://github.com/jetbrains/kotlin#-installing-the-latest-kotlin-plugin) build, and using the associated Gradle plug-in and standard library from the [Kotlin development repository](https://bintray.com/kotlin/kotlin-dev/kotlin):
    * Lots of errors in the source files of modules depending on a multi-platform module.
    * You have to build and run the examples using the Gradle runner by enabling the following setting: Settings » Build, Execution, Deployment » Gradle » Runner » Delegate IDE build/run actions to gradle), or use the command line. To run the Java app:
  
            ./gradlew :jvm-app:run

        Or to run the JavaScript app:
  
            ./gradlew :js-app:compileKotlin2Js

        And open `js-app/JsApp.html`.
