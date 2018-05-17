package actions.service.actionssdk.conversation.question

import actions.service.actionssdk.api.GoogleActionsV2SignInValue
import actions.service.actionssdk.conversation.InputValueSpec
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.response.SoloQuestion


/** @public */
typealias SignInArgument = GoogleActionsV2SignInValue

/**
 * Hands the user off to a web sign in flow. App sign in and OAuth credentials
 * are set in the {@link https://console.actions.google.com|Actions Console}.
 * Retrieve the access token in subsequent intents using
 * {@link Access#token|conv.user.access.token}.
 *
 * @example
 * ```javascript
 *
 * // Actions SDK
 * const app = actionssdk()
 *
 * app.intent('actions.intent.MAIN', conv => {
 *   conv.ask(new SignIn())
 * })
 *
 * app.intent('actions.intent.SIGN_IN', (conv, input, signin) => {
 *   if (signin.status === 'OK') {
 *     const access = conv.user.access.token // possibly do something with access token
 *     conv.ask('Great, thanks for signing in! What do you want to do next?')
 *   } else {
 *     conv.ask(`I won't be able to save your data, but what do you want to do next?`)
 *   }
 * })
 *
 * // Dialogflow
 * const app = dialogflow()
 *
 * app.intent('actions.intent.MAIN', conv => {
 *   conv.ask(new SignIn())
 * })
 *
 * // Create a Dialogflow intent with the `actions_intent_SIGN_IN` event
 * app.intent('Get Signin', (conv, params, signin) => {
 *   if (signin.status === 'OK') {
 *     const access = conv.user.access.token // possibly do something with access token
 *     conv.ask('Great, thanks for signing in! What do you want to do next?')
 *   } else {
 *     conv.ask(`I won't be able to save your data, but what do you want to do next?`)
 *   }
 * })
 * ```
 *
 * @public
 */
class SignIn(context: String? = null) : SoloQuestion(IntentEnum.SIGN_IN) {
    /**
     * @param context The optional context why the app needs to ask the user to sign in, as a
     *     prefix of a prompt for user consent, e.g. "To track your exercise", or
     *     "To check your account balance".
     * @public
     */
    init {
        this._data(InputValueSpec.SignInValueSpec) {
            optContext = context
        }
    }
}
