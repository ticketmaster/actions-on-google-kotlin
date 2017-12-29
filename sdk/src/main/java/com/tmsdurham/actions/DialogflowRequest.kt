package com.tmsdurham.dialogflow

import com.tmsdurham.dialogflow.google.GoogleData
import com.tmsdurham.actions.*
import com.tmsdurham.actions.actions.ActionRequest
import com.tmsdurham.actions.actions.Sender

fun dialogflowRequest(init: DialogflowRequest.() -> Unit): DialogflowRequest {
    val request = DialogflowRequest()
    request.init()
    return request
}

data class DialogflowRequest(
        var id: String? = null,
        val timestamp: String? = null,
        val lang: String? = null,
        var result: Result = Result(),
        var data: Data = Data(),
        val status: Status? = null,
        val sessionId: String? = null,
        var originalRequest: OriginalRequest? = null) {

    inline fun result(f: Result.() -> Unit) = result.f()

    fun originalRequest(init: OriginalRequest.() -> Unit) {
        originalRequest = OriginalRequest()
        originalRequest!!.init()
    }
}

data class Metadata(
        val intentId: String? = null,
        val webhookUsed: String? = null,
        val webhookForSlotFillingUsed: String? = null,
        val intentName: String? = null)

/**
 * Messages from Dialogflow, such as Cards, simple response, etc, that are added in the console.
 */
data class Messages(
        var type: String = "",
        var items: MutableList<OptionItem>? = null,
        var speech: String? = null,
        var textToSpeech: String? = null,
        var displayText: String? = null,
        var suggestions: MutableList<Suggestions>? = null,
        var destinationName: String? = null,
        var url: String? = null,
        var title: String? = null,
        var subtitle: String? = null,
        var formattedText: String? = null,
        var image: Image? = null,
        var buttons: MutableList<Button>? = null)

data class Fulfillment(
        val speech: String? = null,
        val source: String? = null,
        val displayText: String? = null,
        var messages: MutableList<Messages>? = null,
        val data: Data? = null)

/**
 * Holds data for original platform.  Extends MutableMap so this is extendable
 * to other platforms by adding a field
 */
data class Data(val nothing: Nothing? = null) : MutableMap<String, Any?> by mutableMapOf() {
    var google: GoogleData? by this

    inline fun google(init: GoogleData.() -> Unit) {
        if (google == null) {
            google = GoogleData()
        }
        google?.init()
    }
}

data class Result(
        var contexts: List<Context> = listOf(),
        val source: String = "",
        var resolvedQuery: String = "",
        var action: String = "",
        val actionIncomplete: Boolean = false,
        val parameters: MutableMap<String, Any>? = null,
        val metadata: Metadata? = null,
        val fulfillment: Fulfillment? = null,
        val score: Float = 0f) {

}


data class Status(
        val code: Int = 0,
        val errorType: String? = null)

data class Context(
        val name: String = "",
        var parameters: MutableMap<String, Any>? = null,
        var lifespan: Int = 0) {

    override fun equals(other: Any?) =
            if (other is Context) {
                this.name.toLowerCase().equals(other.name.toLowerCase())
            } else {
                false
            }
}

data class OriginalRequest(
        var source: String? = null,
        var data: ActionRequest? = null,
        var sender: Sender? = null,
        var version: String? = null)

data class Device(val location: DeviceLocation? = null)

data class DeviceLocation(var coordinates: Coordinates? = null, var formattedAddress: String? = null,
                          var zipCode: String? = null, var city: String? = null,
                          var address: String? = null)

data class Coordinates(val latitude: Double? = null, val longitude: Double? = null)


data class User(
        var userId: String = "",
        var accessToken: String? = null,
        var locale: String? = null,
        var profile: Profile? = null)

data class Profile(var displayName: String? = null, var givenName: String? = null, var familyName: String? = null)

data class Surface(
        val capabilities: List<Capabilities>? = null)

data class Capabilities(val name: String? = null)


data class Arguments(
        val datetimeValue: DateTimeValue? = null,
        val boolValue: Boolean? = null,
        val rawText: String? = null,
        var textValue: String? = null,
        var text_value: String? = null,
        var name: String? = null,
        val extension: TransactionRequirementsCheckResult? = null)

data class DateTimeValue(var date: Date? = null, var time: Time? = null)
data class Date(var month: Int? = null, var year: Int? = null, var day: Int? = null)
data class Time(var hours: Int? = null)

data class PostalAddress(var regionCode: String? = null,
                         var recipients: MutableList<String>? = null,
                         var postalCode: String? = null,
                         var locality: String? = null,
                         var addressLines: MutableList<String>? = null,
                         val administrativeArea: String? = null)

data class Location(var zipCode: String? = null,
                    var postalAddress: PostalAddress? = null,
                    var phoneNumber: String? = null,
                    var city: String? = null,
                    var coordinates: Coordinates?)

data class FinalOrderHolder(val finalOrder: Order? = null, val orderDate: String = "", val googleOrderId: String = "")

data class TransactionRequirementsCheckResult(
        val `@type`: String = "",
        val resultType: TransactionValues.ResultType = TransactionValues.ResultType.UNSPECIFIED,
        var userDecision: String = "",
        var status: String = "",
        var location: Location? = null,
        val order: FinalOrderHolder? = null)

data class RawInput(val query: String? = null, val inputType: String? = null)
