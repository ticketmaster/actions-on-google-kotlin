package actions.service.dialogflow.api

import actions.ApiClientObjectMap
import actions.framework.JsonObject
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.dialogflow.DialogflowV1Message

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

interface DialogflowV1OriginalRequest {
    var source: String
    var version: String
    var data: GoogleActionsV2AppRequest
}

class DialogflowV1Parameters: MutableMap<String, Any?> by mutableMapOf() {
//    [parameter: String]: String | Object | undefined
}

data class DialogflowV1Context(
    var name: String? = null,
    var parameters: DialogflowV1Parameters? = null,
    var lifespan: Int? = null
)

interface DialogflowV1Metadata {
    var intentId: String
    var webhookUsed: String
    var webhookForSlotFillingUsed: String
    var nluResponseTime: Int
    var intentName: String
}

interface DialogflowV1Button {
    var text: String
    var postback: String
}


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

interface DialogflowV1Fulfillment {
    var speech: String?
    var messages: MutableList<DialogflowV1Message>?
}

interface DialogflowV1Result {
    var source: String
    var resolvedQuery: String
    var speech: String
    var action: String
    var actionIncomplete: Boolean
    var parameters: DialogflowV1Parameters
    var contexts: MutableList<DialogflowV1Context>
    var metadata: DialogflowV1Metadata
    var fulfillment: DialogflowV1Fulfillment
    var score: Int
}

interface DialogflowV1Status {
    var code: Int
    var errorType: String
    var webhookTimedOut: Boolean
}

interface DialogflowV1WebhookRequest {
    var originalRequest: DialogflowV1OriginalRequest
    var id: String
    var sessionId: String
    var timestamp: String
    var timezone: String
    var lang: String
    var result: DialogflowV1Result
    var status: DialogflowV1Status
}

interface DialogflowV1FollowupEvent {
    var name: String
    var data: DialogflowV1Parameters
}

data class DialogflowV1WebhookResponse(
    var speech: String? = null,
    var displayText: String? = null,
    var messages: MutableList<DialogflowV1Message>? = null,
    var data: ApiClientObjectMap<Any>? = null,
    var contextOut: MutableList<DialogflowV1Context>? = null,
    var source: String? = null,
    var followupEvent: DialogflowV1FollowupEvent? = null)
