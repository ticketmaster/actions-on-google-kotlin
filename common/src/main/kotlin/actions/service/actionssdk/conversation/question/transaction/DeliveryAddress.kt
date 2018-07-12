package actions.service.actionssdk.conversation.question.transaction

import actions.service.actionssdk.api.GoogleActionsV2ConfirmationValueSpec
import actions.service.actionssdk.api.GoogleActionsV2DeliveryAddressValue
import actions.service.actionssdk.api.GoogleActionsV2DeliveryAddressValueSpec
import actions.service.actionssdk.conversation.InputValueSpec
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.response.SoloQuestion

typealias DeliveryAddressArgument = GoogleActionsV2DeliveryAddressValue

/**
 * Asks user for delivery address.
 * @public
 */
class DeliveryAddress(init: GoogleActionsV2DeliveryAddressValueSpec.() -> Unit): SoloQuestion(IntentEnum.DELIVERY_ADDRESS) {
    /**
     * @param options The raw {@link GoogleActionsV2DeliveryAddressValueSpec}
     * @public
     */
    init {
        val options = GoogleActionsV2DeliveryAddressValueSpec()
        options.init()
        this._data(InputValueSpec.DeliveryAddressValueSpec) {
             addressOptions = options?.addressOptions
        }
    }
}
