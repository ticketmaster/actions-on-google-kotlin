package mp

/**
 * Shows the multi-platform implementation of a class by delegating to an expected sub class. Properties:
 * 
 * * Adds artificial types to the class hierarchy.
 * * Allows for platform-specific members in the multi-platform sub class.
 * 
 * See [CommonClassDelegatingToInternalClassHeader] for an alternative way to have a multi-platform implementation of a
 * class.
 */
abstract class CommonClassDelegatingToSubClassHeader protected constructor() {
    fun execute() {
        doIt()
    }
    
    protected abstract fun doIt(): Unit
}
