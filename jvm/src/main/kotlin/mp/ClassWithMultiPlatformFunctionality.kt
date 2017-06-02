package mp

impl class ClassWithMultiPlatformFunctionality {
    impl fun commonFunctionality() {
        println("This is multi-platform functionality implemented in Java")
    }
    
    fun javaFunctionality() {
        println("This is Java-specific functionality")
    }

    override fun toString(): String {
        return "A Java class implementing common and providing Java-specific functionality"
    }
}
