package actions.service.actionssdk.conversation.response

import actions.service.actionssdk.api.GoogleActionsV2UiElementsLinkOutSuggestion


interface RichResponseOptions {
    /**
     * Ordered list of either SimpleResponse objects or BasicCard objects.
     * First item must be SimpleResponse. There can be at most one card.
     * @public
     */
    var items: MutableList<RichResponseItem>?

    /**
     * Ordered list of text suggestions to display. Optional.
     * @public
     */
    var suggestions: MutableList<String>? //TODO support:   | Suggestions

    /**
     * Link Out Suggestion chip for this rich response. Optional.
     * @public
     */
    var link: GoogleActionsV2UiElementsLinkOutSuggestion?
}

//const isOptions = (
//options: RichResponseOptions | RichResponseItem,
//): options is RichResponseOptions => {
//    const test = options as RichResponseOptions
//            return typeof test.link === 'object' ||
//    Array.isArray(test.items) ||
//            Array.isArray(test.suggestions) ||
//            test.suggestions instanceof Suggestions
//}


