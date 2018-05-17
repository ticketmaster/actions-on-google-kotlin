package actions.service.actionssdk.conversation.question.transaction

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
class DeliveryAddress(options: GoogleActionsV2DeliveryAddressValueSpec? = null): SoloQuestion(IntentEnum.DELIVERY_ADDRESS) {
    /**
     * @param options The raw {@link GoogleActionsV2DeliveryAddressValueSpec}
     * @public
     */
    init {
        this._data(InputValueSpec.DeliveryAddressValueSpec) {
             addressOptions = options?.addressOptions
        }
    }
}
