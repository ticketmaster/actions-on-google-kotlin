package actions.service.actionssdk

import actions.service.actionssdk.conversation.response.GoogleActionsV2RichResponseItem

fun <T> MutableList<T>.push(item: T): Unit =
        if (size == 0) {
            add(item)
            Unit
        } else {
            add(0, item)
        }

fun <T> MutableList<T>.push(vararg items: T): Unit =
        items.forEach {
            if (size == 0) {
                add(it)
                Unit
            } else {
                add(0, it)
            }
        }

fun MutableList<GoogleActionsV2RichResponseItem>.push(init: GoogleActionsV2RichResponseItem.() -> Unit): Unit {
    val item = GoogleActionsV2RichResponseItem()
    item.init()
    push(item)
}

