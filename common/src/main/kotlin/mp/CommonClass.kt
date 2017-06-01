package mp

/**
 * A class that is the same on all platforms.
 */
class CommonClass {
    fun execute(task: CommonInterface) {
        task.doIt()
    }

    override fun toString() = "An instance of the common class"
}
