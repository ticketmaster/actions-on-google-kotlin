package com.tmsdurham.actions

import com.ticketmaster.apiai.*
import com.ticketmaster.apiai.google.GoogleData
import com.tmsdurham.actions.BuiltInArgNames



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
    val requestExtractor: RequestExtractor<ApiAiRequest<T>, ApiAiResponse<T>, T>

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
        requestExtractor = RequestExtractor(this)
    }

    /**
     * Asks to collect the user"s input.
     *
     * NOTE: Due to a bug, if you specify the no-input prompts,
     * the mic is closed after the 3rd prompt, so you should use the 3rd prompt
     * for a bye message until the bug is fixed.
     *
     * @example
     * const app = new ApiAiApp({request: request, response: response});
     * const WELCOME_INTENT = "input.welcome";
     * const NUMBER_INTENT = "input.number";
     *
     * function welcomeIntent (app) {
     *   app.ask("Welcome to action snippets! Say a number.",
     *     ["Say any number", "Pick a number", "We can stop here. See you soon."]);
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
     * @param {string|SimpleResponse|RichResponse} inputPrompt The input prompt
     *     response.
     * @param {Array<string>=} noInputs Array of re-prompts when the user does not respond (max 3).
     * @return {Object} HTTP response.
     * @apiai
     */
    fun ask(inputPrompt: RichResponse, noInputs: MutableList<String>? = null): ResponseWrapper<ApiAiResponse<T>>? {
        debug("ask: inputPrompt=$inputPrompt, noInputs=$noInputs")
        if (inputPrompt.isEmpty()) {
            handleError("Invalid input prompt")
            return null
        }
        val response = buildResponse(inputPrompt, true, noInputs)
        if (response == null) {
            error("Error in building response");
            return null
        }
        return doResponse(response, RESPONSE_CODE_OK)
    }

    fun ask(speech: String, vararg noInputs: String = arrayOf()): ResponseWrapper<ApiAiResponse<T>>? {
       debug("ask: speech:$speech")
        if (speech.isBlank()) {
            handleError("Invalid input prompt")
            return null
        }
        val response = buildResponse(speech, true, noInputs = noInputs.toMutableList())
        if (response == null) {
            error("Error in building response")
            return null
        }
        return doResponse(response, RESPONSE_CODE_OK)
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
        if (list.items?.size ?: 0 < 2) {
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

    fun askWithCarousel(inputPrompt: RichResponse, carousel: Carousel): ResponseWrapper<ApiAiResponse<T>>? {
        debug("askWithCarousel: inputPrompt=$inputPrompt, carousel=$carousel")
        if (inputPrompt.isEmpty()) {
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

    override fun getIntent(): String {
        debug("getIntent_");
        return request.body.result.action
    }

    /**
     * Get the argument value by name from the current intent. If the argument
     * is included in originalRequest, and is not a text argument, the entire
     * argument object is returned.
     *
     * Note: If incoming request is using an API version under 2 (e.g. "v1"),
     * the argument object will be in Proto2 format (snake_case, etc).
     *
     * @example
     * val app = ApiAiApp(request = request, response = response)
     * val WELCOME_INTENT = "input.welcome"
     * val NUMBER_INTENT = "input.number"
     *
     * fun welcomeIntent (app: ApiAiApp<Parameters>) {
     *   app.ask("Welcome to action snippets! Say a number.");
     * }
     *
     * fun numberIntent (app: ApiAiApp<Parameter>) {
     *   val number = app.getArgument(NUMBER_ARGUMENT)
     *   app.tell("You said " + number)
     * }
     *
     * val actionMap = mapOf(
     *  WELCOME_INTENT to welcomeIntent,
     *  NUMBER_INTENT to numberIntent)
     * app.handleRequest(actionMap)
     *
     * @param {string} argName Name of the argument.
     * @return {Object} Argument value matching argName
     *     or null if no matching argument.
     * @apiai
     */
    fun getArgument (argName: String): Any? {
        debug("getArgument: argName=$argName")
        if (argName.isBlank()) {
            this.handleError("Invalid argument name")
            return null
        }
        val  parameters = request.body.result.parameters
        if (getProperty(parameters, argName) != null) {
            return getProperty(parameters, argName)
        }
        return requestExtractor.getArgumentCommon(argName)
    }

    fun getDeviceLocation(): DeviceLocation? {
        return request.body.originalRequest?.data?.device?.location
    }

    // INTERNAL FUNCTIONS
    override fun fulfillPermissionRequest(permissionSpec: GoogleData.PermissionsRequest): Unit {
        response.body?.data?.google?.systemIntent = GoogleData.SystemIntent(intent = STANDARD_INTENTS.PERMISSION)
        response.body?.data?.google?.systemIntent?.data?.`@type` = INPUT_VALUE_DATA_TYPES.PERMISSION
        response.body?.data?.google?.systemIntent?.data?.permissions = permissionSpec.permissions
    }

    /**
     * Get the context argument value by name from the current intent. Context
     * arguments include parameters collected in previous intents during the
     * lifespan of the given context. If the context argument has an original
     * value, usually representing the underlying entity value, that will be given
     * as part of the return object.
     *
     * @example
     * const app = new ApiAiApp({request: request, response: response});
     * const WELCOME_INTENT = "input.welcome";
     * const NUMBER_INTENT = "input.number";
     * const OUT_CONTEXT = "output_context";
     * const NUMBER_ARG = "myNumberArg";
     *
     * function welcomeIntent (app) {
     *   const parameters = {};
     *   parameters[NUMBER_ARG] = "42";
     *   app.setContext(OUT_CONTEXT, 1, parameters);
     *   app.ask("Welcome to action snippets! Ask me for your number.");
     * }
     *
     * function numberIntent (app) {
     *   const number = app.getContextArgument(OUT_CONTEXT, NUMBER_ARG);
     *   // number === { value: 42 }
     *   app.tell("Your number is  " + number.value);
     * }
     *
     * const actionMap = new Map();
     * actionMap.set(WELCOME_INTENT, welcomeIntent);
     * actionMap.set(NUMBER_INTENT, numberIntent);
     * app.handleRequest(actionMap);
     *
     * @param {string} contextName Name of the context.
     * @param {string} argName Name of the argument.
     * @return {Object} Object containing value property and optional original
     *     property matching context argument. Null if no matching argument.
     * @apiai
     */
    fun getContextArgument (contextName: String, argName: String): ContextArgument? {
        debug("getContextArgument: contextName=$contextName, argName=$argName")
        if (contextName.isBlank()) {
            this.handleError("Invalid context name")
            return null
        }
        if (argName.isBlank()) {
            this.handleError("Invalid argument name")
            return null
        }
        if (request.body.result.contexts.isEmpty()) {
            this.handleError("No contexts included in request")
            return null
        }
        request.body.result.contexts.forEach {
            if (it.name === contextName) {
                val argument = ContextArgument(value = getProperty(it.parameters, argName))
//                if (context.parameters[argName + ORIGINAL_SUFFIX]) {
//                    argument.original = context.parameters[argName + ORIGINAL_SUFFIX]
//                }
                //TODO set original value from context
                return argument
            }
        }
        debug("Failed to get context argument value: $argName")
        return null
    }

    fun getProperty(obj: Any?, name: String): Any? {
        if (obj == null) return null
        try {
            return obj.javaClass
                    .getMethod(name)
                    .invoke(obj)
        } catch (e: NoSuchMethodException) {

        }
        return null
    }

    data class ContextArgument(var value: Any?)

    /**
     * Returns the option key user chose from options response.
     * @example
     * * val app = ApiAiApp(request = req, response = res);
     * *
     * * fun pickOption (app: ApiAiApp<Parameter>) {
     * *   if (app.hasSurfaceCapability(app.SurfaceCapabilities.SCREEN_OUTPUT) != null) {
     * *     app.askWithCarousel("Which of these looks good?",
     * *       app.getIncomingCarousel().addItems(
     * *         app.buildOptionItem("another_choice", ["Another choice"]).
     * *         setTitle("Another choice").setDescription("Choose me!")))
     * *   } else {
     * *     app.ask("What would you like?")
     * *   }
     * * }
     * *
     * * fun optionPicked (app: ApiAiApp<Parameter>) {
     * *   assistant.ask("You picked " + app.getSelectedOption())
     * * }
     * *
     * * val actionMap = mapOf(
     * *    "pick.option" to pickOption,
     * *    "option.picked" to optionPicked)
     * *
     * * app.handleRequest(actionMap)
     * *
     * *
     * @return {string} Option key of selected item. Null if no option selected or
     * *     if current intent is not OPTION intent.
     * *
     * @apiai
     */
    fun getSelectedOption(): Any? {
        debug("getSelectedOption")
        if (getContextArgument(SELECT_EVENT, BUILT_IN_ARG_NAMES.OPTION)?.value != null) {
            return getContextArgument(SELECT_EVENT, BUILT_IN_ARG_NAMES.OPTION)?.value
        } else if (getArgument(BUILT_IN_ARG_NAMES.OPTION) != null) {
            return getArgument(BUILT_IN_ARG_NAMES.OPTION)
        }
        debug("Failed to get selected option")
        return null
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
    fun buildResponse(richResponse: RichResponse, expectUserResponse: Boolean, noInputs: MutableList<String>? = null): ResponseWrapper<ApiAiResponse<T>>? {
        debug("buildResponse_: textToSpeech=$richResponse, expectUserResponse=$expectUserResponse, noInputs=$noInputs")
        if (richResponse.isEmpty()) {
            handleError("Invalid text to speech")
            return null
        }
        if (richResponse.items?.first()?.simpleResponse == null || richResponse.items?.first()?.simpleResponse?.textToSpeech.isNullOrBlank()) {
            handleError("Invalid text to speech")
            return null
        }

        val speech = richResponse.items?.first()?.simpleResponse?.textToSpeech!!
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
                richResponse = richResponse,
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
