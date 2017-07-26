package com.ticketmaster.apiai

import com.ticketmaster.apiai.google.GoogleData
import com.tmsdurham.actions.Buttons
import com.tmsdurham.actions.Image
import com.tmsdurham.actions.Suggestions

fun <T>apiAiRequest(init: ApiAiRequest<T>.() -> Unit): ApiAiRequest<T> {
    val request = ApiAiRequest<T>()
    request.init()
    return request
}

data class ApiAiRequest<T>(
        var id: String? = null,
        val timestamp: String? = null,
        val lang: String? = null,
        var result: Result<T> = Result<T>(),
        var data: Data = Data.empty,
        val status: Status? = null,
        val sessionId: String? = null,
        var originalRequest: OriginalRequest? = null) {

    inline fun result(f: Result<T>.() -> Unit) = result.f()

    fun originalRequest(init: OriginalRequest.() -> Unit) {
        originalRequest = OriginalRequest()
        originalRequest!!.init()
    }
}

fun <T> request(f: ApiAiRequest<T>.() -> Unit): ApiAiRequest<T>.() -> Unit = f

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
        var buttons: MutableList<Buttons>? = null)

data class Fulfillment(
        val speech: String? = null,
        val source: String? = null,
        val displayText: String? = null,
        val messages: List<Messages>? = null,
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

data class Result<T>(
        val contexts: List<Contexts<T>> = listOf(),
        val source: String = "",
        val resolvedQuery: String = "",
        val action: String = "",
        val actionIncomplete: Boolean = false,
        val parameters: T? = null,
        val metadata: Metadata? = null,
        val fulfillment: Fulfillment? = null,
        val score: Float = 0f) {

}


data class Status(
        val code: Int = 0,
        val errorType: String? = null)

data class Contexts<out T>(
        val name: String = "",
        val parameters: T? = null,
        val lifespan: Int = 0) {

    override fun equals(other: Any?) =
        if (other is Contexts<*>) {
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
        var inputs: List<Inputs>? = null,
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

data class Capabilities(
        val name: String? = null) {

    companion object {
        val AUDIO_INPUT = "actions.capability.AUDIO_INPUT"
        val AUDIO_OUTPUT = "actions.capability.AUDIO_OUTPUT"
        val SCREEN_OUTPUT = "actions.capability.SCREEN_OUTPUT"
        val SCREEN_INPUT = "actions.capability.SCREEN_INPUT"
    }
}

data class Arguments(
        val datetimeValue: String? = null,
        val boolValue: Boolean? = null,
        val rawText: String? = null,
        val textValue: String? = null,
        val name: String? = null,
        val extension: TransactionRequirementsCheckResult? = null) {
    companion object {
        val TRANSACTION_REQUIREMENTS_CHECK_RESULT = "TRANSACTION_REQUIREMENTS_CHECK_RESULT"
        val TRANSACTION_DECISION_VALUE = "TRANSACTION_DECISION_VALUE"
    }
}


data class FinalOrderHolder(val finalOrder: GoogleData.Order? = null, val  orderDate: String = "", val googleOrderId: String = "")

data class TransactionRequirementsCheckResult(
        val `@type`: String = "",
        val resultType: String = "",
        val userDecision: String = "",
        val status: String = "",
        val order: FinalOrderHolder? = null) {

    enum class ResultType {
        OK,
        RESULT_TYPE_UNSPECIFIED,
        USER_ACTION_REQUIRED,
        ASSISTANT_SURFACE_NOT_SUPPORTED,
        REGION_NOT_SUPPORTED
    }

    enum class TransactionUserDecision {
        UNKNOWN_USER_DECISION,
        ORDER_ACCEPTED,
        ORDER_REJECTED,
        DELIVERY_ADDRESS_UPDATED,
        CART_CHANGE_REQUESTED
    }

    companion object {
        const val CHECK_RESULT_TYPE = "type.googleapis.com/google.actions.v2.TransactionRequirementsCheckResult"
    }
}

data class Raw_inputs(val query: String? = null, val inputType: String? = null)

data class Inputs(
        val arguments: List<Arguments>? = null,
        val intent: String? = null,
        val rawInputs: List<Raw_inputs>? = null)

data class DialogState<out T>(val state: String = "", val data:T? = null)

