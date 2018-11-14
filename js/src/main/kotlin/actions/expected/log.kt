package actions.expected

actual fun log(message: String, vararg optionalParameters: Any?) {
    console.log(message)
}