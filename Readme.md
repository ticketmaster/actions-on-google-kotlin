# Recipes for multi-platform Kotlin modules

This project is a testbed for multi-platform Kotlin modules. It shows how you could implement modules that have code that is common for all platforms, and that may need platform-specific code for (some of) its types and functions.

Current issues:

* (Generic) extension functions on header types is not working in Java yet.
* IntelliJ IDEA support is broken. You have to run the examples from the command line. To run the Java app:
  
        ./gradlew :jvm-app:run

    Or to run the JavaScript app:
  
        ./gradlew :js-app:compileKotlin2Js

    And open `js-app/JsApp.html`.
