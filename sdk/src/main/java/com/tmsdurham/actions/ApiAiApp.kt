package com.tmsdurham.actions

import com.ticketmaster.apiai.*
import com.ticketmaster.apiai.google.GoogleData

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


    /**
     * Asks to collect the user"s input with a list.
     *
     * @example
     * const app = new ApiAiApp({request, response});
     * const WELCOME_INTENT = "input.welcome";
     * const OPTION_INTENT = "option.select";
     *
     * function welcomeIntent (app) {
     *   app.askWithList("Which of these looks good?",
     *     app.buildList("List title")
     *      .addItems([
     *        app.buildOptionItem(SELECTION_KEY_ONE,
     *          ["synonym of KEY_ONE 1", "synonym of KEY_ONE 2"])
     *          .setTitle("Title of First List Item"),
     *        app.buildOptionItem(SELECTION_KEY_TWO,
     *          ["synonym of KEY_TWO 1", "synonym of KEY_TWO 2"])
     *          .setTitle("Title of Second List Item"),
     *      ]));
     * }
     *
     * function optionIntent (app) {
     *   if (app.getSelectedOption() === SELECTION_KEY_ONE) {
     *     app.tell("Number one is a great choice!");
     *   } else {
     *     app.tell("Number two is a great choice!");
     *   }
     * }
     *
     * const actionMap = new Map();
     * actionMap.set(WELCOME_INTENT, welcomeIntent);
     * actionMap.set(OPTION_INTENT, optionIntent);
     * app.handleRequest(actionMap);
     *
     * @param {string|RichResponse|SimpleResponse} inputPrompt The input prompt
     *     response.
     * @param {List} list List built with {@link AssistantApp#buildList|buildList}.
     * @return {Object} HTTP response.
     * @apiai
     */

    fun askWithList(richResponse: RichResponse, list: List): ResponseWrapper<ApiAiResponse<T>>? {
        if (richResponse.items?.size ?: 0 < 2) {
            this.handleError("List requires at least 2 items")
            return null
        }
        return askWithResponseAndList(buildResponse(richResponse, true), list)
    }

    fun askWithList(inputPrompt: String?, list: List): ResponseWrapper<ApiAiResponse<T>>? {
        debug("askWithList: inputPrompt=$inputPrompt, list=$list")
        if (inputPrompt.isNullOrBlank()) {
            this.handleError("Invalid input prompt");
            return null
        }

        if (list.items?.size ?: 0 < 2) {
            this.handleError("List requires at least 2 items")
            return null
        }
        return askWithResponseAndList(buildResponse(inputPrompt ?: "", true), list)
    }

    private fun askWithResponseAndList(response: ResponseWrapper<ApiAiResponse<T>>?, list: List): ResponseWrapper<ApiAiResponse<T>>? {
        if (response == null) {
            error("Error in building response")
            return null
        }
        response.body?.data?.google?.systemIntent {
            intent = STANDARD_INTENTS.OPTION
        }
        if (isNotApiVersionOne()) {
            response.body?.data?.google?.systemIntent?.data = GoogleData.Data(
                    `@type` = INPUT_VALUE_DATA_TYPES.OPTION,
                    listSelect = list)
        } else {
            response.body?.data?.google?.systemIntent?.spec = GoogleData.Spec(
                    optionValueSpec = GoogleData.OptionValueSpec(
                            listSelect = list))
        }

        if (response != null) {
            return doResponse(response, RESPONSE_CODE_OK)
        } else {
            return null
        }
    }

    /**
     * Asks to collect the user"s input with a carousel.
     *
     * @example
     * const app = new ApiAiApp({request, response});
     * const WELCOME_INTENT = "input.welcome";
     * const OPTION_INTENT = "option.select";
     *
     * function welcomeIntent (app) {
     *   app.askWithCarousel("Which of these looks good?",
     *     app.buildCarousel()
     *      .addItems([
     *        app.buildOptionItem(SELECTION_KEY_ONE,
     *          ["synonym of KEY_ONE 1", "synonym of KEY_ONE 2"])
     *          .setTitle("Number one"),
     *        app.buildOptionItem(SELECTION_KEY_TWO,
     *          ["synonym of KEY_TWO 1", "synonym of KEY_TWO 2"])
     *          .setTitle("Number two"),
     *      ]));
     * }
     *
     * function optionIntent (app) {
     *   if (app.getSelectedOption() === SELECTION_KEY_ONE) {
     *     app.tell("Number one is a great choice!");
     *   } else {
     *     app.tell("Number two is a great choice!");
     *   }
     * }
     *
     * const actionMap = new Map();
     * actionMap.set(WELCOME_INTENT, welcomeIntent);
     * actionMap.set(OPTION_INTENT, optionIntent);
     * app.handleRequest(actionMap);
     *
     * @param {string|RichResponse|SimpleResponse} inputPrompt The input prompt
     *     response.
     * @param {Carousel} carousel Carousel built with
     *     {@link AssistantApp#buildCarousel|buildCarousel}.
     * @return {Object} HTTP response.
     * @apiai
     */
    fun askWithCarousel(inputPrompt: String, carousel: Carousel): ResponseWrapper<ApiAiResponse<T>>? {
        debug("askWithCarousel: inputPrompt=$inputPrompt, carousel=$carousel")
        if (inputPrompt.isNullOrBlank()) {
            handleError("Invalid input prompt");
            return null
        }
        if (carousel == null) {
            handleError("Invalid carousel")
            return null
        }
        if (carousel.items.size < 2) {
            handleError("Carousel requires at least 2 items")
            return null
        }
        val response = buildResponse(inputPrompt, true)
        if (response == null) {
            error("Error in building response")
            return null
        }
        response.body?.data?.google?.systemIntent = GoogleData.SystemIntent(
                intent = STANDARD_INTENTS.OPTION
        )
        if (isNotApiVersionOne()) {
            response.body?.data?.google?.systemIntent?.data = GoogleData.Data(
                    `@type` = INPUT_VALUE_DATA_TYPES.OPTION,
                    carouselSelect = carousel
            )
        } else {
            response.body?.data?.google?.systemIntent?.spec = GoogleData.Spec(
                optionValueSpec = GoogleData.OptionValueSpec(
                carouselSelect = carousel
                )
            )
        }
        if (response != null) {
            return doResponse(response, RESPONSE_CODE_OK);
        } else {
            return null
        }
    }

    /**
     * Tells the Assistant to render the speech response and close the mic.
     *
     * @example
     * const app = new ApiAiApp({request: request, response: response});
     * const WELCOME_INTENT = "input.welcome";
     * const NUMBER_INTENT = "input.number";
     *
     * function welcomeIntent (app) {
     *   app.ask("Welcome to action snippets! Say a number.");
     * }
     *
     * function numberIntent (app) {
     *   const number = app.getArgument(NUMBER_ARGUMENT);
     *   app.tell("You said " + number);
     * }
     *
     * const actionMap = new Map();
     * actionMap.set(WELCOME_INTENT, welcomeIntent);
     * actionMap.set(NUMBER_INTENT, numberIntent);
     * app.handleRequest(actionMap);
     *
     * @param {string|SimpleResponse|RichResponse} textToSpeech Final response.
     *     Spoken response can be SSML.
     * @return The response that is sent back to Assistant.
     * @apiai
     */
    override fun tell(richResponse: RichResponse?): ResponseWrapper<ApiAiResponse<T>>? {
        debug("tell: richResponse=$richResponse")
        if (richResponse == null || richResponse.isEmpty()) {
            handleError("Invalid rich response")
            return null
        }
        val response = buildResponse(richResponse, false)
        if (response != null) {
            return doResponse(response, RESPONSE_CODE_OK)
        } else {
            return null
        }
    }

    override fun tell(simpleResponse: SimpleResponse): ResponseWrapper<ApiAiResponse<T>>? {
        debug("tell: speechResponse=$simpleResponse")
        if (simpleResponse.isEmpty()) {
            handleError("Invalid speech response")
            return null
        }
        val response = buildResponse(simpleResponse, false)
        if (response != null) {
            return doResponse(response, RESPONSE_CODE_OK)
        } else {
            return null
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
     * @param {SimpleResponse} textToSpeech TTS/response
     *     spoken/shown to end user.
     * @param {boolean} expectUserResponse true if the user response is expected.
     * @param {Array<string>=} noInputs Array of re-prompts when the user does not respond (max 3).
     * @return {Object} The final response returned to Assistant.
     * @private
     * @apiai
     */
    fun buildResponse(simpleResponse: SimpleResponse, expectUserResponse: Boolean, noInputs: MutableList<String>? = null): ResponseWrapper<ApiAiResponse<T>>? {
        debug("buildResponse_: simpleResponse=$simpleResponse, expectUserResponse=$expectUserResponse, noInputs=$noInputs")
        if (simpleResponse.isEmpty()) {
            handleError("Invalid text to speech")
            return null
        }
        return buildResponse(buildRichResponse().addSimpleResponse(simpleResponse), expectUserResponse, noInputs)
    }

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
    fun buildResponse(textToSpeech: RichResponse, expectUserResponse: Boolean, noInputs: MutableList<String>? = null): ResponseWrapper<ApiAiResponse<T>>? {
        debug("buildResponse_: textToSpeech=$textToSpeech, expectUserResponse=$expectUserResponse, noInputs=$noInputs")
        if (textToSpeech.isEmpty()) {
            handleError("Invalid text to speech")
            return null
        }
        if (textToSpeech.items?.first()?.simpleResponse == null || textToSpeech.items?.first()?.simpleResponse?.textToSpeech.isNullOrBlank()) {
            handleError("Invalid text to speech")
            return null
        }

        val speech = textToSpeech.items?.first()?.simpleResponse?.textToSpeech!!
        var noInputsFinal = mutableListOf<GoogleData.NoInputPrompts>()
        val dialogState = DialogState(
                state = state, //TODO (this.state instanceof State ? this.state.getName() : this.state),
                data = data)
        if (noInputs != null) {
            if (noInputs.size > INPUTS_MAX) {
                handleError("Invalid number of no inputs")
                return null
            }
            if (isSsml(speech)) {
                noInputsFinal.addAll(buildPromptsFromSsmlHelper(noInputs))
            } else {
                noInputsFinal.addAll(buildPromptsFromPlainTextHelper(noInputs))
            }
        } else {
            noInputsFinal = mutableListOf()
        }
        val response = ApiAiResponse<T>(
                speech = speech)
        response.data.google = GoogleData(
                expectUserResponse = expectUserResponse,
                isSsml = isSsml(speech),
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
