package mp

object CommonObject {
    fun execute(task: CommonInterface) {
        task.doIt()
    }

    override fun toString() = "The common object"
}
