package actions.expected

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