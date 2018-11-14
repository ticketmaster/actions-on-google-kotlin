package actions.service.actionssdk.conversation.question.transaction

import actions.service.actionssdk.api.GoogleActionsV2TransactionDecisionValue
import actions.service.actionssdk.api.GoogleActionsV2TransactionDecisionValueSpec
import actions.service.actionssdk.conversation.InputValueSpec
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.response.SoloQuestion

typealias TransactionDecisionArgument = GoogleActionsV2TransactionDecisionValue

/**
 * Asks user to confirm transaction information.
 * @public
 */
class TransactionDecision(init: GoogleActionsV2TransactionDecisionValueSpec.() -> Unit): SoloQuestion(IntentEnum.TRANSACTION_DECISION) {
    /**
     * @param options The raw {@link GoogleActionsV2TransactionDecisionValueSpec}
     * @public
     */
    init {
        val options = GoogleActionsV2TransactionDecisionValueSpec()
        options.init()
        this._data(InputValueSpec.TransactionDecisionValueSpec) {
            orderOptions = options.orderOptions
            paymentOptions = options.paymentOptions
            presentationOptions = options.presentationOptions
            proposedOrder = options.proposedOrder
        }
    }
}
