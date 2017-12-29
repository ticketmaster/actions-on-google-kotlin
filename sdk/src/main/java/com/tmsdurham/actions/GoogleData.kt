package com.tmsdurham.dialogflow.google

import com.tmsdurham.actions.*
import com.tmsdurham.dialogflow.Arguments

data class GoogleData(
        var isSsml: Boolean = false,
        var noInputPrompts: MutableList<NoInputPrompts>? = mutableListOf(),
        var permissionsRequest: PermissionsRequest? = null,
        var systemIntent: SystemIntent? = null,
        var expectUserResponse: Boolean = false,
        var possibleIntents: List<PossibleIntent>? = null,
        var richResponse: RichResponse? = null) {

    inline fun systemIntent(init: SystemIntent.() -> Unit) {
        if (systemIntent == null) {
            systemIntent = SystemIntent()
        }
        systemIntent?.init()

    }

    data class PossibleIntent(val intent: String)

    data class NoInputPrompts(val ssml: String? = null, val textToSpeech: String? = null)

    data class PermissionsRequest(
            var optContext: String? = null,
            var permissions: MutableList<String>? = null,
            var expectUserResponse: Boolean = false,
            var arguments: MutableList<Arguments>? = null,
            var intent: String? = null)


    data class SystemIntent(
            var spec: Spec? = null,
            var intent: String? = null,
            var data: Data? = null) {

        inline fun spec(init: Spec.() -> Unit) {
            if (spec == null) {
                this.spec = Spec()
            }
            spec?.init()
        }

        inline fun data(init: Data.() -> Unit) {
            if (data == null) {
                data = Data()
            }
            data?.init()
        }
    }

    data class Spec(var optionValueSpec: OptionValueSpec? = null, var permissionValueSpec: GoogleData.PermissionsRequest? = null)

    data class OptionValueSpec(var listSelect: com.tmsdurham.actions.List? = null, var carouselSelect: Carousel? = null)

    data class Data(
            var `@type`: String? = null,
            var optContext: String? = null,
            var permissions: List<String>? = null,
            var listSelect: com.tmsdurham.actions.List? = null,
            var carouselSelect: Carousel? = null,
            var proposedOrder: Order? = null,
            var name: String? = null,
            var context: String? = null,
            var notificationTitle: String? = null,
            var capabilities: MutableList<String>? = null,
            var paymentOptions: PaymentOptions? = null,
            var addressOptions: AddressOptions? = null,
            var orderOptions: OrderOptions? = null,
            var dialogSpec: AssistantApp.DialogSpec? = null,
            var triggerContext: AssistantApp.TriggerContext? = null,
            var arguments: MutableList<Arguments>? = null) {

        inline fun paymentOptions(init: PaymentOptions.() -> Unit) {
            if (paymentOptions == null) {
                paymentOptions = PaymentOptions()
            }
            paymentOptions?.init()
        }
    }

    data class OrderOptions(var requestDeliveryAddress: Boolean = false, var customerInfoOptions: List<String>? = null)

    data class ActionProvidedOptions(var paymentType: String? = null, var displayName: String? = null)

    data class GoogleProvidedOptions(
            var supportedCardNetworks: MutableList<TransactionValues.CardNetwork>,
            var prepaidCardDisallowed: Boolean,
            var tokenizationParameters: TokenizationParameters? = null)

    data class TokenizationParameters(var tokenizationType: String? = null, var parameters: Any)

    data class PaymentOptions(var actionProvidedOptions: ActionProvidedOptions? = null, var googleProvidedOptions: GoogleData.GoogleProvidedOptions? = null)

    data class AddressOptions(var reason: String? = null)

    data class Price(
            var type: TransactionValues.PriceType? = null,
            var amount: Amount? = null) {
        fun amount(init: Amount.() -> Unit) {
            if (amount == null) {
                amount = Amount()
            }
            amount?.init()
        }
    }

    data class Amount(
            var currencyCode: String? = null,
            var units: Int = 0,
            var nanos: Int = 0)

    data class TotalPrice(
            var type: TransactionValues.PriceType? = null,
            var amount: Amount? = null) {
        fun amount(init: Amount.() -> Unit) {
            if (amount == null) {
                amount = Amount()
            }
            amount?.init()
        }
    }

}
fun totalPrice(init: GoogleData.TotalPrice.() -> Unit): GoogleData.TotalPrice {
        val totalPrice = GoogleData.TotalPrice()
        totalPrice.init()
        return totalPrice
    }
fun price(init: GoogleData.Price.() -> Unit): GoogleData.Price {
    val price = GoogleData.Price()
    price.init()
    return price
}
