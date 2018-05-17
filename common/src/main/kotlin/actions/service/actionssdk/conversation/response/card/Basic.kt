package actions.service.actionssdk.conversation.response.card

import actions.service.actionssdk.api.GoogleActionsV2UiElementsBasicCardImageDisplayOptions
import actions.service.actionssdk.api.GoogleActionsV2UiElementsButton
import actions.service.actionssdk.api.GoogleActionsV2UiElementsImage

interface BasicCardOptions {

    var title: String?

    var subtitle: String?

    var text: String?

    var image: GoogleActionsV2UiElementsImage?

    var buttons: MutableList<GoogleActionsV2UiElementsButton>?

    var display: GoogleActionsV2UiElementsBasicCardImageDisplayOptions?
}

