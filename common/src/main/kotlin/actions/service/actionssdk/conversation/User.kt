package actions.service.actionssdk.conversation

import actions.expected.*
import actions.expected.Serializer.serialize
import actions.service.actionssdk.api.GoogleActionsV2PackageEntitlement
import actions.service.actionssdk.api.GoogleActionsV2User
import actions.service.actionssdk.api.GoogleActionsV2UserPermissions
import actions.service.actionssdk.api.GoogleActionsV2UserProfile


data class Last(
        /**
         * Timestamp for the last access from the user.
         * Undefined if never seen.
         * @public
         */
        var seen: String? = null) {

    /** @hidden */
    constructor(user: GoogleActionsV2User? = null) :
            this(user?.lastSeen)

}

data class Name(
        /**
         * User's display name.
         * @public
         */
        var display: String? = null,

        /**
         * User's family name.
         * @public
         */
        var family: String? = null,

        /**
         * User's given name.
         * @public
         */
        var given: String? = null) {

    constructor(profile: GoogleActionsV2UserProfile? = null) :
            this(display = profile?.displayName,
                    family = profile?.familyName,
                    given = profile?.givenName)
}

data class Access(
        /**
         * Unique Oauth2 token. Only available with account linking.
         * @public
         */
        var token: String? = null) {

    constructor(user: GoogleActionsV2User? = null) :
            this(token = user?.accessToken)
}

data class Profile(
        /**
         * Gets the Profile Payload object encoded in {@link Profile#token|conv.user.profile.token}.
         * Only retrievable with "Google Sign In" linking type set up for account linking in the console.
         *
         * To access just the email in the payload, you can also use {@link User#email|conv.user.email}.
         *
         * @example
         * ```javascript
         *
         * // Dialogflow
         * const app = dialogflow({
         *   clientId: CLIENT_ID,
         * })
         *
         * app.intent('Default Welcome Intent', conv => {
         *   conv.ask(new SignIn('To get your account details'))
         * })
         *
         * // Create a Dialogflow intent with the `actions_intent_SIGN_IN` event
         * app.intent('Get Signin', (conv, params, signin) => {
         *   if (signin.status === 'OK') {
         *     const payload = conv.user.profile.payload
         *     conv.ask(`I got your account details. What do you want to do next?`)
         *   } else {
         *     conv.ask(`I won't be able to save your data, but what do you want to do next?`)
         *   }
         * })
         *
         * // Actions SDK
         * const app = actionssdk({
         *   clientId: CLIENT_ID,
         * })
         *
         * app.intent('actions.intent.MAIN', conv => {
         *   conv.ask(new SignIn('To get your account details'))
         * })
         *
         * app.intent('actions.intent.SIGN_IN', (conv, input, signin) => {
         *   if (signin.status === 'OK') {
         *     const payload = conv.user.profile.payload
         *     conv.ask(`I got your account details. What do you want to do next?`)
         *   } else {
         *     conv.ask(`I won't be able to save your data, but what do you want to do next?`)
         *   }
         * })
         * ```
         *
         * @public
         */
        var payload: TokenPayload? = null,

        /**
         * The `user.idToken` retrieved from account linking.
         * Only retrievable with "Google Sign In" linking type set up for account linking in the console.
         * @public
         */
        var token: String? = null) {

    constructor(user: GoogleActionsV2User? = null) :
            this(token = user?.idToken)

    /** @hidden */
    fun async_verify(client: OAuth2Client, id: String): TokenPayload? {
        val login = client.verifyIdToken(
                IdToken(
                        idToken = this.token,
                        audience = id))

        this.payload = login.getPayload()
        return this.payload
    }
}

data class User<TUserStorage>(
        /**
         * The data persistent across sessions in JSON format.
         * It exists in the same context as `conv.user.id`
         *
         * @example
         * ```javascript
         *
         * // Actions SDK
         * app.intent('actions.intent.MAIN', conv => {
         *   conv.user.storage.someProperty = 'someValue'
         * })
         *
         * // Dialogflow
         * app.intent('Default Welcome Intent', conv => {
         *   conv.user.storage.someProperty = 'someValue'
         * })
         * ```
         *
         * @public
         */
        var storage: TUserStorage? = null,

        /**
         * Random string ID for Google user.
         * @public
         */
        var id: String? = null,

        /**
         * The user locale. String represents the regional language
         * information of the user set in their Assistant settings.
         * For example, 'en-US' represents US English.
         * @public
         */
        var locale: String? = null,

        /** @public */
        var last: Last? = null,

        /** @public */
        var permissions: MutableList<GoogleActionsV2UserPermissions>? = null,

        /**
         * User's permissioned name info.
         * Properties will be undefined if not request with {@link Permission|conv.ask(new Permission)}
         * @public
         */
        var name: Name? = null,

        /**
         * The list of all digital goods that your user purchased from
         * your published Android apps. To enable this feature, see the instructions
         * in the (documentation)[https://developers.google.com/actions/identity/digital-goods].
         * @public
         */
        var entitlements: MutableList<GoogleActionsV2PackageEntitlement>? = null,

        /** @public */
        var access: Access? = null,

        /** @public */
        var profile: Profile? = null,

        /**
         * Gets the user profile email.
         * Only retrievable with "Google Sign In" linking type set up for account linking in the console.
         *
         * See {@link Profile#payload|conv.user.profile.payload} for all the payload properties.
         *
         * @example
         * ```javascript
         *
         * // Dialogflow
         * const app = dialogflow({
         *   clientId: CLIENT_ID,
         * })
         *
         * app.intent('Default Welcome Intent', conv => {
         *   conv.ask(new SignIn('To get your account details'))
         * })
         *
         * // Create a Dialogflow intent with the `actions_intent_SIGN_IN` event
         * app.intent('Get Signin', (conv, params, signin) => {
         *   if (signin.status === 'OK') {
         *     const email = conv.user.email
         *     conv.ask(`I got your email as ${email}. What do you want to do next?`)
         *   } else {
         *     conv.ask(`I won't be able to save your data, but what do you want to next?`)
         *   }
         * })
         *
         * // Actions SDK
         * const app = actionssdk({
         *   clientId: CLIENT_ID,
         * })
         *
         * app.intent('actions.intent.MAIN', conv => {
         *   conv.ask(new SignIn('To get your account details'))
         * })
         *
         * app.intent('actions.intent.SIGN_IN', (conv, input, signin) => {
         *   if (signin.status === 'OK') {
         *     const email = conv.user.email
         *     conv.ask(`I got your email as ${email}. What do you want to do next?`)
         *   } else {
         *     conv.ask(`I won't be able to save your data, but what do you want to next?`)
         *   }
         * })
         * ```
         *
         * @public
         */
        var email: String? = null) {

    /** @hidden */
    constructor(raw: GoogleActionsV2User? = null, initial: TUserStorage? = null) :
            this(id = raw?.userId,
                    locale = raw?.locale,
                    permissions = raw?.permissions ?: mutableListOf(),
                    last = Last(raw),
                    name = Name(profile = raw?.profile ?: GoogleActionsV2UserProfile()),
                    entitlements = raw?.packageEntitlements,
                    access = Access(raw),
                    profile = Profile(raw)
            ) {
        //TODO("handle user storage")
//        this.storage = if (this.raw ? JSON.parse(userStorage).data : (initial || {}),
    }

    /** @hidden */
    fun _serialize(): String? {
        return if (this.storage == null)
        """{"data":{}}"""
        else
            serialize(UserStorage(this.storage))
    }

    /** @hidden */
    fun /* async */ _verifyProfile(client: OAuth2Client, id: String): TokenPayload? {
        val payload = this.profile?.async_verify(client, id)
        this.email = payload?.email
        return payload
    }
}

/**
 * JS lib uses dynamic type.  Examine this closer to see if a Map<String, Any?> works as a type
 */
data class UserStorage<TStorage>(var data: TStorage? = null)
