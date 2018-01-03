package mp

/**
 * This class provides the multi-platform implementation for [CommonClassDelegatingToSubClassHeader].
 */
expect class SubClassHeaderDelegatedTo : CommonClassDelegatingToSubClassHeader {
    override fun doIt(): Unit
}
