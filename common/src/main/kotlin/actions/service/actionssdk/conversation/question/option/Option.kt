package actions.service.actionssdk.conversation.question.option

import actions.service.actionssdk.api.GoogleActionsV2UiElementsCarouselSelectCarouselItem
import actions.service.actionssdk.api.GoogleActionsV2UiElementsImage
import actions.service.actionssdk.conversation.response.Image


/** @public */
typealias OptionArgument = String

class OptionItems<TOptionItem> {
    /**
     * key: Unique string ID for this option.
     * @public
     */
//    [key: string]: TOptionItem
}

/**
 * Option item. Used in actions.intent.OPTION intent.
 * @public
 */
data class OptionItem(

    /**
     * Synonyms that can be used by the user to indicate this option if they do not use the key.
     * @public
     */
    var synonyms: MutableList<String>? = null,

    /**
     * Name of the item.
     * @public
     */
    var title: String? = null,

    /**
     * Optional text describing the item.
     * @public
     */
    var description: String? = null,

    /**
     * Square image to show for this item.
     * @public
     */
    var image: Image? = null
)

/** @hidden */
typealias ApiOptionItem = GoogleActionsV2UiElementsCarouselSelectCarouselItem

/** @hidden */
fun <T>convert(items: OptionItems<T>): Unit? {//=> Object.keys(items).map(key => {
//    const value = items[key]
//    if (typeof value === 'string') {
//        const item: ApiOptionItem = {
//            title: value,
//            optionInfo: {
//            key,
//        },
//        }
//        return item
//    }
//    const { description, image, synonyms, title } = value
//    const item: ApiOptionItem = {
//        optionInfo: {
//        key,
//        synonyms,
//    },
//        description,
//        image,
//        title,
//    }
//    return item
//}
    return null
}
