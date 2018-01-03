package mp

/**
 * This class provides the multi-platform implementation for [CommonClassDelegatingToInternalClassHeader].
 */
expect internal class InternalClassHeaderDelegatedTo {
    constructor(owner: CommonClassDelegatingToInternalClassHeader)
    
    fun doIt(): Unit
}
