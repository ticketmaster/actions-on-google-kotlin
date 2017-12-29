package com.tmsdurham.actions

import com.tmsdurham.dialogflow.google.GoogleData
import com.tmsdurham.actions.actions.*
import com.tmsdurham.dialogflow.DialogflowResponse


// Constants
val CONVERSATION_API_AGENT_VERSION_HEADER = "Agent-Version-Label"
val INPUTS_MAX = 3

// ---------------------------------------------------------------------------
//                   Actions SDK support
// ---------------------------------------------------------------------------

/**
 * Completes serialization/deserialization of response, request, dialogState
 */
interface Serializer {
    fun <T> serialize(obj: T): String
    fun <T> deserialize(str: String, clazz: Class<T>): T
}

/**
 * This is the class that handles the conversation API directly from Assistant,
 * providing implementation for all the methods available in the API.
 */
class ActionsSdkApp : AssistantApp<ActionRequest, ActionResponse> {

    val serializer: Serializer

    /**
     * Constructor for ActionsSdkApp object.
     * To be used in the Actions SDK HTTP endpoint logic.
     *
     * @example
     * val ActionsSdkApp = require("actions-on-google").ActionsSdkApp
     * val app = new ActionsSdkApp({request: request, response: response,
     *   sessionStarted:sessionStarted})
     *
     * @param {Object} options JSON configuration.
     * @param {Object} options.request Express HTTP request object.
     * @param {Object} options.response Express HTTP response object.
     * @param {fun=} options.sessionStarted fun callback when session starts.
     * @actionssdk
     */
    constructor(request: RequestWrapper<ActionRequest>,
                response: ResponseWrapper<ActionResponse>,
                sessionStarted: (() -> Unit)? = null,
                serializer: Serializer) :
            super(request, response, sessionStarted) {
        debug("ActionsSdkApp constructor")
        this.serializer = serializer
        response.body = ActionResponse()

        // If request is AoG and in Proto2 format, convert to Proto3.
        //TODO
//        if (!isNotApiVersionOne()) {
//            request.body.transformToCamelCase()
//        }

        if (request.body.conversation?.type == CONVERSATION_STAGES.NEW &&
                sessionStarted != null) {
            sessionStarted()
        }
    }

    /*
     * Gets the request Conversation API version.
     *
     * @example
     * val app = ActionsSdkApp(request = request, response = response)
     * val apiVersion = app.getApiVersion()
     *
     * @return {String} Version value or null if no value.
     * @actionssdk
     */
    fun getApiVersion(): String {
        debug("getApiVersion")
        return if (apiVersion_ != null) apiVersion_ else actionsApiVersion
    }

    /**
     * Gets the user"s raw input query.
     *
     * @example
     * val app = ActionsSdkApp(request = request, response = response)
     * app.tell("You said " + app.getRawInput())
     *
     * @return {String} User"s raw query or null if no value.
     * @actionssdk
     */
    fun getRawInput(): String? {
        debug("getRawInput")
        val input = getTopInput()
        if (input == null) {
            handleError("Failed to get top Input.")
            return null
        }
        if (input?.rawInputs?.size ?: 0 == 0) {
            handleError("Missing user raw input")
            return null
        }
        val rawInput = input.rawInputs?.get(0)
        if (rawInput?.query == null) {
            handleError("Missing query for user raw input")
            return null
        }
        return rawInput.query
    }

    /**
     * Gets previous JSON dialog state that the app sent to Assistant.
     * Alternatively, use the app.data field to store JSON values between requests.
     *
     * @example
     * val app = ActionsSdkApp(request = request, response = response)
     * val dialogState = app.getDialogState()
     *
     * @return {String|DialogState} JSON object provided to the Assistant in the previous
     *     user turn or {} if no value.
     * @actionssdk
     */
    fun getDialogState(): Any? {
        debug("getDialogState")
        if (this.request.body.conversation?.conversationToken?.isNotBlank() ?: false) {
            var token = request.body.conversation?.conversationToken
            var dialogState: MutableMap<String, Any?>? = null
            try {
                dialogState = serializer.deserialize(token!!, mutableMapOf<String, Any?>()::class.java)
            } catch (e: Exception) {
                debug("Error deserializing conversationToken: " + e.message)
            }

            return dialogState
        }
        return mutableMapOf<String, Any?>()
    }

    /**
     * Gets the "versionLabel" specified inside the Action Package.
     * Used by app to do version control.
     *
     * @example
     * val app = ActionsSdkApp(request = request, response = response)
     * val actionVersionLabel = app.getActionVersionLabel()
     *
     * @return {String} The specified version label or null if unspecified.
     * @actionssdk
     */
    fun getActionVersionLabel(): String? {
        debug("getActionVersionLabel")
        val versionLabel = request.get(CONVERSATION_API_AGENT_VERSION_HEADER)
        if (versionLabel != null) {
            return versionLabel
        } else {
            return null
        }
    }

    /**
     * Gets the unique conversation ID. It"s a new ID for the initial query,
     * and stays the same until the end of the conversation.
     *
     * @example
     * val app = ActionsSdkApp(request = request, response = response)
     * val conversationId = app.getConversationId()
     *
     * @return {String} Conversation ID or null if no value.
     * @actionssdk
     */
    fun getConversationId(): String? {
        debug("getConversationId")
        if (request.body.conversation?.conversationId == null) {
            this.handleError("No conversation ID")
            return null
        }
        return request.body.conversation.conversationId
    }

    /**
     * Get the current intent. Alternatively, using a handler Map with
     * {@link AssistantApp#handleRequest|handleRequest}, the client library will
     * automatically handle the incoming intents.
     *
     * @example
     * val app = ActionsSdkApp(request = request, response = response)
     *
     * fun responseHandler (app: ActionSdkApp) {
     *   val intent = app.getIntent()
     *   when (intent) {
     *     app.StandardIntents.MAIN -> {
     *          val inputPrompt = app.buildInputPrompt(false, "Welcome to action snippets! Say anything.")
     *          app.ask(inputPrompt)
     *       }
     *
     *     app.StandardIntents.TEXT ->
     *       app.tell("You said " + app.getRawInput())
     *   }
     * }
     *
     * app.handleRequest(responseHandler)
     *
     * @return {String} Intent id or null if no value.
     * @actionssdk
     */
    override fun getIntent(): String? {
        debug("getIntent")
        val input = getTopInput()
        if (input == null) {
            handleError("Missing intent from request body")
            return null
        }
        return input.intent
    }

    /**
     * Get the argument value by name from the current intent. If the argument
     * is not a text argument, the entire argument object is returned.
     *
     * Note: If incoming request is using an API version under 2 (e.g. "v1"),
     * the argument object will be in Proto2 format (snake_case, etc).
     *
     * @param {String} argName Name of the argument.
     * @return {String} Argument value matching argName
     *     or null if no matching argument.
     * @actionssdk
     */
    fun getArgument(argName: String): Any? {
        return getArgumentCommon(argName)
    }

    /**
     * Returns the option key user chose from options response.
     *
     * @example
     * val app = App(request = req, response = res)
     *
     * fun pickOption (app: ActionSdkApp) {
     *   if (app.hasSurfaceCapability(app.SurfaceCapabilities.SCREEN_OUTPUT)) {
     *     app.askWithCarousel("Which of these looks good?",
     *       app.buildCarousel().addItems(
     *         app.buildOptionItem("another_choice", ["Another choice"]).
     *         setTitle("Another choice").setDescription("Choose me!")))
     *   } else {
     *     app.ask("What would you like?")
     *   }
     * }
     *
     * fun optionPicked (app: ActionsSdkApp) {
     *   app.ask("You picked " + app.getSelectedOption())
     * }
     *
     * val actionMap = mapOf(
     *      app.StandardIntents.TEXT to pickOption,
     *      app.StandardIntents.OPTION to optionPicked)
     *
     * app.handleRequest(actionMap)
     *
     * @return {String} Option key of selected item. Null if no option selected or
     *     if current intent is not OPTION intent.
     * @actionssdk
     */
    fun getSelectedOption(): Any? {
        debug("getSelectedOption")
        if (getArgument(BUILT_IN_ARG_NAMES.OPTION) != null) {
            return getArgument(BUILT_IN_ARG_NAMES.OPTION)
        }
        debug("Failed to get selected option")
        return null
    }

    /**
     * Asks to collect user"s input all user"s queries need to be sent to
     * the app.
     *
     * @example
     * val app = ActionsSdkApp(request = request, response = response)
     *
     * fun mainIntent (app: ActionSdkApp) {
     *   val inputPrompt = app.buildInputPrompt(true, "<speak>Hi! <break time="1"/> " +
     *         "I can read out an ordinal like " +
     *         "<say-as interpret-as="ordinal">123</say-as>. Say a number.</speak>",
     *         ["I didn\"t hear a number", "If you\"re still there, what\"s the number?", "What is the number?"])
     *   app.ask(inputPrompt)
     * }
     *
     * fun rawInput (app: ActionSdkApp) {
     *   if (app.getRawInput() == "bye") {
     *     app.tell("Goodbye!")
     *   } else {
     *     val inputPrompt = app.buildInputPrompt(true, "<speak>You said, <say-as interpret-as="ordinal">" +
     *       app.getRawInput() + "</say-as></speak>",
     *         ["I didn\"t hear a number", "If you\"re still there, what\"s the number?", "What is the number?"])
     *     app.ask(inputPrompt)
     *   }
     * }
     *
     * val actionMap =  mapOf(
     *      app.StandardIntents.MAIN to ::mainIntent,
     *      app.StandardIntents.TEXT to ::rawInput)
     *
     * app.handleRequest(actionMap)
     *
     * @param {Object|SimpleResponse|RichResponse} inputPrompt Holding initial and
     *     no-input prompts.
     * @param {Object=} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by App.
     * @return The response that is sent to Assistant to ask user to provide input.
     * @actionssdk
     */
    fun ask(inputPrompt: SimpleResponse, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<ActionResponse>? {
        debug("ask: inputPrompt=$inputPrompt, dialogState=$dialogState")
        val expectedIntent = buildExpectedIntent(STANDARD_INTENTS.TEXT)
        if (expectedIntent == null) {
            error("Error in building expected intent")
            return null
        }
        return buildAskHelper(inputPrompt, mutableListOf(expectedIntent), dialogState)
    }

    fun ask(init: SimpleResponse.() -> Unit): ResponseWrapper<ActionResponse>? {
        var simpleResponse = SimpleResponse()
        simpleResponse.init()
        return ask(simpleResponse)
    }

    fun ask(inputPrompt: InputPrompt?, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<ActionResponse>? {
        debug("ask: inputPrompt=$inputPrompt, dialogState=$dialogState")
        if (inputPrompt == null) {
            error("InputPrompt can not be null")
            return null
        }
        val expectedIntent = buildExpectedIntent(STANDARD_INTENTS.TEXT)
        if (expectedIntent == null) {
            error("Error in building expected intent")
            return null
        }
        return buildAskHelper(inputPrompt, mutableListOf(expectedIntent), dialogState)
    }


    fun ask(speech: String?, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<ActionResponse>? {
        debug("ask: speech=$speech, dialogState=$dialogState")
        val expectedIntent = buildExpectedIntent(STANDARD_INTENTS.TEXT)
        if (expectedIntent == null) {
            error("Error in building expected intent")
            return null
        }
        return buildAskHelper(speech, mutableListOf(expectedIntent), dialogState)
    }


    fun ask(inputPrompt: RichResponse?, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<ActionResponse>? {
        debug("ask: inputPrompt=$inputPrompt, dialogState=$dialogState")
        val expectedIntent = buildExpectedIntent(STANDARD_INTENTS.TEXT)
        if (expectedIntent == null) {
            error("Error in building expected intent")
            return null
        }
        return buildAskHelper(inputPrompt, mutableListOf(expectedIntent), dialogState)
    }

    private fun buildAskHelper(inputPrompt: RichResponse?, possibleIntents: MutableList<ActionsSdkApp.ExpectedIntent>, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ActionResponse>? {
        debug("ask: inputPrompt=$inputPrompt, dialogState=$dialogState")

        val inputPrompt = InputPrompt(richInitialPrompt = inputPrompt)
        return buildAskHelper(inputPrompt, possibleIntents, dialogState)
    }

    /**
     * Asks to collect user"s input with a list.
     *
     * @example
     * val app = ActionsSdkApp(request, response)
     *
     * fun welcomeIntent (app: ActionSdkApp) {
     *   app.askWithlist("Which of these looks good?",
     *     app.buildList("List title")
     *      .addItems([
     *        app.buildOptionItem(SELECTION_KEY_ONE,
     *          ["synonym of KEY_ONE 1", "synonym of KEY_ONE 2"])
     *          .setTitle("Number one"),
     *        app.buildOptionItem(SELECTION_KEY_TWO,
     *          ["synonym of KEY_TWO 1", "synonym of KEY_TWO 2"])
     *          .setTitle("Number two"),
     *      ]))
     * }
     *
     * fun optionIntent (app: ActionSDKApp) {
     *   if (app.getSelectedOption() == SELECTION_KEY_ONE) {
     *     app.tell("Number one is a great choice!")
     *   } else {
     *     app.tell("Number two is a great choice!")
     *   }
     * }
     *
     * val actionMap = mapOf(
     *      app.StandardIntents.TEXT to ::welcomeIntent
     *      app.StandardIntents.OPTION to ::optionIntent)
     * app.handleRequest(actionMap)
     *
     * @param {Object|SimpleResponse|RichResponse} inputPrompt Holding initial and
     *     no-input prompts. Cannot contain basic card.
     * @param {List} list List built with {@link AssistantApp#buildList|buildList}.
     * @param {Object=} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant.
     * @return The response that is sent to Assistant to ask user to provide input.
     * @actionssdk
     */
    fun askWithList(inputPrompt: Any, list: List, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<ActionResponse>? {
        debug("askWithList: inputPrompt=$inputPrompt, list=$list, dialogState=$dialogState")
        if (list.items.size < 2) {
            handleError("List requires at least 2 items")
            return null
        }
        val expectedIntent = buildExpectedIntent(STANDARD_INTENTS.OPTION)
        if (expectedIntent == null) {
            error("Error in building expected intent")
            return null
        }
        if (isNotApiVersionOne()) {
            expectedIntent.inputValueData {
                `@type` = INPUT_VALUE_DATA_TYPES.OPTION
                listSelect = list
            }
        } else {
            expectedIntent.inputValueSpec {
                optionValueSpec = GoogleData.OptionValueSpec(
                        listSelect = list)
            }
        }
        return when (inputPrompt) {
            is String -> buildAskHelper(inputPrompt, mutableListOf(expectedIntent), dialogState)
            is SimpleResponse -> buildAskHelper(inputPrompt, mutableListOf(expectedIntent), dialogState)
            is RichResponse -> buildAskHelper(inputPrompt, mutableListOf(expectedIntent), dialogState)
            else -> {
                error("unknown inputPrompt type")
                null
            }
        }
    }

    /**
     * Asks user for delivery address.
     *
     * @example
     * val app = ActionsSdkApp(request, response)
     * val WELCOME_INTENT = app.StandardIntents.MAIN
     * val DELIVERY_INTENT = app.StandardIntents.DELIVERY_ADDRESS
     *
     * fun welcomeIntent (app: ActionSdkApp) {
     *   app.askForDeliveryAddress("To make sure I can deliver to you")
     * }
     *
     * fun addressIntent (app: ActionSdkApp) {
     *   val postalCode = app.getDeliveryAddress().postalAddress.postalCode
     *   if (isInDeliveryZone(postalCode)) {
     *     app.tell("Great looks like you\"re in our delivery area!")
     *   } else {
     *     app.tell("I\"m sorry it looks like we can\"t deliver to you.")
     *   }
     * }
     *
     * val actionMap = mapOf(
     *      WELCOME_INTENT to ::welcomeIntent,
     *      DELIVERY_INTENT to ::addressIntent)
     * app.handleRequest(actionMap)
     *
     * @param {String} reason Reason given to user for asking delivery address.
     * @param {Object=} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant.
     * @return {Object} HTTP response.
     * @dialogflow
     */
    fun askForDeliveryAddress(reason: String, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<ActionResponse>? {
        debug("askForDeliveryAddress: reason=$reason")
        if (reason.isEmpty()) {
            handleError("reason cannot be empty")
            return null
        }
        val expectedIntent = buildExpectedIntent(STANDARD_INTENTS.DELIVERY_ADDRESS)
        if (expectedIntent == null) {
            error("Error in building expected intent")
            return null
        }
        expectedIntent.inputValueData {
            `@type` = INPUT_VALUE_DATA_TYPES.DELIVERY_ADDRESS
            addressOptions = GoogleData.AddressOptions(reason = reason)
        }

        val inputPrompt = buildInputPrompt(false,
                "PLACEHOLDER_FOR_DELIVERY_ADDRESS")
        return buildAskHelper(inputPrompt, mutableListOf(expectedIntent), dialogState)
    }

    /**
     * Asks to collect user"s input with a carousel.
     *
     * @example
     * val app = ActionsSdkApp(request, response)
     *
     * fun welcomeIntent (app: ActionSdkApp) {
     *   app.askWithCarousel("Which of these looks good?",
     *     app.buildCarousel()
     *      .addItems([
     *        app.buildOptionItem(SELECTION_KEY_ONE,
     *          ["synonym of KEY_ONE 1", "synonym of KEY_ONE 2"])
     *          .setTitle("Number one"),
     *        app.buildOptionItem(SELECTION_KEY_TWO,
     *          ["synonym of KEY_TWO 1", "synonym of KEY_TWO 2"])
     *          .setTitle("Number two"),
     *      ]))
     * }
     *
     * fun optionIntent (app: ActionSdk) {
     *   if (app.getSelectedOption() == SELECTION_KEY_ONE) {
     *     app.tell("Number one is a great choice!")
     *   } else {
     *     app.tell("Number two is a great choice!")
     *   }
     * }
     *
     * val actionMap = mapOf(
     *      app.StandardIntents.TEXT to ::welcomeIntent)
     *      app.StandardIntents.OPTION to ::optionIntent)
     *      app.handleRequest(actionMap)
     *
     * @param {Object|SimpleResponse|RichResponse} inputPrompt Holding initial and
     *     no-input prompts. Cannot contain basic card.
     * @param {Carousel} carousel Carousel built with
     *      {@link AssistantApp#buildCarousel|buildCarousel}.
     * @param {Object=} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant.
     * @return The response that is sent to Assistant to ask user to provide input.
     * @actionssdk
     */
    fun askWithCarousel(inputPrompt: Any, carousel: Carousel, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<ActionResponse>? {
        debug("askWithCarousel: inputPrompt=$inputPrompt, carousel=$carousel, dialogState=$dialogState")

        if (carousel.items.size < 2) {
            this.handleError("Carousel requires at least 2 items")
            return null
        }
        val expectedIntent = buildExpectedIntent(STANDARD_INTENTS.OPTION)
        if (expectedIntent == null) {
            error("Error in building expected intent")
            return null
        }
        if (isNotApiVersionOne()) {
            expectedIntent.inputValueData {
                `@type` = INPUT_VALUE_DATA_TYPES.OPTION
                carouselSelect = carousel
            }
        } else {
            expectedIntent.inputValueSpec {
                optionValueSpec {
                    carouselSelect = carousel
                }
            }
        }
        return when (inputPrompt) {
            is String -> buildAskHelper(inputPrompt, mutableListOf(expectedIntent), dialogState)
            is SimpleResponse -> buildAskHelper(inputPrompt, mutableListOf(expectedIntent), dialogState)
            is RichResponse -> buildAskHelper(inputPrompt, mutableListOf(expectedIntent), dialogState)
            else -> {
                error("unknown inputPrompt type")
                null
            }
        }
    }

    /**
     * Tells Assistant to render the speech response and close the mic.
     *
     * @example
     * val app = ActionsSdkApp(request = request, response = response)
     *
     * fun mainIntent (app) {
     *   val inputPrompt = app.buildInputPrompt(true, "<speak>Hi! <break time="1"/> " +
     *         "I can read out an ordinal like " +
     *         "<say-as interpret-as="ordinal">123</say-as>. Say a number.</speak>",
     *         ["I didn\"t hear a number", "If you\"re still there, what\"s the number?", "What is the number?"])
     *   app.ask(inputPrompt)
     * }
     *
     * fun rawInput (app) {
     *   if (app.getRawInput() == "bye") {
     *     app.tell("Goodbye!")
     *   } else {
     *     val inputPrompt = app.buildInputPrompt(true, "<speak>You said, <say-as interpret-as="ordinal">" +
     *       app.getRawInput() + "</say-as></speak>",
     *         ["I didn\"t hear a number", "If you\"re still there, what\"s the number?", "What is the number?"])
     *     app.ask(inputPrompt)
     *   }
     * }
     *
     * val actionMap = new Map()
     * actionMap.set(app.StandardIntents.MAIN, mainIntent)
     * actionMap.set(app.StandardIntents.TEXT, rawInput)
     *
     * app.handleRequest(actionMap)
     *
     * @param {string|SimpleResponse|RichResponse} textToSpeech Final response.
     *     Spoken response can be SSML.
     * @return The HTTP response that is sent back to Assistant.
     * @actionssdk
     */
    override fun tell(speech: String, displayText: String?): ResponseWrapper<ActionResponse>? {
        debug("tell: speech=$speech displayText=$displayText")
        val simpleResponse = SimpleResponse()
        val finalResponse = FinalResponse()
        if (isSsml(speech)) {
            simpleResponse.ssml = speech
        } else {
            simpleResponse.textToSpeech = speech
        }
        simpleResponse.displayText = displayText
        if (displayText.isNullOrBlank()) {
            finalResponse.speechResponse = simpleResponse
        } else {
            finalResponse.richResponse = buildRichResponse().addSimpleResponse(simpleResponse)
        }

        val response = buildResponseHelper(null, false, null, finalResponse)
        return this.doResponse(response, RESPONSE_CODE_OK)
    }

    override fun tell(speech: String): ResponseWrapper<ActionResponse>? = tell(speech, null)

    override fun tell(simpleResponse: SimpleResponse): ResponseWrapper<ActionResponse>? {
        debug("tell: simpleResponse=$simpleResponse")
        val finalResponse = FinalResponse()

        finalResponse.richResponse = this.buildRichResponse()
                .addSimpleResponse(simpleResponse)

        val response = buildResponseHelper(null, false, null, finalResponse)
        return this.doResponse(response, RESPONSE_CODE_OK)
    }

    override fun tell(richResponse: RichResponse?): ResponseWrapper<ActionResponse>? {
        debug("tell: richResponse=$richResponse")
        val finalResponse = FinalResponse()
        finalResponse.richResponse = richResponse

        val response = buildResponseHelper(null, false, null, finalResponse)
        return doResponse(response, RESPONSE_CODE_OK)
    }

    /**
     * Builds the {@link https://developers.google.com/actions/reference/conversation#InputPrompt|InputPrompt object}
     * from initial prompt and no-input prompts.
     *
     * The App needs one initial prompt to start the conversation. If there is no user response,
     * the App re-opens the mic and renders the no-input prompts three times
     * (one for each no-input prompt that was configured) to help the user
     * provide the right response.
     *
     * Note: we highly recommend app to provide all the prompts required here in order to ensure a
     * good user experience.
     *
     * @example
     * val inputPrompt = app.buildInputPrompt(false, "Welcome to action snippets! Say a number.",
     *     ["Say any number", "Pick a number", "What is the number?"])
     * app.ask(inputPrompt)
     *
     * @param {boolean} isSsml Indicates whether the text to speech is SSML or not.
     * @param {string} initialPrompt The initial prompt the App asks the user.
     * @param {Array<string>=} noInputs Array of re-prompts when the user does not respond (max 3).
     * @return {Object} An {@link https://developers.google.com/actions/reference/conversation#InputPrompt|InputPrompt object}.
     * @actionssdk
     */
    fun buildInputPrompt(isSsml: Boolean, initialPrompt: String, noInputs: MutableList<String> = mutableListOf()): InputPrompt? {
        debug("buildInputPrompt: isSsml=$isSsml, initialPrompt=$initialPrompt, noInputs=$noInputs")
        val initials = mutableListOf<String>()

        if (noInputs.isNotEmpty()) {
            if (noInputs.size > INPUTS_MAX) {
                handleError("Invalid number of no inputs")
                return null
            }
        }

        maybeAddItemToArray(initialPrompt, initials)
        if (isSsml) {
            return InputPrompt(
                    initialPrompts = buildPromptsFromSsmlHelper(initials),
                    noInputPrompts = buildPromptsFromSsmlHelper(noInputs.map { it }.toMutableList())
            )
        } else {
            return InputPrompt(
                    initialPrompts = buildPromptsFromPlainTextHelper(initials),
                    noInputPrompts = buildPromptsFromPlainTextHelper(noInputs.map { it }.toMutableList()))
        }
    }

// ---------------------------------------------------------------------------
//                   Private Helpers
// ---------------------------------------------------------------------------

    /**
     * Get the top most Input object.
     *
     * @return {object} Input object.
     * @private
     * @actionssdk
     */
    private fun getTopInput(): Input? {
        debug("getTopInput")
        if (request.body.inputs?.size == 0) {
            this.handleError("Missing inputs from request body")
            return null
        }
        return request.body.inputs?.get(0)
    }

    /**
     * Builds the response to send back to Assistant.
     *
     * @param {string} conversationToken The dialog state.
     * @param {boolean} expectUserResponse The expected user response.
     * @param {Object} expectedInput The expected response.
     * @param {boolean} finalResponse The final response.
     * @return {Object} Final response returned to server.
     * @private
     * @actionssdk
     */
    fun buildResponseHelper(conversationToken: String?, expectUserResponse: Boolean, expectedInput: ExpectedInput?, finalResponse: FinalResponse?): ResponseWrapper<ActionResponse> {
        debug("buildResponseHelper: conversationToken=$conversationToken, expectUserResponse=$expectUserResponse, " +
                "expectedInput=$expectedInput, finalResponse=$finalResponse")
        response.body = ActionResponse()
        if (!conversationToken.isNullOrBlank()) {
            response.body?.conversationToken = conversationToken
        }
        response.body?.expectUserResponse = expectUserResponse
        if (expectedInput != null) {
            response.body?.expectedInputs = mutableListOf(expectedInput)
        }
        if (!expectUserResponse && finalResponse != null) {
            response.body?.finalResponse = finalResponse
        }
        return response
    }

    /**
     * Helper to add item to an array.
     *
     * @private
     * @actionssdk
     */
    private fun <T> maybeAddItemToArray(item: T, array: MutableList<T>): Unit {
        debug("maybeAddItemToArray_: item=$item, array=$array")
        if (item == null) {
            // ignore add
            return
        }
        array.add(item)
    }

    /**
     * Extract session data from the incoming JSON request.
     *
     * @private
     * @actionssdk
     */
    override fun extractData() {
        debug("extractData")
        if (request.body.conversation?.conversationToken != null) {
            val json = request.body.conversation.conversationToken
              //TODO extract state from token
//            data = json.data
//            state = json?.get("state") as String?
        } else {
            data = mutableMapOf()
        }
    }

    /**
     * Uses a PermissionsValueSpec object to construct and send a
     * permissions request to user.
     *
     * @param {Object} permissionsValueSpec PermissionsValueSpec object containing
     *     the permissions prefix and the permissions requested.
     * @param {Object} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant.
     * @return {Object} HTTP response object.
     * @private
     * @actionssdk
     */
    override fun fulfillPermissionsRequest(permissionsSpec: GoogleData.PermissionsRequest, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ActionResponse>? {
        debug("fulfillPermissionsRequest_: permissionsValueSpec=$permissionsSpec, dialogState=$dialogState")
        // Build an Expected Intent object.
        val expectedIntent = ExpectedIntent(
                intent = STANDARD_INTENTS.PERMISSION)
        if (isNotApiVersionOne()) {
            expectedIntent.inputValueData {
                `@type` = INPUT_VALUE_DATA_TYPES.PERMISSION
                optContext = permissionsSpec.optContext
                permissions = permissionsSpec.permissions
                expectUserResponse = permissionsSpec.expectUserResponse
            }
        } else {
            expectedIntent.inputValueSpec = InputValueSpec(
                    permissionValueSpec = permissionsSpec)
        }
        val inputPrompt = this.buildInputPrompt(false, "PLACEHOLDER_FOR_PERMISSION")
        var outDialogState = dialogState
        if (dialogState == null) {
            outDialogState = mutableMapOf(
                    "state" to state,
                    "data" to this.data)
        }
        return buildAskHelper(inputPrompt, mutableListOf(expectedIntent), outDialogState)
    }

    /**
     * Uses TransactionRequirementsCheckValueSpec to construct and send a
     * transaction requirements request to Google.
     *
     * @param {Object} transactionRequirementsSpec TransactionRequirementsSpec
     *     object.
     * @param {Object} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant.
     * @return {Object} HTTP response.
     * @private
     * @actionssdk
     */
    override fun fulfillTransactionRequirementsCheck(transactionRequirementsSpec: TransactionRequirementsCheckSpec, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ActionResponse>? {
        debug("fulfillTransactionRequirementsCheck: transactionRequirementsSpec=$transactionRequirementsSpec," +
                " dialogState=$dialogState")
        // Build an Expected Intent object.
        val expectedIntent = ExpectedIntent(
                intent = STANDARD_INTENTS.TRANSACTION_REQUIREMENTS_CHECK)
        expectedIntent.inputValueData {
            `@type` = INPUT_VALUE_DATA_TYPES.TRANSACTION_REQ_CHECK
            paymentOptions = transactionRequirementsSpec.paymentOptions
            orderOptions = transactionRequirementsSpec.orderOptions
        }

        val inputPrompt = this.buildInputPrompt(false, "PLACEHOLDER_FOR_TXN_REQUIREMENTS")
        var outState = dialogState
        if (dialogState == null) {
            outState = mutableMapOf(
                    "state" to this.state,
                    "data" to this.data)
        }
        return buildAskHelper(inputPrompt, mutableListOf(expectedIntent), outState)
    }

    /**
     * Uses TransactionDecisionValueSpec to construct and send a transaction
     * requirements request to Google.
     *
     * @param {Object} transactionDecisionValueSpec TransactionDecisionValueSpec
     *     object.
     * @param {Object} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant.
     * @return {Object} HTTP response.
     * @private
     * @actionssdk
     */
    override fun fulfillTransactionDecision(transactionDecisionValueSpec: TransactionDecisionValueSpec, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ActionResponse>? {
        debug("fulfillTransactionDecision: transactionDecisionValueSpec=$transactionDecisionValueSpec" +
                " dialogState=$dialogState")
        // Build an Expected Intent object.
        val expectedIntent = ExpectedIntent(
                intent = STANDARD_INTENTS.TRANSACTION_DECISION
        )
        expectedIntent.inputValueData {
            `@type` = INPUT_VALUE_DATA_TYPES.TRANSACTION_DECISION
            proposedOrder = transactionDecisionValueSpec.proposedOrder
            orderOptions = transactionDecisionValueSpec.orderOptions
            paymentOptions = transactionDecisionValueSpec.paymentOptions
        }

        // Send an Ask request to Assistant.
        val inputPrompt = this.buildInputPrompt(false, "PLACEHOLDER_FOR_TXN_DECISION")
        var outDialogState = dialogState
        if (dialogState == null) {
            outDialogState = mutableMapOf(
                    "state" to this.state,
                    "data" to this.data)
        }
        return buildAskHelper(inputPrompt, mutableListOf(expectedIntent), outDialogState)
    }

    /**
     * Uses ConfirmationValueSpec to construct and send a confirmation request to
     * Google.
     *
     * @param {Object} confirmationValueSpec ConfirmationValueSpec object.
     * @return {Object} HTTP response.
     * @private
     * @actionssdk
     */
    override fun fulfillConfirmationRequest(confirmationValueSpec: ConfirmationValueSpec, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ActionResponse>? {
        debug("fulfillConfirmationRequest_: confirmationValueSpec=$confirmationValueSpec," +
                " dialogState=$dialogState")
        // Build an Expected Intent object.
        val expectedIntent = ExpectedIntent(
                intent = STANDARD_INTENTS.CONFIRMATION)
        expectedIntent.inputValueData {
            `@type` = INPUT_VALUE_DATA_TYPES.CONFIRMATION
            dialogSpec = confirmationValueSpec.dialogSpec
        }

        // Send an Ask request to Assistant.
        val inputPrompt = this.buildInputPrompt(false, "PLACEHOLDER_FOR_CONFIRMATION")
        var outDialogState = dialogState
        if (dialogState == null) {
            outDialogState = mutableMapOf(
                    "state" to this.state,
                    "data" to this.data)
        }
        return buildAskHelper(inputPrompt, mutableListOf(expectedIntent), outDialogState)
    }

    data class DateTimeValueSpec(var tmp: String? = null)

    /**
     * Uses DateTimeValueSpec to construct and send a datetime request to Google.
     *
     * @param {Object} dateTimeValueSpec DateTimeValueSpec object.
     * @return {Object} HTTP response.
     * @private
     * @actionssdk
     */
    override fun fulfillDateTimeRequest(confirmationValueSpec: ConfirmationValueSpec, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ActionResponse>? {
        debug("fulfillDateTimeRequest_: dateTimeValueSpec=$confirmationValueSpec," +
                " dialogState=$dialogState")
        // Build an Expected Intent object.
        val expectedIntent = ExpectedIntent(
                intent = STANDARD_INTENTS.DATETIME)
        expectedIntent.inputValueData {
            `@type` = INPUT_VALUE_DATA_TYPES.DATETIME
            dialogSpec = confirmationValueSpec.dialogSpec
        }

        // Send an Ask request to Assistant.
        val inputPrompt = this.buildInputPrompt(false, "PLACEHOLDER_FOR_DATETIME")
        var outDialogState = dialogState
        if (dialogState == null) {
            outDialogState = mutableMapOf(
                    "state" to this.state,
                    "data" to this.data)
        }
        return buildAskHelper(inputPrompt, mutableListOf(expectedIntent), outDialogState)
    }

    /**
     * Construct and send a sign in request to Google.
     *
     * @return {Object} HTTP response.
     * @private
     * @actionssdk
     */
    override fun fulfillSignInRequest(dialogState: MutableMap<String, Any?>?): ResponseWrapper<ActionResponse>? {
        debug("fulfillSignInRequest_: dialogState=$dialogState")
        // Build an Expected Intent object.
        val expectedIntent = ExpectedIntent(
                intent = STANDARD_INTENTS.SIGN_IN)
        expectedIntent.inputValueData = InputValueData()
        // Send an Ask request to Assistant.
        val inputPrompt = buildInputPrompt(false, "PLACEHOLDER_FOR_SIGN_IN")
        var outDialogState = dialogState
        if (dialogState == null) {
            outDialogState = mutableMapOf(
                    "state" to this.state,
                    "data" to data)
        }
        return buildAskHelper(inputPrompt, mutableListOf(expectedIntent), outDialogState)
    }


    override fun fulfillSystemIntent(intent: String, specType: String, intentSpec: NewSurfaceValueSpec, promptPlaceholder: String?, dialogState: MutableMap<String, Any?>?): ResponseWrapper<DialogflowResponse>? {
        TODO("not implemented for Actions SDK - please make an issue on actions-on-google-kotlin if this is needed")
    }

    override fun fulfillRegisterUpdateIntent(intent: String, specType: String, intentSpec: RegisterUpdateValueSpec, promptPlaceholder: String?, dialogState: MutableMap<String, Any?>?): ResponseWrapper<DialogflowResponse>? {
        TODO("not implemented for Actions SDK - please make an issue on actions-on-google-kotlin if this is needed")
    }

    /**
     * Builds the ask response to send back to Assistant.
     *
     * @param {InputPrompt} inputPrompt Holding initial and no-input prompts.
     * @param {Array} possibleIntents Array of ExpectedIntents.
     * @param {DialogState} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant.
     * @return The response that is sent to Assistant to ask user to provide input.
     * @private
     * @actionssdk
     */
    private fun buildAskHelper(inputPrompt: InputPrompt?, possibleIntents: MutableList<ActionsSdkApp.ExpectedIntent>, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ActionResponse>? {
        debug("buildAskHelper_: inputPrompt=$inputPrompt, possibleIntents,  dialogState=$dialogState")
        if (inputPrompt == null) {
            handleError("Invalid input prompt")
            return null
        }

        var outDialogState = dialogState
        if (dialogState == null) {
            outDialogState = mutableMapOf(
                    "state" to this.state,
                    "data" to this.data)
        }

        val expectedInputs = ExpectedInput(
                inputPrompt = inputPrompt,
                possibleIntents = possibleIntents
        )
        buildResponseHelper(
                serializer.serialize(outDialogState),
                true, // expectedUserResponse
                expectedInputs,
                null // finalResponse is null b/c dialog is active
        )
        return doResponse(response, RESPONSE_CODE_OK)
    }

    fun buildAskHelper(inputPrompt: String?, possibleIntents: MutableList<ExpectedIntent>, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ActionResponse>? {
        debug("buildAskHelper: inputPrompt=$inputPrompt, possibleIntents=$possibleIntents,  dialogState=$dialogState")
        if (inputPrompt == null) {
            handleError("Invalid input prompt")
            return null
        }
        val inputPrompt = buildInputPrompt(isSsml(inputPrompt), inputPrompt)
        return buildAskHelper(inputPrompt, possibleIntents, dialogState)
    }

    private fun buildAskHelper(simpleResponse: SimpleResponse, possibleIntents: MutableList<ExpectedIntent>, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ActionResponse>? {
        debug("buildAskHelper: inputPrompt=$simpleResponse, possibleIntents=$possibleIntents,  dialogState=$dialogState")
        if (simpleResponse == null) {
            handleError("Invalid input prompt")
            return null
        }
//        val inputPrompt = buildInputPrompt(isSsml(simpleResponse.textToSpeech ?: ""), simpleResponse.textToSpeech ?: "")
        val inputPrompt = InputPrompt(richInitialPrompt = buildRichResponse().addSimpleResponse(simpleResponse))
        return buildAskHelper(inputPrompt, possibleIntents, dialogState)
    }

    /**
     * Builds an ExpectedIntent object. Refer to {@link ActionsSdkApp#newRuntimeEntity} to create the list
     * of runtime entities required by this method. Runtime entities need to be defined in
     * the Action Package.
     *
     * @param {string} intent Developer specified in-dialog intent inside the Action
     *     Package or an App built-in intent like
     *     "assistant.intent.action.TEXT".
     * @return {Object} An {@link https://developers.google.com/actions/reference/conversation#ExpectedIntent|ExpectedIntent object}
    encapsulating the intent and the runtime entities.
     * @private
     * @actionssdk
     */
    fun buildExpectedIntent(intent: String): ExpectedIntent? {
        debug("buildExpectedIntent_: intent=$intent")
        if (intent.isBlank()) {
            handleError("Invalid intent")
            return null
        }
        val expectedIntent = ExpectedIntent(
                intent = intent)
        return expectedIntent
    }

    data class ExpectedIntent(var intent: String? = null,
                              var inputValueType: String? = null,
                              var inputValueSpec: InputValueSpec? = null,
                              var inputValueData: InputValueData? = null) {

        fun inputValueData(init: InputValueData.() -> Unit): InputValueData {
            if (inputValueData == null) {
                inputValueData = InputValueData()
            }
            inputValueData?.init()
            return inputValueData!!
        }

        fun inputValueSpec(init: InputValueSpec.() -> Unit) {
            if (inputValueSpec == null) {
                inputValueData = InputValueData()
            }
            inputValueSpec?.init()
        }


    }

    data class InputValueSpec(var permissionValueSpec: GoogleData.PermissionsRequest? = null,
                              var optionValueSpec: GoogleData.OptionValueSpec? = null) {
        fun optionValueSpec(init: GoogleData.OptionValueSpec.() -> Unit) {
            if (optionValueSpec == null) {
                optionValueSpec = GoogleData.OptionValueSpec()
            }
            optionValueSpec?.init()
        }
    }

    data class InputValueData(
            var `@type`: String = "",
            var addressOptions: GoogleData.AddressOptions? = null,
            var listSelect: List? = null,
            var proposedOrder: Order? = null,
            var orderOptions: GoogleData.OrderOptions? = null,
            var paymentOptions: GoogleData.PaymentOptions? = null,
            var dialogSpec: AssistantApp.DialogSpec? = null,
            var carouselSelect: Carousel? = null,
            var optContext: String? = null,
            var permissions: MutableList<String>? = null,
            var expectUserResponse: Boolean = false)

}

data class ExpectedInput(var inputPrompt: InputPrompt? = null,
                         var possibleIntents: MutableList<ActionsSdkApp.ExpectedIntent>? = null)

