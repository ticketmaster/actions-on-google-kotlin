package mp

/**
 * Generic extension function for a multi-platform class.
 */
// Copied from "kotlin.io.Closeable.kt"
fun <TCloseable: MpCloseable?, TResult> TCloseable.mpUse(block: (TCloseable) -> TResult): TResult {
    var closed = false
    try {
        return block(this)
    } catch (e: Exception) {
        closed = true
        try {
            this?.close()
        } catch (closeException: Exception) {
        }
        throw e
    } finally {
        if (!closed) {
            this?.close()
        }
    }
}

/**
 * Generic extension function for a common class.
 */
fun <TCommonClass: CommonClass?, TResult> TCommonClass.mpUse(block: (TCommonClass) -> TResult): TResult {
    return block(this)
}

/**
 * Generic extension function for a common interface.
 */
fun <TCommonInterface: CommonInterface?, TResult> TCommonInterface.mpUse(block: (TCommonInterface) -> TResult): TResult {
    return block(this)
}

/**
 * Generic extension function for a common object.
 */
fun <TCommonObject: CommonObject?, TResult> TCommonObject.mpUse(block: (TCommonObject) -> TResult): TResult {
    return block(this)
}
