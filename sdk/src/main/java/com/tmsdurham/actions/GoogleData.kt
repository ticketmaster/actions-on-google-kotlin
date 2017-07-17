package com.ticketmaster.apiai.google

import com.tmsdurham.actions.Carousel
import com.tmsdurham.actions.RichResponse

data class GoogleData(
        var isSsml: Boolean = false,
        var noInputPrompts: MutableList<NoInputPrompts>? = mutableListOf(),
        var permissionsRequest: PermissionsRequest? = null,
        var systemIntent: SystemIntent? = null,
        var expectUserResponse: Boolean = false,
        var possibleIntents: List<PossibleIntent>? = null,
        var richResponse: RichResponse? = null) {

    fun systemIntent(init: SystemIntent.() -> Unit) {
        systemIntent = SystemIntent()
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
            var data: Data? = null)

    data class Spec(var optionValueSpec: OptionValueSpec)

    data class OptionValueSpec(var listSelect: com.tmsdurham.actions.List? = null, var carouselSelect: Carousel? = null)

    data class Data(
            var `@type`: String? = null,
            var optContext: String? = null,
            var permissions: List<String>? = null,
            var listSelect: com.tmsdurham.actions.List? = null,
            var carouselSelect: Carousel? = null,
            var proposedOrder: Order? = null,
            var name: String? = null,
            var paymentOptions: PaymentOptions? = null)

    data class OrderOptions(var requestDeliveryAddress: Boolean = false)

    data class ActionProvidedOptions(var paymentType: String, var displayName: String)

    data class PaymentOptions(var actionProvidedOptions: ActionProvidedOptions)

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
