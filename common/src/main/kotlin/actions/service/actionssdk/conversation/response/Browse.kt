package actions.service.actionssdk.conversation.response

import actions.service.actionssdk.api.*

data class BrowseCarouselOptions(
        /**
         * Sets the display options for the images in this carousel.
         * @public
         */
        var display: GoogleActionsV2UiElementsCarouselBrowseImageDisplayOptions? = null,

        /**
         * List of 2-20 items to show in this carousel. Required.
         * @public
         */
        var items: MutableList<GoogleActionsV2UiElementsCarouselBrowseItem>? = null
)

data class BrowseCarouselItemOptions(
        /**
         * Title of the option item. Required.
         * @public
         */
        var title: String? = null,

        /**
         * The URL of the link opened by clicking the BrowseCarouselItem. Optional.
         * @public
         */
        var url: String? = null,

        /**
         * Description text of the item. Optional.
         * @public
         */
        var description: String? = null,

        /**
         * Footer text of the item. Optional.
         * @public
         */
        var footer: String? = null,

        /**
         * Image to show on item. Optional.
         * @public
         */
        var image: GoogleActionsV2UiElementsImage? = null
)

/**
 * Class for initializing and constructing BrowseCarousel Items
 * @public
 */
class BrowseCarouselItem(override var description: String? = null,
                         override var footer: String? = null,
                         override var image: GoogleActionsV2UiElementsImage? = null,
                         override var openUrlAction: GoogleActionsV2UiElementsOpenUrlAction? = null,
                         override var title: String? = null) : GoogleActionsV2UiElementsCarouselBrowseItem {
    /**
     * @param options BrowseCarouselItem options
     * @public
     */
    constructor(init: BrowseCarouselItemOptions.() -> Unit) : this() {
        val options = BrowseCarouselItemOptions()
        options.init()
        title = options.title
        openUrlAction = GoogleActionsV2UiElementsOpenUrlAction(
                url = options.url)
        description = options.description
        footer = options.footer
        image = options.image
    }
}

//const isOptions = (
//options: BrowseCarouselOptions | Api.GoogleActionsV2UiElementsCarouselBrowseItem,
//): options is BrowseCarouselOptions => {
//    const test = options as BrowseCarouselOptions
//            return Array.isArray(test.items)
//}


