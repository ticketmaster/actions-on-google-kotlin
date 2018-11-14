package actions.service.dialogflow.api

import actions.ApiClientObjectMap
import actions.framework.JsonObject
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.dialogflow.DialogflowV1Message
import actions.service.dialogflow.GoogleAssistantResponse
import actions.service.dialogflow.Message
import actions.service.dialogflow.PayloadGoogle

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

/* tslint:disable:no-any max-line-length written like auto generated types from protobufs */

data class DialogflowV1OriginalRequest(
    var source: String? = null,
    var version: String? = null,
    var data: GoogleActionsV2AppRequest? = null
)

typealias DialogflowV1Parameters = MutableMap<String, Any>
//class DialogflowV1Parameters: MutableMap<String, Any?> by mutableMapOf() {
//    [parameter: String]: String | Object | undefined
//}

data class DialogflowV1Context(
    var name: String? = null,
    var lifespan: Int? = null,
    var parameters: DialogflowV1Parameters? = null
)

data class DialogflowV1Metadata(
    var intentId: String? = null,
    var webhookUsed: String? = null,
    var webhookForSlotFillingUsed: String? = null,
    var nluResponseTime: Int? = null,
    var intentName: String? = null
)

data class DialogflowV1Button(
    var text: String? = null,
    var postback: String? = null
)


/*
type DialogflowV1Message =
DialogflowV1MessageText |
DialogflowV1MessageImage |
DialogflowV1MessageCard |
DialogflowV1MessageQuickReplies |
DialogflowV1MessageCustomPayload |
DialogflowV1MessageSimpleResponse |
DialogflowV1MessageBasicCard |
DialogflowV1MessageList |
DialogflowV1MessageSuggestions |
DialogflowV1MessageCarousel |
DialogflowV1MessageLinkOut |
DialogflowV1MessageGooglePayload
*/

data class DialogflowV1Fulfillment(
    var speech: String? = null,
    var messages: MutableList<Message>? = null,
    var data: Data? = null
)

data class DialogflowV1Result(
    var source: String? = null,
    var resolvedQuery: String? = null,
    var speech: String? = null,
    var action: String? = null,
    var actionIncomplete: Boolean? = null,
    var parameters: DialogflowV1Parameters? = null,
    var contexts: MutableList<DialogflowV1Context>? = null,
    var metadata: DialogflowV1Metadata? = null,
    var fulfillment: DialogflowV1Fulfillment? = null,
    var score: Float? = null
)

/**
 * Holds data for original platform.  Extends MutableMap so this is extendable
 * to other platforms by adding a field
 * Requires custom serialization in JVM.  See DataTypeAdapter
 */
data class Data(val nothing: Nothing? = null) : MutableMap<String, Any?> by mutableMapOf() {
    var google: GoogleAssistantResponse? by this

    inline fun google(init: GoogleAssistantResponse.() -> Unit) {
        if (google == null) {
            google = GoogleAssistantResponse()
        }
        google?.init()
    }
}

data class DialogflowV1Status(
    var code: Int? = null,
    var errorType: String? = null,
    var webhookTimedOut: Boolean? = null
)

data class DialogflowV1WebhookRequest(
    var originalRequest: DialogflowV1OriginalRequest? = null,
    var id: String? = null,
    var sessionId: String? = null,
    var timestamp: String? = null,
    var timezone: String? = null,
    var lang: String? = null,
    var result: DialogflowV1Result? = null,
    var status: DialogflowV1Status? = null
)

data class DialogflowV1FollowupEvent(
    var name: String? = null,
    var data: DialogflowV1Parameters? = null
)

data class DialogflowV1WebhookResponse(
    var speech: String? = null,
    var displayText: String? = null,
    var messages: MutableList<Message>? = null,
    var data: Data? = null,
//    var data: ApiClientObjectMap<Any>? = null,
    var contextOut: MutableList<DialogflowV1Context>? = null,
    var source: String? = null,
    var followupEvent: DialogflowV1FollowupEvent? = null)
