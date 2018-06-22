package actions.service.dialogflow

import actions.expected.OAuth2Client
import actions.framework.Headers
import actions.framework.StandardHandler
import actions.framework.StandardResponse
import actions.service.actionssdk.WebhookError
import actions.service.actionssdk.api.GoogleActionsV2Argument
import actions.service.actionssdk.api.GoogleRpcStatus
import actions.service.actionssdk.conversation.*
import actions.service.actionssdk.push


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

typealias DialogflowIntentHandler1<TConvData, TUserStorage> = (conv: DialogflowConversation<TUserStorage, TConvData, TUserStorage>) -> Any

typealias DialogflowIntentHandler2<TConvData, TUserStorage, TParameters, TArgument, TContext> = (conv: DialogflowConversation<TUserStorage, TConvData, TUserStorage, TContext>, param: TParameters) -> Any
typealias DialogflowIntentHandler3<TConvData, TUserStorage, TParameters, TArgument, TContext> = (conv: DialogflowConversation<TUserStorage, TConvData, TUserStorage, TContext>, param: TParameters, arg: GoogleActionsV2Argument?) -> Any
typealias DialogflowIntentHandler4<TConvData, TUserStorage, TParameters, TArgument, TContext> = (conv: DialogflowConversation<TUserStorage, TConvData, TUserStorage, TContext>, param: TParameters, arg: GoogleActionsV2Argument?, status: GoogleRpcStatus?) -> Any

/*
/** @public */
fun <TConvData, TUserStorage, TContexts, TConversation, TParameters, TArgument> dialogflowIntentHandler(
    var conv: TConversation,
    var params: TParameters,
    /**
     * The first argument value from the current intent.
     * See {@link Arguments#get|Arguments.get}
     * Same as `conv.arguments.parsed.list[0]`
     */
    var argument: TArgument,
    /**
     * The first argument status from the current intent.
     * See {@link Arguments#status|Arguments.status}
     * Same as `conv.arguments.status.list[0]`
     */
    var status: GoogleRpcStatus?
    // tslint:disable-next-line:no-any allow developer to return any just detect if is promise
    ): Any {
}
*/

/** @hidden */
class DialogflowIntentHandlers<TConvData, TUserStorage, TParameters, TArgument, TContext> : MutableMap<String, DialogflowIntentHandler4<TConvData, TUserStorage, TParameters, TArgument, TContext>> by mutableMapOf() {

}


/** @hidden */
data class DialogflowHandlers<
        TConvData,
        TUserStorage,
        TContext,
        TConversation>(
        var intents: DialogflowIntentHandlers<TConvData, TUserStorage, *, *, TContext>,
        var catcher: ExceptionHandler<TUserStorage, TConversation>? = null,
        var fallback: DialogflowIntentHandler4<TConvData, TUserStorage, *, *, TContext>? = null //| string
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
        TConvData,
        TUserStorage,
        TContexts,
        TConversation,
        TArgument> : ConversationApp<TConvData, TUserStorage>() {
    /** @hidden */
    abstract var _handlers: DialogflowHandlers<TConvData, TUserStorage, TContexts, TConversation>

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
    abstract fun <TParameters> intent(
            intent: MutableList<String>,
            handler: DialogflowIntentHandler4<
                    TConvData,
                    TUserStorage,
                    TParameters,
                    TArgument, TContexts> //| string,
    ): DialogflowApp<TConvData, TUserStorage, TContexts, TConversation, TArgument>

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
    fun intent<TParameters, TArgument> (
            intent: MutableList<String>,
            handler: DialogflowIntentHandler4<
                    TConvData,
                    TUserStorage,
                    TContexts,
                    TConversation,
                    TParameters,
                    TArgument
                    > //| string,
    ): DialogflowApp<TConvData, TUserStorage, TContexts, TConversation>
    */

    /** @public */
    abstract fun catch(catcher: ExceptionHandler<TUserStorage, TConversation>): DialogflowApp<TConvData, TUserStorage, TContexts, TConversation, TArgument>

    /** @public */
    abstract fun <TParameters> fallback(
            handler: DialogflowIntentHandler4<
                    TConvData,
                    TUserStorage,
                    TParameters,
                    TArgument, TContexts> //| string,
    ): DialogflowApp<TConvData, TUserStorage, TContexts, TConversation, TArgument>

    abstract var _middlewares: MutableList<DialogflowMiddleware> //<DialogflowConversation<TConvData, TUserStorage, TContexts>>>

    /** @public */
    abstract fun middleware/*<TConversationPlugin>*/(
            middleware: DialogflowMiddleware//<TConversationPlugin>
    ): DialogflowApp<TConvData, TUserStorage, TContexts, TConversation, TArgument>

    /** @public */
    abstract var verification: DialogflowVerification? // | DialogflowVerificationHeaders
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
data class DialogflowOptions<TConvData, TUserStorage>(

        /**
         * Verifies whether the request comes from Dialogflow.
         * Uses header keys and values to check against ones specified by the developer
         * in the Dialogflow Fulfillment settings of the app.
         *
         * HTTP Code 403 will be thrown by default on verification error.
         *
         * @public
         */
        var verification: DialogflowVerification?, // | DialogflowVerificationHeaders,
        override var init: (() -> ConversationOptionsInit<TConvData, TUserStorage>)?,
        override var clientId: String?,
        override var debug: Boolean?
) : ConversationAppOptions<TConvData, TUserStorage>

/*
/** @public */
interface Dialogflow {
    /** @public */
    fun <TConvData,
            TUserStorage,
            TContexts,
            Conversation> process(
            options: DialogflowOptions<TConvData, TUserStorage>?
    ): DialogflowApp<TConvData, TUserStorage, TContexts, Conversation>

    /** @public */
    fun <TContexts, Conversation> process(
            options: DialogflowOptions<*, *>): /*AppHandler & */DialogflowApp<*, *, TContexts, Conversation>

    /** @public */
    fun <TConversation>process(options: DialogflowOptions<*, *>): /*AppHandler & */ DialogflowApp<*, *, Contexts, TConversation>
}
*/

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
fun <TConvData, TUserStorage, TContexts, TConversation, TArgument> dialogflow(options: DialogflowOptions<TConvData, TUserStorage>): DialogflowApp<TConvData, TUserStorage, TContexts, TConversation, TArgument> {
    return DialogflowSdk<TConvData, TUserStorage, TContexts, TConversation, TArgument>(options)
}

class DialogflowSdk<TConvData, TUserStorage, TContexts, TConversation, TArgument>(options: DialogflowOptions<TConvData, TUserStorage>? = null) : DialogflowApp<TConvData, TUserStorage, TContexts, TConversation, TArgument>() {

    override var _handlers: DialogflowHandlers<TConvData, TUserStorage, TContexts, TConversation> = DialogflowHandlers(
            intents = DialogflowIntentHandlers<TConvData, TUserStorage, TParameters, TArgument, TContexts>(),
            catcher = { conv, e -> throw e })

    override var _middlewares: MutableList<DialogflowMiddleware> = mutableListOf()


    override fun <TParameters> intent(
            intents: MutableList<String>,
            handler: DialogflowIntentHandler4<
                    TConvData,
                    TUserStorage,
                    TParameters,
                    TArgument,
                    TContexts> //| string,
    ): DialogflowApp<TConvData, TUserStorage, TContexts, TConversation, TArgument> {
        for (intent in intents) {
            this._handlers.intents[intent] = { conv, status, g, arg -> handler(conv, status, g, arg) }
        }
        return this
    }

    override fun catch(catcher: ExceptionHandler<TUserStorage, TConversation>): DialogflowSdk<TConvData, TUserStorage, TContexts, TConversation, TArgument> {
        this._handlers.catcher = catcher
        return this
    }

    override fun <TParameters> fallback(handler: DialogflowIntentHandler4<
            TConvData,
            TUserStorage,
            TParameters,
            TArgument,
            TContexts>): DialogflowSdk<TConvData, TUserStorage, TContexts, TConversation, TArgument> {
        this._handlers.fallback = handler
        return this
    }

    override fun middleware(middleware: DialogflowMiddleware//<TConversationPlugin>
    ): DialogflowApp<TConvData, TUserStorage, TContexts, TConversation, TArgument> {
        this._middlewares.push(middleware)
        return this
    }

    override var init: (() -> ConversationOptionsInit<TConvData, TUserStorage>)? = options?.init

    override var verification: DialogflowVerification? = options?.verification


    override var _client: OAuth2Client? = if (options?.verification != null || options?.clientId != null)
        OAuth2Client(options.clientId!!) else null

    override var auth: OAuth2Config? = if (options?.clientId != null) OAuth2Config(client = OAuth2ConfigClient(id = options.clientId)) else null

    override var handler: StandardHandler<TUserStorage> = object : StandardHandler<TUserStorage> {
        override fun handle(body: Any, headers: Headers): StandardResponse {
//    override fun handler(
//            body: GoogleCloudDialogflowV2WebhookRequest, headers: DialogflowVerificationHeaders) {
            val debug = debug
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


                var conv = DialogflowConversation<TConvData, TUserStorage, TContexts, TParameters>(
                        DialogflowConversationOptions<TConvData, TUserStorage>(
                                body = body,
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


                val intent = conv.intent
                val traversed: TraversedDialogflowHandlers<TConvData, TUserStorage, TParameters, TArgument> = TraversedDialogflowHandlers()
                var handler = _handlers.intents[intent]
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
                            conv.parameters,
                            conv.arguments.parsed.list?.firstOrNull(),
                            conv.arguments.status.list?.firstOrNull())
                } catch (e: Exception) {
//            await this._handlers.catcher(conv as TConversation, e)
                    this._handlers.catcher?.invoke(conv)
                }
                return StandardResponse(
                        status = 200,
                        headers = mutableMapOf(),
                        body = conv.serialize())
            }
        }