# Recipes for Multi-Platform Kotlin Modules

This project is a testbed for multi-platform Kotlin modules. It shows how you could implement modules that have code that is common for all platforms, and that may need platform-specific code for (some of) its types and functions.

Recipes for unit tests for common and platform-specific code are available. Execute the following command to run the Java tests. The test output can be found in `jvm/build/reports/tests/test/index.html`:

    ./gradlew :jvm:test
    
Execute the following command to run the JavaScript tests. The test output can be found in the build output:

    ./gradlew :js:test

## Current Issues

Not everything is working as it should as multi-platform support is still being developed. This is the list of known issues:

* For module `common` warnings are emitted about the use of deprecated type alias for `org.junit.Test`. The warning indicates `kotlin.test.Test` should be used instead, but this type is not available in `kotlin-test-common-*.jar`.
* IntelliJ IDEA support using stable plug-ins is broken. The experience should be better with an [EAP](https://discuss.kotlinlang.org/c/eap) or [development](https://github.com/jetbrains/kotlin#-installing-the-latest-kotlin-plugin) build, and using the associated Gradle plug-in and standard library from the [Kotlin development repository](https://bintray.com/kotlin/kotlin-dev/kotlin):
    * Lots of errors in the source files of modules depending on a multi-platform module.
    * You have to build and run the examples using the Gradle runner by enabling the following setting: Settings » Build, Execution, Deployment » Gradle » Runner » Delegate IDE build/run actions to gradle), or use the command line. To run the Java app:
  
            ./gradlew :jvm-app:run

        Or to run the JavaScript app:
  
            ./gradlew :js-app:compileKotlin2Js

        And open `js-app/JsApp.html`.
