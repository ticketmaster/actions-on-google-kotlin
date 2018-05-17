package jvm

import actions.service.actionssdk.actionssdk
import actions.service.actionssdk.api.GoogleActionsV2UiElementsOpenUrlAction
import actions.service.actionssdk.conversation.response.BasicCard
import actions.service.actionssdk.conversation.response.card.Button
import actions.service.actionssdk.conversation.response.card.ButtonOptions

data class UserStorage(var name: String? = null)
data class ConversationData(var lastResponse: String? = null)

fun servlet() {
    val app = actionssdk<ConversationData, UserStorage>()

    app.intent("test") { conv, arg, g ->
        conv.ask("Can you hear me?")
        conv.ask(BasicCard(
                title = "This is a title",
                buttons = mutableListOf(Button(object : ButtonOptions {
                    override var title = "This is a btn title"
                    override var url: String? = "https://google.com"
                    override var action: GoogleActionsV2UiElementsOpenUrlAction? = null
                })))
        )
        conv.response()
        conv.close(BasicCard())
    }
}
