package actions.service.actionssdk.conversation.response.card

import actions.service.actionssdk.api.GoogleActionsV2UiElementsButton
import actions.service.actionssdk.api.GoogleActionsV2UiElementsOpenUrlAction
import actions.service.actionssdk.conversation.response.OpenUrlAction


/** @public */
interface ButtonOptions {
    /**
     * Text shown on the button.
     * @public
     */
    var title: String

    /**
     * String URL to open.
     * @public
     */
    var url: String?

    /**
     * Action to take when selected. Recommended to use the url property for simple web page url open.
     * @public
     */
    var action: GoogleActionsV2UiElementsOpenUrlAction?
}

/**
 * Card Button. Shown below cards. Open a URL when selected.
 * @public
 */
data class Button(override var openUrlAction: GoogleActionsV2UiElementsOpenUrlAction? = null,
                  override var title: String? = null) : GoogleActionsV2UiElementsButton {
    /**
     * @param options Button options
     * @public
     */
    constructor(options: ButtonOptions) : this(
            title = options.title,
            openUrlAction = when {
                options.url != null -> OpenUrlAction(url = options.url)
                options.action != null -> options.action
                else -> null
            })

}

