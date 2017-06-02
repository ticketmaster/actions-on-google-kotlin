package mp

impl class ClassWithMultiPlatformFunctionality {
    impl fun commonFunctionality() {
        println("This is multi-platform functionality implemented in JavaScript")
    }
    
    fun javascriptFunctionality() {
        println("This is JavaScript-specific functionality")
    }

    override fun toString(): String {
        return "A JavaScript class implementing common and providing JavaScript-specific functionality"
    }
}
