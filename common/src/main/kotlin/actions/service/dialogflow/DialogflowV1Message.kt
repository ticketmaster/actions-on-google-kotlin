package actions.service.dialogflow

import actions.ApiClientObjectMap
import actions.framework.JsonObject
import actions.service.dialogflow.api.DialogflowV1Button

data class Message(
        var type: String = "",
        var platform: String? = "",
        var payload: String? = "",
        var items: MutableList<DialogflowV1MessageOptionItem>? = null,
        var speech: String? = null,
        var textToSpeech: String? = null,
        var displayText: String? = null,
        var suggestions: MutableList<DialogflowV1MessageSuggestion>? = null,
        var destinationName: String? = null,
        var url: String? = null,
        var title: String? = null,
        var subtitle: String? = null,
        var formattedText: String? = null,
        var image: DialogflowV1MessageImage? = null,
        var buttons: MutableList<DialogflowV1MessageBasicCardButton>? = null)

sealed class DialogflowV1Message : DialogflowV1BaseMessage() {}

data class DialogflowV1MessageText(var speech: String? = null) : DialogflowV1BaseMessage()

open class DialogflowV1BaseMessage(
        var platform: String? = null, //"facebook" | "kik" | "line" | "skype" | "slack" | "telegram" | "viber"
        var type: String? = null // <-- investigate Int/String - what is actually returned? both? look in Incoming.kt for examples
)


data class DialogflowV1MessageImage(var imageUrl: String? = null) : DialogflowV1BaseMessage()

data class DialogflowV1MessageCard(
        var buttons: MutableList<DialogflowV1Button>? = null,
        var imageUrl: String? = null,
        var subtitle: String? = null,
        var title: String? = null
) : DialogflowV1BaseMessage(type = "1") // 1

data class DialogflowV1MessageQuickReplies(
        var replies: MutableList<String>? = null,
        var title: String? = null
) : DialogflowV1BaseMessage(type = "2") //<2> {

data class DialogflowV1MessageCustomPayload(
        var payload: JsonObject? = null
) : DialogflowV1BaseMessage(type = "4") //<4> {


open class DialogflowV1BaseGoogleMessage(
        var platform: String? = null, //= "Google"
        var type: Int? = null)

data class DialogflowV1MessageSimpleResponse(
        var displayText: String? = null,
        var textToSpeech: String? = null
) : DialogflowV1BaseGoogleMessage() //{//<"simple_response"> {

data class DialogflowV1MessageBasicCardButtonAction(
        var url: String? = null
)

data class DialogflowV1MessageBasicCardButton(
        var openUrlAction: DialogflowV1MessageBasicCardButtonAction? = null,
        var title: String? = null
)

data class DialogflowV1MessageBasicCard(
        var buttons: MutableList<DialogflowV1MessageBasicCardButton>? = null,
        var formattedText: String? = null,
        var image: DialogflowV1MessageImage? = null,
        var subtitle: String? = null,
        var title: String? = null
) : DialogflowV1BaseGoogleMessage()

interface DialogflowV1MessageOptionInfo {
    var key: String
    var synonyms: MutableList<String>
}

data class DialogflowV1MessageOptionItem(
        var description: String? = null,
        var image: DialogflowV1MessageImage? = null,
        var optionInfo: DialogflowV1MessageOptionInfo? = null,
        var title: String? = null
)

data class DialogflowV1MessageList(
        var items: MutableList<DialogflowV1MessageOptionItem>? = null,
        var title: String? = null
) : DialogflowV1BaseGoogleMessage()

data class DialogflowV1MessageSuggestion(
        var title: String? = null
)

data class DialogflowV1MessageSuggestions(
        var suggestions: MutableList<DialogflowV1MessageSuggestion>? = null
) : DialogflowV1BaseGoogleMessage()

data class DialogflowV1MessageCarousel(
        var items: MutableList<DialogflowV1MessageOptionItem>? = null
) : DialogflowV1BaseGoogleMessage()

data class DialogflowV1MessageLinkOut(
        var destinationName: String? = null,
        var url: String? = null
) : DialogflowV1BaseGoogleMessage()

data class DialogflowV1MessageGooglePayload(var payload: ApiClientObjectMap<Any>? = null) : DialogflowV1BaseGoogleMessage()



