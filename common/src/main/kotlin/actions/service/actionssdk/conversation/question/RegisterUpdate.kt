package actions.service.actionssdk.conversation.question

import actions.service.actionssdk.api.GoogleActionsV2Argument
import actions.service.actionssdk.api.GoogleActionsV2RegisterUpdateValue
import actions.service.actionssdk.api.GoogleActionsV2TriggerContextTimeContextFrequency
import actions.service.actionssdk.conversation.InputValueSpec
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.response.SoloQuestion


typealias RegisterUpdateArgument = GoogleActionsV2RegisterUpdateValue

interface RegisterUpdateOptions {
    /**
     * The Dialogflow/Actions SDK intent name to be triggered when the update is received.
     * @public
     */
    var intent: String

    /**
     * The necessary arguments to fulfill the intent triggered on update.
     * These can be retrieved using {@link Arguments#get|conv.arguments.get}.
     * @public
     */
    var arguments: MutableList<GoogleActionsV2Argument>

    /**
     * The high-level frequency of the recurring update.
     * @public
     */
    var frequency: GoogleActionsV2TriggerContextTimeContextFrequency
}

/**
 * Requests the user to register for daily updates.
 *
 * @example
 * ```javascript
 *
 * // Actions SDK
 * const app = actionssdk()
 *
 * app.intent('actions.intent.MAIN', conv => {
 *   conv.ask(new RegisterUpdate({
 *     frequency: 'DAILY',
 *     intent: 'show.image',
 *     arguments: [{
 *       name: 'image_to_show',
 *       textValue: 'image_type_1',
 *     }],
 *   }))
 * })
 *
 * app.intent('show.image', conv => {
 *   const arg = conv.arguments.get('image_to_show') // will be 'image_type_1'
 *   // do something with arg
 * })
 *
 * // Dialogflow
 * const app = dialogflow()
 *
 * app.intent('Default Welcome Intent', conv => {
 *   conv.ask(new RegisterUpdate({
 *     frequency: 'DAILY',
 *     intent: 'Show Image',
 *     arguments: [{
 *       name: 'image_to_show',
 *       textValue: 'image_type_1',
 *     }],
 *   }))
 * })
 *
 * app.intent('Show Image', conv => {
 *   const arg = conv.arguments.get('image_to_show') // will be 'image_type_1'
 *   // do something with arg
 * })
 * ```
 *
 * @public
 */
class RegisterUpdate(options: RegisterUpdateOptions) : SoloQuestion(IntentEnum.REGISTER_UPDATE) {
    /**
     * @param options RegisterUpdate options
     * @public
     */
    init {
        this._data(InputValueSpec.RegisterUpdateValueSpec) {
            intent = options.intent
            arguments = options.arguments
            triggerContext = actions.service.actionssdk.api.GoogleActionsV2TriggerContext(
                    timeContext = actions.service.actionssdk.api.GoogleActionsV2TriggerContextTimeContext(
                            frequency = options.frequency)
            )
        }
    }
}
