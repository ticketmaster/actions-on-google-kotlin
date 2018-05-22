package actions.service.actionssdk.conversation.response.card

import actions.service.actionssdk.api.GoogleActionsV2UiElementsBasicCardImageDisplayOptions
import actions.service.actionssdk.api.GoogleActionsV2UiElementsButton
import actions.service.actionssdk.api.GoogleActionsV2UiElementsImage

data class BasicCardOptions(
    var title: String? = null,

    var subtitle: String? = null,

    var text: String? = null,

    var image: GoogleActionsV2UiElementsImage? = null,

    var buttons: MutableList<GoogleActionsV2UiElementsButton>? = null,

    var display: GoogleActionsV2UiElementsBasicCardImageDisplayOptions? = null)

