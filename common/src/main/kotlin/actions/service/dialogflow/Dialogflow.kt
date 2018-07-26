package actions.service.dialogflow

import actions.BaseAppPlugin
import actions.Plugin
import actions.expected.BuiltinFrameworks
import actions.expected.OAuth2Client
import actions.expected.Serializer
import actions.expected.log
import actions.framework.Headers
import actions.framework.OmniHandler
import actions.framework.StandardHandler
import actions.framework.StandardResponse
import actions.service.actionssdk.ActionsSdkIntentHandler4
import actions.service.actionssdk.WebhookError
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.actionssdk.api.GoogleActionsV2Argument
import actions.service.actionssdk.api.GoogleRpcStatus
import actions.service.actionssdk.conversation.*
import actions.service.actionssdk.push
import actions.service.dialogflow.api.DialogflowV1Parameters
import actions.service.dialogflow.api.DialogflowV1WebhookRequest
import actions.service.dialogflow.api.GoogleCloudDialogflowV2WebhookRequest


/**
 * Copyright 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

typealias DialogflowIntentHandler1<TUserStorage> = (conv: DialogflowConversation<TUserStorage>) -> Any

typealias DialogflowIntentHandler2<TUserStorage> = (conv: DialogflowConversation<TUserStorage>, param: DialogflowV1Parameters) -> Any
typealias DialogflowIntentHandler3<TUserStorage> = (conv: DialogflowConversation<TUserStorage>, param: DialogflowV1Parameters, arg: GoogleActionsV2Argument?) -> Any
typealias DialogflowIntentHandler4<TUserStorage> = (conv: DialogflowConversation<TUserStorage>, param: DialogflowV1Parameters, arg: GoogleActionsV2Argument?, status: GoogleRpcStatus?) -> Any


/** @hidden */
class DialogflowIntentHandlers<TUserStorage> : MutableMap<String, DialogflowIntentHandler4<TUserStorage>> by mutableMapOf()


/** @hidden */
data class DialogflowHandlers<
        TUserStorage,
        TConversation>(
        var intents: DialogflowIntentHandlers<TUserStorage>,
        var catcher: ExceptionHandler<TUserStorage, TConversation>? = null,
        var fallback: DialogflowIntentHandler4<TUserStorage>? = null //| string
)

/** @public */
interface DialogflowMiddleware
/*
TConversationPlugin extends DialogflowConversation<{}, {}, Contexts>
> {
(
    conv: DialogflowConversation<{}, {}, Contexts>,
): (DialogflowConversation<{}, {}, Contexts> & TConversationPlugin) | void
}
*/

/** @public */
abstract class DialogflowApp<
        TUserStorage,
        TConversation,
        TArgument> : ConversationApp<TUserStorage>() {
    /** @hidden */
    abstract var _handlers: DialogflowHandlers<TUserStorage, TConversation>

    /**
     * Sets the IntentHandler to be execute when the fulfillment is called
     * with a given Dialogflow intent name.
     *
     * @param intent The Dialogflow intent name to match.
     *     When given an array, sets the IntentHandler for any intent name in the array.
     * @param handler The IntentHandler to be executed when the intent name is matched.
     *     When given a string instead of a function, the intent fulfillment will be redirected
     *     to the IntentHandler of the redirected intent name.
     * @public
     */
    abstract fun intent(intents: MutableList<String>, handler: DialogflowIntentHandler1<TUserStorage> /*| Intent,*/): DialogflowApp<TUserStorage, TConversation, TArgument>

    abstract fun intent(intents: MutableList<String>, handler: DialogflowIntentHandler2<TUserStorage> /*| Intent,*/): DialogflowApp<TUserStorage, TConversation, TArgument>
    abstract fun intent(intents: MutableList<String>, handler: DialogflowIntentHandler3<TUserStorage> /*| Intent,*/): DialogflowApp<TUserStorage, TConversation, TArgument>
    abstract fun intent(
            intent: MutableList<String>,
            handler: DialogflowIntentHandler4<
                    TUserStorage> //| string,
    ): DialogflowApp<TUserStorage, TConversation, TArgument>

    /**
     * Sets the IntentHandler to be execute when the fulfillment is called
     * with a given Dialogflow intent name.
     *
     * @param intent The Dialogflow intent name to match.
     *     When given an array, sets the IntentHandler for any intent name in the array.
     * @param handler The IntentHandler to be executed when the intent name is matched.
     *     When given a string instead of a function, the intent fulfillment will be redirected
     *     to the IntentHandler of the redirected intent name.
     * @public
     */
    abstract fun intent(intents: String, handler: DialogflowIntentHandler1<TUserStorage> /*| Intent,*/): DialogflowApp<TUserStorage, TConversation, TArgument>

    abstract fun intent(intents: String, handler: DialogflowIntentHandler2<TUserStorage> /*| Intent,*/): DialogflowApp<TUserStorage, TConversation, TArgument>
    abstract fun intent(intents: String, handler: DialogflowIntentHandler3<TUserStorage> /*| Intent,*/): DialogflowApp<TUserStorage, TConversation, TArgument>
    abstract fun intent(
            intent: String,
            handler: DialogflowIntentHandler4<
                    TUserStorage> //| string,
    ): DialogflowApp<TUserStorage, TConversation, TArgument>

    /**
     * Sets the IntentHandler to be execute when the fulfillment is called
     * with a given Dialogflow intent name.
     *
     * @param intent The Dialogflow intent name to match.
     *     When given an array, sets the IntentHandler for any intent name in the array.
     * @param handler The IntentHandler to be executed when the intent name is matched.
     *     When given a string instead of a function, the intent fulfillment will be redirected
     *     to the IntentHandler of the redirected intent name.
     * @public
     */
    /*
    abstract fun <TParameters>intent(
            intent: MutableList<String>,
            handler: DialogflowIntentHandler4<
                    TConvData,
                    TUserStorage,
                    TParameters,
                    TArgument> //| string,
    ): DialogflowApp<TConvData, TUserStorage, TContexts, TConversation, TArgument>
    */


    /** @public */
    abstract fun catch(catcher: ExceptionHandler<TUserStorage, TConversation>): DialogflowApp<TUserStorage, TConversation, TArgument>

    /** @public */
    abstract fun fallback(
            handler: DialogflowIntentHandler4<
                    TUserStorage>
    ): DialogflowApp<TUserStorage, TConversation, TArgument>

    abstract var _middlewares: MutableList<DialogflowMiddleware>

    /** @public */
    abstract fun middleware/*<TConversationPlugin>*/(
            middleware: DialogflowMiddleware//<TConversationPlugin>
    ): DialogflowApp<TUserStorage, TConversation, TArgument>

    /** @public */
    abstract var verification: DialogflowVerification?
}

/** @public */
//class DialogflowVerificationHeaders : MutableMap<String, String> by mutableMapOf() {
/**
 * A header key value pair to check against.
 * @public
 */
//    [key: string]: string
//}

/** @public */
data class DialogflowVerification(
        /**
         * An object representing the header key to value map to check against,
         * @public
         */
        // Reusing Headers from framework.  Difference is Map<String, String> vs Map<String, List<String>>
        var headers: Headers? = null, //DialogflowVerificationHeaders? = null,

        /**
         * Custom status code to return on verification error.
         * @public
         */
        var status: Int? = null,

        /**
         * Custom error message as a string or a function that returns a string
         * given the original error message set by the library.
         *
         * The message will get sent back in the JSON top level `error` property.
         * @public
         */
        var error: String? = null //| ((error: string) => string)
)

/** @public */
data class DialogflowOptions<TUserStorage>(

        /**
         * Verifies whether the request comes from Dialogflow.
         * Uses header keys and values to check against ones specified by the developer
         * in the Dialogflow Fulfillment settings of the app.
         *
         * HTTP Code 403 will be thrown by default on verification error.
         *
         * @public
         */
        var verification: DialogflowVerification? = null, // | DialogflowVerificationHeaders,
        override var init: (() -> ConversationOptionsInit<TUserStorage>)? = null,
        override var clientId: String? = null,
        override var debug: Boolean? = null
) : ConversationAppOptions<TUserStorage>


fun isVerification(): Boolean = true
//val isVerification = (verification: DialogflowVerification | DialogflowVerificationHeaders):
//verification is DialogflowVerification =>
//typeof (verification as DialogflowVerification).headers === "object"

/**
 * This is the function that creates the app instance which on new requests,
 * creates a way to handle the communication with Dialogflow"s fulfillment API.
 *
 * Supports Dialogflow v1 and v2.
 *
 * @example
 * ```javascript
 *
 * val app = dialogflow()
 *
 * app.intent("Default Welcome Intent", conv => {
 *   conv.ask("How are you?")
 * })
 * ```
 *
 * @public
 */
fun <TUserStorage, TConversation, TArgument> dialogflow(init: (DialogflowOptions<TUserStorage>.() -> Unit)? = null): DialogflowApp<TUserStorage, TConversation, TArgument> {
    val options = DialogflowOptions<TUserStorage>()
    options.init?.invoke()
    return DialogflowSdk(options)
}

class DialogflowSdk<TUserStorage, TConversation, TArgument>(options: DialogflowOptions<TUserStorage>? = null) : DialogflowApp<TUserStorage, TConversation, TArgument>() {


    override lateinit var frameworks: BuiltinFrameworks<TUserStorage>

    override fun <TService, TPlugin> use(plugin: Plugin<TService, TPlugin>): BaseAppPlugin<TPlugin, TUserStorage> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var debug: Boolean = false

    override var _handlers: DialogflowHandlers<TUserStorage, TConversation> = DialogflowHandlers(
            intents = DialogflowIntentHandlers<TUserStorage>(),
            catcher = { conv, e -> throw e })

    override var _middlewares: MutableList<DialogflowMiddleware> = mutableListOf()


    override fun intent(
            intents: MutableList<String>,
            handler: DialogflowIntentHandler4<
                    TUserStorage> //| string,
    ): DialogflowApp<TUserStorage, TConversation, TArgument> {
        for (intent in intents) {
            this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv, status, g, arg) }
        }
        return this
    }

    override fun intent(intent: String, handler: DialogflowIntentHandler4<TUserStorage>): DialogflowApp<TUserStorage, TConversation, TArgument> {
        this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv, status, g, arg) }
        return this
    }

    override fun intent(intents: MutableList<String>, handler: DialogflowIntentHandler1<TUserStorage>): DialogflowApp<TUserStorage, TConversation, TArgument> {
        for (intent in intents) {
            this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv) }
        }
        return this
    }

    override fun intent(intents: MutableList<String>, handler: DialogflowIntentHandler2<TUserStorage>): DialogflowApp<TUserStorage, TConversation, TArgument> {
        for (intent in intents) {
            this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv, status) }
        }
        return this
    }

    override fun intent(intents: MutableList<String>, handler: DialogflowIntentHandler3<TUserStorage>): DialogflowApp<TUserStorage, TConversation, TArgument> {
        for (intent in intents) {
            this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv, status, g) }
        }
        return this
    }

    override fun intent(intent: String, handler: DialogflowIntentHandler1<TUserStorage>): DialogflowApp<TUserStorage, TConversation, TArgument> {
        this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv) }
        return this
    }

    override fun intent(intent: String, handler: DialogflowIntentHandler2<TUserStorage>): DialogflowApp<TUserStorage, TConversation, TArgument> {
        this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv, status) }
        return this
    }

    override fun intent(intent: String, handler: DialogflowIntentHandler3<TUserStorage>): DialogflowApp<TUserStorage, TConversation, TArgument> {
        this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv, status, g) }
        return this
    }

    override fun catch(catcher: ExceptionHandler<TUserStorage, TConversation>): DialogflowSdk<TUserStorage, TConversation, TArgument> {
        this._handlers.catcher = catcher
        return this
    }

    override fun fallback(handler: DialogflowIntentHandler4<TUserStorage>): DialogflowSdk<TUserStorage, TConversation, TArgument> {
        this._handlers.fallback = handler
        return this
    }

    override fun middleware(middleware: DialogflowMiddleware
    ): DialogflowApp<TUserStorage, TConversation, TArgument> {
        this._middlewares.push(middleware)
        return this
    }

    override var init: (() -> ConversationOptionsInit<TUserStorage>)? = options?.init

    override var verification: DialogflowVerification? = options?.verification


    override var _client: OAuth2Client? = if (options?.verification != null || options?.clientId != null)
        OAuth2Client(options.clientId!!) else null

    override var auth: OAuth2Config? = if (options?.clientId != null) OAuth2Config(client = OAuth2ConfigClient(id = options.clientId)) else null

    override var handler: StandardHandler<TUserStorage> = object : StandardHandler<TUserStorage> {
        override fun handle(body: Any, headers: Headers, overrideHandler: DialogflowIntentHandler4<TUserStorage>?, aogOverrideHandler: ActionsSdkIntentHandler4<TUserStorage>?): StandardResponse {
            val convBodyV1 = body as? DialogflowV1WebhookRequest
            val convBodyV2 = body as? GoogleCloudDialogflowV2WebhookRequest

//    override fun handler(
//            body: GoogleCloudDialogflowV2WebhookRequest, headers: DialogflowVerificationHeaders) {
            val debug = debug
//        }

            val init = init
            val verification = verification

            if (verification != null) {

                val newVerification = DialogflowVerification(headers)
                val verificationHeaders = newVerification.headers
                val status = newVerification.status
                val error = newVerification.error

//            val StandardResponse(
//                headers = newVerification.headers, //verificationHeaders,
//                status = 403,
//                error = WebhookError(error = error)
//            ) = isVerification(verification) ? verification :
//            { headers: verification } as DialogflowVerification

                newVerification.headers?.forEach {
                    val check = headers!![it.key.toLowerCase()]
                    if (check?.isNotEmpty() == true) {
                        error("A verification header key was not found")
                        return StandardResponse(
                                status,
                                body = WebhookError(
                                        error = error)
                        )
                    }
                    val value = verificationHeaders!![it.key]?.first()
//                    val checking = common.toArray(check)
                    if (check == null || check.indexOf(value) < 0) {
                        error("A verification header value was invalid")
                        return StandardResponse(
                                status = status,
                                body = WebhookError(
                                        error = error)
                        )
                    }
                }
            }


            val conv = DialogflowConversation(
                    DialogflowConversationOptions(
                            body = convBodyV2,
                            bodyV1 = convBodyV1,
                            headers = headers,
                            init = init?.invoke(), //init &&init (),
                            debug = debug))

            if (conv.user.profile?.token != null) {
//            await conv . user . _verifyProfile (this._client!, this.auth!.client.id)
                conv.user._verifyProfile(_client!!, auth?.client?.id!!)
            }
            for (middleware in _middlewares) {
                TODO("Handle middleware")
//                    conv = middleware(conv) //as DialogflowConversation<TConvData, TUserStorage, TContexts> | void)
//        || conv
            }
//        val log = debug ? common.info : common.debug
//        log("Conversation", common.stringify(conv, "request", "headers", "body"))

            //Using the action field to match handlers here.  Nodejs lib uses intent, which differs from V1 and may break some agents.
            //TODO: match the implementation of nodejs once issue is resolved: https://github.com/actions-on-google/actions-on-google-nodejs/issues/132
            val intent = conv.action
            val traversed: TraversedDialogflowHandlers<TUserStorage, TArgument> = TraversedDialogflowHandlers()
            var handler = if (overrideHandler != null) overrideHandler else _handlers.intents[intent]
//            while (typeof handler !== 'function') {
            while (false) {
                //TODO why is this loop here? handle intents mapped to a string?
                if (handler == null) {
                    if (_handlers.fallback == null) {
                        throw Error("Dialogflow IntentHandler not found for intent: $intent")
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
                /*await*/ handler?.invoke(
                        conv,
                        conv.parameters!!,
                        conv.arguments.parsed?.list?.firstOrNull(),
                        conv.arguments.status?.list?.firstOrNull())
            } catch (e: Exception) {
//            await this._handlers.catcher(conv as TConversation, e)
//                _handlers.catcher?.invoke(conv!!, e)
                log(e.message ?: "")
            }
            return StandardResponse(
                    status = 200,
                    headers = mutableMapOf("Content-type" to mutableListOf("UTF-8")),
                    body = if (convBodyV1 != null) conv.serializeV1() else conv.serialize())
        }
    }

    //TODO - refactor init from Dialogflow & ActionsSdk into common function
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
