package actions.service.actionssdk.conversation.question.option

import actions.service.actionssdk.api.GoogleActionsV2OptionInfo
import actions.service.actionssdk.api.GoogleActionsV2UiElementsCarouselSelectCarouselItem
import actions.service.actionssdk.api.GoogleActionsV2UiElementsCarouselSelectImageDisplayOptions

/**
 * Carousel class in Response.kt file because it is a part of the Response sealed class
 */

data class CarouselOptions(
        /**
         * Sets the display options for the images in this carousel.
         * @public
         */
        var display: GoogleActionsV2UiElementsCarouselSelectImageDisplayOptions? = null,

        /**
         * List of 2-20 items to show in this carousel. Required.
         * @public
         */
        var items: MutableMap<String, OptionItem>? = null
)


fun MutableMap<String, OptionItem>?.toGoogleActionsV2CarouselItem(): MutableList<GoogleActionsV2UiElementsCarouselSelectCarouselItem> {
    return this?.map {
        GoogleActionsV2UiElementsCarouselSelectCarouselItem(
                title = it.value.title,
                description = it.value.description,
                image = it.value.image,
                optionInfo = GoogleActionsV2OptionInfo(key = it.key, synonyms = it.value.synonyms)
        )
    }?.toMutableList() ?: mutableListOf()
}


