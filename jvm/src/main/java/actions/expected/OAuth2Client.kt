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

actual val MutableMap<String, Any?>.deliveryAddress: GoogleActionsV2Location?
    get() {
        val address = get("deliveryAddress")
        return if (address != null) {
            val jsonElement = gson.toJsonTree(address)
            gson.fromJson(jsonElement, GoogleActionsV2Location::class.java)
        } else {
            null
        }
    }
