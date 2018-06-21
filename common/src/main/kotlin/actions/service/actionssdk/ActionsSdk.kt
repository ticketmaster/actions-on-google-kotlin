package actions.service.actionssdk

import actions.*
import actions.expected.*
import actions.framework.*
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.actionssdk.api.GoogleActionsV2Argument
import actions.service.actionssdk.api.GoogleRpcStatus
import actions.service.actionssdk.conversation.*

typealias ActionsSdkIntentHandler1<TConvData, TUserStorage> = (conv: ActionsSdkConversation<TConvData, TUserStorage>) -> Any
typealias ActionsSdkIntentHandler2<TConvData, TUserStorage> = (conv: ActionsSdkConversation<TConvData, TUserStorage>, argument: Any) -> Any
typealias ActionsSdkIntentHandler3<TConvData, TUserStorage> = (conv: ActionsSdkConversation<TConvData, TUserStorage>, argument: Any, arg: GoogleActionsV2Argument?) -> Any
typealias ActionsSdkIntentHandler4<TConvData, TUserStorage> = (conv: ActionsSdkConversation<TConvData, TUserStorage>, argument: Any, arg: GoogleActionsV2Argument?, status: GoogleRpcStatus?) -> Any

interface ActionsSdkIntentHandlerTest<TConvData, TUserStorage> {
    operator fun invoke(conv: ActionsSdkConversation<TConvData, TUserStorage>, argument: Any? = null, status: GoogleRpcStatus? = null)
}

fun <TConvData, TUserStorage, TConversation : ActionsSdkConversation<TConvData, TUserStorage>, TArgument : GoogleActionsV2Argument> actionsSdkIntentHandler(
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

class ActionSdkIntentHandlers<TConvData, TUserStorage> : MutableMap<String, ActionsSdkIntentHandler4<TConvData, TUserStorage>?> by mutableMapOf() {
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
        var fallback: ActionsSdkIntentHandler4<TConvData, TUserStorage>? = null //| string
)

interface ActionsSdkMiddleware
//<
//        TConversationPlugin: ActionsSdkConversation<*, *>> {
//    (conv: ActionsSdkConversation<*, *>): (ActionsSdkConversation<*,*> & TConversationPlugin) | void
//}

/** @public */
abstract class ActionsSdkApp<TConvData, TUserStorage> : ConversationApp<TConvData, TUserStorage>() {
    /** @hidden */
    abstract var _handlers: ActionsSdkHandlers<TConvData, TUserStorage>

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
    abstract fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler1<TConvData, TUserStorage> /*| Intent,*/): ActionsSdkApp<TConvData, TUserStorage>

    abstract fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler2<TConvData, TUserStorage> /*| Intent,*/): ActionsSdkApp<TConvData, TUserStorage>
    abstract fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler3<TConvData, TUserStorage> /*| Intent,*/): ActionsSdkApp<TConvData, TUserStorage>
    abstract fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler4<TConvData, TUserStorage> /*| Intent,*/): ActionsSdkApp<TConvData, TUserStorage>

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
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler1<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>

    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler2<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler3<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler4<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>

    /** @public */
    abstract fun catch(catcher: ExceptionHandler<TUserStorage, ActionsSdkConversation<TConvData, TUserStorage>>): ActionsSdkApp<TConvData, TUserStorage>

    /** @public */
    abstract fun fallback(handler: ActionsSdkIntentHandler3<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>

    /** @hidden */
    abstract var _middlewares: MutableList<ActionsSdkMiddleware>

    /** @public */
    abstract fun <TConversationPlugin : ActionsSdkConversation<*, *>> middleware(middleware: ActionsSdkMiddleware): ActionsSdkApp<TConvData, TUserStorage>

    /** @public */
    abstract var verification: ActionsSdkVerification? //| string
}

abstract class BaseService<THandler, TIntentHandler, TConversation, TMiddleware, TVerification, TConvData, TUserStorage> : ConversationApp<TConvData, TUserStorage>() {
    /** @hidden */
    abstract var _handlers: THandler//<TConvData, TUserStorage>

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
    abstract fun intent(intents: MutableList<IntentEnum>, handler: TIntentHandler/*<TConvData, TUserStorage>*/ /*| Intent,*/): ActionsSdkApp<TConvData, TUserStorage>

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
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler1<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>

    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler2<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler3<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler4<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>

    /** @public */
    abstract fun catch(catcher: ExceptionHandler<TUserStorage, ActionsSdkConversation<TConvData, TUserStorage>>): ActionsSdkApp<TConvData, TUserStorage>

    /** @public */
    abstract fun fallback(handler: ActionsSdkIntentHandler3<TConvData, TUserStorage> /*| string,*/): ActionsSdkApp<TConvData, TUserStorage>

    /** @hidden */
    abstract var _middlewares: MutableList<ActionsSdkMiddleware>

    /** @public */
    abstract fun <TConversationPlugin : ActionsSdkConversation<*, *>> middleware(middleware: ActionsSdkMiddleware): ActionsSdkApp<TConvData, TUserStorage>

    /** @public */
    abstract var verification: ActionsSdkVerification? //| string
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

data class ActionsSdkOptions<TConvData, TUserStorage>(
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
        var verification: ActionsSdkVerification? = null,
        override var init: (() -> ConversationOptionsInit<TConvData, TUserStorage>)? = null,
        override var clientId: String? = null,
        override var debug: Boolean? = null
) : ConversationAppOptions<TConvData, TUserStorage>

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
//    return attach<ActionsSdk<TConvData, TUserStorage>, TConvData, TUserStorage>(ActionsSdk(options), options)
    return ActionsSdk(options)
}

fun <TConvData, TUserStorage> actionssdk(init: (ActionsSdkOptions<TConvData, TUserStorage>.() -> Unit)? = null): ActionsSdk<TConvData, TUserStorage> {
    val options = ActionsSdkOptions<TConvData, TUserStorage>()
    options.init?.invoke()
    return ActionsSdk(options)
}

class ActionsSdk<TConvData, TUserStorage>(options: ActionsSdkOptions<TConvData, TUserStorage>? = null) : ActionsSdkApp<TConvData, TUserStorage>() {



    override lateinit var frameworks: BuiltinFrameworks<TUserStorage>

    override fun <TService, TPlugin> use(plugin: Plugin<TService, TPlugin>): BaseAppPlugin<TPlugin, TUserStorage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var debug: Boolean = false


    override var _handlers: ActionsSdkHandlers<TConvData, TUserStorage> = ActionsSdkHandlers<TConvData, TUserStorage>(
            intents = ActionSdkIntentHandlers(),
            catcher = { conv, e -> throw e }
    )

    override fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler1<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent.value] = { conv, status, g, arg -> handler(conv) }
        }
        return this
    }

    override fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler2<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent.value] = { conv, status, g, arg -> handler(conv, status) }
        }
        return this
    }

    override fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler3<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent.value] = { conv, status, g, arg -> handler(conv, status, g) }
        }
        return this
    }

override fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler4<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun intent(vararg intents: String, handler: ActionsSdkIntentHandler1<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent] = { conv, status, g, arg-> handler(conv) }
        }
        return this
    }

    override fun intent(vararg intents: String, handler: ActionsSdkIntentHandler2<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv, status) }
        }
        return this
    }

    override fun intent(vararg intents: String, handler: ActionsSdkIntentHandler3<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv, status, g) }
        }
        return this
    }

    override fun intent(vararg intents: String, handler: ActionsSdkIntentHandler4<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent] = handler
        }
        return this
    }

    override fun catch(catcher: ExceptionHandler<TUserStorage, ActionsSdkConversation<TConvData, TUserStorage>>): ActionsSdkApp<TConvData, TUserStorage> {
        this._handlers.catcher = catcher
        return this
    }

    override fun fallback(handler: ActionsSdkIntentHandler3<TConvData, TUserStorage>): ActionsSdkApp<TConvData, TUserStorage> {
        this._handlers.fallback = {conv, status, g, arg -> handler.invoke(conv, status, g) }
        return this
    }

    override var _middlewares: MutableList<ActionsSdkMiddleware> = mutableListOf()

    override fun <TConversationPlugin : ActionsSdkConversation<*, *>> middleware(middleware: ActionsSdkMiddleware): ActionsSdkApp<TConvData, TUserStorage> {
        this._middlewares.push(middleware)
        return this
    }

    override var verification: ActionsSdkVerification? = options?.verification

    override var init: (() -> ConversationOptionsInit<TConvData, TUserStorage>)? = options?.init

    override var auth: OAuth2Config? = if (options?.clientId != null) OAuth2Config(client = OAuth2ConfigClient(id = options.clientId)) else null

    override var _client: OAuth2Client? = if (options?.verification != null || options?.clientId != null)
        OAuth2Client(options.clientId!!) else null

    override var handler: StandardHandler<TUserStorage> = object : StandardHandler<TUserStorage> {
        override fun handle(body: Any, headers: Headers): StandardResponse {
            val convBody = body as GoogleActionsV2AppRequest
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
                    body = convBody,
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
            while (false) {
                //TODO why is this loop here? handle intents mapped to a string?
                if (handler == null) {
                    if (_handlers.fallback == null) {
                        throw Error("Actions SDK IntentHandler not found for intent: $intent")
                    }
                    handler = _handlers.fallback
                    break
                }
                if (traversed[handler] == true) {
                    throw Error("Circular intent map detected: $handler traversed twice")
                }
                traversed[handler] = true
//                handler = _handlers.intents[handler]
            }
            try {
                /* await */ handler?.invoke(
                        conv,
                        conv.input.raw!!,
                        conv.arguments.parsed?.list?.firstOrNull(),
                        conv.arguments.status?.list?.firstOrNull()
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

    init {
        frameworks = BuiltinFrameworks()
//        val baseApp = create(options)
        omni = object : OmniHandler {
            override fun handle(vararg args: Any): Any {
                log("Args in omniHandler: ${args.map { it.toString() }.joinToString { it }}")
                for (framework in frameworks) {
                    if (framework.check(*args)) {
                        return framework.handle(handler).handle(*args)
                    }
                }
                return handler.handle(args[0] as GoogleActionsV2AppRequest, args[1] as Headers)
            }
        }

//        var handler = baseApp.handler
        val standard = object : StandardHandler<TUserStorage> {
            override fun handle(body: Any, headers: Headers): StandardResponse {
                val body = body as GoogleActionsV2AppRequest
                log("Request", Serializer.serialize(body))
                log("Headers", Serializer.serialize(headers))
                val response = /* await */ handler.handle(body, headers)
                response.headers?.get("content-type")?.add("application/json; charset=utf-8")
                log("Response", Serializer.serialize(response))
                return response
            }
        }
//        baseApp.omni = omni
//        baseApp.handler = standard

//    var appResult = object: OmniHandler by omni, actions.BaseApp by baseApp, actions.framework.StandardHandler by standardHandler, actions.ServiceBaseApp by service {
//
//    }

//    var attachedResult = AttachResult(
//            baseApp = baseApp,
//            service = service,
//            omni = omni,
//            handler = standardHandler)

//        return object: AppResult() {
//        handler = standard

        fun <TService, TPlugin, TUserStorage> use(plugin: Plugin<TService, TPlugin>): BaseAppPlugin<TPlugin, TUserStorage> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

//            override fun handle(body: JsonObject, headers: Headers): StandardResponse = standard.handle(body, headers)
//
//            override var frameworks: BuiltinFrameworks = baseApp.frameworks
//
//            override var debug: Boolean = baseApp.debug
//        }
    }

}

data class WebhookError(var error: String? = null)

