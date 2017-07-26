package com.ticketmaster.apiai.google

import com.tmsdurham.actions.Carousel
import com.tmsdurham.actions.CustomerInfoOptions
import com.tmsdurham.actions.RichResponse

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
            var expectUserResponse: Boolean = false)


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
            var paymentOptions: PaymentOptions? = null,
            var addressOptions: AddressOptions? = null,
            var orderOptions: OrderOptions? = null) {

        inline fun paymentOptions(init: PaymentOptions.() -> Unit) {
            if (paymentOptions == null) {
                paymentOptions = PaymentOptions()
            }
            paymentOptions?.init()
        }
    }

    data class OrderOptions(var requestDeliveryAddress: Boolean = false, var customerInfoOptions: List<String>? = null)

    data class ActionProvidedOptions(var paymentType: String, var displayName: String)

    data class GoogleProvidedOptions(
            var supportedCardNetworks: MutableList<String>,
            var prepaidCardDisallowed: Boolean,
            var tokenizationParameters: TokenizationParameters? = null)

    data class TokenizationParameters(var tokenizationType: String? = null, var parameters: Any)

    data class PaymentOptions(var actionProvidedOptions: ActionProvidedOptions? = null, var googleProvidedOptions: GoogleData.GoogleProvidedOptions? = null)

    data class AddressOptions(var reason: String? = null)

    data class TransactionRequirementsCheckSpec(
            var orderOptions: OrderOptions? = null,
            var paymentOptions: PaymentOptions? = null)

    data class Price(
            var type: String? = null,
            var amount: Amount? = null)

    data class LineItems(
            var id: String? = null,
            var name: String? = null,
            var type: String? = null,
            var price: Price? = null,
            var quantity: Int = 0)

    data class Merchant(
            var id: String? = null,
            var name: String? = null)

    data class Cart(
            var lineItems: List<LineItems>? = null,
            var merchant: Merchant? = null,
            var notes: String? = null)

    data class Amount(
            var currencyCode: String? = null,
            var units: Int = 0,
            var nanos: Int = 0)

    data class TotalPrice(
            var type: String? = null,
            var amount: Amount? = null)

    data class Order(
            var id: String? = null,
            var cart: Cart? = null,
            var otherItems: List<LineItems>? = null,
            var totalPrice: TotalPrice? = null)

}
