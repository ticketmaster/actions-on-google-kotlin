package actions.service.dialogflow

import actions.ProtoAny
import actions.expected.Serializer
import actions.framework.Headers
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.actionssdk.api.GoogleActionsV2RichResponse
import actions.service.actionssdk.api.GoogleActionsV2SimpleResponse
import actions.service.actionssdk.conversation.Conversation
import actions.service.actionssdk.conversation.ConversationBaseOptions
import actions.service.actionssdk.conversation.ConversationOptions
import actions.service.actionssdk.conversation.ConversationOptionsInit
import actions.service.dialogflow.api.*

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

const val APP_DATA_CONTEXT = "_actions_on_google"
const val APP_DATA_CONTEXT_LIFESPAN = 99

/** @hidden */
data class SystemIntent(
        var intent: String? = null,
        var data: ProtoAny)

/** @hidden */
data class GoogleAssistantResponse(
        var expectUserResponse: Boolean? = null,
        var noInputPrompts: MutableList<GoogleActionsV2SimpleResponse>? = null,
        var isSsml: Boolean? = null,
        var richResponse: GoogleActionsV2RichResponse? = null,
        var systemIntent: SystemIntent? = null,
        var userStorage: String? = null
)

/** @hidden */
data class PayloadGoogle(
        var google: GoogleAssistantResponse? = null)

/** @public */
data class DialogflowConversationOptions<TUserStorage>(var body: GoogleCloudDialogflowV2WebhookRequest? = null,
                                                                  var bodyV1: DialogflowV1WebhookRequest? = null,
                                                                  override var headers: Headers? = null,
                                                                  override var init: ConversationOptionsInit<TUserStorage>?,
                                                                  override var debug: Boolean?
) : ConversationBaseOptions<TUserStorage> {
    fun isV1(): Boolean = bodyV1 != null

    fun getRequest(): GoogleActionsV2AppRequest? {
        return if (isV1()) {
            bodyV1?.originalRequest?.data
            null
        } else {
            body?.originalDetectIntentRequest!!.payload
        }

    }
}

//val getRequest = (
//        body: Api.GoogleCloudDialogflowV2WebhookRequest | ApiV1.DialogflowV1WebhookRequest,
//) => {
//    if (isV1(body)) {
//        val { originalRequest = {} } = body
//        val { data = {} } = originalRequest
//        return data
//    }
//    return body.originalDetectIntentRequest!.payload!
//}

/** @public */
class DialogflowConversation<TUserStorage>(options: DialogflowConversationOptions<TUserStorage>) : Conversation<TUserStorage>(
        ConversationOptions<TUserStorage>(request = options.getRequest(),
                headers = options.headers,
                init = options.init)) {

    /** @public */
    var body: GoogleCloudDialogflowV2WebhookRequest?
    var bodyV1: DialogflowV1WebhookRequest?

    /**
     * Get the current Dialogflow action name.
     *
     * @example
     * ```javascript
     *
     * app.intent("Default Welcome Intent", conv => {
     *   val action = conv.action
     * })
     * ```
     *
     * @public
     */
    var action: String

    /**
     * Get the current Dialogflow intent name.
     *
     * @example
     * ```javascript
     *
     * app.intent("Default Welcome Intent", conv => {
     *   val intent = conv.intent // will be "Default Welcome Intent"
     * })
     * ```
     *
     * @public
     */
    var intent: String

    /**
     * The Dialogflow parameters from the current intent.
     * Values will only be a string, an Object, or undefined if not included.
     *
     * Will also be sent via intent handler 3rd argument which is the encouraged method to retrieve.
     *
     * @example
     * ```javascript
     *
     * // Encouraged method through intent handler
     * app.intent("Tell Greeting", (conv, params) => {
     *   val color = params.color
     *   val num = params.num
     * })
     *
     * // Encouraged method through destructuring in intent handler
     * app.intent("Tell Greeting", (conv, { color, num }) => {
     *   // now use color and num as variables
     * }))
     *
     * // Using conv.parameters
     * app.intent("Tell Greeting", conv => {
     *   val parameters = conv.parameters
     *   // or destructed
     *   val { color, num } = conv.parameters
     * })
     * ```
     *
     * @public
     */
    var parameters: DialogflowV1Parameters

    /** @public */
    var contexts: ContextValues

    /** @public */
    var incoming: Incoming

    /**
     * The user"s raw input query.
     *
     * @example
     * ```javascript
     *
     * app.intent("User Input", conv => {
     *   conv.close(`You said ${conv.query}`)
     * })
     * ```
     *
     * @public
     */
    var query: String

    /**
     * The session data in JSON format.
     * Stored using contexts.
     *
     * @example
     * ```javascript
     *
     * app.intent("Default Welcome Intent", conv => {
     *   conv.data.someProperty = "someValue"
     * })
     * ```
     *
     * @public
     */
    var data: MutableMap<String, Any?> = mutableMapOf()


    /** @public */
    var version: Int

    /** @public */
    init {

        val init = options

        this.body = options.body
        this.bodyV1 = options.bodyV1

        if (options.isV1()) {
            this.version = 1

            val result = bodyV1?.result

            val action = result?.action ?: ""
            val parameters = result?.parameters
            val contexts = result?.contexts
            val resolvedQuery = result?.resolvedQuery
            val metadata = result?.metadata
            val fulfillment = result?.fulfillment
            val intentName = metadata?.intentName

            this.action = action
            this.intent = intentName ?: ""
            this.parameters = parameters ?: mutableMapOf()
            this.contexts = ContextValues(contexts)
            this.incoming = Incoming(fulfillment!!)
            this.query = resolvedQuery ?: ""
        } else {
            this.version = 2

            val queryResult = this.body?.queryResult
            val action = queryResult?.action
            val parameters = queryResult?.parameters
            val outputContexts = queryResult?.outputContexts
            val intent = queryResult?.intent
            val queryText = queryResult?.queryText
            val fulfillmentMessages = queryResult?.fulfillmentMessages

            val displayName = intent?.displayName

            this.action = action ?: ""
            this.intent = displayName ?: ""
            this.parameters = parameters ?: mutableMapOf()
            this.contexts = ContextValues(outputContexts = outputContexts, session = this.body?.session, flag = true)
            this.incoming = Incoming(fulfillmentMessages!!)
            this.query = queryText ?: ""
        }

        parameters.forEach {
            //Not needed for kotlin SDK
//            val value = this.parameters[key]
//            if (typeof value !== "object") {
            // Convert all non-objects to strings for consistency
//                this.parameters[key] = String(value)
//            }
        }

        //TODO find a way to do this in kotlin
//        this.data = (init && init.data) || {} as TConvData
//        this.data = init.body?.originalDetectIntentRequest?.s

        val context = this.contexts.input?.get(APP_DATA_CONTEXT)
        if (context != null) {
            val data = context.parameters?.get("data")//?.data
            if (data is String) {
                this.data = Serializer.deserializeMap(data)
            }
        }
    }

    /**
     * Triggers an intent of your choosing by sending a followup event from the webhook.
     *
     * @example
     * ```javascript
     *
     * val app = dialogflow()
     *
     * // Create a Dialogflow intent with event "apply-for-license-event"
     *
     * app.intent("Default Welcome Intent", conv => {
     *   conv.followup("apply-for-license-event", {
     *     date: new Date().toISOString(),
     *   })
     *   // The dialogflow intent with the "apply-for-license-event" event
     *   // will be triggered with the given parameters `date`
     * })
     * ```
     *
     * @param event Name of the event
     * @param parameters Parameters to send with the event
     * @param lang The language of this query.
     *     See {@link https://dialogflow.com/docs/languages|Language Support}
     *     for a list of the currently supported language codes.
     *     Note that queries in the same session do not necessarily need to specify the same language.
     *     By default, it is the languageCode sent with Dialogflow"s queryResult.languageCode
     * @public
     */
    fun followup(event: String, parameters: DialogflowV1Parameters?, lang: String?) {
        /*
        if (this.version === 1) {
            return this.json<DialogflowV1WebhookResponse>({
                followupEvent: { name: event,
                                 data: parameters,
            },
            })
        }
        val body = this.body as GoogleCloudDialogflowV2WebhookRequest
        return this.json<GoogleCloudDialogflowV2WebhookResponse>({
            followupEventInput: {
            name: event,
            parameters,
            languageCode: lang || body.queryResult!.languageCode,
        },
        })
        */
    }


    fun commonPayload(): Data {

        return if (platformData != null) {
           platformData!!
        } else {
            val response = this.response()
            val richResponse = response.richResponse
            val expectUserResponse = response.expectUserResponse ?: true
            val userStorage = response.userStorage
            val expectedIntent = response.expectedIntent
            val systemIntent = if (expectedIntent?.intent != null)
                SystemIntent(
                        intent = expectedIntent?.intent,
                        data = expectedIntent?.inputValueData ?: ProtoAny())
            else
                null

            val googleData = Data()
            googleData.google = GoogleAssistantResponse(
                    expectUserResponse = expectUserResponse,
                    richResponse = richResponse!!,
                    userStorage = userStorage,
                    systemIntent = systemIntent
            )
            googleData
        }

    }

    fun serializeV1(): DialogflowV1WebhookResponse {
        if (this._raw != null) {
            //TODO investigate _raw and implementation
            return this._raw as DialogflowV1WebhookResponse
        }
        val contextOut = this.contexts._serializeV1()
        val payload = commonPayload()
        val response = DialogflowV1WebhookResponse(
                speech = textToSpeech,
                displayText = displayText,
                data = payload,
                contextOut = contextOut)
        return response
    }

    /** @public */
    fun serialize(): GoogleCloudDialogflowV2WebhookResponse {
        if (this._raw != null) {
            return this._raw as GoogleCloudDialogflowV2WebhookResponse
        }

        val payload = commonPayload()

        val data = mutableMapOf<String, Any>()
        data["data"] = Serializer.serialize(this.data) ?: Any()

        this.contexts.set(APP_DATA_CONTEXT, APP_DATA_CONTEXT_LIFESPAN, data)

        val outputContexts = this.contexts._serialize()
        val response = GoogleCloudDialogflowV2WebhookResponse(
                fulfillmentText = textToSpeech,
                payload = payload,
                outputContexts = outputContexts)
        return response
    }
}
