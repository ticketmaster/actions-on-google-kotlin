package actions.service.actionssdk

import actions.ServiceBaseApp
import actions.attach
import actions.expected.ConversationTokenSerializer.Companion.stringify
import actions.expected.IdToken
import actions.expected.OAuth2Client
import actions.expected.log
import actions.framework.Headers
import actions.framework.JsonObject
import actions.framework.StandardHandler
import actions.framework.StandardResponse
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.actionssdk.api.GoogleRpcStatus
import actions.service.actionssdk.conversation.*
import actions.service.actionssdk.conversation.argument.Argument

typealias ActionsSdkIntentHandler<TConvData, TUserStorage> = (conv: ActionsSdkConversation<TConvData, TUserStorage>, argument: Any, status: GoogleRpcStatus?) -> Any

fun <TConvData, TUserStorage, TConversation : ActionsSdkConversation<TConvData, TUserStorage>, TArgument : Argument> actionsSdkIntentHandler(
        conv: TConversation,
        /**
         * The user's raw input query.
         * See {@link Input#raw|Input.raw}
         * Same as `conv.input.raw`
         */
        input: String,
        /**
         * The first argument value from the current intent.
         * See {@link Arguments#get|Arguments.get}
         * Same as `conv.arguments.parsed.list[0]`
         */
        argument: TArgument,
        /**
         * The first argument status from the current intent.
         * See {@link Arguments#status|Arguments.status}
         * Same as `conv.arguments.status.list[0]`
         */
        status: GoogleRpcStatus?
        // tslint:disable-next-line:no-any allow developer to return any just detect if is promise
): Any {
    return Any()
}
//
///** @public */
//interface ActionsSdkIntentHandler<
//        TConvData,
//        TUserStorage,
//        TConversation : ActionsSdkConversation<TConvData, TUserStorage>,
//        TArgument : Argument> {
//    var conv: TConversation
//    /**
//     * The user's raw input query.
//     * See {@link Input#raw|Input.raw}
//     * Same as `conv.input.raw`
//     */
//    var input: String
//    /**
//     * The first argument value from the current intent.
//     * See {@link Arguments#get|Arguments.get}
//     * Same as `conv.arguments.parsed.list[0]`
//     */
//    var argument: TArgument
//    /**
//     * The first argument status from the current intent.
//     * See {@link Arguments#status|Arguments.status}
//     * Same as `conv.arguments.status.list[0]`
//     */
//    var status: GoogleRpcStatus?
//    // tslint:disable-next-line:no-any allow developer to return any just detect if is promise
//    ): Promise<any> | any
//}

class ActionSdkIntentHandlers<TConvData, TUserStorage> : MutableMap<String, ActionsSdkIntentHandler<TConvData, TUserStorage>?> by mutableMapOf() {
//    [intent: string]: ActionsSdkIntentHandler<
//    {},
//    {},
//    ActionsSdkConversation<
//    {},
//    {}>,
//    Argument
//    > | string | undefined
}

data class ActionsSdkHandlers<TConvData, TUserStorage>(
        var intents: ActionSdkIntentHandlers<TConvData, TUserStorage> = ActionSdkIntentHandlers(),
        var catcher: ExceptionHandler<TUserStorage, ActionsSdkConversation<TConvData, TUserStorage>>? = null, //TODO provide defaults for these nulls
        var fallback: ActionsSdkIntentHandler<TConvData, TUserStorage>? = null //| string
)

interface ActionsSdkMiddleware
//<
//        TConversationPlugin: ActionsSdkConversation<*, *>> {
//    (conv: ActionsSdkConversation<*, *>): (ActionsSdkConversation<*,*> & TConversationPlugin) | void
//}

/** @public */
interface ActionsSdkApp<TConvData, TUserStorage> : ConversationApp<TConvData, TUserStorage> {
    /** @hidden */
    var _handlers: ActionsSdkHandlers<TConvData, TUserStorage>

    /**
     * Sets the IntentHandler to be executed when the fulfillment is called
     * with a given Actions SDK intent.
     *
     * @param intent The Actions SDK intent to match.
     *     When given an array, sets the IntentHandler for any intent in the array.
     * @param handler The IntentHandler to be executed when the intent is matched.
     *     When given a string instead of a function, the intent fulfillment will be redirected
     *     to the IntentHandler of the redirected intent.
     * @public
     */
    fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler<TConvData, TUserStorage> /*| Intent,*/): ActionsSdkApp<TConvData, TUserStorage>

    /**
     * Sets the IntentHandler to be executed when the fulfillment is called
     * with a given Actions SDK intent.
     *
     * @param intent The Actions SDK intent to match.
     *     When given an array, sets the IntentHandler for any intent in the array.
     * @param handler The IntentHandler to be executed when the intent is matched.
     *     When given a string instead of a function, the intent fulfillment will be redirected
     *     to the IntentHandler of the redirected intent.
     * @public
     */
    fun intent(vararg intents: String, handler: ActionsSdkIntentHandler<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>

    /** @public */
    fun catch(catcher: ExceptionHandler<TUserStorage, ActionsSdkConversation<TConvData, TUserStorage>>): ActionsSdkApp<TConvData, TUserStorage>

    /** @public */
    fun fallback(handler: ActionsSdkIntentHandler<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>

    /** @hidden */
    var _middlewares: MutableList<ActionsSdkMiddleware>

    /** @public */
    fun <TConversationPlugin : ActionsSdkConversation<*, *>> middleware(middleware: ActionsSdkMiddleware): ActionsSdkApp<TConvData, TUserStorage>

    /** @public */
    var verification: ActionsSdkVerification? //| string
}

//interface ActionsSdk {
//    <TConvData,TUserStorage,Conversation: ActionsSdkConversation<TConvData, TUserStorage> =
//    ActionsSdkConversation<TConvData, TUserStorage>>
//    (options: ActionsSdkOptions<TConvData, TUserStorage>?): AppHandler & ActionsSdkApp<
//    TConvData, TUserStorage, Conversation>
//    <Conversation: ActionsSdkConversation<*,*> = ActionsSdkConversation<*,*>>(
//    options: ActionsSdkOptions<*,*>): AppHandler & ActionsSdkApp<*,*, Conversation>
//}

interface ActionsSdkVerification {
    /**
     * Google Cloud Project ID for the Assistant app.
     * @public
     */
    var project: String

    /**
     * Custom status code to return on verification error.
     * @public
     */
    var status: Int?

    /**
     * Custom error message as a string or a function that returns a string
     * given the original error message set by the library.
     *
     * The message will get sent back in the JSON top level `error` property.
     * @public
     */
    var error: String //| ((error: string) => string)
}

interface ActionsSdkOptions<TConvData, TUserStorage> : ConversationAppOptions<TConvData, TUserStorage> {
    /**
     * Validates whether request is from Google through signature verification.
     * Uses Google-Auth-Library to verify authorization token against given Google Cloud Project ID.
     * Auth token is given in request header with key, "authorization".
     *
     * HTTP Code 403 will be thrown by default on verification error.
     *
     * @example
     * ```javascript
     *
     * const app = actionssdk({ verification: 'nodejs-cloud-test-project-1234' })
     * ```
     *
     * @public
     */
    var verification: ActionsSdkVerification? //| string
}

/**
 * This is the function that creates the app instance which on new requests,
 * creates a way to interact with the conversation API directly from Assistant,
 * providing implementation for all the methods available in the API.
 *
 * Only supports Actions SDK v2.
 *
 * @example
 * ```javascript
 *
 * const app = actionssdk()
 *
 * app.intent('actions.intent.MAIN', conv => {
 *   conv.ask('How are you?')
 * })
 * ```
 *
 * @public
 */

fun <TConvData, TUserStorage> actionssdk(options: ActionsSdkOptions<TConvData, TUserStorage>? = null): ActionsSdk<TConvData, TUserStorage> {
//    attach()
    return ActionsSdk(options)
}

class ActionsSdk<TConvData, TUserStorage>(options: ActionsSdkOptions<TConvData, TUserStorage>? = null) : ActionsSdkApp<TConvData, TUserStorage> {
    override var _handlers: ActionsSdkHandlers<TConvData, TUserStorage> = ActionsSdkHandlers()

    override fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent.value] = handler
        }
        return this
    }

    override fun intent(vararg intents: String, handler: ActionsSdkIntentHandler<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent] = handler
        }
        return this
    }

    override fun catch(catcher: ExceptionHandler<TUserStorage, ActionsSdkConversation<TConvData, TUserStorage>>): ActionsSdkApp<TConvData, TUserStorage> {
        this._handlers.catcher = catcher
        return this
    }

    override fun fallback(handler: ActionsSdkIntentHandler<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        this._handlers.fallback = handler
        return this
    }

    override var _middlewares: MutableList<ActionsSdkMiddleware>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override fun <TConversationPlugin : ActionsSdkConversation<*, *>> middleware(middleware: ActionsSdkMiddleware): ActionsSdkApp<TConvData, TUserStorage> {
        this._middlewares.push(middleware)
        return this
    }

    override var verification: ActionsSdkVerification? = options?.verification

    override var init: (() -> ConversationOptionsInit<TConvData, TUserStorage>)? = options?.init

    override var auth: OAuth2Config? = if (options?.clientId != null) OAuth2Config(client = OAuth2ConfigClient(id = options.clientId)) else null

    override var _client: OAuth2Client? = if (options?.verification != null || options?.clientId != null)
        OAuth2Client(options.clientId!!) else null

    override var handler: StandardHandler = object : StandardHandler {
        override fun handle(body: JsonObject, headers: Headers): StandardResponse {
            val debug: Boolean = options?.debug ?: false
            val init = init
            val verification = verification
            if (verification != null) {
                val project = verification.status
                val status = 403
                val error = verification.error
                //} = typeof verification === 'string' ? { project: verification } : verification
                val token = headers["authorization"]?.firstOrNull()
                try {
                    _client!!.verifyIdToken(IdToken(
                            idToken = token,
                            audience = project.toString())
                    )
                } catch (e: Exception) {
                    return StandardResponse(
                            status = status,
                            body = WebhookError(
                                    error = error) //typeof error === 'string' ? error :
                            //error(`ID token verification failed: ${e.stack || e.message || e}`),
                    )
                }
            }

            val conv = ActionsSdkConversation(ActionsSdkConversationOptions(
                    body = body as GoogleActionsV2AppRequest,
                    headers = headers,
                    init = init?.invoke(), //init && init (),
                    debug = debug))

            if (conv.user.profile?.token != null) {
                /*await */ conv.user._verifyProfile(_client!!, auth!!.client?.id!!)
            }
            for (middleware in _middlewares) {
//                conv = (middleware(conv) as ActionsSdkConversation<TConvData, TUserStorage>) //| void) || conv
            }
//            val log = debug ? common.info : common.debug
//                    log("Conversation", stringify(conv, {
//                        request: null,
//                        headers: null,
//                        body: null,
//                    }))
            val intent = conv.intent
            val traversed: Traversed<TConvData, TUserStorage> = Traversed()
            var handler = _handlers.intents[intent]
//            while (typeof handler !== 'function') {
            while (true) {
                if (handler == null) {
                    if (_handlers.fallback != null) {
                        throw Error("Actions SDK IntentHandler not found for intent: $intent")
                    }
                    handler = _handlers.fallback
                    break
                }
                if (traversed[handler] == true) {
                    throw Error("Circular intent map detected: $handler traversed twice")
                }
                traversed[handler] = true
                handler = _handlers.intents[intent]
            }
            try {
                /* await */ handler?.invoke(
                        conv,
                        conv.input.raw!!,
                        conv.arguments.status?.list?.lastOrNull()
//                        conv.arguments.parsed.list[0]
                )
            } catch (e: Exception) {
                //TODO provide default catcher
                /*await */ _handlers.catcher?.invoke(conv, e)
            }
            return StandardResponse(
                    status = 200,
                    headers = mutableMapOf(),
                    body = conv.serialize()
            )
        }
    }

}

data class WebhookError(var error: String? = null)

