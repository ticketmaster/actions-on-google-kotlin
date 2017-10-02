package mp

actual internal class InternalClassHeaderDelegatedTo actual constructor(private val owner: CommonClassDelegatingToInternalClassHeader) {
    actual fun doIt() {
        println("Internal actual class in JavaScript for: $owner")
    }
}
