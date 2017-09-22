package mp

impl internal class InternalClassHeaderDelegatedTo impl constructor(private val owner: CommonClassDelegatingToInternalClassHeader) {
    impl fun doIt() {
        println("Internal class impl in JavaScript for: $owner")
    }
}
