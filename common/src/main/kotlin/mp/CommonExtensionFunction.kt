package mp

/**
 * Extension function for a multi-platform class.
 */
fun MpCloseable.printThis() = println(this)

/**
 * Extension function for a common class.
 */
fun CommonClass.printThis() = println(this)

/**
 * Extension function for a common interface.
 */
fun CommonInterface.printThis() = println(this)

/**
 * Extension function for a common object.
 */
fun CommonObject.printThis() = println(this)
