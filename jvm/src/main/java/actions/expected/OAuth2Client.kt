package actions.expected

import actions.service.actionssdk.api.GoogleActionsV2Location

actual class OAuth2Client actual constructor(clientId: String) {
    actual fun verifyIdToken(idToken: IdToken): LoginTicket {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual class Date actual constructor(var timeStamp: String?) {

    actual fun toISOString(): String? {
        return ""
    }

}

fun <T> MutableMap<String, Any?>.deserializeValue(key: String, clazz: Class<T>): T? {
    val value = get(key)
    return if (value != null) {
        val jsonElement = Serializer.aogGson.toJsonTree(value)
        Serializer.aogGson.fromJson(jsonElement, clazz)
    } else {
        null
    }
}

actual val MutableMap<String, Any?>.deliveryAddress: GoogleActionsV2Location?
    get() = deserializeValue("deliveryAddress", GoogleActionsV2Location::class.java)
