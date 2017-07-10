package com.tmsdurham.actions

import com.ticketmaster.apiai.ApiAiRequest
import com.ticketmaster.apiai.ApiAiResponse
import com.ticketmaster.apiai.DeviceLocation
import com.ticketmaster.apiai.User
import com.ticketmaster.apiai.google.GoogleData
import com.tmsdurham.actions.gui.Data
import com.tmsdurham.actions.gui.SimpleResponse


class ApiAiApp<T>(request: RequestWrapper<ApiAiRequest<T>>, response: ResponseWrapper<ApiAiResponse>)
    : AssistantApp<ApiAiRequest<T>, ApiAiResponse>(request, response) {

    // Constants
    val RESPONSE_CODE_OK = 200
    val ACTIONS_API_AI_CONTEXT = "_actions_on_google_"
    val MAX_LIFESPAN = 100
    val INPUTS_MAX = 3
    val ORIGINAL_SUFFIX = ".original"
    val SELECT_EVENT = "actions_intent_option"

    // API.AI Rich Response item types
    val SIMPLE_RESPONSE = "simple_response"
    val BASIC_CARD = "basic_card"
    val LIST = "list_card"
    val CAROUSEL = "carousel_card"
    val SUGGESTIONS = "suggestion_chips"
    val LINK_OUT_SUGGESTION = "link_out_chip"
    val TYPE = "type"
    val PLATFORM = "platform"

    var data: T? = null

    fun ask(response: SimpleResponse.() -> Unit): Unit {

    }

    fun ask(action: ApiAiApp<T>) {

    }

    fun buildRichResponse(richResponse: Data.() -> Unit): ApiAiApp<T> {
        return this
    }

    fun simpleResponse(action: ApiAiApp<T>.() -> Unit): Unit {

    }

    fun getUser(): User? {
        if (request.body.originalRequest?.data?.user == null) {
            return null
        }
        return request.body.originalRequest?.data?.user
    }

    override fun getIntent(): String {
        debug("getIntent_");
        return request.body.result.action
    }

    fun getDeviceLocation(): DeviceLocation? {
        return request.body.originalRequest?.data?.device?.location;
    }

    // INTERNAL FUNCTIONS
    override fun fulfillPermissionRequest(permissionSpec: GoogleData.PermissionsRequest): Unit {
        response.body?.data?.google?.systemIntent = GoogleData.SystemIntent(intent = StandardIntents.PERMISSION.value)
        response.body?.data?.google?.systemIntent?.data?.`@type` = InputValueDataTypes.PERMISSION.value
        response.body?.data?.google?.systemIntent?.data?.permissions = permissionSpec.permissions
    }

    /**
     * Extract the session data from the incoming JSON request.
     *
     */
    override fun extractData() {
        debug("extractData")
        data = request.body.result.contexts.find { it.name == ACTIONS_API_AI_CONTEXT }?.parameters
    }

}
