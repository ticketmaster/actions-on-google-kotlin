package mp

/**
 * An object that is the same on all platforms.
 */
object CommonObject {
    fun execute(task: CommonInterface) {
        task.doIt()
    }

    override fun toString() = "The common object"
}
