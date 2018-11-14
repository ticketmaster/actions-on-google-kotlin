package actions.expected

import actions.service.actionssdk.api.GoogleActionsV2Location

actual class OAuth2Client actual constructor(clientId: String) {
    actual fun verifyIdToken(idToken: IdToken): LoginTicket {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

actual class Date actual constructor(timeStamp: String?) {
    actual fun toISOString(): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

actual val MutableMap<String, Any?>.deliveryAddress: GoogleActionsV2Location?
    get() {
        val address = get("deliveryAddress")
        return if (address != null) {
            null
        } else {
            null
        }
    }