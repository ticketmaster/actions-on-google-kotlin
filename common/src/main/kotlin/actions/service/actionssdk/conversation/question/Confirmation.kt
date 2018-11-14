package actions.service.actionssdk.conversation.question

import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.response.SoloQuestion


typealias ConfirmationArgument = Boolean

/**
 * Asks user for a confirmation.
 *
 * @example
 * ```javascript
 *
 * // Actions SDK
 * const app = actionssdk()
 *
 * app.intent('actions.intent.MAIN', conv => {
 *   conv.ask(new Confirmation('Are you sure you want to do that?'))
 * })
 *
 * app.intent('actions.intent.CONFIRMATION', (conv, input, confirmation) => {
 *   if (confirmation) {
 *     conv.close(`Great! I'm glad you want to do it!`)
 *   } else {
 *     conv.close(`That's okay. Let's not do it now.`)
 *   }
 * })
 *
 * // Dialogflow
 * const app = dialogflow()
 *
 * app.intent('Default Welcome Intent', conv => {
 *   conv.ask(new Confirmation('Are you sure you want to do that?'))
 * })
 *
 * // Create a Dialogflow intent with the `actions_intent_CONFIRMATION` event
 * app.intent('Get Confirmation', (conv, input, confirmation) => {
 *   if (confirmation) {
 *     conv.close(`Great! I'm glad you want to do it!`)
 *   } else {
 *     conv.close(`That's okay. Let's not do it now.`)
 *   }
 * })
 * ```
 *
 * @public
 */
class Confirmation(text: String): SoloQuestion(IntentEnum.CONFIRMATION) {

    /**
     * @param text The confirmation prompt presented to the user to
     *     query for an affirmative or negative response.
     * @public
     */
    init {
        this._data(actions.service.actionssdk.conversation.InputValueSpec.ConfirmationValueSpec) {
            dialogSpec {
                requestConfirmationText = text
            }
        }
    }
}
