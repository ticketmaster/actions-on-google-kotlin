package actions.service.actionssdk.conversation.question.option

import actions.service.actionssdk.api.GoogleActionsV2OptionInfo
import actions.service.actionssdk.api.GoogleActionsV2UiElementsListSelectListItem

data class ListOptions(var title: String? = null,
                       var items: MutableList<GoogleActionsV2UiElementsListSelectListItem>? = null)

data class ListOptions2(var title: String? = null,
                        var items: MutableMap<String, OptionItem>? = null)

fun item(name: String, init: OptionItem.() -> Unit): Pair<String, OptionItem> {
        val item = OptionItem()
        item.init()
        return name to item
}

fun MutableMap<String, OptionItem>?.toGoogleActionsV2ListItem(): MutableList<GoogleActionsV2UiElementsListSelectListItem> {
    return this?.map {
        GoogleActionsV2UiElementsListSelectListItem(
                title = it.value.title,
                description = it.value.description,
                image = it.value.image,
                optionInfo = GoogleActionsV2OptionInfo(key = it.key, synonyms = it.value.synonyms)
        )
    }?.toMutableList() ?: mutableListOf()
}

