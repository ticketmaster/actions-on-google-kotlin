package mp

/**
 * An interface that is multi-platform, i.e. the implementation differs per platform.
 */
expect interface MpCloseable {
    fun close()
}
