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


enum class StandardIntents(val value: String) {
    /** App fires MAIN intent for queries like [talk to $app]. */
    MAIN("actions.intent.MAIN"),
    /** App fires TEXT intent when action issues ask intent. */
    TEXT("actions.intent.TEXT"),
    /** App fires PERMISSION intent when action invokes askForPermission. */
    PERMISSION("actions.intent.PERMISSION"),
    /** App fires OPTION intent when user chooses from options provided. */
    OPTION("actions.intent.OPTION"),
    /** App fires TRANSACTION_REQUIREMENTS_CHECK intent when action sets up transaction. */
    TRANSACTION_REQUIREMENTS_CHECK("actions.intent.TRANSACTION_REQUIREMENTS_CHECK"),
    /** App fires DELIVERY_ADDRESS intent when action asks for delivery address. */
    DELIVERY_ADDRESS("actions.intent.DELIVERY_ADDRESS"),
    /** App fires TRANSACTION_DECISION intent when action asks for transaction decision. */
    TRANSACTION_DECISION("actions.intent.TRANSACTION_DECISION"),
    /** App fires CONFIRMATION intent when requesting affirmation from user. */
    CONFIRMATION("actions.intent.CONFIRMATION"),
    /** App fires DATETIME intent when requesting date/time from user. */
    DATETIME("actions.intent.DATETIME"),
    /** App fires SIGN_IN intent when requesting sign-in from user. */
    SIGN_IN("actions.intent.SIGN_IN")
}

enum class SupportedIntent(val value: String) {
    /**
     * The user"s name as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#UserProfile|UserProfile object}
     */
    NAME("NAME"),
    /**
     * The location of the user"s current device, as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#Location|Location object}.
     */
    DEVICE_PRECISE_LOCATION("DEVICE_PRECISE_LOCATION"),
    /**
     * City and zipcode corresponding to the location of the user"s current device, as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#Location|Location object}.
     */
    DEVICE_COARSE_LOCATION("DEVICE_COARSE_LOCATION")
}

/**
 * List of built-in argument names.
 */
enum class BuiltInArgNames(value: String) {
    /** Permission granted argument. */
    PERMISSION_GRANTED("PERMISSION"),
    /** Option selected argument. */
    OPTION("OPTION"),
    /** Transaction requirements check result argument. */
    TRANSACTION_REQ_CHECK_RESULT("TRANSACTION_REQUIREMENTS_CHECK_RESULT"),
    /** Delivery address value argument. */
    DELIVERY_ADDRESS_VALUE("DELIVERY_ADDRESS_VALUE"),
    /** Transactions decision argument. */
    TRANSACTION_DECISION_VALUE("TRANSACTION_DECISION_VALUE"),
    /** Confirmation argument. */
    CONFIRMATION("CONFIRMATION"),
    /** DateTime argument. */
    DATETIME("DATETIME"),
    /** Sign in status argument. */
    SIGN_IN("SIGN_IN")
}

const val ANY_TYPE_PROPERTY = "@type"

/**
 * List of built-in value type names.
 */
enum class InputValueDataTypes(val value: String) {
    /** Permission Value Spec. */
    PERMISSION("type.googleapis.com/google.actions.v2.PermissionValueSpec"),
    /** Option Value Spec. */
    OPTION("type.googleapis.com/google.actions.v2.OptionValueSpec"),
    /** Transaction Requirements Check Value Spec. */
    TRANSACTION_REQ_CHECK("type.googleapis.com/google.actions.v2.TransactionRequirementsCheckSpec"),
    /** Delivery Address Value Spec. */
    DELIVERY_ADDRESS("type.googleapis.com/google.actions.v2.DeliveryAddressValueSpec"),
    /** Transaction Decision Value Spec. */
    TRANSACTION_DECISION("type.googleapis.com/google.actions.v2.TransactionDecisionValueSpec"),
    /** Confirmation Value Spec. */
    CONFIRMATION("type.googleapis.com/google.actions.v2.ConfirmationValueSpec"),
    /** DateTime Value Spec. */
    DATETIME("type.googleapis.com/google.actions.v2.DateTimeValueSpec")
}

/**
 * List of supported permissions the app supports.
 */
enum class SupportedPermissions(value: String) {
    /**
     * The user"s name as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#UserProfile|UserProfile object}
     */
    NAME("NAME"),
    /**
     * The location of the user"s current device, as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#Location|Location object}.
     */
    DEVICE_PRECISE_LOCATION("DEVICE_PRECISE_LOCATION"),
    /**
     * City and zipcode corresponding to the location of the user"s current device, as defined in the
     * {@link https://developers.google.com/actions/reference/conversation#Location|Location object}.
     */
    DEVICE_COARSE_LOCATION("DEVICE_COARSE_LOCATION")
}

enum class SignInStatus(value: String) {
    // Unknown status.
    UNSPECIFIED("SIGN_IN_STATUS_UNSPECIFIED"),
    // User successfully completed the account linking.
    OK("OK"),
    // Cancelled or dismissed account linking.
    CANCELLED("CANCELLED"),
    // System or network error.
    ERROR("ERROR")
}


abstract class AssistantApp<T, S>(val request: RequestWrapper<T>, val response: ResponseWrapper<S>) {
    var actionsApiVersion: String = ""
    val logger = Logger.getAnonymousLogger()

    init {
        debug("AssistantApp constructor");

        if (request == null) {
            this.handleError("Request can NOT be empty.")
        } else if (response == null) {
            this.handleError("Response can NOT be empty.")
        } else {
            if (request.headers[ACTIONS_CONVERSATION_API_VERSION_HEADER] != null) {
                actionsApiVersion = request.headers[ACTIONS_CONVERSATION_API_VERSION_HEADER]!!
                debug("Actions API version from header: " + this.actionsApiVersion);
            }
            if (request.body is ApiAiRequest<*>) {
                if (request.body.originalRequest != null) {
                    this.actionsApiVersion = request.body.originalRequest?.version ?: "missing version"
                    debug("Actions API version from APIAI: " + this.actionsApiVersion);
                }
            }
        }
    }

    fun handleRequest(handler: Any) {
        debug("handleRequest: handler=${handler::javaClass.get().name}")
        if (handler is Map<*, *>) {
            invokeIntentHandler(handler as Map<*, Handler<T, S>>, getIntent())
        } else {
            (handler as Handler<T, S>)(this)
        }
    }

    fun askForPermissions(context: String, permissions: List<SupportedIntent>): Any? {
        if (context.isEmpty()) {
            handleError("Assistant context can NOT be empty.");
            return null
        }
        return fulfillPermissionRequest(GoogleData.PermissionsRequest(
                optContext = context,
                permissions = permissions.map { it.value }.toMutableList()

        ))
    }

    internal abstract fun fulfillPermissionRequest(permissionSpec: GoogleData.PermissionsRequest): Any

    abstract fun getIntent(): String

    // ---------------------------------------------------------------------------
    //                   Private Helpers
    // ---------------------------------------------------------------------------

    private var lastErrorMessage: String? = null
    val dummyHandler = ApiAiApp<T>(request = RequestWrapper(body = ApiAiRequest()), response = ResponseWrapper())
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

