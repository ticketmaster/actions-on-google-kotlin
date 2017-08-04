package com.ticketmaster.apiai

import com.ticketmaster.apiai.google.GoogleData
import com.tmsdurham.actions.*

fun apiAiRequest(init: ApiAiRequest.() -> Unit): ApiAiRequest {
    val request = ApiAiRequest()
    request.init()
    return request
}

data class ApiAiRequest(
        var id: String? = null,
        val timestamp: String? = null,
        val lang: String? = null,
        var result: Result = Result(),
        var data: Data = Data.empty,
        val status: Status? = null,
        val sessionId: String? = null,
        var originalRequest: OriginalRequest? = null) {

    inline fun result(f: Result.() -> Unit) = result.f()

    fun originalRequest(init: OriginalRequest.() -> Unit) {
        originalRequest = OriginalRequest()
        originalRequest!!.init()
    }
}

fun request(f: ApiAiRequest.() -> Unit): ApiAiRequest.() -> Unit = f

data class Metadata(
        val intentId: String? = null,
        val webhookUsed: String? = null,
        val webhookForSlotFillingUsed: String? = null,
        val intentName: String? = null)

/**
 * Messages from ApiAi, such as Cards, simple response, etc, that are added in the console.
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

data class Data(
        var google: GoogleData? = null) {

    inline fun google(init: GoogleData.() -> Unit) {
        if (google == null) {
            google = GoogleData()
        }
        google?.init()
    }

    companion object {
        val empty = Data()
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
        var data: OriginalRequestData? = null,
        var version: String? = null)

data class OriginalRequestData(
        var conversation: Conversation? = null,
        var user: User? = null,
        var device: Device? = null,
        var surface: Surface? = null,
        var sender: Sender? = null,
        var inputs: MutableList<Inputs>? = null,
        var isInSandbox: Boolean? = null)

data class Conversation(var type: String)
data class Device(val location: DeviceLocation? = null)

data class DeviceLocation(var coordinates: Coordinates? = null, var formattedAddress: String? = null,
                          var zipCode: String? = null, var city: String? = null)

data class Coordinates(val latitude: Double? = null, val longitude: Double? = null)


data class Sender(val id: String? = null)

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
        val datetimeValue: String? = null,
        val boolValue: Boolean? = null,
        val rawText: String? = null,
        var textValue: String? = null,
        var text_value: String? = null,
        val name: String? = null,
        val otherValue: Any? = null,
        val extension: TransactionRequirementsCheckResult? = null)

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

data class FinalOrderHolder(val finalOrder: Order? = null, val  orderDate: String = "", val googleOrderId: String = "")

data class TransactionRequirementsCheckResult(
        val `@type`: String = "",
        val resultType: TransactionValues.ResultType = TransactionValues.ResultType.UNSPECIFIED,
        val userDecision: String = "",
        val status: String = "",
        var location: Location? = null,
        val order: FinalOrderHolder? = null)


data class RawInput(val query: String? = null, val inputType: String? = null)

data class Inputs(
        var arguments: List<Arguments>? = null,
        val intent: String? = null,
        var speech: String? = null,
        val rawInputs: List<RawInput>? = null)

data class DialogState(val state: String = "", val data: MutableMap<String, Any>? = null)

