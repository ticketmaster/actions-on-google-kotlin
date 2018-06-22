package actions.service.dialogflow

import actions.ApiClientObjectMap
import actions.framework.JsonObject
import actions.service.dialogflow.api.DialogflowV1Button

sealed class DialogflowV1Message:DialogflowV1BaseMessage {}

interface DialogflowV1MessageText: DialogflowV1BaseMessage {//<0>
    var speech: String
}

interface DialogflowV1BaseMessage {
    var platform: String? //"facebook" | "kik" | "line" | "skype" | "slack" | "telegram" | "viber"
    var type: String? // <-- investigate Int/String - what is actually returned? both? look in Incoming.kt for examples
}


interface DialogflowV1MessageImage: DialogflowV1BaseMessage {//<3> {
    var imageUrl: String
}

interface DialogflowV1MessageCard: DialogflowV1BaseMessage {//<1> {
    var buttons: MutableList<DialogflowV1Button>
    var imageUrl: String
    var subtitle: String
    var title: String
}

interface DialogflowV1MessageQuickReplies: DialogflowV1BaseMessage {//<2> {
    var replies: MutableList<String>
    var title: String
}

interface DialogflowV1MessageCustomPayload: DialogflowV1BaseMessage {//<4> {
    var payload: JsonObject
}


interface DialogflowV1BaseGoogleMessage {
    var platform: String //= "Google"
    var type: Int
}

interface DialogflowV1MessageSimpleResponse: DialogflowV1BaseGoogleMessage {//<"simple_response"> {
    var displayText: String
    var textToSpeech: String
}

interface DialogflowV1MessageBasicCardButtonAction {
    var url: String
}

interface DialogflowV1MessageBasicCardButton {
    var openUrlAction: DialogflowV1MessageBasicCardButtonAction
    var title: String
}

interface DialogflowV1MessageBasicCard: DialogflowV1BaseGoogleMessage {
    var buttons: MutableList<DialogflowV1MessageBasicCardButton>
    var formattedText: String
    var image: DialogflowV1MessageImage
    var subtitle: String
    var title: String
}

interface DialogflowV1MessageOptionInfo {
    var key: String
    var synonyms: MutableList<String>
}

interface DialogflowV1MessageOptionItem {
    var description: String
    var image: DialogflowV1MessageImage
    var optionInfo: DialogflowV1MessageOptionInfo
    var title: String
}

interface DialogflowV1MessageList: DialogflowV1BaseGoogleMessage {
    var items: MutableList<DialogflowV1MessageOptionItem>
    var title: String
}

interface DialogflowV1MessageSuggestion {
    var title: String
}

interface DialogflowV1MessageSuggestions: DialogflowV1BaseGoogleMessage {
    var suggestions: MutableList<DialogflowV1MessageSuggestion>
}

interface DialogflowV1MessageCarousel: DialogflowV1BaseGoogleMessage {
    var items: MutableList<DialogflowV1MessageOptionItem>
}

interface DialogflowV1MessageLinkOut: DialogflowV1BaseGoogleMessage {
    var destinationName: String
    var url: String
}

interface DialogflowV1MessageGooglePayload: DialogflowV1BaseGoogleMessage {
    var payload: ApiClientObjectMap<Any>
}

