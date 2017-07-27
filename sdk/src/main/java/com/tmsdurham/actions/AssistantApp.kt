package com.tmsdurham.actions

import com.ticketmaster.apiai.*
import com.ticketmaster.apiai.google.GoogleData
import java.util.logging.Logger

typealias Handler<T, S, U> = (AssistantApp<T, S, U>) -> Unit

internal val logger = Logger.getAnonymousLogger()

// Constants
val ERROR_MESSAGE = "Sorry, I am unable to process your request."
val API_ERROR_MESSAGE_PREFIX = "Action Error: "
val CONVERSATION_API_VERSION_HEADER = "Google-Assistant-API-Version"
val ACTIONS_CONVERSATION_API_VERSION_HEADER = "Google-Actions-API-Version"
val ACTIONS_CONVERSATION_API_VERSION_TWO = 2
val RESPONSE_CODE_OK = 200
val RESPONSE_CODE_BAD_REQUEST = 400
val HTTP_CONTENT_TYPE_HEADER = "Content-Type"
val HTTP_CONTENT_TYPE_JSON = "application/json"


class StandardIntents(val isNotVersionOne: Boolean) {
    /** App fires MAIN intent for queries like [talk to $app]. */
    val MAIN = if (isNotVersionOne) "actions.intent.MAIN" else "assistant.intent.action.MAIN"
    /** App fires TEXT intent when action issues ask intent. */
    val TEXT = if (isNotVersionOne) "actions.intent.TEXT" else "assistant.intent.action.TEXT"
    /** App fires PERMISSION intent when action invokes askForPermission. */
    val PERMISSION = if (isNotVersionOne) "actions.intent.PERMISSION" else "assistant.intent.action.PERMISSION"
    /** App fires OPTION intent when user chooses from options provided. */
    val OPTION = if (isNotVersionOne) "actions.intent.OPTION" else "assistant.intent.action.PERMISSION"
    /** App fires TRANSACTION_REQUIREMENTS_CHECK intent when action sets up transaction. */
    val TRANSACTION_REQUIREMENTS_CHECK = "actions.intent.TRANSACTION_REQUIREMENTS_CHECK"
    /** App fires DELIVERY_ADDRESS intent when action asks for delivery address. */
    val DELIVERY_ADDRESS = "actions.intent.DELIVERY_ADDRESS"
    /** App fires TRANSACTION_DECISION intent when action asks for transaction decision. */
    val TRANSACTION_DECISION = "actions.intent.TRANSACTION_DECISION"
    /** App fires CONFIRMATION intent when requesting affirmation from user. */
    val CONFIRMATION = "actions.intent.CONFIRMATION"
    /** App fires DATETIME intent when requesting date/time from user. */
    val DATETIME = "actions.intent.DATETIME"
    /** App fires SIGN_IN intent when requesting sign-in from user. */
    val SIGN_IN = "actions.intent.SIGN_IN"
}

class SupportedIntent {
    /**
     * The user"s name as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#UserProfile|UserProfile object}
     */
    val NAME = "NAME"
    /**
     * The location of the user"s current device, as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#Location|Location object}.
     */
    val DEVICE_PRECISE_LOCATION = "DEVICE_PRECISE_LOCATION"
    /**
     * City and zipcode corresponding to the location of the user"s current device, as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#Location|Location object}.
     */
    val DEVICE_COARSE_LOCATION = "DEVICE_COARSE_LOCATION"
}

/**
 * List of built-in argument names.
 */
class BuiltInArgNames(isNotVersionOne: Boolean) {
    /** Permission granted argument. */
    val PERMISSION_GRANTED = if (isNotVersionOne) "PERMISSION" else "permission_granted"
    /** Option selected argument. */
    val OPTION = "OPTION"
    /** Transaction requirements check result argument. */
    val TRANSACTION_REQ_CHECK_RESULT = "TRANSACTION_REQUIREMENTS_CHECK_RESULT"
    /** Delivery address value argument. */
    val DELIVERY_ADDRESS_VALUE = "DELIVERY_ADDRESS_VALUE"
    /** Transactions decision argument. */
    val TRANSACTION_DECISION_VALUE = "TRANSACTION_DECISION_VALUE"
    /** Confirmation argument. */
    val CONFIRMATION = "CONFIRMATION"
    /** DateTime argument. */
    val DATETIME = "DATETIME"
    /** Sign in status argument. */
    val SIGN_IN = "SIGN_IN"
}

const val ANY_TYPE_PROPERTY = "@type"

/**
 * List of built-in value type names.
 */
class InputValueDataTypes {
    /** Permission Value Spec. */
    val PERMISSION = "type.googleapis.com/google.actions.v2.PermissionValueSpec"
    /** Option Value Spec. */
    val OPTION = "type.googleapis.com/google.actions.v2.OptionValueSpec"
    /** Transaction Requirements Check Value Spec. */
    val TRANSACTION_REQ_CHECK = "type.googleapis.com/google.actions.v2.TransactionRequirementsCheckSpec"
    /** Delivery Address Value Spec. */
    val DELIVERY_ADDRESS = "type.googleapis.com/google.actions.v2.DeliveryAddressValueSpec"
    /** Transaction Decision Value Spec. */
    val TRANSACTION_DECISION = "type.googleapis.com/google.actions.v2.TransactionDecisionValueSpec"
    /** Confirmation Value Spec. */
    val CONFIRMATION = "type.googleapis.com/google.actions.v2.ConfirmationValueSpec"
    /** DateTime Value Spec. */
    val DATETIME = "type.googleapis.com/google.actions.v2.DateTimeValueSpec"
}

/**
 * List of possible conversation stages, as defined in the
 * {@link https://developers.google.com/actions/reference/conversation#Conversation|Conversation object}.
 * @actionssdk
 * @apiai
 */
class ConversationStages(val isNotVersionOne: Boolean) {
    /**
     * Unspecified conversation state.
     */
    val UNSPECIFIED = if (isNotVersionOne) "UNSPECIFIED" else "0"
    /**
     * A new conversation.
     */
    val NEW = if (isNotVersionOne) "NEW" else "1"
    /**
     * An active (ongoing) conversation.
     */
    val ACTIVE = if (isNotVersionOne) "ACTIVE" else "2"
}

/**
 * List of supported permissions the app supports.
 */
class SupportedPermissions {
    /**
     * The user"s name as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#UserProfile|UserProfile object}
     */
    val NAME = "NAME"
    /**
     * The location of the user"s current device, as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#Location|Location object}.
     */
    val DEVICE_PRECISE_LOCATION = "DEVICE_PRECISE_LOCATION"
    /**
     * City and zipcode corresponding to the location of the user"s current device, as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#Location|Location object}.
     */
    val DEVICE_COARSE_LOCATION = "DEVICE_COARSE_LOCATION"
}

class SignInStatus {
    // Unknown status.
    val UNSPECIFIED = "SIGN_IN_STATUS_UNSPECIFIED"
    // User successfully completed the account linking.
    val OK = "OK"
    // Cancelled or dismissed account linking.
    val CANCELLED = "CANCELLED"
    // System or network error.
    val ERROR = "ERROR"
}


open abstract class AssistantApp<T, S, U>(val request: RequestWrapper<T>, val response: ResponseWrapper<S>, val sessionStarted: (() -> Unit)? = null) {
    var actionsApiVersion: String = "1"
    lateinit var STANDARD_INTENTS: StandardIntents
    val SUPPORTED_INTENT = SupportedIntent()
    lateinit var BUILT_IN_ARG_NAMES: BuiltInArgNames
    val INPUT_VALUE_DATA_TYPES = InputValueDataTypes()
    lateinit var CONVERSATION_STAGES: ConversationStages
    val SUPPORTED_PERMISSIONS = SupportedPermissions()
    val SIGN_IN_STATUS = SignInStatus()

    var responded = false
    var apiVersion: String = ""
    var state: String = ""
    var contexts = listOf<ContextOut<U>>()
    val requestExtractor: RequestExtractor<T, S, U>

    init {
        debug("AssistantApp constructor");

        if (request == null) {
            handleError("Request can NOT be empty.")
        } else if (response == null) {
            handleError("Response can NOT be empty.")
        } else {
            if (request.headers[ACTIONS_CONVERSATION_API_VERSION_HEADER] != null) {
                actionsApiVersion = request.headers[ACTIONS_CONVERSATION_API_VERSION_HEADER]!!
                debug("Actions API version from header: " + this.actionsApiVersion);
            }
            if (request.body is ApiAiRequest<*>) {
                if (request.body.originalRequest != null) {
                    actionsApiVersion = request.body.originalRequest?.version ?: "1"
                    debug("Actions API version from APIAI: " + this.actionsApiVersion);
                }
            }
        }
        STANDARD_INTENTS = StandardIntents(isNotApiVersionOne())
        BUILT_IN_ARG_NAMES = BuiltInArgNames(isNotApiVersionOne())
        CONVERSATION_STAGES = ConversationStages(isNotApiVersionOne())

        /**
         * API version describes version of the Assistant request.
         * @deprecated
         * @private
         * @type {string}
         */
        // Populates API version.
        if (request.get(CONVERSATION_API_VERSION_HEADER) != null) {
            apiVersion = request.get(CONVERSATION_API_VERSION_HEADER) ?: ""
            debug("Assistant API version: " + apiVersion)
        }

        requestExtractor = RequestExtractor(this)
    }

    fun getUser(): User? {
        return requestExtractor.getUser()
    }

    fun handleRequest(handler: Handler<T, S, U>) {
        debug("handleRequest: handler=${handler::javaClass.get().name}")
        handler(this)
    }

    fun handleRequest(handler: Map<*, *>) {
        debug("handleRequest: handler=${handler::javaClass.get().name}")
        invokeIntentHandler(handler as Map<*, Handler<T, S, U>>, getIntent())
    }

    /**
     * Equivalent to {@link AssistantApp#askForPermission|askForPermission},
     * but allows you to prompt the user for more than one permission at once.
     *
     * Notes:
     *
     * * The order in which you specify the permission prompts does not matter -
     *   it is controlled by the Assistant to provide a consistent user experience.
     * * The user will be able to either accept all permissions at once, or none.
     *   If you wish to allow them to selectively accept one or other, make several
     *   dialog turns asking for each permission independently with askForPermission.
     * * Asking for DEVICE_COARSE_LOCATION and DEVICE_PRECISE_LOCATION at once is
     *   equivalent to just asking for DEVICE_PRECISE_LOCATION
     *
     * @example
     * val app = ApiAIApp(request = req, response = res)
     * val REQUEST_PERMISSION_ACTION = "request_permission"
     * val GET_RIDE_ACTION = "get_ride"
     *
     * fun requestPermission (app) {
     *   app.askForPermissions("To pick you up", app.SupportedPermissions.NAME,
     *     app.SupportedPermissions.DEVICE_PRECISE_LOCATION)
     * }
     *
     * fun sendRide (app) {
     *   if (app.isPermissionGranted()) {
     *     val displayName = app.getUserName().displayName
     *     val address = app.getDeviceLocation().formattedAddress
     *     app.tell("I will tell your driver to pick up " + displayName +
     *         " at " + address)
     *   } else {
     *     // Response shows that user did not grant permission
     *     app.tell("Sorry, I could not figure out where to pick you up.")
     *   }
     * }
     * val actionMap = mapOf(
     *      REQUEST_PERMISSION_ACTION to requestPermission,
     *      GET_RIDE_ACTION to sendRide)
     * app.handleRequest(actionMap)
     *
     * @param {String} context Context why the permission is being asked; it"s the TTS
     *     prompt prefix (action phrase) we ask the user.
     * @param {Array<String>} permissions Array of permissions App supports, each of
     *     which comes from AssistantApp.SupportedPermissions.
     * @param {DialogState=} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant. Used in {@link ActionsSdkAssistant}.
     * @return A response is sent to Assistant to ask for the user"s permission; for any
     *     invalid input, we return null.
     * @actionssdk
     * @apiai
     */
    fun askForPermissions(context: String, vararg permissions: String, dialogState: DialogState<T>? = null): ResponseWrapper<S>? {
        debug("askForPermissions: context=$context, permissions=$permissions, dialogState=$dialogState")
        if (context.isEmpty()) {
            handleError("Assistant context can NOT be empty.")
            return null
        }
        if (permissions.isEmpty()) {
            handleError("At least one permission needed.")
            return null
        }
        permissions.forEach {
            if (it !== SUPPORTED_PERMISSIONS.NAME &&
                    it !== SUPPORTED_PERMISSIONS.DEVICE_PRECISE_LOCATION &&
                    it !== SUPPORTED_PERMISSIONS.DEVICE_COARSE_LOCATION) {
                this.handleError("Assistant permission must be one of " +
                        "[NAME, DEVICE_PRECISE_LOCATION, DEVICE_COARSE_LOCATION]")
                return null
            }
        }
        if (dialogState != null) {
            //TODO support dialogState
//            dialogState = {
//                "state": (this.state instanceof State ? this.state.getName() : this.state),
//                "data": this.data
//            };
        }
        return fulfillPermissionsRequest(GoogleData.PermissionsRequest(
                optContext = context,
                permissions = permissions.toMutableList()))
    }

    /**
     * Asks user for a confirmation.
     *
     * @example
     * val app = ApiAiApp(request, response)
     * val WELCOME_INTENT = "input.welcome"
     * val CONFIRMATION = "confirmation"
     *
     * fun welcomeIntent (app: MyAction) {
     *   app.askForConfirmation("Are you sure you want to do that?")
     * }
     *
     * fun confirmation (app: MyAction) {
     *   if (app.getUserConfirmation()) {
     *     app.tell("Great! I\"m glad you want to do it!")
     *   } else {
     *     app.tell("That\"s okay. Let\"s not do it now.")
     *   }
     * }
     *
     * val actionMap = mapOf(
     *      WELCOME_INTENT to ::welcomeIntent,
     *      CONFIRMATION to ::confirmation)
     * app.handleRequest(actionMap)
     *
     * @param {String=} prompt The confirmation prompt presented to the user to
     *     query for an affirmative or negative response. If undefined or null,
     *     Google will use a generic yes/no prompt.
     * @param {DialogState?=} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant. Used in {@link ActionsSdkAssistant}.
     * @actionssdk
     * @apiai
     */
    fun askForConfirmation (prompt: String = "", dialogState: DialogState<U>? = null): ResponseWrapper<S>? {
        debug("askForConfirmation: prompt=$prompt, dialogState=$dialogState")
        val confirmationValueSpec = ConfirmationValueSpec()
        if (prompt.isNotBlank()) {
            confirmationValueSpec.dialogSpec = DialogSpec(
                requestConfirmationText = prompt)
        }
        return fulfillConfirmationRequest(confirmationValueSpec, dialogState)
    }


    /**
     * Asks user for a timezone-agnostic date and time.
     *
     * @example
     * val app = ApiAiApp(request, response )
     * val WELCOME_INTENT = "input.welcome"
     * val DATETIME = "datetime"
     *
     * fun welcomeIntent (app: MyAction) {
     *   app.askForDateTime("When do you want to come in?",
     *     "Which date works best for you?",
     *     "What time of day works best for you?")
     * }
     *
     * function datetime (app: MyAction) {
     *   app.tell({speech: "Great see you at your appointment!",
     *     displayText: "Great, we will see you on "
     *     + app.getDateTime().date.month
     *     + "/" + app.getDateTime().date.day
     *     + " at " + app.getDateTime().time.hours
     *     + (app.getDateTime().time.minutes || "")})
     * }
     *
     * val actionMap = mapOf(
     *      WELCOME_INTENT to ::welcomeIntent,
     *      DATETIME, ::datetime)
     * app.handleRequest(actionMap)
     *
     * @param {String=} initialPrompt The initial prompt used to ask for a
     *     date and time. If undefined or null, Google will use a generic
     *     prompt.
     * @param {String=} datePrompt The prompt used to specifically ask for the
     *     date if not provided by user. If undefined or null, Google will use a
     *     generic prompt.
     * @param {String=} timePrompt The prompt used to specifically ask for the
     *     time if not provided by user. If undefined or null, Google will use a
     *     generic prompt.
     * @param {DialogState<T>?=} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant. Used in {@link ActionsSdkAssistant}.
     * @actionssdk
     * @apiai
     */
    fun askForDateTime (initialPrompt: String? = null, datePrompt: String? =null, timePrompt: String? = null, dialogState: DialogState<U>? = null): ResponseWrapper<S>? {
        debug("askForConfirmation: initialPrompt=$initialPrompt, datePrompt=$datePrompt, timePrompt=$timePrompt, dialogState=$dialogState")
        val confirmationValueSpec = ConfirmationValueSpec()
        if (initialPrompt != null || datePrompt != null || timePrompt != null) {
            confirmationValueSpec.dialogSpec = DialogSpec(
                requestDatetimeText = initialPrompt,
                requestDateText = datePrompt,
                requestTimeText = timePrompt)
        }
        return fulfillDateTimeRequest(confirmationValueSpec, dialogState)
    }

    abstract fun fulfillDateTimeRequest(confirmationValueSpec: ConfirmationValueSpec, dialogState: DialogState<U>?): ResponseWrapper<S>?
    abstract fun fulfillConfirmationRequest(confirmationValueSpec: ConfirmationValueSpec, dialogState: DialogState<U>?): ResponseWrapper<S>?

    data class ConfirmationValueSpec(var dialogSpec: DialogSpec? = null)

    data class DialogSpec(var requestConfirmationText: String? = null,
                          var requestDatetimeText: String? = null,
                          var requestDateText: String? = null,
                          var requestTimeText: String? = null)
    
    /**
     * Checks whether user is in transactable state.
     *
     * @example
     * val app = ApiAiApp(request = request, response = response)
     * val WELCOME_INTENT = "input.welcome"
     * val TXN_REQ_COMPLETE = "txn.req.complete"
     *
     * val transactionConfig = GooglePaymentTransactionConfig(
     *     deliveryAddressRequired = false,
     *     type = app.Transactions.PaymentType.BANK,
     *     displayName = "Checking-1234"
     * )
     * fun welcomeIntent (app) {
     *   app.askForTransactionRequirements(transactionConfig)
     * }
     *
     * fun txnReqCheck (app) {
     *   if (app.getTransactionRequirementsResult() === app.Transactions.ResultType.OK) {
     *     // continue cart building flow
     *   } else {
     *     // don"t continue cart building
     *   }
     * }
     *
     * val actionMap = mapOf(
     *      WELCOME_INTENT to ::welcomeIntent,
     *      TXN_REQ_COMPLETE to ::txnReqCheck)
     * app.handleRequest(actionMap)
     *
     * @param {ActionPaymentTransactionConfig|GooglePaymentTransactionConfig=}
     *     transactionConfig Configuration for the transaction. Includes payment
     *     options and order options. Optional if order has no payment or
     *     delivery.
     * @param {DialogState<U>=} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant. Used in {@link ActionsSdkAssistant}.
     * @return {ResponseWrapper<S>} HTTP response.
     * @actionssdk
     * @apiai
     */
    fun askForTransactionRequirements (transactionConfig: TransactionConfig, dialogState: DialogState<U>? = null): ResponseWrapper<S>? {
        debug("checkForTransactionRequirements: transactionConfig=$transactionConfig," +
                " dialogState=$dialogState")
        if (transactionConfig.type?.isNullOrBlank() ?: true &&
                transactionConfig.cardNetworks?.isEmpty() ?: true) {
            handleError("Invalid transaction configuration. Must be of type" +
                    "ActionPaymentTransactionConfig or GooglePaymentTransactionConfig")
            return null
        }
        val transactionRequirementsCheckSpec = TransactionRequirementsCheckSpec()
        if (transactionConfig.deliveryAddressRequired ?: false) {
            transactionRequirementsCheckSpec.orderOptions = GoogleData.OrderOptions(
                requestDeliveryAddress = transactionConfig.deliveryAddressRequired ?: false)
        }
        if (transactionConfig.type != null ||
                transactionConfig.cardNetworks != null) {
            transactionRequirementsCheckSpec.paymentOptions =
                    buildPaymentOptions(transactionConfig)
        }
        return fulfillTransactionRequirementsCheck(transactionRequirementsCheckSpec,
                dialogState)
    }


    /**
     * Asks user to confirm transaction information.
     *
     * @example
     * val app = ApiAiApp(request = request, response = response);
     * val WELCOME_INTENT = "input.welcome"
     * val TXN_COMPLETE = "txn.complete"
     *
     * val transactionConfig = GooglePaymentTransactionConfig(
     *     deliveryAddressRequired = false,
     *     type = app.Transactions.PaymentType.BANK,
     *     displayName = "Checking-1234"
     * )
     *
     * val order = app.buildOrder()
     * // fill order cart
     *
     * fun welcomeIntent (app) {
     *   app.askForTransaction(order, transactionConfig)
     * }
     *
     * function txnComplete (app) {
     *   // respond with order update
     * }
     *
     * val actionMap = mapOf(
     *      WELCOME_INTENT to ::welcomeIntent,
     *      TXN_COMPLETE to ::txnComplete)
     * app.handleRequest(actionMap)
     *
     * @param {Order} order Order built with buildOrder().
     * @param {ActionPaymentTransactionConfig|GooglePaymentTransactionConfig}
     *     transactionConfig Configuration for the transaction. Includes payment
     *     options and order options.
     * @param {DialogState=} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant. Used in {@link ActionsSdkAssistant}.
     * @apiai
     */
    fun askForTransactionDecision (order: GoogleData.Order, transactionConfig: TransactionConfig, dialogState: DialogState<U>? = null): ResponseWrapper<S>? {
        debug("askForTransactionDecision: order=$order, transactionConfig=$transactionConfig, dialogState=$dialogState")
        if (order == null) {
            this.handleError("Invalid order")
            return null
        }
        if (transactionConfig?.type != null &&
                transactionConfig?.cardNetworks?.isNotEmpty() ?: false) {
            handleError("Invalid transaction configuration. Must be of type" +
                    "ActionPaymentTransactionConfig or GooglePaymentTransactionConfig")
            return null
        }
        val transactionDecisionValueSpec = TransactionDecisionValueSpec(
            proposedOrder = order)

        if (transactionConfig.deliveryAddressRequired ?: false) {
            transactionDecisionValueSpec.orderOptions = GoogleData.OrderOptions(
                requestDeliveryAddress = transactionConfig.deliveryAddressRequired ?: false
            )
        }
        if (transactionConfig.type?.isNotBlank() ?: false ||
                transactionConfig.cardNetworks?.isNotEmpty() ?: false) {
            transactionDecisionValueSpec.paymentOptions =
                    buildPaymentOptions(transactionConfig)
        }
        if (transactionConfig.customerInfoOptions != null) {
            if (transactionDecisionValueSpec.orderOptions == null) {
                transactionDecisionValueSpec.orderOptions = GoogleData.OrderOptions()
            }
            transactionDecisionValueSpec.orderOptions?.customerInfoOptions =
                    transactionConfig.customerInfoOptions
        }
        return fulfillTransactionDecision(transactionDecisionValueSpec,
                dialogState)
    }

    data class TransactionDecisionValueSpec(var proposedOrder: GoogleData.Order, var orderOptions: GoogleData.OrderOptions? = null, var paymentOptions: GoogleData.PaymentOptions? = null)

    data class TransactionRequirementsCheckSpec(var orderOptions: GoogleData.OrderOptions? = null,
                                                var paymentOptions: GoogleData.PaymentOptions? = null)

    abstract fun  fulfillTransactionRequirementsCheck(transactionRequirementsCheckSpec: TransactionRequirementsCheckSpec, dialogState: DialogState<U>? = null): ResponseWrapper<S>?
    abstract fun  fulfillTransactionDecision(transactionDecisionValueSpec: TransactionDecisionValueSpec, dialogState: DialogState<U>? = null): ResponseWrapper<S>?


    fun doResponse(response: ResponseWrapper<S>?, responseCode: Int = 0): ResponseWrapper<S>? {
        debug("doResponse_: responseWrapper=$response., responseCode=$responseCode")
        if (responded) {
            return null
        }
        if (response == null || response.body == null) {
            this.handleError("Response can NOT be empty.")
            return null
        } else {
            var code = RESPONSE_CODE_OK
            if (responseCode != 0) {
                code = responseCode
            }
            if (this.apiVersion !== null) {
                this.response.append(CONVERSATION_API_VERSION_HEADER, apiVersion)
            }
            response.append(HTTP_CONTENT_TYPE_HEADER, HTTP_CONTENT_TYPE_JSON)
            // If request was in Proto2 format, convert response to Proto2
            if (!this.isNotApiVersionOne()) {
                //TODO migrate data
//                if (response.data) {
//                    response.data = transformToSnakeCase(response.data);
//                } else {
//                    response = transformToSnakeCase(response);
//                }
            }
            debug("Response $response")
            val httpResponse = response.status(code).send(response.body!!)
            this.responded = true
            return httpResponse
        }
    }

    /**
     * Constructs List with chainable property setters.
     *
     * @param {string=} title A title to set for a new List.
     * @return {List} Constructed List.
     */
    fun buildList(title: String? = null): List {
        return List(title)
    }

    /**
     * Constructs Carousel with chainable property setters.
     *
     * @return {Carousel} Constructed Carousel.
     */
    fun buildCarousel(): Carousel {
        return Carousel()
    }

    /**
     * Constructs OptionItem with chainable property setters.
     *
     * @param {string=} key A unique key to identify this option. This key will
     *     be returned as an argument in the resulting actions.intent.OPTION
     *     intent.
     * @param {string|Array<string>=} synonyms A list of synonyms which the user may
     *     use to identify this option instead of the option key.
     * @return {OptionItem} Constructed OptionItem.
     */
    fun buildOptionItem(key: String, vararg synonyms: String): OptionItem {
        val optionInfo = OptionInfo()
        if (!key.isNullOrBlank()) {
            optionInfo.key = key
        }
        if (synonyms.isNotEmpty()) {
            optionInfo.synonyms = synonyms.toMutableList()
        }
        return OptionItem(optionInfo)
    }


    // ---------------------------------------------------------------------------
    //                   Transaction Builders
    // ---------------------------------------------------------------------------

    /**
     * Constructs Order with chainable property setters.
     *
     * @param {String} orderId Unique identifier for the order.
     * @return {Order} Constructed Order.
     */
    fun buildOrder (orderId: String): GoogleData.Order {
        return GoogleData.Order(id = orderId)
    }

    internal abstract fun fulfillPermissionsRequest(permissionsSpec: GoogleData.PermissionsRequest): ResponseWrapper<S>?

    abstract fun getIntent(): String
    abstract fun tell(speech: String, displayText: String = ""): ResponseWrapper<S>?
    abstract fun tell(richResponse: RichResponse?): ResponseWrapper<S>?
    abstract fun tell(simpleResponse: SimpleResponse): ResponseWrapper<S>?
//    abstract fun askWithList(speech: String? = null, richResponse: RichResponse): ResponseWrapper<S>?
//    abstract fun askWithList(speech: String? = null, list: List): ResponseWrapper<S>?

    // ---------------------------------------------------------------------------
    //                   Private Helpers
    // ---------------------------------------------------------------------------

    private var lastErrorMessage: String? = null
//    val dummyHandler = ApiAiApp<T>(request = RequestWrapper(body = ApiAiRequest()), response = ResponseWrapper())
    /**
     * Utility function to invoke an intent handler.
     *
     * @param {Object} handler The handler for the request.
     * @param {string} intent The intent to handle.
     * @return {boolean} true if the handler was invoked.
     * @private
     */
    private fun invokeIntentHandler(handler: Map<*, Handler<T, S, U>>, intent: String): Boolean {
        debug("invokeIntentHandler_: handler=${handler::class.java.name}, intent=$intent")
        lastErrorMessage = null

        // map of intents or states
        //TODO handle State, Intent, & Map
        val intentHandler = handler[intent]

        if (intentHandler != null) {
            intentHandler(this)
            return true
        } else {
            this.handleError("no matching intent handler for: " + intent)
            return false
        }
    }

    /**
     * Utility function to detect SSML markup.
     *
     * @param {string} text The text to be checked.
     * @return {boolean} true if text is SSML markup.
     * @private
     */
    fun isSsml(text: String): Boolean {
        debug("isSsml_: text=$text")
        if (text.isEmpty()) {
            this.handleError("text can NOT be empty.")
            return false
        }
        return ResponseBuilder.isSsml(text)
    }


    /**
     * Utility function to detect incoming request format.
     *
     * @return {boolean} true if request is not Action API Version 1.
     * @private
     */
    internal fun isNotApiVersionOne(): Boolean {
        debug("isNotApiVersionOne_")
        return actionsApiVersion.isNotEmpty() &&
                (actionsApiVersion.toInt() >= ACTIONS_CONVERSATION_API_VERSION_TWO)
    }

    internal abstract fun extractData()

    // ---------------------------------------------------------------------------
    //                   Response Builders
    // ---------------------------------------------------------------------------

    /**
     * Constructs RichResponse with chainable property setters.
     *
     * @param {RichResponse=} richResponse RichResponse to clone.
     * @return {RichResponse} Constructed RichResponse.
     */
    fun buildRichResponse(richResponse: RichResponse? = null): RichResponse {
        if (richResponse != null) {
            return richResponse.copy()
        } else {
            return RichResponse()
        }
    }

    /**
     * Constructs BasicCard with chainable property setters.
     *
     * @param {string=} bodyText Body text of the card. Can be set using setTitle
     *     instead.
     * @return {BasicCard} Constructed BasicCard.
     */
    fun buildBasicCard(bodyText: String): BasicCard {
        val card = BasicCard()
        if (bodyText.isNotBlank()) {
            card.formattedText = bodyText
        }
        return card
    }

    /**
     * Helper to build prompts from SSML"s.
     *
     * @param {Array<string>} ssmls Array of ssml.
     * @return {Array<Object>} Array of SpeechResponse objects.
     * @private
     */
    fun buildPromptsFromSsmlHelper(ssmls: MutableList<String>): MutableList<GoogleData.NoInputPrompts> {
        debug("buildPromptsFromSsmlHelper_: ssmls=$ssmls")
        return ssmls.map { GoogleData.NoInputPrompts(ssml = it) }.toMutableList()
    }

    /**
     * Helper to build prompts from plain texts.
     *
     * @param {Array<String>} plainTexts Array of plain text to speech.
     * @return {Array<NoInputPrompt>} Array of SpeechResponse objects.
     * @private
     */
    fun buildPromptsFromPlainTextHelper(plainTexts: MutableList<String>): MutableList<GoogleData.NoInputPrompts> {
        debug("buildPromptsFromPlainTextHelper_: plainTexts=$plainTexts")
        return plainTexts.map { GoogleData.NoInputPrompts(textToSpeech = it) }.toMutableList()
    }


    /**
     * If granted permission to user"s name in previous intent, returns user"s
     * display name, family name, and given name. If name info is unavailable,
     * returns null.

     * @example
     * * val app = ApiAIApp(request = req, response = res)
     * * val REQUEST_PERMISSION_ACTION = "request_permission"
     * * val SAY_NAME_ACTION = "get_name"
     * *
     * * fun requestPermission (app) {
     * *   const permission = app.SupportedPermissions.NAME;
     * *   app.askForPermission("To know who you are", permission);
     * * }
     * *
     * * fun sayName (app) {
     * *   if (app.isPermissionGranted()) {
     * *     app.tell("Your name is " + app.getUserName().displayName));
     * *   } else {
     * *     // Response shows that user did not grant permission
     * *     app.tell("Sorry, I could not get your name.");
     * *   }
     * * }
     * * val actionMap = mapOf(
     * *    REQUEST_PERMISSION_ACTION to requestPermission,
     * *    SAY_NAME_ACTION to sayName)
     * * app.handleRequest(actionMap)
     * *
     * @return {Profile} Null if name permission is not granted.
     * *
     * @actionssdk
     * *
     * @apiai
     */
    fun getUserName(): Profile? {
        debug("getUserName")
        return getUser()?.profile
    }

    /**
     * Gets the user locale. Returned string represents the regional language
     * information of the user set in their Assistant settings.
     * For example, "en-US" represents US English.

     * @example
     * * val app = ApiAiApp(request, response)
     * * val locale = app.getUserLocale()
     * *
     * *
     * @return {String} User"s locale, e.g. "en-US". Null if no locale given.
     * *
     * @actionssdk
     * *
     * @apiai
     */
    fun getUserLocale(): String? {
        debug("getUserLocale")
        return getUser()?.locale
    }

    fun handleError(text: String?) {
        debug("handleError_: text=%$text")
        if (text.isNullOrEmpty()) {
            error("Missing text")
            return
        }
        // Log error
//        error.apply(text, Array.prototype.slice.call(arguments, 1));
        // Tell app to say error
        if (responded) {
            return
        }
        // Don"t call other methods; just do directly
        this.response.status(RESPONSE_CODE_BAD_REQUEST).send(API_ERROR_MESSAGE_PREFIX + text)
        this.responded = true;
    }
    
    /**
     * Helper to process a transaction config and create a payment options object.
     *
     * @param {ActionPaymentTransactionConfig|GooglePaymentTransactionConfig}
     *     transactionConfig Configuration for the transaction. Includes payment
     *     options and order options.
     * @return {PaymentOptions} paymentOptions
     * @private
     */
    fun buildPaymentOptions (transactionConfig: TransactionConfig): GoogleData.PaymentOptions {
        debug("buildPromptsFromPlainTextHelper_: transactionConfig=${transactionConfig}")
        var paymentOptions = GoogleData.PaymentOptions()
        if (transactionConfig.type != null) { // Action payment
            paymentOptions.actionProvidedOptions = GoogleData.ActionProvidedOptions(
                paymentType = transactionConfig.type ?: "",
                displayName = transactionConfig.displayName ?: ""
            )
        } else { // Google payment
            paymentOptions.googleProvidedOptions = GoogleData.GoogleProvidedOptions(
                supportedCardNetworks = transactionConfig.cardNetworks ?: mutableListOf(),
                prepaidCardDisallowed = transactionConfig.prepaidCardDisallowed ?: false
            )
            if (transactionConfig.tokenizationParameters != null) {
                paymentOptions?.googleProvidedOptions?.tokenizationParameters = GoogleData.TokenizationParameters(
                    tokenizationType = "PAYMENT_GATEWAY",
                    parameters = transactionConfig.tokenizationParameters
                )
            }
        }
        return paymentOptions;
    }
}

fun debug(msg: String) {
    logger.info(msg)
}

// ---------------------------------------------------------------------------
//                   Kotlin Specific
// ---------------------------------------------------------------------------

