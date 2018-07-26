package actions.service.actionssdk

import actions.*
import actions.expected.*
import actions.framework.*
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.actionssdk.api.GoogleActionsV2Argument
import actions.service.actionssdk.api.GoogleRpcStatus
import actions.service.actionssdk.conversation.*
import actions.service.dialogflow.DialogflowIntentHandler4

typealias ActionsSdkIntentHandler1<TUserStorage> = (conv: ActionsSdkConversation<TUserStorage>) -> Any
typealias ActionsSdkIntentHandler2<TUserStorage> = (conv: ActionsSdkConversation<TUserStorage>, argument: Any) -> Any
typealias ActionsSdkIntentHandler3<TUserStorage> = (conv: ActionsSdkConversation<TUserStorage>, argument: Any, arg: GoogleActionsV2Argument?) -> Any
typealias ActionsSdkIntentHandler4<TUserStorage> = (conv: ActionsSdkConversation<TUserStorage>, argument: Any, arg: GoogleActionsV2Argument?, status: GoogleRpcStatus?) -> Any

class ActionSdkIntentHandlers<TUserStorage> : MutableMap<String, ActionsSdkIntentHandler4<TUserStorage>?> by mutableMapOf()

data class ActionsSdkHandlers<TUserStorage>(
        var intents: ActionSdkIntentHandlers<TUserStorage> = ActionSdkIntentHandlers(),
        var catcher: ExceptionHandler<TUserStorage, ActionsSdkConversation<TUserStorage>>? = null, //TODO provide defaults for these nulls
        var fallback: ActionsSdkIntentHandler4<TUserStorage>? = null
)

interface ActionsSdkMiddleware
//<
//        TConversationPlugin: ActionsSdkConversation<*, *>> {
//    (conv: ActionsSdkConversation<*, *>): (ActionsSdkConversation<*,*> & TConversationPlugin) | void
//}

/** @public */
abstract class ActionsSdkApp<TUserStorage> : ConversationApp<TUserStorage>() {
    /** @hidden */
    abstract var _handlers: ActionsSdkHandlers<TUserStorage>

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
    abstract fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler1<TUserStorage> /*| Intent,*/): ActionsSdkApp<TUserStorage>
    abstract fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler2<TUserStorage> /*| Intent,*/): ActionsSdkApp<TUserStorage>
    abstract fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler3<TUserStorage> /*| Intent,*/): ActionsSdkApp<TUserStorage>
    abstract fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler4<TUserStorage> /*| Intent,*/): ActionsSdkApp<TUserStorage>

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
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler1<TUserStorage> /*| string,*/): ActionsSdkApp<TUserStorage>
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler2<TUserStorage> /*| string,*/): ActionsSdkApp<TUserStorage>
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler3<TUserStorage> /*| string,*/): ActionsSdkApp<TUserStorage>
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler4<TUserStorage> /*| string,*/): ActionsSdkApp<TUserStorage>

    /** @public */
    abstract fun catch(catcher: ExceptionHandler<TUserStorage, ActionsSdkConversation<TUserStorage>>): ActionsSdkApp<TUserStorage>

    /** @public */
    abstract fun fallback(handler: ActionsSdkIntentHandler3<TUserStorage> /*| string,*/): ActionsSdkApp<TUserStorage>

    /** @hidden */
    abstract var _middlewares: MutableList<ActionsSdkMiddleware>

    /** @public */
    abstract fun <TConversationPlugin : ActionsSdkConversation<*>> middleware(middleware: ActionsSdkMiddleware): ActionsSdkApp<TUserStorage>

    /** @public */
    abstract var verification: ActionsSdkVerification? //| string
}

abstract class BaseService<THandler, TIntentHandler, TConversation, TMiddleware, TVerification, TConvData, TUserStorage> : ConversationApp<TUserStorage>() {
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
    abstract fun intent(intents: MutableList<IntentEnum>, handler: TIntentHandler): ActionsSdkApp<TUserStorage>

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
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler1<TUserStorage>): ActionsSdkApp<TUserStorage>

    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler2<TUserStorage>): ActionsSdkApp<TUserStorage>
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler3<TUserStorage>): ActionsSdkApp<TUserStorage>
    abstract fun intent(vararg intents: String, handler: ActionsSdkIntentHandler4<TUserStorage>): ActionsSdkApp<TUserStorage>

    /** @public */
    abstract fun catch(catcher: ExceptionHandler<TUserStorage, ActionsSdkConversation<TUserStorage>>): ActionsSdkApp<TUserStorage>

    /** @public */
    abstract fun fallback(handler: ActionsSdkIntentHandler3<TUserStorage> ): ActionsSdkApp<TUserStorage>

    /** @hidden */
    abstract var _middlewares: MutableList<ActionsSdkMiddleware>

    /** @public */
    abstract fun <TConversationPlugin : ActionsSdkConversation<*>> middleware(middleware: ActionsSdkMiddleware): ActionsSdkApp<TUserStorage>

    /** @public */
    abstract var verification: ActionsSdkVerification? //| string
}

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

data class ActionsSdkOptions<TUserStorage>(
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
        override var init: (() -> ConversationOptionsInit<TUserStorage>)? = null,
        override var clientId: String? = null,
        override var debug: Boolean? = null
) : ConversationAppOptions<TUserStorage>

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

fun <TUserStorage> actionssdk(options: ActionsSdkOptions<TUserStorage>? = null): ActionsSdk<TUserStorage> {
    return ActionsSdk(options)
}

fun <TUserStorage> actionssdk(init: (ActionsSdkOptions<TUserStorage>.() -> Unit)? = null): ActionsSdk<TUserStorage> {
    val options = ActionsSdkOptions<TUserStorage>()
    options.init?.invoke()
    return ActionsSdk(options)
}

class ActionsSdk<TUserStorage>(options: ActionsSdkOptions<TUserStorage>? = null) : ActionsSdkApp<TUserStorage>() {


    override lateinit var frameworks: BuiltinFrameworks<TUserStorage>

    override fun <TService, TPlugin> use(plugin: Plugin<TService, TPlugin>): BaseAppPlugin<TPlugin, TUserStorage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var debug: Boolean = false


    override var _handlers: ActionsSdkHandlers<TUserStorage> = ActionsSdkHandlers<TUserStorage>(
            intents = ActionSdkIntentHandlers(),
            catcher = { conv, e -> throw e }
    )

    override fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler1<TUserStorage>): ActionsSdkApp<TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent.value] = { conv, status, g, arg -> handler(conv) }
        }
        return this
    }

    override fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler2<TUserStorage>): ActionsSdkApp<TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent.value] = { conv, status, g, arg -> handler(conv, status) }
        }
        return this
    }

    override fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler3<TUserStorage>): ActionsSdkApp<TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent.value] = { conv, status, g, arg -> handler(conv, status, g) }
        }
        return this
    }

    override fun intent(intents: MutableList<IntentEnum>, handler: ActionsSdkIntentHandler4<TUserStorage>): ActionsSdkApp<TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent.value] = { conv, status, g, arg -> handler(conv, status, g, arg) }
        }
        return this
    }

    override fun intent(vararg intents: String, handler: ActionsSdkIntentHandler1<TUserStorage>): ActionsSdkApp<TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv) }
        }
        return this
    }

    override fun intent(vararg intents: String, handler: ActionsSdkIntentHandler2<TUserStorage>): ActionsSdkApp<TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv, status) }
        }
        return this
    }

    override fun intent(vararg intents: String, handler: ActionsSdkIntentHandler3<TUserStorage>): ActionsSdkApp<TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv, status, g) }
        }
        return this
    }

    override fun intent(vararg intents: String, handler: ActionsSdkIntentHandler4<TUserStorage>): ActionsSdkApp<TUserStorage> {
        for (intent in intents) {
            this._handlers.intents[intent] = handler
        }
        return this
    }

    override fun catch(catcher: ExceptionHandler<TUserStorage, ActionsSdkConversation<TUserStorage>>): ActionsSdkApp<TUserStorage> {
        this._handlers.catcher = catcher
        return this
    }

    override fun fallback(handler: ActionsSdkIntentHandler3<TUserStorage>): ActionsSdkApp<TUserStorage> {
        this._handlers.fallback = { conv, status, g, arg -> handler.invoke(conv, status, g) }
        return this
    }

    override var _middlewares: MutableList<ActionsSdkMiddleware> = mutableListOf()

    override fun <TConversationPlugin : ActionsSdkConversation<*>> middleware(middleware: ActionsSdkMiddleware): ActionsSdkApp<TUserStorage> {
        this._middlewares.push(middleware)
        return this
    }

    override var verification: ActionsSdkVerification? = options?.verification

    override var init: (() -> ConversationOptionsInit<TUserStorage>)? = options?.init

    override var auth: OAuth2Config? = if (options?.clientId != null) OAuth2Config(client = OAuth2ConfigClient(id = options.clientId)) else null

    override var _client: OAuth2Client? = if (options?.verification != null || options?.clientId != null)
        OAuth2Client(options.clientId!!) else null

    override var handler: StandardHandler<TUserStorage> = object : StandardHandler<TUserStorage> {
        override fun handle(body: Any, headers: Headers, overrideHandler: DialogflowIntentHandler4<TUserStorage>?, aogOverrideHandler: ActionsSdkIntentHandler4<TUserStorage>?): StandardResponse {
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
            val traversedActionsHandlers: TraversedActionsHandlers<TUserStorage> = TraversedActionsHandlers()
            var handler = aogOverrideHandler ?: _handlers.intents[intent]
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
                if (traversedActionsHandlers[handler] == true) {
                    throw Error("Circular intent map detected: $handler traversed twice")
                }
                traversedActionsHandlers[handler] = true
//                handler = _handlers.intents[handler]
            }
            try {
                /* await */ handler?.invoke(
                        conv,
                        conv.input.raw ?: "",
                        conv.arguments.raw?.input?.values?.firstOrNull(),
//                        conv.arguments.parsed?.list?.firstOrNull(),
                        conv.arguments.status?.list?.firstOrNull()
                )
            } catch (e: Exception) {
                //TODO provide default catcher
                /*await */ _handlers.catcher?.invoke(conv, e)

            }
            return StandardResponse(
                    status = 200,
                    headers = mutableMapOf("Content-type" to mutableListOf("application/json; charset=UTF-8")),
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
            override fun handle(body: Any, headers: Headers, overrideHandler: DialogflowIntentHandler4<TUserStorage>?, aogOverrideHandler: ActionsSdkIntentHandler4<TUserStorage>?): StandardResponse {
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

