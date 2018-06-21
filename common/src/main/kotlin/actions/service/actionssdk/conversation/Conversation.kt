package actions.service.actionssdk.conversation

import actions.AppOptions
import actions.ServiceBaseApp
import actions.BaseApp
import actions.expected.OAuth2Client
import actions.framework.Headers
import actions.framework.JsonObject
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.actionssdk.api.GoogleActionsV2ConversationType
import actions.service.actionssdk.api.GoogleActionsV2ExpectedIntent
import actions.service.actionssdk.api.GoogleActionsV2RichResponse
import actions.service.actionssdk.conversation.argument.Arguments
import actions.service.actionssdk.conversation.response.*
import actions.service.actionssdk.push
import actions.service.actionssdk.ActionsSdkIntentHandler3
import actions.service.actionssdk.ActionsSdkIntentHandler4


//TODO test enum vs sealed class
enum class IntentEnum(val value: String) {
    MAIN("actions.intent.MAIN"),
    TEXT("actions.intent.TEXT"),
    PERMISSION("actions.intent.PERMISSION"),
    OPTION("actions.intent.OPTION"),
    TRANSACTION_REQUIREMENTS_CHECK("actions.intent.TRANSACTION_REQUIREMENTS_CHECK"),
    DELIVERY_ADDRESS("actions.intent.DELIVERY_ADDRESS"),
    TRANSACTION_DECISION("actions.intent.TRANSACTION_DECISION"),
    CONFIRMATION("actions.intent.CONFIRMATION"),
    DATETIME("actions.intent.DATETIME"),
    SIGN_IN("actions.intent.SIGN_IN"),
    NO_INPUT("actions.intent.NO_INPUT"),
    CANCEL("actions.intent.CANCEL"),
    NEW_SURFACE("actions.intent.NEW_SURFACE"),
    REGISTER_UPDATE("actions.intent.REGISTER_UPDATE"),
    CONFIGURE_UPDATES("actions.intent.CONFIGURE_UPDATES"),
    PLACE("actions.intent.PLACE"),
    LINK("actions.intent.LINK"),
    MEDIA_STATUS("actions.intent.MEDIA_STATUS")
}

enum class InputValueSpec(val value: String) {
    PermissionValueSpec("type.googleapis.com/google.actions.v2.PermissionValueSpec"),
    OptionValueSpec("type.googleapis.com/google.actions.v2.OptionValueSpec"),
    TransactionRequirementsCheckSpec("type.googleapis.com/google.actions.v2.TransactionRequirementsCheckSpec"),
    DeliveryAddressValueSpec("type.googleapis.com/google.actions.v2.DeliveryAddressValueSpec"),
    TransactionDecisionValueSpec("type.googleapis.com/google.actions.v2.TransactionDecisionValueSpec"),
    ConfirmationValueSpec("type.googleapis.com/google.actions.v2.ConfirmationValueSpec"),
    DateTimeValueSpec("type.googleapis.com/google.actions.v2.DateTimeValueSpec"),
    NewSurfaceValueSpec("type.googleapis.com/google.actions.v2.NewSurfaceValueSpec"),
    RegisterUpdateValueSpec("type.googleapis.com/google.actions.v2.RegisterUpdateValueSpec"),
    SignInValueSpec("type.googleapis.com/google.actions.v2.SignInValueSpec"),
    PlaceValueSpec("type.googleapis.com/google.actions.v2.PlaceValueSpec"),
    LinkValueSpec("type.googleapis.com/google.actions.v2.LinkValueSpec")
}

enum class DialogSpec(val value: String) {
    PlaceDialogSpec("type.googleapis.com/google.actions.v2.PlaceValueSpec.PlaceDialogSpec"),
    LinkDialogSpec("type.googleapis.com/google.actions.v2.LinkValueSpec.LinkDialogSpec")
}

data class ConversationResponse(
        var richResponse: GoogleActionsV2RichResponse? = null,
        var expectUserResponse: Boolean? = null,
        var userStorage: String? = null,
        var expectedIntent: GoogleActionsV2ExpectedIntent? = null)

interface ConversationOptionsInit<TConvData, TUserStorage> {
    var data: TConvData?

    var storage: TUserStorage?
}

interface ConversationBaseOptions<TConvData, TUserStorage> {
    var headers: Headers?

    var init: ConversationOptionsInit<TConvData, TUserStorage>?

    var debug: Boolean?
}

data class ConversationOptions<TUserStorage>(
        var request: GoogleActionsV2AppRequest? = null,
        var headers: Headers?,
        var init: ConversationOptionsInit<*, TUserStorage>? = null)

abstract class Conversation<TUserStorage> {
    var request: GoogleActionsV2AppRequest?

    var headers: Headers?

    var responses: MutableList<Response> = mutableListOf()
    var strResponses: MutableList<String> = mutableListOf()

    var expectUserResponse = true

    var surface: Surface

    var available: Available

    var digested = false

    /**
     * True if the app is being tested in sandbox mode. Enable sandbox
     * mode in the [Actions console](console.actions.google.com) to test
     * transactions.
     * @public
     */
    var sandbox: Boolean

    var input: Input

    /**
     * Gets the {@link User} object.
     * The user object contains information about the user, including
     * a string identifier and personal information (requires requesting permissions,
     * see {@link Permission|conv.ask(new Permission)}).
     * @public
     */
    var user: User<TUserStorage>

    var arguments: Arguments

    var device: Device

    /**
     * Gets the unique conversation ID. It's a new ID for the initial query,
     * and stays the same until the end of the conversation.
     *
     * @example
     * ```javascript
     *
     * app.intent('actions.intent.MAIN', conv => {
     *   const conversationId = conv.id
     * })
     * ```
     *
     * @public
     */
    var id: String

    var type: GoogleActionsV2ConversationType

    /**
     * Shortcut for
     * {@link Capabilities|conv.surface.capabilities.has('actions.capability.SCREEN_OUTPUT')}
     * @public
     */
    var screen: Boolean

    var _raw: JsonObject? = null

    var _responded = false

    constructor(options: ConversationOptions<TUserStorage>) {
        val request = options.request
        val headers = options.headers
        val init = options.init

        this.request = request
        this.headers = headers

        this.sandbox = this.request?.isInSandbox ?: false

        val inputs = this.request?.inputs
        val conversation = this.request?.conversation
        val input = inputs?.firstOrNull()
        val rawInputs = input?.rawInputs

        this.input = Input(rawInputs?.get(0))
        this.surface = Surface(this.request?.surface)
        this.available = Available(this.request?.availableSurfaces)

        this.user = User(this.request?.user/*, init && init.storage*/)

        this.arguments = Arguments(input?.arguments)

        this.device = Device(this.request?.device)

        this.id = conversation?.conversationId ?: ""

        this.type = conversation?.type ?: GoogleActionsV2ConversationType.TYPE_UNSPECIFIED

        this.screen = this.surface.capabilities?.has(SurfaceCapability.ACTIONS_CAPABILITY_SCREEN_OUTPUT) == true
    }

    fun json(json: JsonObject): Conversation<TUserStorage> {
        this._raw = json
        this._responded = true
        return this
    }

    fun add(vararg responses: Response): Conversation<TUserStorage> {
        if (this.digested) {
            throw Error("Response has already been sent. " +
                    "Is this being used in an async call that was not " +
                    "returned as a promise to the intent handler?")
        }
        this.responses.addAll(responses)
        this._responded = true
        return this
    }

    fun add(vararg responses: String): Conversation<TUserStorage> {
        responses.forEach {
            this.add(SimpleResponse(it))
        }
//        if (this.digested) {
//            throw Error("Response has already been sent. " +
//                    "Is this being used in an async call that was not " +
//                    "returned as a promise to the intent handler?")
//        }
//        this.strResponses.addAll(responses)
//        this._responded = true
        return this
    }

    /**
     * Asks to collect user's input. All user's queries need to be sent to the app.
     * {@link https://developers.google.com/actions/policies/general-policies#user_experience|
     *     The guidelines when prompting the user for a response must be followed at all times}.
     *
     * @example
     * ```javascript
     *
     * // Actions SDK
     * const app = actionssdk()
     *
     * app.intent('actions.intent.MAIN', conv => {
     *   const ssml = '<speak>Hi! <break time="1"/> ' +
     *     'I can read out an ordinal like <say-as interpret-as="ordinal">123</say-as>. ' +
     *     'Say a number.</speak>'
     *   conv.ask(ssml)
     * })
     *
     * app.intent('actions.intent.TEXT', (conv, input) => {
     *   if (input === 'bye') {
     *     return conv.close('Goodbye!')
     *   }
     *   const ssml = `<speak>You said, <say-as interpret-as="ordinal">${input}</say-as></speak>`
     *   conv.ask(ssml)
     * })
     *
     * // Dialogflow
     * const app = dialogflow()
     *
     * app.intent('Default Welcome Intent', conv => {
     *   conv.ask('Welcome to action snippets! Say a number.')
     * })
     *
     * app.intent('Number Input', (conv, {num}) => {
     *   conv.close(`You said ${num}`)
     * })
     * ```
     *
     * @param responses A response fragment for the library to construct a single complete response
     * @public
     */
    fun ask(vararg responses: Response): Conversation<TUserStorage> {
        this.expectUserResponse = true
        return this.add(*responses)
    }

    fun ask(vararg responses: String): Conversation<TUserStorage> {
        this.expectUserResponse = true
        return this.add(*responses)
    }

    /**
     * Have Assistant render the speech response and close the mic.
     *
     * @example
     * ```javascript
     *
     * // Actions SDK
     * const app = actionssdk()
     *
     * app.intent('actions.intent.MAIN', conv => {
     *   const ssml = '<speak>Hi! <break time="1"/> ' +
     *     'I can read out an ordinal like <say-as interpret-as="ordinal">123</say-as>. ' +
     *     'Say a number.</speak>'
     *   conv.ask(ssml)
     * })
     *
     * app.intent('actions.intent.TEXT', (conv, input) => {
     *   if (input === 'bye') {
     *     return conv.close('Goodbye!')
     *   }
     *   const ssml = `<speak>You said, <say-as interpret-as="ordinal">${input}</say-as></speak>`
     *   conv.ask(ssml)
     * })
     *
     * // Dialogflow
     * const app = dialogflow()
     *
     * app.intent('Default Welcome Intent', conv => {
     *   conv.ask('Welcome to action snippets! Say a number.')
     * })
     *
     * app.intent('Number Input', (conv, {num}) => {
     *   conv.close(`You said ${num}`)
     * })
     * ```
     *
     * @param responses A response fragment for the library to construct a single complete response
     * @public
     */
    fun close(vararg responses: Response): Conversation<TUserStorage> {
        this.expectUserResponse = false
        return this.add(*responses)
    }

    fun close(vararg responses: String): Conversation<TUserStorage> {
        this.expectUserResponse = false
        return this.add(*responses)
    }

    /** @public */
    fun response(): ConversationResponse {
        if (!this._responded) {
            throw Error("No response has been set. " +
                    "Is this being used in an async call that was not " +
                    "returned as a promise to the intent handler?")
        }
        if (this.digested) {
            throw Error("Response has already been digested")
        }
        this.digested = true
        val expectUserResponse = this.expectUserResponse
        var richResponse = RichResponse()
        var expectedIntent: GoogleActionsV2ExpectedIntent? = null

//        this.strResponses.forEach {
//            richResponse.add(it)
//        }

        for (response in this.responses) {
            when (response) {

                is Question -> {
                    expectedIntent = response
                    if (response is SoloQuestion) {
                        // SoloQuestions don't require a SimpleResponse
                        // but API still requires a SimpleResponse
                        // so a placeholder is added to not error

                        // It won't show up to the user as PLACEHOLDER
                        richResponse.add("PLACEHOLDER")
                    }
                }

                is RichResponse -> richResponse = response

                is Suggestions -> {
                    if (richResponse.suggestions == null || richResponse.suggestions?.isEmpty() == true) {
                        richResponse.suggestions = mutableListOf()
                    }
                    richResponse.suggestions!!.push(*response.suggestions.toTypedArray())
                }

                is Image -> richResponse.add(BasicCard(image = response))

                is MediaObject -> richResponse.add(MediaResponse(/*response*/))

                is RichResponseItem -> richResponse.add(response)
            }
        }
        val userStorage = this.user._serialize()
        return ConversationResponse(expectUserResponse = expectUserResponse,
                richResponse = richResponse,
                userStorage = userStorage,
                expectedIntent = expectedIntent)
    }
}

typealias ExceptionHandler<TUserStorage, TConversation> = (TConversation, Exception) -> Any

//    interface ExceptionHandler<TUserStorage, TConversation: Conversation<TUserStorage>> {
//        /** @public */
//         tslint:disable-next-line:no-any allow to return any just detect if is promise
//
//        (conv: TConversation, error: Error): Promise<any> | any
//    }

/** @hidden */
class Traversed<TConvData, TUserStorage> : MutableMap<ActionsSdkIntentHandler4<TConvData, TUserStorage>, Boolean> by mutableMapOf()


/** @hidden */
interface ConversationAppOptions<TConvData, TUserStorage> : AppOptions {
    var init: (() -> ConversationOptionsInit<TConvData, TUserStorage>)?

    /**
     * Client ID for User Profile Payload Verification
     * See {@link Profile#payload|conv.user.profile.payload}
     * @public
     */
    var clientId: String?
}

data class OAuth2ConfigClient(var id: String? = null)

data class OAuth2Config(var client: OAuth2ConfigClient? = null)

abstract class ConversationApp<TConvData, TUserStorage> : BaseApp<TUserStorage>() {
    /** @public */
    abstract var init: (() -> ConversationOptionsInit<TConvData, TUserStorage>)?

    /** @public */
    abstract var auth: OAuth2Config?

    abstract var _client: OAuth2Client?
}

