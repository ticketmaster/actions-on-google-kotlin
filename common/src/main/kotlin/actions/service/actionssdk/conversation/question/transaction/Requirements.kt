package actions.service.actionssdk.conversation.question.transaction

import actions.service.actionssdk.api.GoogleActionsV2TransactionRequirementsCheckResult
import actions.service.actionssdk.api.GoogleActionsV2TransactionRequirementsCheckSpec
import actions.service.actionssdk.conversation.InputValueSpec
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.response.SoloQuestion

typealias TransactionRequirementsArgument = GoogleActionsV2TransactionRequirementsCheckResult

/**
 * Checks whether user is in transactable state.
 * @public
 */
class TransactionRequirements(init: GoogleActionsV2TransactionRequirementsCheckSpec.() -> Unit): SoloQuestion(IntentEnum.TRANSACTION_REQUIREMENTS_CHECK) {
    /**
     * @param options The raw {@link GoogleActionsV2TransactionRequirementsCheckSpec}
     * @public
     */
    init {
        val options = GoogleActionsV2TransactionRequirementsCheckSpec()
        options.init()
        this._data(InputValueSpec.TransactionRequirementsCheckSpec) {
            orderOptions = options.orderOptions
            paymentOptions = options.paymentOptions
        }
    }
}
