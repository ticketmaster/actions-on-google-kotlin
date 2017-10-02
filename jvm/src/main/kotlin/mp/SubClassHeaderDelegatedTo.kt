package mp

actual class SubClassHeaderDelegatedTo : CommonClassDelegatingToSubClassHeader() {
    actual override fun doIt() {
        println("Delegated to a Java sub class")
    }
}
