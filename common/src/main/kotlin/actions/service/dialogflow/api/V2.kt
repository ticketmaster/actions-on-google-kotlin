package actions.service.dialogflow.api

import actions.ApiClientObjectMap

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
    var parameters: ApiClientObjectMap<Any>
)

interface GoogleCloudDialogflowV2EventInput {
    var name: String
    var parameters: ApiClientObjectMap<Any>
    var languageCode: String
}

interface GoogleCloudDialogflowV2Intent {
    var name: String
    var displayName: String
    var webhookState: GoogleCloudDialogflowV2IntentWebhookState
    var priority: Int
    var isFallback: Boolean
    var mlDisabled: Boolean
    var inputContextNames: MutableList<String>
    var events: MutableList<String>
    var trainingPhrases: MutableList<GoogleCloudDialogflowV2IntentTrainingPhrase>
    var action: String
    var outputContexts: MutableList<DialogflowV1Context>
    var resetContexts: Boolean
    var parameters: MutableList<GoogleCloudDialogflowV2IntentParameter>
    var messages: MutableList<GoogleCloudDialogflowV2IntentMessage>
    var defaultResponsePlatforms: MutableList<GoogleCloudDialogflowV2IntentDefaultResponsePlatforms>
    var rootFollowupIntentName: String
    var parentFollowupIntentName: String
    var followupIntentInfo: MutableList<GoogleCloudDialogflowV2IntentFollowupIntentInfo>
}

interface GoogleCloudDialogflowV2IntentFollowupIntentInfo {
    var followupIntentName: String
    var parentFollowupIntentName: String
}

interface GoogleCloudDialogflowV2IntentMessage {
    var text: GoogleCloudDialogflowV2IntentMessageText
    var image: GoogleCloudDialogflowV2IntentMessageImage
    var quickReplies: GoogleCloudDialogflowV2IntentMessageQuickReplies
    var card: GoogleCloudDialogflowV2IntentMessageCard
    var payload: ApiClientObjectMap<Any>
    var simpleResponses: GoogleCloudDialogflowV2IntentMessageSimpleResponses
    var basicCard: GoogleCloudDialogflowV2IntentMessageBasicCard
    var suggestions: GoogleCloudDialogflowV2IntentMessageSuggestions
    var linkOutSuggestion: GoogleCloudDialogflowV2IntentMessageLinkOutSuggestion
    var listSelect: GoogleCloudDialogflowV2IntentMessageListSelect
    var carouselSelect: GoogleCloudDialogflowV2IntentMessageCarouselSelect
    var platform: GoogleCloudDialogflowV2IntentMessagePlatform
}

interface GoogleCloudDialogflowV2IntentMessageBasicCard {
    var title: String
    var subtitle: String
    var formattedText: String
    var image: GoogleCloudDialogflowV2IntentMessageImage
    var buttons: MutableList<GoogleCloudDialogflowV2IntentMessageBasicCardButton>
}

interface GoogleCloudDialogflowV2IntentMessageBasicCardButton {
    var title: String
    var openUriAction: GoogleCloudDialogflowV2IntentMessageBasicCardButtonOpenUriAction
}

interface GoogleCloudDialogflowV2IntentMessageBasicCardButtonOpenUriAction {
    var uri: String
}

interface GoogleCloudDialogflowV2IntentMessageCard {
    var title: String
    var subtitle: String
    var imageUri: String
    var buttons: MutableList<GoogleCloudDialogflowV2IntentMessageCardButton>
}

interface GoogleCloudDialogflowV2IntentMessageCardButton {
    var text: String
    var postback: String
}

interface GoogleCloudDialogflowV2IntentMessageCarouselSelect {
    var items: MutableList<GoogleCloudDialogflowV2IntentMessageCarouselSelectItem>
}

interface GoogleCloudDialogflowV2IntentMessageCarouselSelectItem {
    var info: GoogleCloudDialogflowV2IntentMessageSelectItemInfo
    var title: String
    var description: String
    var image: GoogleCloudDialogflowV2IntentMessageImage
}

interface GoogleCloudDialogflowV2IntentMessageImage {
    var imageUri: String
    var accessibilityText: String
}

interface GoogleCloudDialogflowV2IntentMessageLinkOutSuggestion {
    var destinationName: String
    var uri: String
}

interface GoogleCloudDialogflowV2IntentMessageListSelect {
    var title: String
    var items: MutableList<GoogleCloudDialogflowV2IntentMessageListSelectItem>
}

interface GoogleCloudDialogflowV2IntentMessageListSelectItem {
    var info: GoogleCloudDialogflowV2IntentMessageSelectItemInfo
    var title: String
    var description: String
    var image: GoogleCloudDialogflowV2IntentMessageImage
}

interface GoogleCloudDialogflowV2IntentMessageQuickReplies {
    var title: String
    var quickReplies: MutableList<String>
}

interface GoogleCloudDialogflowV2IntentMessageSelectItemInfo {
    var key: String
    var synonyms: MutableList<String>
}

interface GoogleCloudDialogflowV2IntentMessageSimpleResponse {
    var textToSpeech: String
    var ssml: String
    var displayText: String
}

interface GoogleCloudDialogflowV2IntentMessageSimpleResponses {
    var simpleResponses: MutableList<GoogleCloudDialogflowV2IntentMessageSimpleResponse>
}

interface GoogleCloudDialogflowV2IntentMessageSuggestion {
    var title: String
}

interface GoogleCloudDialogflowV2IntentMessageSuggestions {
    var suggestions: MutableList<GoogleCloudDialogflowV2IntentMessageSuggestion>
}

interface GoogleCloudDialogflowV2IntentMessageText {
    var text: MutableList<String>
}

interface GoogleCloudDialogflowV2IntentParameter {
    var name: String
    var displayName: String
    var value: String
    var defaultValue: String
    var entityTypeDisplayName: String
    var mandatory: Boolean
    var prompts: MutableList<String>
    var isList: Boolean
}

interface GoogleCloudDialogflowV2IntentTrainingPhrase {
    var name: String
    var type: GoogleCloudDialogflowV2IntentTrainingPhraseType
    var parts: MutableList<GoogleCloudDialogflowV2IntentTrainingPhrasePart>
    var timesAddedCount: Int
}

interface GoogleCloudDialogflowV2IntentTrainingPhrasePart {
    var text: String
    var entityType: String
    var alias: String
    var userDefined: Boolean
}

interface GoogleCloudDialogflowV2OriginalDetectIntentRequest {
    var source: String
    var payload: ApiClientObjectMap<Any>
}

interface GoogleCloudDialogflowV2QueryResult {
    var queryText: String
    var languageCode: String
    var speechRecognitionConfidence: Int
    var action: String
    var parameters: ApiClientObjectMap<Any>
    var allRequiredParamsPresent: Boolean
    var fulfillmentText: String
    var fulfillmentMessages: MutableList<GoogleCloudDialogflowV2IntentMessage>
    var webhookSource: String
    var webhookPayload: ApiClientObjectMap<Any>
    var outputContexts: MutableList<DialogflowV1Context>
    var intent: GoogleCloudDialogflowV2Intent
    var intentDetectionConfidence: Int
    var diagnosticInfo: ApiClientObjectMap<Any>
}

interface GoogleCloudDialogflowV2WebhookRequest {
    var session: String
    var responseId: String
    var queryResult: GoogleCloudDialogflowV2QueryResult
    var originalDetectIntentRequest: GoogleCloudDialogflowV2OriginalDetectIntentRequest
}

data class GoogleCloudDialogflowV2WebhookResponse(
    var fulfillmentText: String? = null,
    var fulfillmentMessages: MutableList<GoogleCloudDialogflowV2IntentMessage>? = null,
    var source: String? = null,
    var payload: ApiClientObjectMap<Any>? = null,
    var outputContexts: MutableList<DialogflowV1Context>? = null,
    var followupEventInput: GoogleCloudDialogflowV2EventInput? = null)
