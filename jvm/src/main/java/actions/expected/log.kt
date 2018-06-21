package actions.expected

import java.util.logging.Logger

val logger = Logger.getAnonymousLogger()

actual fun log(message: String, vararg optionalParameters: Any?) {
    System.out.println(message)
    logger.info(message + if (optionalParameters != null) " :$optionalParameters" else "")
}