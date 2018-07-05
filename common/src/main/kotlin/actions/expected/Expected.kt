package actions.expected

/*
 Classes that must have platform specific implementations.  May need refactoring into separate files
 */

data class Date(var timeStamp: String? = null) {

}


data class TokenPayload(val accessToken: String? = null,
                        val email: String? = null)

data class LoginTicket(var tokenPayload: TokenPayload? = null) {
   fun getPayload(): TokenPayload? {
       return null
   }
}

data class IdToken(var idToken: String? = null,
                   var audience: String? = null)

expect class OAuth2Client(clientId: String) {
   fun verifyIdToken(idToken: IdToken): LoginTicket
}



expect fun <T> deserialize(json: String?): T?


expect fun log(message: String, vararg optionalParameters: Any? )

fun info(message: String) {

}

fun warn(message: String) {

}

fun error(message: String, exception: Exception) {

}


data class ConversationTokenData(var data: Any? = null)


expect object Serializer {
    fun stringifyConversationToken(data: Any?): ConversationTokenData
    fun serialize(any: Any?): String?
    fun <T>deserialize(json: String): T
}

