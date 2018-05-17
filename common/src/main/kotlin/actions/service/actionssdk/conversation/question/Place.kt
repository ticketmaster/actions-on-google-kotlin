package actions.service.actionssdk.conversation.question

import actions.DialogSpecData
import actions.service.actionssdk.api.GoogleActionsV2Location
import actions.service.actionssdk.conversation.DialogSpec
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.response.SoloQuestion


/** @public */
interface PlaceOptions {
    /**
     * This is the initial response by location sub-dialog.
     * For example: "Where do you want to get picked up?"
     * @public
     */
    var prompt: String

    /**
     * This is the context for seeking permissions.
     * For example: "To find a place to pick you up"
     * Prompt to user: "*To find a place to pick you up*, I just need to check your location.
     *     Can I get that from Google?".
     * @public
     */
    var context: String
}

typealias PlaceArgument = GoogleActionsV2Location?

/**
 * Asks user to provide a geo-located place, possibly using contextual information,
 * like a store near the user's location or a contact's address.
 *
 * Developer provides custom text prompts to tailor the request handled by Google.
 *
 * @example
 * ```javascript
 *
 * // Actions SDK
 * const app = actionssdk()
 *
 * app.intent('actions.intent.MAIN', conv => {
 *   conv.ask(new Place({
 *     prompt: 'Where do you want to get picked up?',
 *     context: 'To find a place to pick you up',
 *   }))
 * })
 *
 * app.intent('actions.intent.PLACE', (conv, input, place, status) => {
 *   if (place) {
 *     conv.close(`Ah, I see. You want to get picked up at ${place.formattedAddress}`)
 *   } else {
 *     // Possibly do something with status
 *     conv.close(`Sorry, I couldn't find where you want to get picked up`)
 *   }
 * })
 *
 * // Dialogflow
 * const app = dialogflow()
 *
 * app.intent('Default Welcome Intent', conv => {
 *   conv.ask(new Place({
 *     prompt: 'Where do you want to get picked up?',
 *     context: 'To find a place to pick you up',
 *   }))
 * })
 *
 * // Create a Dialogflow intent with the `actions_intent_PLACE` event
 * app.intent('Get Place', (conv, params, place, status) => {
 *   if (place) {
 *     conv.close(`Ah, I see. You want to get picked up at ${place.formattedAddress}`)
 *   } else {
 *     // Possibly do something with status
 *     conv.close(`Sorry, I couldn't find where you want to get picked up`)
 *   }
 * })
 * ```
 *
 * @public
 */
class Place(options: PlaceOptions) : SoloQuestion(IntentEnum.PLACE) {
    /**
     * @param options Place options
     * @public
     */
    init {
        val extension = DialogSpecData(
                `@type` = DialogSpec.PlaceDialogSpec.value,
                permissionContext = options.context,
                requestPrompt = options.prompt)

        this._data(actions.service.actionssdk.conversation.InputValueSpec.PlaceValueSpec) {
            dialogSpec = extension

        }
    }
}
