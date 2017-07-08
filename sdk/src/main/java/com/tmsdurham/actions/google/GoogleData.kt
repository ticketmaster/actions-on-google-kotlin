package com.ticketmaster.apiai.google

data class GoogleData(
        var noInputPrompts: MutableList<NoInputPrompts>? = null,
        var permissionsRequest: PermissionsRequest? = null,
        var systemIntent: SystemIntent? = null,
        var expectUserResponse: Boolean = false,
        var possibleIntents: List<PossibleIntent>? = null,
        var richResponse: RichResponse? = null) {

    companion object {
        var OPTION_VALUE_SPEC_TYPE = "type.googleapis.com/google.actions.v2.OptionValueSpec"
        var SELECT_INTENT = "actions.intent.OPTION"
        var TRANSACTION_CHECK_INTENT = "actions.intent.TRANSACTION_REQUIREMENTS_CHECK"
        var TRANSACTION_CHECK_TYPE = "type.googleapis.com/google.actions.v2.TransactionRequirementsCheckSpec"
        var TRANSACTION_DECISION_INTENT = "actions.intent.TRANSACTION_DECISION"
        var TRANSACTION_DECISION_TYPE = "type.googleapis.com/google.actions.v2.TransactionDecisionValueSpec"
        val PERMISSION_VALUE_SPEC = "type.googleapis.com/google.actions.v2.PermissionValueSpec"
        val PERMISSION_INTENT = "actions.intent.PERMISSION"
        var DEVICE_PRECISE_LOCATION = "DEVICE_PRECISE_LOCATION"
        var DEVICE_COARSE_LOCATION = "DEVICE_COARSE_LOCATION"
        var NAME = "NAME"
    }

    data class PossibleIntent(val intent: String)

    class NoInputPrompts

    data class PermissionsRequest(
            var optContext: String? = null,
            var permissions: MutableList<String>? = null,
            var expectUserResponse: Boolean = false)


    data class Suggestions(var title: String? = null)

    data class Image(var url: String? = null)

    data class Items(
            var optionInfo: OptionInfo? = null,
            var title: String? = null,
            var description: String? = null,
            var image: Image? = null)


    data class RichResponse(
            var items: MutableList<RichResponseItem>? = null,
            var suggestions: MutableList<Suggestions>? = null,
            var altLinkSuggestion: AltLinkSuggestion? = null,
            var linkOutSuggestion: LinkOutSuggestion? = null)

    data class LinkOutSuggestion(
            var destination_name: String? = null,
            var url: String? = null)

    //weird structure here.  This is Wrapper for ONE of the objects below.
    data class RichResponseItem(
            var simpleResponse: SimpleResponse? = null,
            var basicCard: BasicCard? = null,
            var structuredResponse: StructuredResponse? = null)

    data class StructuredResponse(var orderUpdate: OrderUpdate)

    data class OrderUpdate(var googleOrderId: String, var orderState: OrderState)

    data class OrderState(var state: String, var label: String)

    data class AltLinkSuggestion(var url: String? = null)

    data class SimpleResponse(
            var textToSpeech: String? = null,
            var ssml: String? = null,
            var displayText: String? = null)


    data class BasicCard(
            var formattedText: String? = null,
            var buttons: MutableList<Buttons>? = null,
            var title: String? = null,
            var image: Image? = null,
            var subtitle: String? = null)


    data class Buttons(
            var title: String? = null,
            var openUrlAction: OpenUrlAction? = null)

    data class OpenUrlAction(var url: String? = null)

    data class SystemIntent(
            var intent: String? = null,
            var data: Data? = null)

    data class Data(
            var `@type`: String? = null,
            var optContext: String? = null,
            var permissions: List<String>? = null,
            var listSelect: ListSelect? = null,
            var carouselSelect: CarouselSelect? = null,
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

    data class ListSelect(
            var title: String? = null,
            var items: MutableList<Items>? = null)

    data class CarouselSelect(
            var title: String? = null,
            var items: MutableList<Items>? = null)

    data class OptionInfo(
            var key: String? = null,
            var synonyms: MutableList<String>? = null)
}
