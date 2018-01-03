package mp

actual class ClassWithMultiPlatformFunctionality {
    actual fun commonFunctionality() {
        println("This is multi-platform functionality implemented in Java")
    }
    
    fun javaFunctionality() {
        println("This is Java-specific functionality")
    }

    override fun toString(): String {
        return "A Java class implementing common and providing Java-specific functionality"
    }
}
