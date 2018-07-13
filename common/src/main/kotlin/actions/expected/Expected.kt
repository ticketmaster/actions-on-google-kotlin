package actions.expected

import actions.service.actionssdk.api.GoogleActionsV2Location

/*
 Classes that must have platform specific implementations.  May need refactoring into separate files
 */

expect class Date(timeStamp: String? = null) {
    fun toISOString(): String?

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


expect fun log(message: String, vararg optionalParameters: Any?)

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
    fun deserializeMap(json: String): MutableMap<String, Any?>
}


expect val MutableMap<String, Any?>.deliveryAddress: GoogleActionsV2Location?
val Test.delivery: GoogleActionsV2Location?
    get() = null

typealias Test = MutableMap<String, Any?>

