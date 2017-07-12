package com.tmsdurham.actions

import com.ticketmaster.apiai.*
import com.ticketmaster.apiai.google.GoogleData
import com.tmsdurham.actions.gui.SimpleResponse


class ApiAiApp<T> : AssistantApp<ApiAiRequest<T>, ApiAiResponse<T>, T> {

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

    constructor(request: RequestWrapper<ApiAiRequest<T>>, response: ResponseWrapper<ApiAiResponse<T>>, sessionStarted: (() -> Unit)? = null) :
            super(request, response, sessionStarted) {
        debug("ApiAiApp constructor")

        // If request contains originalRequest, convert to Proto3.
        if (request.body.originalRequest != null && !isNotApiVersionOne()) {
            //TODO("convert to Proto3")
        }

        debug("new == ${CONVERSATION_STAGES.NEW}")
        debug("type == ${request.body.originalRequest?.data?.conversation?.type}")
        if ((request.body.originalRequest?.data?.conversation?.type ==
                CONVERSATION_STAGES.NEW) && sessionStarted != null) {
            sessionStarted()
        }
    }


    override fun tell(speech: String, displayText: String): ResponseWrapper<ApiAiResponse<T>>? {
        debug("tell: speechResponse=$speech")
        if (speech.isEmpty()) {
            handleError("Invalid speech response")
            return null
        }
        val response = this.buildResponse(speech, false)
        if (response != null) {
            return this.doResponse(response, RESPONSE_CODE_OK)
        } else {
            return null
        }
    }


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
        response.body?.data?.google?.systemIntent = GoogleData.SystemIntent(intent = STANDARD_INTENTS.PERMISSION)
        response.body?.data?.google?.systemIntent?.data?.`@type` = INPUT_VALUE_DATA_TYPES.PERMISSION
        response.body?.data?.google?.systemIntent?.data?.permissions = permissionSpec.permissions
    }


    //TODO builderResponse(richResponse,...)
    /*

if (!isStringResponse) {
            if (textToSpeech.speech) {
                // Convert SimpleResponse to RichResponse
                textToSpeech = this.buildRichResponse().addSimpleResponse(textToSpeech);
            } else if (!(textToSpeech.items &&
                    textToSpeech.items[0] &&
                    textToSpeech.items[0].simpleResponse)) {
                handleError("Invalid RichResponse. First item must be SimpleResponse");
                return null
            }
        }
             */
    /**
     * Builds a response for API.AI to send back to the Assistant.
     *
     * @param {string|RichResponse|SimpleResponse} textToSpeech TTS/response
     *     spoken/shown to end user.
     * @param {boolean} expectUserResponse true if the user response is expected.
     * @param {Array<string>=} noInputs Array of re-prompts when the user does not respond (max 3).
     * @return {Object} The final response returned to Assistant.
     * @private
     * @apiai
     */
    fun buildResponse(textToSpeech: String, expectUserResponse: Boolean, noInputs: MutableList<String>? = null): ResponseWrapper<ApiAiResponse<T>>? {
        debug("buildResponse_: textToSpeech=$textToSpeech, expectUserResponse=$expectUserResponse, noInputs=$noInputs")
        if (textToSpeech.isEmpty()) {
            handleError("Invalid text to speech")
            return null
        }

        var noInputsFinal = mutableListOf<GoogleData.NoInputPrompts>()
        val dialogState = DialogState(
                state = state, //TODO (this.state instanceof State ? this.state.getName() : this.state),
                data = data)
        if (noInputs != null) {
            if (noInputs.size > INPUTS_MAX) {
                handleError("Invalid number of no inputs")
                return null
            }
            if (isSsml(textToSpeech)) {
                noInputsFinal.addAll(buildPromptsFromSsmlHelper(noInputs))
            } else {
                noInputsFinal.addAll(buildPromptsFromPlainTextHelper(noInputs))
            }
        } else {
            noInputsFinal = mutableListOf()
        }
        val response = ApiAiResponse<T>(
                speech = textToSpeech)
//                    textToSpeech.items[0].simpleResponse.textToSpeech,
        response.data.google = GoogleData(
                expectUserResponse = expectUserResponse,
                isSsml = isSsml(textToSpeech),
                noInputPrompts = noInputsFinal)
        if (expectUserResponse) {
            response.contextOut.add(
                    ContextOut(
                            name = ACTIONS_API_AI_CONTEXT,
                            lifespan = MAX_LIFESPAN,
                            parameters = dialogState.data))
        }
        response.contextOut.addAll(contexts)
        this.response.body = response
        return this.response
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
