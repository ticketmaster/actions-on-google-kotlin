package actions.service.actionssdk.conversation.response

import actions.service.actionssdk.api.GoogleActionsV2DevicesAndroidApp
import actions.service.actionssdk.api.GoogleActionsV2UiElementsOpenUrlAction
import actions.service.actionssdk.api.GoogleActionsV2UiElementsOpenUrlActionUrlTypeHint

interface OpenUrlActionOptions {
    var url: String?
}

data class OpenUrlAction(override var androidApp: GoogleActionsV2DevicesAndroidApp? = null,
                         override var url: String? = null,
                         override var urlTypeHint: GoogleActionsV2UiElementsOpenUrlActionUrlTypeHint? = null) : GoogleActionsV2UiElementsOpenUrlAction {

    constructor(options: OpenUrlActionOptions? = null) : this(
            url = options?.url)
}
