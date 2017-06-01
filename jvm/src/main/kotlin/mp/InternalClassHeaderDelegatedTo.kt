package mp

impl internal class InternalClassHeaderDelegatedTo(private val owner: CommonClassDelegatingToInternalClassHeader) {
    impl fun doIt() {
        println("Internal class impl in Java for: $owner")
    }
}
