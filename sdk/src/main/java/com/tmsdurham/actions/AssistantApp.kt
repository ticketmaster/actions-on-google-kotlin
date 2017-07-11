package com.tmsdurham.actions

import com.ticketmaster.apiai.ApiAiRequest
import com.ticketmaster.apiai.google.GoogleData
import java.util.logging.Logger

typealias Handler<reified T, reified S> = (AssistantApp<T, S>) -> Unit

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


open abstract class AssistantApp<T, S>(val request: RequestWrapper<T>, val response: ResponseWrapper<S>, val sessionStarted: (() -> Unit)? = null) {
    var actionsApiVersion: String = "1"
    val logger = Logger.getAnonymousLogger()
    lateinit var STANDARD_INTENTS: StandardIntents
    val SUPPORTED_INTENT = SupportedIntent()
    lateinit var BUILT_IN_ARG_NAMES: BuiltInArgNames
    val INPUT_VALUE_DATA_TYPES = InputValueDataTypes()
    lateinit var CONVERSATION_STAGES: ConversationStages
    val SUPPORTED_PERMISSIONS = SupportedPermissions()
    val SIGN_IN_STATUS = SignInStatus()

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
    }

    fun handleRequest(handler: Handler<T, S>) {
        debug("handleRequest: handler=${handler::javaClass.get().name}")
        handler(this)
    }

    fun handleRequest(handler: Map<*, *>) {
        debug("handleRequest: handler=${handler::javaClass.get().name}")
        invokeIntentHandler(handler as Map<*, Handler<T, S>>, getIntent())
    }

    fun askForPermissions(context: String, permissions: MutableList<String>): Any? {
        if (context.isEmpty()) {
            handleError("Assistant context can NOT be empty.");
            return null
        }
        return fulfillPermissionRequest(GoogleData.PermissionsRequest(
                optContext = context,
                permissions = permissions

        ))
    }

    internal abstract fun fulfillPermissionRequest(permissionSpec: GoogleData.PermissionsRequest): Any

    abstract fun getIntent(): String

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
    private fun invokeIntentHandler(handler: Map<*, Handler<T, S>>, intent: String): Boolean {
        debug("invokeIntentHandler_: handler=${handler::class.java.name}, intent=$intent");
        lastErrorMessage = null

        // map of intents or states
        //TODO handle State, Intent, & Map
        val intentHandler = handler[intent]

        if (intentHandler != null) {
            intentHandler(this)
            return true
        } else {
            this.handleError("no matching intent handler for: " + intent);
            return false
        }
    }

    /**
     * Utility function to detect incoming request format.
     *
     * @return {boolean} true if request is not Action API Version 1.
     * @private
     */
    internal fun isNotApiVersionOne(): Boolean {
        debug("isNotApiVersionOne_");
        return actionsApiVersion.isNotEmpty() &&
                (actionsApiVersion.toInt() >= ACTIONS_CONVERSATION_API_VERSION_TWO)
    }

    internal abstract fun extractData()

    // ---------------------------------------------------------------------------
    //                   Response Builders
    // ---------------------------------------------------------------------------

    fun handleError(text: String?) {

    }

    fun debug(msg: String) {
        logger.info(msg)
    }
}

