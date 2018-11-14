package actions.service.actionssdk.conversation.question

import actions.service.actionssdk.api.GoogleActionsV2DateTime
import actions.service.actionssdk.conversation.InputValueSpec
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.response.SoloQuestion


/** @public */
typealias DateTimeArgument = GoogleActionsV2DateTime

interface DateTimeOptionsPrompts {
    /**
     * The initial prompt used to ask for a date and time.
     * If not provided, Google will use a generic prompt.
     * @public
     */
    var initial: String?

    /**
     * The prompt used to specifically ask for the date if not provided by user.
     * If not provided, Google will use a generic prompt.
     * @public
     */
    var date: String

    /**
     * The prompt used to specifically ask for the time if not provided by user.
     * If not provided, Google will use a generic prompt.
     * @public
     */
    var time: String?
}

/** @public */
interface DateTimeOptions {
    /**
     * Prompts for the user
     * @public
     */
    var prompts: DateTimeOptionsPrompts?
}

/**
 * Asks user for a timezone-agnostic date and time.
 *
 * @example
 * ```javascript
 *
 * // Actions SDK
 * const app = actionssdk()
 *
 * app.intent('actions.intent.MAIN', conv => {
 *   conv.ask(new DateTime({
 *     prompts: {
 *       initial: 'When do you want to come in?',
 *       date: 'Which date works best for you?',
 *       time: 'What time of day works best for you?',
 *     }
 *   }))
 * })
 *
 * app.intent('actions.intent.DATETIME', (conv, input, datetime) => {
 *   const { month, day } = datetime.date
 *   const { hours, minutes } = datetime.time
 *   conv.close(new SimpleResponse({
 *     speech: 'Great see you at your appointment!',
 *     text: `Great, we will see you on ${month}/${day} at ${hours} ${minutes || ''}`
 *   }))
 * })
 *
 * // Dialogflow
 * const app = dialogflow()
 *
 * app.intent('Default Welcome Intent', conv => {
 *   conv.ask(new DateTime({
 *     prompts: {
 *       initial: 'When do you want to come in?',
 *       date: 'Which date works best for you?',
 *       time: 'What time of day works best for you?',
 *     }
 *   }))
 * })
 *
 * // Create a Dialogflow intent with the `actions_intent_DATETIME` event
 * app.intent('Get Datetime', (conv, params, datetime) => {
 *   const { month, day } = datetime.date
 *   const { hours, minutes } = datetime.time
 *   conv.close(new SimpleResponse({
 *     speech: 'Great see you at your appointment!',
 *     text: `Great, we will see you on ${month}/${day} at ${hours} ${minutes || ''}`
 *   }))
 * })
 * ```
 *
 * @public
 */
class DateTime(options: DateTimeOptions? = null) : SoloQuestion(IntentEnum.DATETIME) {
    /**
     * @param options DateTime options
     * @public
     */
    init {
        this._data(InputValueSpec.DateTimeValueSpec) {
            options?.prompts?.let {
                dialogSpec {
                    requestDatetimeText = it.initial
                    requestDateText = it.date
                    requestTimeText = it.time
                }
            }
        }
    }
}
