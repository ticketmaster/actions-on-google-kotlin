package mp

/**
 * Shows the multi-platform implementation of a class by delegating to an instance of an expected class. Properties:
 *
 * * Keeps the class hierarchy shallow.
 * * Needs a platform-specific sub class for platform-specific members.
 * 
 * See [CommonClassDelegatingToSubClassHeader] for an alternative way to have a multi-platform implementation of a
 * class.
 */
class CommonClassDelegatingToInternalClassHeader {
    private val multiPlatformCode = InternalClassHeaderDelegatedTo(this)
    
    fun execute() {
        mutableMapOf<String,String>()
        multiPlatformCode.doIt()
    }

    override fun toString() = "An instance of the common class delegating to an internal expected class"
}
