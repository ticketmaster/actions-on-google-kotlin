# Recipes for multi-platform Kotlin modules

This project is a testbed for multi-platform Kotlin modules. It shows
how you could implement modules that need platform-specific code for
(some of) its types and functions.

Current issues:

* (Generic) extension functions on header types is not working in Java
yet.
* IntelliJ IDEA support is broken. You have to run the examples from the
  command line:
  
    ./gradlew :jvm-client:run
