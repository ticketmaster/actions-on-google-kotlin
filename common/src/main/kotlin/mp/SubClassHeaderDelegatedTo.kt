package mp

/**
 * This class provides the multi-platform implementation for [CommonClassDelegatingToSubClassHeader].
 */
header class SubClassHeaderDelegatedTo : CommonClassDelegatingToSubClassHeader() {
    override fun doIt(): Unit
}
