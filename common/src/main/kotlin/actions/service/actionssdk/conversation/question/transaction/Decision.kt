package actions.service.actionssdk.conversation.question.transaction

import actions.service.actionssdk.api.GoogleActionsV2TransactionDecisionValue
import actions.service.actionssdk.conversation.InputValueSpec
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.response.SoloQuestion

typealias TransactionDecisionArgument = GoogleActionsV2TransactionDecisionValue

/**
 * Asks user to confirm transaction information.
 * @public
 */
class TransactionDecision(options: GoogleActionsV2TransactionDecisionValue): SoloQuestion(IntentEnum.TRANSACTION_DECISION) {
    /**
     * @param options The raw {@link GoogleActionsV2TransactionDecisionValueSpec}
     * @public
     */
    init {
        this._data(InputValueSpec.TransactionDecisionValueSpec) {
            checkResult = options.checkResult
            deliveryAddress = options.deliveryAddress
            order = options.order
            userDecision = options.userDecision
        }
    }
}
