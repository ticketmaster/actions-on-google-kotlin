package actions.service.dialogflow.api

import actions.ApiClientObjectMap
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.dialogflow.GoogleAssistantResponse
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

// DO NOT MANUALLY EDIT: this file contains types generated from protobuf messages

/* tslint:disable:no-any max-line-length auto generated from protobufs */

enum class GoogleCloudDialogflowV2IntentDefaultResponsePlatforms {
    PLATFORM_UNSPECIFIED,
    FACEBOOK,
    SLACK,
    TELEGRAM,
    KIK,
    SKYPE,
    LINE,
    VIBER,
    ACTIONS_ON_GOOGLE
}


enum class GoogleCloudDialogflowV2IntentMessagePlatform {
    PLATFORM_UNSPECIFIED,
    FACEBOOK,
    SLACK,
    TELEGRAM,
    KIK,
    SKYPE,
    LINE,
    VIBER,
    ACTIONS_ON_GOOGLE
}


enum class GoogleCloudDialogflowV2IntentTrainingPhraseType {
    TYPE_UNSPECIFIED,
    EXAMPLE,
    TEMPLATE
}


enum class GoogleCloudDialogflowV2IntentWebhookState {
    WEBHOOK_STATE_UNSPECIFIED,
    WEBHOOK_STATE_ENABLED,
    WEBHOOK_STATE_ENABLED_FOR_SLOT_FILLING
}


//TESTING IF COMMON CONTEXT model will work for both.  Using V1Contexts for internal logic now.

data class GoogleCloudDialogflowV2Context(
        var name: String,
        var lifespanCount: Int,
//    var parameters: ApiClientObjectMap<Any>
        var parameters: DialogflowV1Parameters? = null
)

data class GoogleCloudDialogflowV2EventInput(
        var name: String? = null,
        var parameters: ApiClientObjectMap<Any>? = null,
        var languageCode: String? = null
)

data class GoogleCloudDialogflowV2Intent(
        var name: String? = null,
        var displayName: String? = null,
        var webhookState: GoogleCloudDialogflowV2IntentWebhookState? = null,
        var priority: Int? = null,
        var isFallback: Boolean? = null,
        var mlDisabled: Boolean? = null,
        var inputContextNames: MutableList<String>? = null,
        var events: MutableList<String>? = null,
        var trainingPhrases: MutableList<GoogleCloudDialogflowV2IntentTrainingPhrase>? = null,
        var action: String? = null,
        var outputContexts: MutableList<DialogflowV1Context>? = null,
        var resetContexts: Boolean? = null,
        var parameters: MutableList<GoogleCloudDialogflowV2IntentParameter>? = null,
        var messages: MutableList<GoogleCloudDialogflowV2IntentMessage>? = null,
        var defaultResponsePlatforms: MutableList<GoogleCloudDialogflowV2IntentDefaultResponsePlatforms>? = null,
        var rootFollowupIntentName: String? = null,
        var parentFollowupIntentName: String? = null,
        var followupIntentInfo: MutableList<GoogleCloudDialogflowV2IntentFollowupIntentInfo>? = null
)

data class GoogleCloudDialogflowV2IntentFollowupIntentInfo(
        var followupIntentName: String? = null,
        var parentFollowupIntentName: String? = null
)

data class GoogleCloudDialogflowV2IntentMessage(
        var text: GoogleCloudDialogflowV2IntentMessageText? = null,
        var image: GoogleCloudDialogflowV2IntentMessageImage? = null,
        var quickReplies: GoogleCloudDialogflowV2IntentMessageQuickReplies? = null,
        var card: GoogleCloudDialogflowV2IntentMessageCard? = null,
        var payload: ApiClientObjectMap<Any>? = null,
        var simpleResponses: GoogleCloudDialogflowV2IntentMessageSimpleResponses? = null,
        var basicCard: GoogleCloudDialogflowV2IntentMessageBasicCard? = null,
        var suggestions: GoogleCloudDialogflowV2IntentMessageSuggestions? = null,
        var linkOutSuggestion: GoogleCloudDialogflowV2IntentMessageLinkOutSuggestion? = null,
        var listSelect: GoogleCloudDialogflowV2IntentMessageListSelect? = null,
        var carouselSelect: GoogleCloudDialogflowV2IntentMessageCarouselSelect? = null,
        var platform: GoogleCloudDialogflowV2IntentMessagePlatform? = null
)

data class GoogleCloudDialogflowV2IntentMessageBasicCard(
    var title: String? = null,
    var subtitle: String? = null,
    var formattedText: String? = null,
    var image: GoogleCloudDialogflowV2IntentMessageImage? = null,
    var buttons: MutableList<GoogleCloudDialogflowV2IntentMessageBasicCardButton>? = null
)

data class GoogleCloudDialogflowV2IntentMessageBasicCardButton(
    var title: String? = null,
    var openUriAction: GoogleCloudDialogflowV2IntentMessageBasicCardButtonOpenUriAction? = null
)

data class GoogleCloudDialogflowV2IntentMessageBasicCardButtonOpenUriAction(
    var uri: String? = null
)

data class GoogleCloudDialogflowV2IntentMessageCard(
    var title: String? = null,
    var subtitle: String? = null,
    var imageUri: String? = null,
    var buttons: MutableList<GoogleCloudDialogflowV2IntentMessageCardButton>? = null
)

data class GoogleCloudDialogflowV2IntentMessageCardButton (
    var text: String? = null,
    var postback: String? = null
)

data class GoogleCloudDialogflowV2IntentMessageCarouselSelect(
    var items: MutableList<GoogleCloudDialogflowV2IntentMessageCarouselSelectItem>? = null
)

data class GoogleCloudDialogflowV2IntentMessageCarouselSelectItem(
    var info: GoogleCloudDialogflowV2IntentMessageSelectItemInfo? = null,
    var title: String? = null,
    var description: String? = null,
    var image: GoogleCloudDialogflowV2IntentMessageImage? = null
)

data class GoogleCloudDialogflowV2IntentMessageImage(
    var imageUri: String? = null,
    var accessibilityText: String? = null
)

data class GoogleCloudDialogflowV2IntentMessageLinkOutSuggestion(
    var destinationName: String? = null,
    var uri: String? = null
)

data class GoogleCloudDialogflowV2IntentMessageListSelect(
    var title: String? = null,
    var items: MutableList<GoogleCloudDialogflowV2IntentMessageListSelectItem>? = null
)

data class GoogleCloudDialogflowV2IntentMessageListSelectItem(
    var info: GoogleCloudDialogflowV2IntentMessageSelectItemInfo? = null,
    var title: String? = null,
    var description: String? = null,
    var image: GoogleCloudDialogflowV2IntentMessageImage? = null
)

data class GoogleCloudDialogflowV2IntentMessageQuickReplies(
    var title: String? = null,
    var quickReplies: MutableList<String>? = null
)

data class GoogleCloudDialogflowV2IntentMessageSelectItemInfo(
    var key: String? = null,
    var synonyms: MutableList<String>? = null
)

data class GoogleCloudDialogflowV2IntentMessageSimpleResponse(
    var textToSpeech: String? = null,
    var ssml: String? = null,
    var displayText: String? = null
)

data class GoogleCloudDialogflowV2IntentMessageSimpleResponses(
    var simpleResponses: MutableList<GoogleCloudDialogflowV2IntentMessageSimpleResponse>? = null
)

data class GoogleCloudDialogflowV2IntentMessageSuggestion(
    var title: String? = null
)

data class GoogleCloudDialogflowV2IntentMessageSuggestions(
    var suggestions: MutableList<GoogleCloudDialogflowV2IntentMessageSuggestion>? = null
)

data class GoogleCloudDialogflowV2IntentMessageText(
        var text: MutableList<String>? = null
)

data class GoogleCloudDialogflowV2IntentParameter(
    var name: String? = null,
    var displayName: String? = null,
    var value: String? = null,
    var defaultValue: String? = null,
    var entityTypeDisplayName: String? = null,
    var mandatory: Boolean? = null,
    var prompts: MutableList<String>? = null,
    var isList: Boolean? = null
)

data class GoogleCloudDialogflowV2IntentTrainingPhrase (
    var name: String? = null,
    var type: GoogleCloudDialogflowV2IntentTrainingPhraseType? = null,
    var parts: MutableList<GoogleCloudDialogflowV2IntentTrainingPhrasePart>? = null,
    var timesAddedCount: Int? = null
)

data class GoogleCloudDialogflowV2IntentTrainingPhrasePart (
    var text: String? = null,
    var entityType: String? = null,
    var alias: String? = null,
    var userDefined: Boolean? = null
)

data class GoogleCloudDialogflowV2OriginalDetectIntentRequest(
        var source: String? = null,
        var payload: GoogleActionsV2AppRequest? = null
//    var payload: ApiClientObjectMap<Any>
)

data class GoogleCloudDialogflowV2QueryResult(
        var queryText: String? = null,
        var languageCode: String? = null,
        var speechRecognitionConfidence: Int? = null,
        var action: String? = null,
        var parameters: DialogflowV1Parameters? = null,
//    var parameters: ApiClientObjectMap<Any>
        var allRequiredParamsPresent: Boolean? = null,
        var fulfillmentText: String? = null,
        var fulfillmentMessages: MutableList<GoogleCloudDialogflowV2IntentMessage>? = null,
        var webhookSource: String? = null,
        var webhookPayload: ApiClientObjectMap<Any>? = null,
        var outputContexts: MutableList<GoogleCloudDialogflowV2Context>? = null,
        var intent: GoogleCloudDialogflowV2Intent? = null,
        var intentDetectionConfidence: Float? = null,
        var diagnosticInfo: ApiClientObjectMap<Any>? = null
)

data class GoogleCloudDialogflowV2WebhookRequest(
        var session: String? = null,
        var responseId: String? = null,
        var queryResult: GoogleCloudDialogflowV2QueryResult? = null,
        var originalDetectIntentRequest: GoogleCloudDialogflowV2OriginalDetectIntentRequest? = null
)

data class GoogleCloudDialogflowV2WebhookResponse(
        var fulfillmentText: String? = null,
        var fulfillmentMessages: MutableList<GoogleCloudDialogflowV2IntentMessage>? = null,
        var source: String? = null,
        var payload: Data? = null,
//    var payload: ApiClientObjectMap<Any>? = null,
        var outputContexts: MutableList<GoogleCloudDialogflowV2Context>? = null,
        var followupEventInput: GoogleCloudDialogflowV2EventInput? = null)
