package com.tmsdurham.actions

import com.ticketmaster.apiai.*
import com.ticketmaster.apiai.google.GoogleData


class ApiAiApp : AssistantApp<ApiAiRequest, ApiAiResponse> {


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


    constructor(request: RequestWrapper<ApiAiRequest>, response: ResponseWrapper<ApiAiResponse>, sessionStarted: (() -> Unit)? = null) :
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
     * Verifies whether the request comes from API.AI.
     *
     * @param {String} key The header key specified by the developer in the
     *     API.AI Fulfillment settings of the app.
     * @param {String} value The private value specified by the developer inside the
     *     fulfillment header.
     * @return {Boolean} True if the request comes from API.AI.
     * @apiai
     */
    fun isRequestFromApiAi(key: String, value: String): Boolean {
        debug("isRequestFromApiAi: key=$key, value=$value")
        if (key.isBlank()) {
            handleError("key must be specified.")
            return false
        }
        if (value.isBlank()) {
            handleError("value must be specified.")
            return false
        }
        return request.get(key) == value
    }

    /**
     * Get the current intent. Alternatively, using a handler Map with
     * {@link AssistantApp#handleRequest|handleRequest},
     * the client library will automatically handle the incoming intents.
     *
     * @example
     * val app = ApiAiApp(request = request, response = response)
     *
     * fun responseHandler (app: ApiAiApp) {
     *   val intent = app.getIntent()
     *   when (intent) {
     *     WELCOME_INTENT ->
     *       app.ask("Welcome to action snippets! Say a number.")
     *
     *     NUMBER_INTENT -> {
     *       val number = app.getArgument(NUMBER_ARGUMENT)
     *       app.tell("You said $number")
     *       }
     *   }
     * }
     *
     * app.handleRequest(responseHandler);
     *
     * @return {string} Intent id or null if no value.
     * @apiai
     */
    override fun getIntent(): String? {
        debug("getIntent")
        if (request.body.result?.action == null) {
            handleError("Missing result from request body")
            return null
        }
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
     * @param {String} argName Name of the argument.
     * @return {Object} Argument value matching argName
     *     or null if no matching argument.
     * @apiai
     */
    fun getArgument(argName: String): Any? {
        debug("getArgument: argName=$argName")
        if (argName.isBlank()) {
            this.handleError("Invalid argument name")
            return null
        }
        val parameters = request.body.result.parameters
        if (parameters != null) {
            if (parameters[argName] != null) {
                return parameters[argName]
            }
        }
        return requestExtractor.getArgumentCommon(argName)
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
    fun getContextArgument(contextName: String, argName: String): ContextArgument? {
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
                if (it.parameters != null) {
                    val argument = ContextArgument(value = it.parameters!![argName])
                    if (it.parameters!![argName + ORIGINAL_SUFFIX] != null) {
                        argument.original = it.parameters!![argName + ORIGINAL_SUFFIX]
                    }
                    return argument
                }
            }
        }
        debug("Failed to get context argument value: $argName")
        return null
    }


    /**
     * Returns the RichResponse constructed in API.AI response builder.
     *
     * @example
     * val app = ApiAiApp(request = req, response = res)
     *
     * fun tellFact (app: ApiAiApp<T>) {
     *   val fact = "Google was founded in 1998"
     *
     *   if (app.hasSurfaceCapability(app.SurfaceCapabilities.SCREEN_OUTPUT)) {
     *     app.ask(app.getIncomingRichResponse().addSimpleResponse("Here\"s a " +
     *       "fact for you. " + fact + " Which one do you want to hear about " +
     *       "next, Google\"s history or headquarters?"))
     *   } else {
     *     app.ask("Here\"s a fact for you. " + fact + " Which one " +
     *       "do you want to hear about next, Google\"s history or headquarters?")
     *   }
     * }
     *
     * val actionMap = mapOf("tell.fact" to tellFact)
     *
     * app.handleRequest(actionMap)
     *
     * @return {RichResponse} RichResponse created in API.AI. If no RichResponse was
     *     created, an empty RichResponse is returned.
     * @apiai
     */
    fun getIncomingRichResponse(): RichResponse {
        debug("getIncomingRichResponse")
        val response = buildRichResponse()
        request.body.result.fulfillment?.messages?.forEach {
            when (it.type) {
                SIMPLE_RESPONSE -> {
                    val item = SimpleResponse()
                    item.textToSpeech = it.textToSpeech
                    item.displayText = it.displayText
                    if (response.items.size == 0) {
                        response.items.add(RichResponseItem(item))
                    } else {
                        response.items.add(0, RichResponseItem(item))
                    }
                }
                BASIC_CARD -> {
                    val item = BasicCard()
                    item.formattedText = it.formattedText
                    if (it.buttons != null) {
                        item.buttons = it.buttons!!
                    }
                    item.image = it.image
                    item.subtitle = it.subtitle
                    item.title = it.title ?: ""
                    response.items.add(RichResponseItem(basicCard = item))
                }
                SUGGESTIONS -> {
                    response.suggestions = it.suggestions
                }
                LINK_OUT_SUGGESTION -> response.linkOutSuggestion = LinkOutSuggestion(it.destinationName, it.url)
            }
        }
        return response
    }

    /**
     * Returns the List constructed in API.AI response builder.
     *
     * @example
     * val app = ApiAiApp(request = req, response = res)
     *
     * fun pickOption (app: ApiAiApp) {
     * if (app.hasSurfaceCapability(app.SurfaceCapabilities.SCREEN_OUTPUT)) {
     *     app.askWithList("Which of these looks good?",
     *       app.getIncomingList().addItems(
     *         app.buildOptionItem("another_choice", ["Another choice"]).
     *         setTitle("Another choice")))
     *   } else {
     *     app.ask("What would you like?")
     *   }
     * }
     *
     * val actionMap = mapOf(
     *      "pick.option" to ::pickOption)
     *
     * app.handleRequest(actionMap)
     *
     * @return {List} List created in API.AI. If no List was created, an empty
     *     List is returned.
     * @apiai
     */
    fun getIncomingList(): List {
        debug("getIncomingList")
        val list = buildList()
        request.body.result.fulfillment?.messages?.forEach {
            if (it.type == LIST) {
                list.title = it.title
                list.items = it.items
            }
        }
        return list
    }


    /**
     * Returns the Carousel constructed in API.AI response builder.
     *
     * @example
     * val app = ApiAiApp(request = req, response = res)
     *
     * fun pickOption (app: ApiAiApp) {
     * if (app.hasSurfaceCapability(app.SurfaceCapabilities.SCREEN_OUTPUT)) {
     *     app.askWithCarousel("Which of these looks good?",
     *       app.getIncomingCarousel().addItems(
     *         app.buildOptionItem("another_choice", ["Another choice"]).
     *         setTitle("Another choice").setDescription("Choose me!")))
     *   } else {
     *     app.ask("What would you like?")
     *   }
     * }
     *
     * val actionMap = mapOf(
     *  "pick.option" to ::pickOption)
     *
     * app.handleRequest(actionMap)
     *
     * @return {Carousel} Carousel created in API.AI. If no Carousel was created,
     *     an empty Carousel is returned.
     * @apiai
     */
    fun getIncomingCarousel(): Carousel {
        debug("getIncomingCarousel")
        val carousel = buildCarousel()
        request.body.result.fulfillment?.messages?.forEach {
            if (it.type == CAROUSEL) {
                carousel.items = it.items ?: mutableListOf()
            }
        }
        return carousel
    }

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

    data class ContextArgument(var value: Any?, var original: Any? = null)

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
    fun ask(inputPrompt: RichResponse, noInputs: MutableList<String>? = null): ResponseWrapper<ApiAiResponse>? {
        debug("ask: inputPrompt=$inputPrompt, noInputs=$noInputs")
        if (inputPrompt.isEmpty()) {
            handleError("Invalid input prompt")
            return null
        }
        val response = buildResponse(inputPrompt, true, noInputs)
        if (response == null) {
            error("Error in building response")
            return null
        }
        return doResponse(response, RESPONSE_CODE_OK)
    }

    fun ask(speech: String, vararg noInputs: String = arrayOf()): ResponseWrapper<ApiAiResponse>? {
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

    fun askWithList(richResponse: RichResponse, list: List): ResponseWrapper<ApiAiResponse>? {
        if (list.items?.size ?: 0 < 2) {
            this.handleError("List requires at least 2 items")
            return null
        }
        return askWithResponseAndList(buildResponse(richResponse, true), list)
    }

    fun askWithList(inputPrompt: String?, list: List): ResponseWrapper<ApiAiResponse>? {
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

    private fun askWithResponseAndList(response: ResponseWrapper<ApiAiResponse>?, list: List): ResponseWrapper<ApiAiResponse>? {
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

        return doResponse(response, RESPONSE_CODE_OK)
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
    fun askWithCarousel(inputPrompt: String, carousel: Carousel): ResponseWrapper<ApiAiResponse>? {
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
        return doResponse(response, RESPONSE_CODE_OK);
    }


    /**
     * Same as #askWithCarousel(input:String, carousel: Carousel), except takes a RichResponse.
     */
    fun askWithCarousel(inputPrompt: RichResponse, carousel: Carousel): ResponseWrapper<ApiAiResponse>? {
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
        return doResponse(response, RESPONSE_CODE_OK);
    }

    /**
     * Asks user for delivery address.
     *
     * @example
     * val app = ApiAiApp(request = request, response = response)
     * val WELCOME_INTENT = "input.welcome"
     * val DELIVERY_INTENT = "delivery.address"
     *
     * fun welcomeIntent (app) {
     *   app.askForDeliveryAddress("To make sure I can deliver to you")
     * }
     *
     * fun addressIntent (app) {
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
     * @return {ResponseWrapper<ApiAiResponse<T>>} HTTP response.
     * @apiai
     */
    fun askForDeliveryAddress(reason: String): ResponseWrapper<ApiAiResponse>? {
        debug("askForDeliveryAddress: reason=$reason")
        if (reason.isBlank()) {
            this.handleError("reason cannot be empty")
            return null
        }
        val response = buildResponse("PLACEHOLDER_FOR_DELIVERY_ADDRESS", true)
        response {
            body {
                data {
                    google {
                        systemIntent {
                            intent = STANDARD_INTENTS.DELIVERY_ADDRESS
                            data {
                                `@type` = INPUT_VALUE_DATA_TYPES.DELIVERY_ADDRESS
                                addressOptions = GoogleData.AddressOptions(reason)
                            }
                        }
                    }
                }
            }
        }
        return doResponse(response, RESPONSE_CODE_OK)
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
    override fun tell(richResponse: RichResponse?): ResponseWrapper<ApiAiResponse>? {
        debug("tell: richResponse=$richResponse")
        if (richResponse == null || richResponse.isEmpty()) {
            handleError("Invalid rich response")
            return null
        }
        val response = buildResponse(richResponse, false)
        return doResponse(response, RESPONSE_CODE_OK)
    }

    override fun tell(simpleResponse: SimpleResponse): ResponseWrapper<ApiAiResponse>? {
        debug("tell: speechResponse=$simpleResponse")
        if (simpleResponse.isEmpty()) {
            handleError("Invalid speech response")
            return null
        }
        val response = buildResponse(simpleResponse, false)
        return doResponse(response, RESPONSE_CODE_OK)
    }

    override fun tell(speech: String, displayText: String): ResponseWrapper<ApiAiResponse>? {
        debug("tell: speechResponse=$speech")
        if (speech.isEmpty()) {
            handleError("Invalid speech response")
            return null
        }
        val response = buildResponse(speech, false)
        return this.doResponse(response, RESPONSE_CODE_OK)
    }

    /**
     * Set a new context for the current intent.
     *
     * @example
     * val app = ApiAiApp(request = request, response = response)
     * val CONTEXT_NUMBER = "number"
     * val NUMBER_ARGUMENT = "myNumber"
     *
     * fun welcomeIntent (app: ApiAiApp) {
     *   app.setContext(CONTEXT_NUMBER)
     *   app.ask("Welcome to action snippets! Say a number.")
     * }
     *
     * fun numberIntent (app: ApiAiApp) {
     *   val number = app.getArgument(NUMBER_ARGUMENT)
     *   app.tell("You said " + number)
     * }
     *
     * val actionMap = mapOf(
     *      WELCOME_INTENT to ::welcomeIntent,
     *      NUMBER_INTENT to ::numberIntent)
     * app.handleRequest(actionMap)
     *
     * @param {String} name Name of the context. API.AI converts to lowercase.
     * @param {Int} [lifespan=1] Context lifespan.
     * @param {Map<String, Any?>=} parameters Context JSON parameters.
     * @apiai
     */
    fun setContext(name: String, lifespan: Int? = null, parameters: MutableMap<String, Any>? = null): Unit {
        debug("setContext: context=$contexts, lifespan=$lifespan, parameters=$parameters")
        if (name.isEmpty()) {
            handleError("Invalid context name")
            return
        }
        val newContext = Context(
                name = name,
                lifespan = 1
        )
        if (lifespan != null) {
            newContext.lifespan = lifespan
        }
        if (parameters != null) {
            newContext.parameters = parameters
        }
        this.contexts[name] = newContext
    }

    /**
     * Returns the incoming contexts for this intent.
     *
     * @example
     * val app = ApiAiApp(request = request, response = response)
     * val CONTEXT_NUMBER = "number"
     * val NUMBER_ARGUMENT = "myNumber"
     *
     * function welcomeIntent (app: ApiAiApp) {
     *   app.setContext(CONTEXT_NUMBER)
     *   app.ask("Welcome to action snippets! Say a number.")
     * }
     *
     * fun numberIntent (app: ApiAiApp) {
     *   val contexts = app.getContexts()
     *   // contexts === [{
     *   //   name: "number",
     *   //   lifespan: 0,
     *   //   parameters: {
     *   //     myNumber: "23",
     *   //     myNumber.original: "23"
     *   //   }
     *   // }]
     *   val number = app.getArgument(NUMBER_ARGUMENT)
     *   app.tell("You said " + number)
     * }
     *
     * val actionMap = mapOf(
     *      WELCOME_INTENT to ::welcomeIntent,
     *      NUMBER_INTENT to numberIntent)
     * app.handleRequest(actionMap)
     *
     * @return {MutableList<Context>} Empty if no active contexts.
     * @apiai
     */
    fun getContexts(): MutableList<Context> {
        debug("getContexts")
        if (request.body.result?.contexts?.isEmpty()) {
            handleError("No contexts included in request")
            return mutableListOf()
        }
        return request.body.result?.contexts?.filter { it.name != ACTIONS_API_AI_CONTEXT }?.filterNotNull().toMutableList()
    }

    /**
     * Returns the incoming context by name for this intent.
     *
     * @example
     * val app = ApiAiapp(request = request, response = response)
     * val CONTEXT_NUMBER = "number"
     * val NUMBER_ARGUMENT = "myNumber"
     *
     * fun welcomeIntent (app: ApiAiApp) {
     *   app.setContext(CONTEXT_NUMBER)
     *   app.ask("Welcome to action snippets! Say a number.")
     * }
     *
     * fun numberIntent (app: ApiAiApp) {
     *   val context = app.getContext(CONTEXT_NUMBER)
     *   // context === {
     *   //   name: "number",
     *   //   lifespan: 0,
     *   //   parameters: {
     *   //     myNumber: "23",
     *   //     myNumber.original: "23"
     *   //   }
     *   // }
     *   val number = app.getArgument(NUMBER_ARGUMENT)
     *   app.tell("You said " + number)
     * }
     *
     * const actionMap = mapOf(
     *      WELCOME_INTENT to ::welcomeIntent,
     *      NUMBER_INTENT to ::numberIntent)
     * app.handleRequest(actionMap)
     *
     * @return {Object} Context value matching name
     *     or null if no matching context.
     * @apiai
     */

    fun getContext(name: String): Context? {
        debug("getContext: name=$name")
        if (request.body.result.contexts == null) {
            handleError("No contexts included in request")
            return null
        }
        request.body.result.contexts.forEach {
            if (it.name == name) {
                return it
            }
        }
        debug("Failed to get context: $name")
        return null
    }


    /**
     * Gets the user"s raw input query.

     * @example
     * * val app = ApiAiApp(request = request, response = response)
     * * app.tell("You said " + app.getRawInput())
     * *
     * *
     * @return {String} User"s raw query or null if no value.
     * *
     * @apiai
     */
    fun getRawInput(): String? {
        debug("getRawInput")
        if (request.body.result.resolvedQuery.isEmpty()) {
            handleError("No raw input")
            return null
        }
        return request.body.result.resolvedQuery
    }

    // ---------------------------------------------------------------------------
    //                   Private Helpers
    // ---------------------------------------------------------------------------

    /**
     * Uses a PermissionsValueSpec object to construct and send a
     * permissions request to the user.
     *
     * @param {Object} permissionsValueSpec PermissionsValueSpec object containing
     *     the permissions prefix and permissions requested.
     * @return {Object} The HTTP response.
     * @private
     * @apiai
     */
    override fun fulfillPermissionsRequest(permissionsSpec: GoogleData.PermissionsRequest, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ApiAiResponse>? {
        debug("fulfillPermissionsRequest_: permissionsValueSpec=$permissionsSpec")
        val inputPrompt = "PLACEHOLDER_FOR_PERMISSION"
        val response = buildResponse(inputPrompt, true)

        response {
            body {
                data {
                    google {
                        systemIntent {
                            intent = STANDARD_INTENTS.PERMISSION
                        }
                    }
                }
            }
        }

        if (isNotApiVersionOne()) {
            response {
                body {
                    data {
                        google {
                            systemIntent {
                                data {
                                    `@type` = INPUT_VALUE_DATA_TYPES.PERMISSION
                                    permissions = permissionsSpec.permissions
                                    optContext = permissionsSpec.optContext
                                }
                            }
                        }
                    }
                }
            }
        } else {
            response {
                body {
                    data {
                        google {
                            systemIntent {
                                spec {
                                    permissionValueSpec = permissionsSpec
                                }
                            }
                        }
                    }
                }
            }
        }
        return doResponse(response, RESPONSE_CODE_OK)
    }

    /**
     * Uses TransactionRequirementsCheckValueSpec to construct and send a
     * transaction requirements request to Google.
     *
     * @param {TransactionRequirementsCheckSpec} transactionRequirementsSpec TransactionRequirementsSpec
     *     object.
     * @return {ResponseWrapper<T>} HTTP response.
     * @private
     * @apiai
     */
    override fun fulfillTransactionRequirementsCheck(transactionRequirementsCheckSpec: TransactionRequirementsCheckSpec,
                                                     dialogState: MutableMap<String, Any?>?): ResponseWrapper<ApiAiResponse>? {
        debug("fulfillTransactionRequirementsCheck_: transactionRequirementsSpec=%s")
        val response = buildResponse("PLACEHOLDER_FOR_TXN_REQUIREMENTS", true)
        response {
            body {
                data {
                    google {
                        systemIntent {
                            intent = STANDARD_INTENTS.TRANSACTION_REQUIREMENTS_CHECK
                            data {
                                `@type` = INPUT_VALUE_DATA_TYPES.TRANSACTION_REQ_CHECK
                                paymentOptions = transactionRequirementsCheckSpec.paymentOptions
                                orderOptions = transactionRequirementsCheckSpec.orderOptions
                            }
                        }
                    }
                }
            }
        }
        return doResponse(response, RESPONSE_CODE_OK)
    }

    /**
     * Uses TransactionDecisionValueSpec to construct and send a transaction
     * requirements request to Google.
     *
     * @param {TransactionDecisionValueSpec} transactionDecisionValueSpec TransactionDecisionValueSpec
     *     object.
     * @return {ResponseWrapper<ApiAiResponse<T>>} HTTP response.
     * @private
     * @apiai
     */
    override fun fulfillTransactionDecision(transactionDecisionValueSpec: TransactionDecisionValueSpec, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ApiAiResponse>? {
        debug("fulfillTransactionDecision_: transactionDecisionValueSpec=$transactionDecisionValueSpec")
        val response = buildResponse("PLACEHOLDER_FOR_TXN_DECISION", true)
        response {
            body {
                data {
                    google {
                        systemIntent {
                            intent = STANDARD_INTENTS.TRANSACTION_DECISION
                            data {
                                `@type` = INPUT_VALUE_DATA_TYPES.TRANSACTION_DECISION
                                paymentOptions = transactionDecisionValueSpec.paymentOptions
                                orderOptions = transactionDecisionValueSpec.orderOptions
                                proposedOrder = transactionDecisionValueSpec.proposedOrder
                            }
                        }
                    }
                }
            }
        }
        return doResponse(response, RESPONSE_CODE_OK)
    }


    /**
     * Uses ConfirmationValueSpec to construct and send a confirmation request to
     * Google.
     *
     * @param {Object} confirmationValueSpec ConfirmationValueSpec object.
     * @return {Object} HTTP response.
     * @private
     * @apiai
     */
    override fun fulfillConfirmationRequest(confirmationValueSpec: ConfirmationValueSpec, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ApiAiResponse>? {
        debug("fulfillConfirmationRequest_: confirmationValueSpec=$confirmationValueSpec")
        val response = this.buildResponse("PLACEHOLDER_FOR_CONFIRMATION", true)
        response {
            body {
                data {
                    google {
                        systemIntent {
                            intent = STANDARD_INTENTS.CONFIRMATION
                            data {
                                `@type` = INPUT_VALUE_DATA_TYPES.CONFIRMATION
                                dialogSpec = confirmationValueSpec.dialogSpec
                            }
                        }
                    }
                }
            }
        }
        return doResponse(response, RESPONSE_CODE_OK)
    }

    /**
     * Uses DateTimeValueSpec to construct and send a datetime request to Google.
     *
     * @param {Object} dateTimeValueSpec DateTimeValueSpec object.
     * @return {Object} HTTP response.
     * @private
     * @apiai
     */
    override fun fulfillDateTimeRequest(confirmationValueSpec: ConfirmationValueSpec, dialogState: MutableMap<String, Any?>?): ResponseWrapper<ApiAiResponse>? {
        debug("fulfillDateTimeRequest_: dateTimeValueSpec=$confirmationValueSpec")
        val response = buildResponse("PLACEHOLDER_FOR_DATETIME", true)
        response {
            body {
                data {
                    google {
                        systemIntent {
                            intent = STANDARD_INTENTS.DATETIME
                            data {
                                `@type` = INPUT_VALUE_DATA_TYPES.DATETIME
                                dialogSpec = confirmationValueSpec.dialogSpec
                            }
                        }
                    }
                }
            }
        }
        return doResponse(response, RESPONSE_CODE_OK)
    }

    /**
     * Constructs and sends a sign in request to Google.
     *
     * @return {ResponseWrapper<ApiAiResponse<T>>} HTTP response.
     * @private
     * @apiai
     */
    override fun fulfillSignInRequest(dialogState: MutableMap<String, Any?>?): ResponseWrapper<ApiAiResponse>? {
        debug("fulfillSignInRequest_")
        val response = buildResponse("PLACEHOLDER_FOR_SIGN_IN", true)
        response {
            body {
                data {
                    google {
                        systemIntent {
                            intent = STANDARD_INTENTS.SIGN_IN
                            data {
                                `@type` = INPUT_VALUE_DATA_TYPES.SIGN_IN
                            }
                        }
                    }
                }
            }
        }
        return doResponse(response, RESPONSE_CODE_OK)
    }


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
    fun buildResponse(simpleResponse: SimpleResponse, expectUserResponse: Boolean, noInputs: MutableList<String>? = null): ResponseWrapper<ApiAiResponse>? {
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
    fun buildResponse(richResponse: RichResponse, expectUserResponse: Boolean, noInputs: MutableList<String>? = null): ResponseWrapper<ApiAiResponse>? {
        debug("buildResponse_: textToSpeech=$richResponse, expectUserResponse=$expectUserResponse, noInputs=$noInputs")
        if (richResponse.isEmpty()) {
            handleError("Invalid text to speech")
            return null
        }
        if (richResponse.items?.first()?.simpleResponse == null || richResponse.items?.first()?.simpleResponse?.textToSpeech.isNullOrBlank()) {
            handleError("Invalid RichResponse. First item must be SimpleResponse")
            return null
        }

        val speech = richResponse.items?.first()?.simpleResponse?.textToSpeech!!
        var noInputsFinal = mutableListOf<GoogleData.NoInputPrompts>()
        val dialogState = mutableMapOf<String, Any?>(
                "state" to state, //TODO (this.state instanceof State ? this.state.getName() : this.state),
                "data" to data)
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
        val response = ApiAiResponse(
                speech = speech)
        response.data.google = GoogleData(
                expectUserResponse = expectUserResponse,
                isSsml = isSsml(speech),
                richResponse = richResponse,
                noInputPrompts = noInputsFinal)
        if (expectUserResponse) {
            response.contextOut.add(
                    Context(
                            name = ACTIONS_API_AI_CONTEXT,
                            lifespan = MAX_LIFESPAN,
                            parameters = dialogState["data"] as MutableMap<String, Any>?))
        }
        response.contextOut = response.contextOut.plus(contexts.values).toMutableList()
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
    fun buildResponse(textToSpeech: String, expectUserResponse: Boolean, noInputs: MutableList<String>? = null): ResponseWrapper<ApiAiResponse>? {
        debug("buildResponse_: textToSpeech=$textToSpeech, expectUserResponse=$expectUserResponse, noInputs=$noInputs")
        if (textToSpeech.isEmpty()) {
            handleError("Invalid text to speech")
            return null
        }

        var noInputsFinal = mutableListOf<GoogleData.NoInputPrompts>()
        val dialogState = mutableMapOf<String, Any?>(
                "state" to state, //TODO (this.state instanceof State ? this.state.getName() : this.state),
                "data" to data)
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
        val response = ApiAiResponse(
                speech = textToSpeech)
        response.data.google = GoogleData(
                expectUserResponse = expectUserResponse,
                isSsml = isSsml(textToSpeech),
                noInputPrompts = noInputsFinal)
        if (expectUserResponse) {
            response.contextOut.add(
                    Context(
                            name = ACTIONS_API_AI_CONTEXT,
                            lifespan = MAX_LIFESPAN,
                            parameters = dialogState["data"] as MutableMap<String, Any>?))
        }
        response.contextOut.addAll(contexts.values)
        this.response.body = response
        return this.response
    }

    /**
     * Extract the session data from the incoming JSON request.
     *
     */
    override fun extractData() {
        debug("extractData")
        data = request.body.result.contexts.find { it.name == ACTIONS_API_AI_CONTEXT }?.parameters ?: mutableMapOf()
    }

}
