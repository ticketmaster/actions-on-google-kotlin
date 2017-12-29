package com.tmsdurham.actions

import com.tmsdurham.dialogflow.*
import com.tmsdurham.dialogflow.google.GoogleData
import java.util.logging.Logger

typealias Handler<T, S> = (AssistantApp<T, S>) -> Unit

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
    /** App fires NO_INPUT intent when user doesn't provide input. */
    val NO_INPUT = "actions.intent.NO_INPUT"
    /** App fires CANCEL intent when user exits app mid-dialog. */
    val CANCEL = "actions.intent.CANCEL"
    /** App fires NEW_SURFACE intent when requesting handoff to a new surface from user. */
    val NEW_SURFACE = "actions.intent.NEW_SURFACE"
    /** App fires REGISTER_UPDATE intent when requesting the user to register for proactive updates. */
    val REGISTER_UPDATE = "actions.intent.REGISTER_UPDATE"
    /** App receives CONFIGURE_UPDATES intent to indicate a custom REGISTER_UPDATE intent should be sent. */
    val CONFIGURE_UPDATES = "actions.intent.CONFIGURE_UPDATES"
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
    /** Reprompt count for consecutive NO_INPUT intents. */
    val REPROMPT_COUNT = "REPROMPT_COUNT"
    /** Flag representing finality of NO_INPUT intent. */
    val IS_FINAL_REPROMPT = "IS_FINAL_REPROMPT"
    /** New surface value argument. */
    val NEW_SURFACE = "NEW_SURFACE"
    /** Update registration value argument. */
    val REGISTER_UPDATE = "REGISTER_UPDATE"
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
    /** Sign in Value Spec. */
    val SIGN_IN = "type.googleapis.com/google.actions.v2.SignInValueSpec"
    /** New Surface Value Spec. */
    val NEW_SURFACE = "type.googleapis.com/google.actions.v2.NewSurfaceValueSpec"
    /** Register Update Value Spec. */
    val REGISTER_UPDATE = "type.googleapis.com/google.actions.v2.RegisterUpdateValueSpec"
}

/**
 * List of possible conversation stages, as defined in the
 * {@link https://developers.google.com/actions/reference/conversation#Conversation|Conversation object}.
 * @actionssdk
 * @dialogflow
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
 * List of surface capabilities supported by the app.
 * @readonly
 * @enum {string}
 * @actionssdk
 * @dialogflow
 */
class SurfaceCapabilities {
    /**
     * The ability to output audio.
     */
    val AUDIO_OUTPUT = "actions.capability.AUDIO_OUTPUT"
    /**
     * The ability to output on a screen
     */
    val SCREEN_OUTPUT = "actions.capability.SCREEN_OUTPUT"
}

/**
 * List of possible user input types.
 * @readonly
 * @enum {number}
 * @actionssdk
 * @dialogflow
 */
class InputTypes(val isNotApiVersionOne: Boolean) {
    /**
     * Unspecified.
     */
    val UNSPECIFIED = if (isNotApiVersionOne) "UNSPECIFIED" else "0"
    /**
     * Input given by touch.
     */
    val TOUCH = if (isNotApiVersionOne) "TOUCH" else "1"
    /**
     * Input given by voice (spoken).
     */
    val VOICE = if (isNotApiVersionOne) "VOICE" else "2"
    /**
     * Input given by keyboard (typed).
     */
    val KEYBOARD = if (isNotApiVersionOne) "KEYBOARD" else "3"
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
    /**
     * Confirmation to receive proactive content at any time from the app.
     */
    val UPDATE = "UPDATE"
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

/**
 * Possible update trigger time context frequencies.
 * @readonly
 * @type {object}
 */
class TimeContextFrequency {
    val DAILY = "DAILY"
}


open abstract class AssistantApp<T, S>(val request: RequestWrapper<T>, val response: ResponseWrapper<S>, val sessionStarted: (() -> Unit)? = null) {
    var actionsApiVersion: String = "2"
    var STANDARD_INTENTS: StandardIntents
    val SUPPORTED_INTENT = SupportedIntent()
    var BUILT_IN_ARG_NAMES: BuiltInArgNames
    val INPUT_VALUE_DATA_TYPES = InputValueDataTypes()
    var CONVERSATION_STAGES: ConversationStages
    val SURFACE_CAPABILITIES = SurfaceCapabilities()
    var INPUT_TYPES: InputTypes
    val SUPPORTED_PERMISSIONS = SupportedPermissions()
    val SIGN_IN_STATUS = SignInStatus()
    val TIME_CONTEXT_FREQUENCY = TimeContextFrequency()

    var responded = false
    var apiVersion_: String = "2"
    // Unique to Kotlin sdk - state is a json String that is serialized/deserialized
    var state: String? = null
    var data: MutableMap<String, Any> = mutableMapOf()
    var contexts = mutableMapOf<String, Context>()
    val requestExtractor: RequestExtractor<T, S>

    /**
     * Values related to supporting {@link Transactions}.
     * @readonly
     * @type {object}
     */

    init {
        debug("AssistantApp constructor")

        if (request == null) {
            handleError("Request can NOT be empty.")
        } else if (response == null) {
            handleError("Response can NOT be empty.")
        } else {
            if (request.headers[ACTIONS_CONVERSATION_API_VERSION_HEADER] != null) {
                actionsApiVersion = request.headers[ACTIONS_CONVERSATION_API_VERSION_HEADER] ?: "2"
                debug("Actions API version from header: " + this.actionsApiVersion)
            } else if (request.headers[CONVERSATION_API_VERSION_HEADER] != null) {
                actionsApiVersion = if (request.headers[CONVERSATION_API_VERSION_HEADER] == "v1") "1" else "2"
            }
            if (request.body is DialogflowRequest) {
                if (request.body.originalRequest != null) {
                    actionsApiVersion = request.body.originalRequest?.version ?: "1"
                    debug("Actions API version from Dialogflow: " + this.actionsApiVersion)
                }
            }
        }
        STANDARD_INTENTS = StandardIntents(isNotApiVersionOne())
        BUILT_IN_ARG_NAMES = BuiltInArgNames(isNotApiVersionOne())
        CONVERSATION_STAGES = ConversationStages(isNotApiVersionOne())
        INPUT_TYPES = InputTypes(isNotApiVersionOne())

        /**
         * API version describes version of the Assistant request.
         * @deprecated
         * @private
         * @type {string}
         */
        // Populates API version.
        if (request.get(CONVERSATION_API_VERSION_HEADER) != null) {
            apiVersion_ = request.get(CONVERSATION_API_VERSION_HEADER) ?: ""
            debug("Assistant API version: " + apiVersion_)
        }

        requestExtractor = RequestExtractor(this)
    }


    fun handleRequest(handler: Handler<T, S>) {
        debug("handleRequest: handler=${handler::javaClass.get().name}")
        handler(this)
    }

    fun handleRequest(handler: Map<*, *>) {
        debug("handleRequest: handler=${handler::javaClass.get().name}")
        invokeIntentHandler(handler as Map<*, Handler<T, S>>, getIntent())
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
     * val app = DialogflowApp(request = req, response = res)
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
     * @dialogflow
     */
    fun askForPermissions(context: String, vararg permissions: String, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<S>? {
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
        return fulfillPermissionsRequest(GoogleData.PermissionsRequest(
                optContext = context,
                permissions = permissions.toMutableList()), dialogState)
    }


    /**
     * Prompts the user for permission to send proactive updates at any time.
     *
     * @example
     * val app = new DialogflowApp({request, response})
     * val REQUEST_PERMISSION_ACTION = "request.permission"
     * val PERMISSION_REQUESTED = "permission.requested"
     * val SHOW_IMAGE = "show.image"
     *
     * fun requestPermission (app) {
     *   app.askForUpdatePermission("show.image", [
     *     {
     *       name: "image_to_show",
     *       textValue: "image_type_1"
     *     }
     *   ])
     * }
     *
     * fun checkPermission (app) {
     *   if (app.isPermissionGranted()) {
     *     app.tell("Great, I"ll send an update whenever I notice a change")
     *   } else {
     *     // Response shows that user did not grant permission
     *     app.tell("Alright, just let me know whenever you need the weather!")
     *   }
     * }
     *
     * fun showImage (app) {
     *   showPicture(app.getArgument("image_to_show"))
     * }
     *
     * val actionMap = new Map()
     * actionMap.set(REQUEST_PERMISSION_ACTION, requestPermission)
     * actionMap.set(PERMISSION_REQUESTED, checkPermission)
     * actionMap.set(SHOW_IMAGE, showImage)
     * app.handleRequest(actionMap)
     *
     * @param {String} intent If using Dialogflow, the action name of the intent
     *     to be triggered when the update is received. If using Actions SDK, the
     *     intent name to be triggered when the update is received.
     * @param {Array<IntentArgument>} intentArguments The necessary arguments
     *     to fulfill the intent triggered on update. These can be retrieved using
     *     {@link AssistantApp#getArgument}.
     * @param {MutableMap<String, Any?>} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant. Used in {@link ActionsSdkApp}.
     * @return {ResponseWrapper<S>?} A response is sent to Assistant to ask for the user"s permission for any
     *     invalid input, we return null.
     * @actionssdk
     * @dialogflow
     */
    fun askForUpdatePermission(intent: String, intentArguments: MutableList<Arguments>? = null, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<S>? {
        debug("askForUpdatePermission: intent=$intent, intentArguments=$intentArguments, dialogState=$dialogState")
        if (intent.isBlank()) {
            handleError("Name of intent to trigger on update must be specified")
            return null
        }
        val updatePermissionValueSpec = GoogleData.PermissionsRequest(intent = intent)
        if (intentArguments?.isNotEmpty() == true) {
            updatePermissionValueSpec.arguments = intentArguments
        }
        updatePermissionValueSpec.permissions = mutableListOf(this.SUPPORTED_PERMISSIONS.UPDATE)

        return this.fulfillPermissionsRequest(
                permissionsSpec = updatePermissionValueSpec, dialogState = dialogState)
    }

    /**
     * Asks user for a confirmation.
     *
     * @example
     * val app = DialogflowApp(request, response)
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
     * @dialogflow
     */
    fun askForConfirmation(prompt: String = "", dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<S>? {
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
     * val app = DialogflowApp(request, response )
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
     * @dialogflow
     */
    fun askForDateTime(initialPrompt: String? = null, datePrompt: String? = null, timePrompt: String? = null, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<S>? {
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

    /**
     * Hands the user off to a web sign in flow. App sign in and OAuth credentials
     * are set in the {@link https://console.actions.google.com|Actions Console}.
     * Retrieve the access token in subsequent intents using
     * app.getUser().accessToken.
     *
     * Note: Currently this API requires enabling the app for Transactions APIs.
     * To do this, fill out the App Info section of the Actions Console project
     * and check the box indicating the use of Transactions under "Privacy and
     * consent".
     *
     * @example
     * val app = DialogflowApp(request, response)
     * val WELCOME_INTENT = "input.welcome"
     * val SIGN_IN = "sign.in"
     *
     * fun welcomeIntent (app: MyAction) {
     *   app.askForSignIn()
     * }
     *
     * fun signIn (app: MyAction) {
     *   if (app.getSignInStatus() == app.SignInstatus.OK) {
     *     val accessToken = app.getUser().accessToken
     *     app.ask("Great, thanks for signing in!")
     *   } else {
     *     app.ask("I won\"t be able to save your data, but let\"s continue!")
     *   }
     * }
     *
     * val actionMap = mapOf(
     *      WELCOME_INTENT to ::welcomeIntent,
     *      SIGN_IN to ::signIn)
     * app.handleRequest(actionMap)
     *
     * @param {DialogState?=} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant. Used in {@link ActionsSdkAssistant}.
     * @actionssdk
     * @dialogflow
     */
    fun askForSignIn(dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<S>? {
        debug("askForSignIn: dialogState=$dialogState")
        return fulfillSignInRequest(dialogState)
    }

    /**
     * Requests the user to switch to another surface during the conversation.
     *
     * @example
     * val app = DialogflowApp(request, response)
     * val WELCOME_INTENT = 'input.welcome'
     * val SHOW_IMAGE = 'show.image'
     *
     * fun welcomeIntent (app) {
     *   if (app.hasSurfaceCapability(app.SurfaceCapabilities.SCREEN_OUTPUT)) {
     *     showPicture(app)
     *   } else if (app.hasAvailableSurfaceCapabilities(app.SurfaceCapabilities.SCREEN_OUTPUT)) {
     *     app.askForNewSurface("To show you an image",
     *       "Check out this image",
     *       mutableListOf(app.SurfaceCapabilities.SCREEN_OUTPUT)
     *     )
     *   } else {
     *     app.tell("This part of the app only works on screen devices. Sorry about that")
     *   }
     * }
     *
     * fun showImage (app) {
     *   if (!app.isNewSurface()) {
     *     app.tell("Ok, I understand. You don't want to see pictures. Bye")
     *   } else {
     *     showPicture(app, pictureType)
     *   }
     * }
     *
     * val actionMap = Map()
     * actionMap.set(WELCOME_INTENT, welcomeIntent)
     * actionMap.set(SHOW_IMAGE, showImage)
     * app.handleRequest(actionMap)
     *
     * @param {String} context Context why surface is requested it's the TTS
     *     prompt prefix (action phrase) we ask the user.
     * @param {String} notificationTitle Title of the notification appearing on
     *     surface device.
     * @param {MutableList<String>} capabilities The list of capabilities required in
     *     the surface.
     * @param {MutableMap<String, Any?>?} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant. Used in {@link ActionsSdkApp}.
     * @return {ResponseWrapper<DialogflowResponse>?} HTTP response.
     * @dialogflow
     * @actionssdk
     */
    fun askForNewSurface(context: String, notificationTitle: String, capabilities: MutableList<String>, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<DialogflowResponse>? {
        debug("askForNewSurface: context=$context, notificationTitle=$notificationTitle, capabilities=$capabilities, dialogState=$dialogState")
        val newSurfaceValueSpec = NewSurfaceValueSpec(context, notificationTitle, capabilities)
        return fulfillSystemIntent(this.STANDARD_INTENTS.NEW_SURFACE,
                this.INPUT_VALUE_DATA_TYPES.NEW_SURFACE, newSurfaceValueSpec,
                "PLACEHOLDER_FOR_NEW_SURFACE", dialogState)
    }


    /**
     * Requests the user to register for daily updates.
     *
     * @example
     * val app = DialogflowApp(request, response)
     * val WELCOME_INTENT = "input.welcome"
     * val SHOW_IMAGE = "show.image"
     *
     * fun welcomeIntent (app) {
     *   app.askToRegisterDailyUpdate("show.image", mutableListOf(Arguments(
     *       name: "image_to_show",
     *       textValue: "image_type_1"))
     *
     *
     * fun showImage (app) {
     *   showPicture(app.getArgument("image_to_show"))
     * }
     *
     * val actionMap = new Map()
     * actionMap.set(WELCOME_INTENT, welcomeIntent)
     * actionMap.set(SHOW_IMAGE, showImage)
     * app.handleRequest(actionMap)
     *
     * @param {String} intent If using Dialogflow, the action name of the intent
     *     to be triggered when the update is received. If using Actions SDK, the
     *     intent name to be triggered when the update is received.
     * @param {Array<IntentArgument>?} intentArguments The necessary arguments
     *     to fulfill the intent triggered on update. These can be retrieved using
     *     {@link AssistantApp#getArgument}.
     * @param {MutableMapOf<String, Any?>?} dialogState JSON object the app uses to hold dialog state that
     *     will be circulated back by Assistant. Used in {@link ActionsSdkApp}.
     * @return {ResponseWrapper<S>?} HTTP response.
     * @dialogflow
     * @actionssdk
     */
    fun askToRegisterDailyUpdate(intent: String, intentArguments: MutableList<Arguments>? = null, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<DialogflowResponse>? {
        debug("askToRegisterDailyUpdate: intent=$intent, intentArguments=$intentArguments, dialogState=$dialogState")
        if (intent.isNullOrBlank()) {
            handleError("Name of intent to trigger on update must be specified")
            return null
        }
        val registerUpdateValueSpec = RegisterUpdateValueSpec(
                intent = intent,
                triggerContext = TriggerContext(timeContext = TimeContext(frequency = TIME_CONTEXT_FREQUENCY.DAILY))
        )

        if (intentArguments?.isNotEmpty() == true) {
            registerUpdateValueSpec.arguments = intentArguments
        }
        return this.fulfillRegisterUpdateIntent(this.STANDARD_INTENTS.REGISTER_UPDATE,
                INPUT_VALUE_DATA_TYPES.REGISTER_UPDATE, registerUpdateValueSpec,
                "PLACEHOLDER_FOR_REGISTER_UPDATE", dialogState)
    }


    internal abstract fun fulfillSignInRequest(dialogState: MutableMap<String, Any?>?): ResponseWrapper<S>?
    internal abstract fun fulfillDateTimeRequest(confirmationValueSpec: ConfirmationValueSpec, dialogState: MutableMap<String, Any?>?): ResponseWrapper<S>?
    internal abstract fun fulfillConfirmationRequest(confirmationValueSpec: ConfirmationValueSpec, dialogState: MutableMap<String, Any?>?): ResponseWrapper<S>?
    internal abstract fun fulfillSystemIntent(intent: String, specType: String, intentSpec: NewSurfaceValueSpec, promptPlaceholder: String? = null, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<DialogflowResponse>?
    internal abstract fun fulfillRegisterUpdateIntent(intent: String, specType: String, intentSpec: RegisterUpdateValueSpec, promptPlaceholder: String? = null, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<DialogflowResponse>?

    data class ConfirmationValueSpec(var dialogSpec: DialogSpec? = null)

    data class NewSurfaceValueSpec(var context: String? = null,
                                   var notificationTitle: String? = null,
                                   var capabilities: MutableList<String>? = null)

    data class RegisterUpdateValueSpec(var intent: String? = null,
                                       var arguments: MutableList<Arguments>? = null,
                                       var triggerContext: TriggerContext? = null)

    data class TriggerContext(var timeContext: TimeContext? = null)

    data class TimeContext(var frequency: String? = null)

    data class DialogSpec(var requestConfirmationText: String? = null,
                          var requestDatetimeText: String? = null,
                          var requestDateText: String? = null,
                          var requestTimeText: String? = null)

    /**
     * Checks whether user is in transactable state.
     *
     * @example
     * val app = DialogflowApp(request = request, response = response)
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
     *   if (app.getTransactionRequirementsResult() == app.Transactions.ResultType.OK) {
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
     * @dialogflow
     */
    fun askForTransactionRequirements(transactionConfig: TransactionConfig? = null, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<S>? {
        debug("checkForTransactionRequirements: transactionConfig=$transactionConfig," +
                " dialogState=$dialogState")
        val transactionRequirementsCheckSpec = TransactionRequirementsCheckSpec()
        if (transactionConfig?.deliveryAddressRequired ?: false) {
            transactionRequirementsCheckSpec.orderOptions = GoogleData.OrderOptions(
                    requestDeliveryAddress = transactionConfig?.deliveryAddressRequired ?: false)
        }
        if (transactionConfig?.type != null ||
                transactionConfig?.cardNetworks != null) {
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
     * val app = DialogflowApp(request = request, response = response);
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
     * @dialogflow
     */
    fun askForTransactionDecision(order: Order, transactionConfig: TransactionConfig? = null, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<S>? {
        debug("askForTransactionDecision: order=$order, transactionConfig=$transactionConfig, dialogState=$dialogState")
        if (transactionConfig?.type != null &&
                transactionConfig.cardNetworks?.isNotEmpty() ?: false) {
            handleError("Invalid transaction configuration. Must be of type" +
                    "ActionPaymentTransactionConfig or GooglePaymentTransactionConfig")
            return null
        }
        val transactionDecisionValueSpec = TransactionDecisionValueSpec(
                proposedOrder = order)

        if (transactionConfig?.deliveryAddressRequired ?: false) {
            transactionDecisionValueSpec.orderOptions = GoogleData.OrderOptions(
                    requestDeliveryAddress = transactionConfig?.deliveryAddressRequired ?: false
            )
        }
        if (transactionConfig?.type != null ||
                transactionConfig?.cardNetworks?.isNotEmpty() ?: false) {
            transactionDecisionValueSpec.paymentOptions =
                    buildPaymentOptions(transactionConfig)
        }
        if (transactionConfig?.customerInfoOptions != null) {
            if (transactionDecisionValueSpec.orderOptions == null) {
                transactionDecisionValueSpec.orderOptions = GoogleData.OrderOptions()
            }
            transactionDecisionValueSpec.orderOptions?.customerInfoOptions =
                    transactionConfig.customerInfoOptions
        }
        return fulfillTransactionDecision(transactionDecisionValueSpec,
                dialogState)
    }

    data class TransactionDecisionValueSpec(var proposedOrder: Order, var orderOptions: GoogleData.OrderOptions? = null, var paymentOptions: GoogleData.PaymentOptions? = null)

    data class TransactionRequirementsCheckSpec(var orderOptions: GoogleData.OrderOptions? = null,
                                                var paymentOptions: GoogleData.PaymentOptions? = null)

    internal abstract fun fulfillTransactionRequirementsCheck(transactionRequirementsCheckSpec: TransactionRequirementsCheckSpec, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<S>?
    internal abstract fun fulfillTransactionDecision(transactionDecisionValueSpec: TransactionDecisionValueSpec, dialogState: MutableMap<String, Any?>? = null): ResponseWrapper<S>?


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
            if (apiVersion_ != null) {
                response.append(CONVERSATION_API_VERSION_HEADER, apiVersion_)
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
    fun buildOrder(orderId: String): Order {
        return Order(orderId)
    }

    /**
     * Constructs Cart with chainable property setters.
     *
     * @param {String=} id Unique identifier for the cart.
     * @return {Cart} Constructed Cart.
     */
    fun buildCart(cartId: String? = null): Cart {
        return Cart(cartId)
    }

    /**
     * Constructs LineItem with chainable property setters.
     *
     * @param {String} name Name of the line item.
     * @param {String} id Unique identifier for the item.
     * @return {LineItem} Constructed LineItem.
     */
    fun buildLineItem(name: String, id: String): LineItem {
        return LineItem(name, id)
    }

    /**
     * Constructs OrderUpdate with chainable property setters.
     *
     * @param {String} orderId Unique identifier of the order.
     * @param {Boolean} isGoogleOrderId True if the order ID is provided by
     *     Google. False if the order ID is app provided.
     * @return {OrderUpdate} Constructed OrderUpdate.
     */
    fun buildOrderUpdate(orderId: String, isGoogleOrderId: Boolean): OrderUpdate {
        return OrderUpdate(orderId, isGoogleOrderId)
    }


    internal abstract fun fulfillPermissionsRequest(permissionsSpec: GoogleData.PermissionsRequest, dialogState: MutableMap<String, Any?>?): ResponseWrapper<S>?

    abstract fun getIntent(): String?
    abstract fun tell(speech: String, displayText: String? = null): ResponseWrapper<S>?
    /**
     * One arg function for Convenience from Java
     */
    abstract fun tell(speech: String): ResponseWrapper<S>?

    abstract fun tell(richResponse: RichResponse?): ResponseWrapper<S>?
    abstract fun tell(simpleResponse: SimpleResponse): ResponseWrapper<S>?

    // ---------------------------------------------------------------------------
    //                   Private Helpers
    // ---------------------------------------------------------------------------

    private var lastErrorMessage: String? = null
    /**
     * Utility function to invoke an intent handler.
     *
     * @param {Object} handler The handler for the request.
     * @param {string} intent The intent to handle.
     * @return {boolean} true if the handler was invoked.
     * @private
     */
    private fun invokeIntentHandler(handler: Map<*, Handler<T, S>>, intent: String?): Boolean {
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
    fun isNotApiVersionOne(): Boolean {
        debug("isNotApiVersionOne_")
        return if (actionsApiVersion.isEmpty()) {
            true
        } else {
            (actionsApiVersion.toInt() >= ACTIONS_CONVERSATION_API_VERSION_TWO)
        }
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
     * Zero arg function for convenience when calling from Java
     */
    fun buildRichResponse() = buildRichResponse(null)


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
     * * val app = DialogflowApp(request = req, response = res)
     * * val REQUEST_PERMISSION_ACTION = "request_permission"
     * * val SAY_NAME_ACTION = "get_name"
     * *
     * * fun requestPermission (app: DialogflowApp) {
     * *   val permission = app.SupportedPermissions.NAME
     * *   app.askForPermission("To know who you are", permission)
     * * }
     * *
     * * fun sayName (app: DialogflowApp) {
     * *   if (app.isPermissionGranted()) {
     * *     app.tell("Your name is " + app.getUserName().displayName))
     * *   } else {
     * *     // Response shows that user did not grant permission
     * *     app.tell("Sorry, I could not get your name.")
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
     * @dialogflow
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
     * * val app = DialogflowApp(request, response)
     * * val locale = app.getUserLocale()
     * *
     * *
     * @return {String} User"s locale, e.g. "en-US". Null if no locale given.
     * *
     * @actionssdk
     * *
     * @dialogflow
     */
    fun getUserLocale(): String? {
        debug("getUserLocale")
        return getUser()?.locale
    }

    /**
     * Returns the set of other available surfaces for the user.
     *
     * @return {Array<Surface>} Empty if no available surfaces.
     * @actionssdk
     * @dialogflow
     */
    fun getAvailableSurfaces(): MutableList<Surface> {
        debug("getAvailableSurfaces")
        return requestExtractor.requestData()?.availableSurfaces ?: mutableListOf()
    }

    /**
     * Returns true if user has an available surface which includes all given
     * capabilities. Available surfaces capabilities may exist on surfaces other
     * than that used for an ongoing conversation.
     *
     * @param {string|Array<string>} capabilities Must be one of
     *     {@link SurfaceCapabilities}.
     * @return {boolean} True if user has a capability available on some surface.
     *
     * @dialogflow
     * @actionssdk
     */
    fun hasAvailableSurfaceCapabilities(vararg capabilities: String): Boolean {
        debug("hasAvailableSurfaceCapabilities: capabilities=$capabilities")
        val availableSurfaces = requestExtractor.requestData()
        availableSurfaces?.availableSurfaces?.forEach {
            val availableCapabilities = it.capabilities?.map { it.name }
            val unavailableCapabilities = capabilities.filter { !(availableCapabilities?.contains(it) ?: false) }
            if (unavailableCapabilities.isEmpty()) {
                return true
            }
        }
        return false
    }

    /**
     * Returns the result of the AskForNewSurface helper.
     *
     * @return {boolean} True if user has triggered conversation on a new device
     *     following the NEW_SURFACE intent.
     * @actionssdk
     * @dialogflow
     */
    fun isNewSurface(): Boolean {
        debug("isNewSurface")
        val argument = requestExtractor.findArgument(this.BUILT_IN_ARG_NAMES.NEW_SURFACE)
        return argument?.extension?.status == "OK"
    }

    /**
     * Returns true if user device has a given surface capability.
     *
     * @param {string} capability Must be one of {@link SurfaceCapabilities}.
     * @return {boolean} True if user device has the given capability.
     *
     * @example
     * val app = DialogflowApp(request = req, response = res)
     * val DESCRIBE_SOMETHING = "DESCRIBE_SOMETHING"
     *
     * fun describe (app: DialogflowApp) {
     *   if (app.hasSurfaceCapability(app.SurfaceCapabilities.SCREEN_OUTPUT)) {
     *     app.tell(richResponseWithBasicCard)
     *   } else {
     *     app.tell("Let me tell you about ...")
     *   }
     * }
     * val actionMap = mapOf(
     *      DESCRIBE_SOMETHING to ::describe)
     * app.handleRequest(actionMap)
     *
     * @dialogflow
     * @actionssdk
     */
    fun hasSurfaceCapability(requestedCapability: String): Boolean {
        debug("hasSurfaceCapability: requestedCapability=$requestedCapability")
        val capabilities = getSurfaceCapabilities()
        if (capabilities?.isEmpty() ?: false) {
            error("No incoming capabilities to search " +
                    "for request capability: $requestedCapability")
            return false
        }
        return capabilities?.contains(requestedCapability) ?: false
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
        //TODO revist if response should be sent on all errors - issue with context not set when from other platforms
        this.response.status(RESPONSE_CODE_BAD_REQUEST)
//        this.response.status(RESPONSE_CODE_BAD_REQUEST).send(API_ERROR_MESSAGE_PREFIX + text)
//        this.responded = true
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
    fun buildPaymentOptions(transactionConfig: TransactionConfig? = null): GoogleData.PaymentOptions {
        debug("buildPromptsFromPlainTextHelper_: transactionConfig=${transactionConfig}")
        var paymentOptions = GoogleData.PaymentOptions()
        if (transactionConfig?.type != null) { // Action payment
            paymentOptions.actionProvidedOptions = GoogleData.ActionProvidedOptions(
                    paymentType = transactionConfig.type,
                    displayName = transactionConfig.displayName
            )
        } else { // Google payment
            paymentOptions.googleProvidedOptions = GoogleData.GoogleProvidedOptions(
                    supportedCardNetworks = transactionConfig?.cardNetworks ?: mutableListOf(),
                    prepaidCardDisallowed = transactionConfig?.prepaidCardDisallowed ?: false
            )
            if (transactionConfig?.tokenizationParameters != null) {
                paymentOptions.googleProvidedOptions?.tokenizationParameters = GoogleData.TokenizationParameters(
                        tokenizationType = "PAYMENT_GATEWAY",
                        parameters = transactionConfig.tokenizationParameters
                )
            }
        }
        return paymentOptions
    }


    fun getUser() = requestExtractor.getUser()
    fun getDeviceLocation() = requestExtractor.getDeviceLocation()
    fun getArgumentCommon(argName: String) = requestExtractor.getArgumentCommon(argName)
    fun getTransactionRequirementsResult() = requestExtractor.getTransactionRequirementsResult()
    fun getDeliveryAddress() = requestExtractor.getDeliveryAddress()
    fun getTransactionDecision() = requestExtractor.getTransactionDecision()
    fun getUserConfirmation() = requestExtractor.getUserConfirmation()
    fun getDateTime() = requestExtractor.getDateTime()
    fun getSignInStatus() = requestExtractor.getSignInStatus()
    fun isInSandbox() = requestExtractor.isInSandbox()
    fun getSurfaceCapabilities() = requestExtractor.getSurfaceCapabilities()
    fun getInputType() = requestExtractor.getInputType()
    fun isPermissionGranted() = requestExtractor.isPermissionGranted()
    fun getRepromptCount() = requestExtractor.getRepromptCount()
    fun isFinalReprompt() = requestExtractor.isFinalReprompt()
    fun isUpdateRegistered() = requestExtractor.isUpdateRegistered()
}

/**
 * reference to Functions that will be called for logging and errors.
 */
val defaultLogFunction: ((String) -> Unit)? = {
    logger.info(it)
}
var debugFunction: ((String) -> Unit)? = null
var errorFunction: ((String) -> Unit)? = defaultLogFunction

fun debug(msg: String) {
    debugFunction?.invoke(msg)
}

fun error(msg: String) {
    errorFunction?.invoke(msg)
}

