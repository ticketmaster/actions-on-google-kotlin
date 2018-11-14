package actions.service.actionssdk.conversation

import actions.service.actionssdk.api.GoogleActionsV2RawInput
import actions.service.actionssdk.api.GoogleActionsV2RawInputInputType


class Input(var input: GoogleActionsV2RawInput? = null) {
    /**
     * Gets the user's raw input query.
     *
     * Will also be sent via intent handler 2nd argument which is the encouraged method to retrieve.
     *
     * @example
     * ```javascript
     *
     * // Encouraged method through intent handler
     * app.intent('actions.intent.TEXT', (conv, input) => {
     *  conv.close(`You said ${input}`)
     * })
     *
     * // Using conv.input.raw
     * app.intent('actions.intent.TEXT', conv => {
     *  conv.close(`You said ${conv.input.raw}`)
     * })
     * ```
     *
     * @public
     */
    var raw: String? = null

    /**
     * Gets type of input used for this request.
     * @public
     */
    var type: GoogleActionsV2RawInputInputType? = null

    init {
        this.raw = input?.query
        this.type = input?.inputType
    }
}
