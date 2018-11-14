package actions.service.actionssdk.conversation.response

import actions.ApiClientObjectMap
import actions.ProtoAny
import actions.service.actionssdk.api.*
import actions.service.actionssdk.conversation.InputValueSpec
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.question.option.*
import actions.service.actionssdk.conversation.response.card.BasicCardOptions
import actions.service.actionssdk.conversation.response.card.TableOptions
import actions.service.actionssdk.push

sealed class Response


/**
 * Sealed class of all RichResponseItem types
 * Equivalent to TypeScript Union:
 *      type RichResponseItem =
SimpleResponse |
BasicCard |
Table |
BrowseCarousel |
MediaResponse |
OrderUpdate |
LinkOutSuggestion |
Api.GoogleActionsV2RichResponseItem

 *  String type is handled in separate functions
 */
sealed class RichResponseItem : Response()


/**
 * Class for initializing and constructing Rich Responses with chainable interface.
 * @public
 */
class RichResponse(override var items: MutableList<GoogleActionsV2RichResponseItem>? = null,
                   override var linkOutSuggestion: GoogleActionsV2UiElementsLinkOutSuggestion? = null,
                   override var suggestions: MutableList<GoogleActionsV2UiElementsSuggestion>? = null) : GoogleActionsV2RichResponse, Response() {
    /**
     * @param options RichResponse options
     * @public
     */
    constructor(options: RichResponseOptions) : this() {
        this.items = mutableListOf()
        if (options.items != null && options.items?.isNotEmpty() == true) {
            add(*options.items!!.toTypedArray())
        }
        val link = options.link
        val suggestions = options.suggestions
        this.linkOutSuggestion = link
        if (suggestions != null) {
            this.addSuggestion(*suggestions.toTypedArray())
        }
    }

    /**
     * @param items RichResponse items
     * @public
     */
    constructor(items: MutableList<RichResponseItem>) : this() {
        this.items = mutableListOf()
        TODO("IS this needed?")
    }

    /**
     * @param items RichResponse items
     * @public
     */
    constructor(vararg items: RichResponseItem) : this() {
        this.items = mutableListOf()
        this.add(*items)
    }

//    constructor(options?: RichResponseOptions | RichResponseItem[] | RichResponseItem,
//    ...items: RichResponseItem[],
//    )
//    {
//        this.items = []
//        if (!options) {
//            return
//        }
//        if (Array.isArray(options)) {
//            this.add(... options)
//            return
//        }
//        if (isOptions(options)) {
//            if (options.items) {
//                this.add(... options . items)
//            }
//            const { link, suggestions } = options
//            this.linkOutSuggestion = link
//            if (suggestions) {
//                if (Array.isArray(suggestions)) {
//                    this.addSuggestion(... suggestions)
//                } else {
//                    this.addSuggestion(suggestions)
//                }
//            }
//            return
//        }
//        this.add(options, ... items)
//    }

    constructor(options: RichResponseOptions? = null, vararg items: RichResponseItem) : this() {

    }

    constructor(options: MutableList<RichResponseItem>? = null, vararg items: RichResponseItem) : this() {

    }

    constructor(options: RichResponseItem? = null, vararg items: RichResponseItem) : this() {

    }

    fun add(vararg items: String) {
        items.forEach {
            this.add(SimpleResponse(it))
        }
    }

    /**
     * Add a RichResponse item
     * @public
     */
    fun add(vararg items: RichResponseItem): RichResponse {
        if (this.items == null) {
            this.items = mutableListOf()
        }
        items.forEach {
            when (it) {
//                is String -> { //Handled in fun add(vararg items: String)}

                is LinkOutSuggestion -> this.linkOutSuggestion = it

                is SimpleResponse -> this.items!!.add { simpleResponse = it }

                is BasicCard ->
                    this.items!!.add { basicCard = it }

                is Table ->
                    this.items!!.add { tableCard = it }


                is BrowseCarousel ->
                    this.items!!.add { carouselBrowse = it }


                is MediaResponse ->
                    this.items!!.add { mediaResponse = it }

                is OrderUpdate -> {
                    this.items!!.add { structuredResponse = GoogleActionsV2StructuredResponse(orderUpdate = it) }
                }
            }
        }
        return this
    }

    fun addSuggestion(vararg suggestions: String): RichResponse {
        if (this.suggestions == null) {
            this.suggestions = mutableListOf()
        }
        suggestions.forEach {
            this.suggestions?.push(GoogleActionsV2UiElementsSuggestion(it))
        }
        return this
    }

    /**
     * Adds a single suggestion or list of suggestions to list of items.
     * @public
     */
    fun addSuggestion(vararg suggestions: Suggestions): RichResponse {
        suggestions.forEach {
            this.suggestions?.push(*it.suggestions.toTypedArray())
        }
        return this
    }

    /**
     * Adds a single suggestion or list of suggestions to list of items.
     * @public
     */
    fun addSuggestion(vararg suggestions: GoogleActionsV2UiElementsSuggestion): RichResponse {
        suggestions.forEach {
            this.suggestions?.push(it)
        }
        return this
    }
}

fun MutableList<GoogleActionsV2RichResponseItem>.add(init: GoogleActionsV2RichResponseItem.() -> Unit) {
    val item = GoogleActionsV2RichResponseItem()
    item.init()
    add(item)
}

/**
 * Image type shown on visual elements.
 * @public
 */
data class Image(override var accessibilityText: String? = null,
                 override var height: Int? = null,
                 override var url: String? = null,
                 override var width: Int? = null) : GoogleActionsV2UiElementsImage, Response() {
    /**
     * @param options Image options
     * @public
     */
    constructor(option: ImageOptions? = null) : this(url = option?.url,
            accessibilityText = option?.alt,
            height = option?.height,
            width = option?.width)

    constructor(init: ImageOptions.() -> Unit) : this({ val options = ImageOptions(url = "", alt = ""); options.init(); options }.invoke())
}


/**
 * Suggestions to show with response.
 * @public
 */
data class Suggestions(
        var suggestions: MutableList<GoogleActionsV2UiElementsSuggestion> = mutableListOf()) : Response() {

    /**
     * @param suggestions Texts of the suggestions.
     * @public
     */
    constructor(vararg suggestions: String) : this(suggestions = suggestions.map { GoogleActionsV2UiElementsSuggestion(title = it) }.toMutableList())

//    constructor(suggestions: Array<String>) : this(suggestions = suggestions.map { GoogleActionsV2UiElementsSuggestion(title = it) }.asReversed().toMutableList())

    fun add(vararg suggs: String): Suggestions {
        this.suggestions = suggs.map { GoogleActionsV2UiElementsSuggestion(title = it) }.toMutableList()
        return this
    }
}


/**
 * Class for initializing and constructing MediaObject
 * @public
 */
class MediaObject(override var contentUrl: String? = null,
                  override var description: String? = null,
                  override var icon: GoogleActionsV2UiElementsImage? = null,
                  override var largeImage: GoogleActionsV2UiElementsImage? = null,
                  override var name: String? = null) : GoogleActionsV2MediaObject, Response() {
    /**
     * @param options MediaObject options or just a string for the url
     * @public
     */
    constructor(init: MediaObjectOptions.() -> Unit) : this() {
        val options = MediaObjectOptions()
        options.init()
        contentUrl = options.url
        description = options.description
        icon = options.icon
        largeImage = options.image
        name = options.name
    }

    constructor(options: String) : this(contentUrl = options)
}

abstract class Question(intent: IntentEnum) : GoogleActionsV2ExpectedIntent, Response() {
    override var inputValueData: ProtoAny? = null

    override var intent: String? = null

    override var parameterName: String? = null

    init {
        this.intent = intent.value
    }

    fun _data(type: InputValueSpec, init: ProtoAny.() -> Unit) {
        if (inputValueData == null) {
            inputValueData = ProtoAny()
        }
        inputValueData?.`@type` = type.value
        inputValueData?.init()
    }
}

abstract class SoloQuestion(intent: IntentEnum) : Question(intent)


/**
 * Simple Response type.
 * @public
 */

data class SimpleResponse(override var displayText: String? = null,
                          override var ssml: String? = null,
                          override var textToSpeech: String? = null) : GoogleActionsV2SimpleResponse, RichResponseItem() {
    /**
     * @param options SimpleResponse options
     * @public
     */
    constructor(options: SimpleResponseOptions) : this(textToSpeech = options.speech,
            displayText = options.text)

    constructor(init: SimpleResponseOptions.() -> Unit) : this({ val options = SimpleResponseOptions(speech = ""); options.init(); options }.invoke())

    constructor(options: String? = null) : this(textToSpeech = options)
}


class BasicCard(override var buttons: MutableList<GoogleActionsV2UiElementsButton>? = null,
                override var formattedText: String? = null,
                override var image: GoogleActionsV2UiElementsImage? = null,
                override var imageDisplayOptions: GoogleActionsV2UiElementsBasicCardImageDisplayOptions? = null,
                override var subtitle: String? = null,
                override var title: String? = null) : GoogleActionsV2UiElementsBasicCard, RichResponseItem() {

    constructor(options: BasicCardOptions) : this(
            title = options.title,
            subtitle = options.subtitle,
            formattedText = options.text,
            image = options.image,
            buttons = options.buttons,
            imageDisplayOptions = options.display
    )

    constructor(init: BasicCardOptions.() -> Unit) : this({ val options = BasicCardOptions(); options.init();options }.invoke())
}


/**
 * Creates a Table card.
 *
 * @example
 * ```javascript
 *
 * // Simple table
 * conv.ask('Simple Response')
 * conv.ask(new Table({
 *   dividers: true,
 *   columns: ['header 1', 'header 2', 'header 3'],
 *   rows: [
 *     ['row 1 item 1', 'row 1 item 2', 'row 1 item 3'],
 *     ['row 2 item 1', 'row 2 item 2', 'row 2 item 3'],
 *   ],
 * }))
 *
 * // All fields
 * conv.ask('Simple Response')
 * conv.ask(new Table({
 *   title: 'Table Title',
 *   subtitle: 'Table Subtitle',
 *   image: new Image({
 *     url: 'https://avatars0.githubusercontent.com/u/23533486',
 *     alt: 'Actions on Google'
 *   }),
 *   columns: [
 *     {
 *       header: 'header 1',
 *       align: 'CENTER',
 *     },
 *     {
 *       header: 'header 2',
 *       align: 'LEADING',
 *     },
 *     {
 *       header: 'header 3',
 *       align: 'TRAILING',
 *     },
 *   ],
 *   rows: [
 *     {
 *       cells: ['row 1 item 1', 'row 1 item 2', 'row 1 item 3'],
 *       dividerAfter: false,
 *     },
 *     {
 *       cells: ['row 2 item 1', 'row 2 item 2', 'row 2 item 3'],
 *       dividerAfter: true,
 *     },
 *     {
 *       cells: ['row 3 item 1', 'row 3 item 2', 'row 3 item 3'],
 *     },
 *   ],
 *   buttons: new Button({
 *     title: 'Button Title',
 *     url: 'https://github.com/actions-on-google'
 *   }),
 * }))
 * ```
 *
 * @public
 */
data class Table(override var buttons: MutableList<GoogleActionsV2UiElementsButton>? = null,
                 override var columnProperties: MutableList<GoogleActionsV2UiElementsTableCardColumnProperties>? = null,
                 override var image: GoogleActionsV2UiElementsImage? = null,
                 override var rows: MutableList<GoogleActionsV2UiElementsTableCardRow>? = null,
                 override var subtitle: String? = null,
                 override var title: String? = null) : GoogleActionsV2UiElementsTableCard, RichResponseItem() {

    constructor(init: TableOptions.() -> Unit) : this(

//            rows = options.rows.map(row => Array.isArray(row) ? {
//        cells: row.map(text => ({ text })),
//        dividerAfter: options.dividers,
//    } as Api.GoogleActionsV2UiElementsTableCardRow : {
//        cells: row.cells!.map(cell => typeof cell === 'string' ? { text: cell } : cell),
//        dividerAfter: typeof row.dividerAfter === 'undefined' ? options.dividers : row.dividerAfter,
//    } as Api.GoogleActionsV2UiElementsTableCardRow)
//    const { columnProperties, columns, buttons } = options
//    if (columnProperties) {
//        this.columnProperties = toColumnProperties(columnProperties)
//    }
//    if (typeof columns !== 'undefined') {
//        if (!this.columnProperties) {
//            this.columnProperties = []
//        }
//        const properties = typeof columns === 'number' ?
//        new Array<Api.GoogleActionsV2UiElementsTableCardColumnProperties>(columns).fill({}) :
//        toColumnProperties(columns)
//        properties.forEach((v, i) => {
//            if (!this.columnProperties![i]) {
//            this.columnProperties![i] = properties[i]
//        }
//        })
//    }
//    this.buttons = if (options.buttons)
//}
//}
    ) {
        val options = TableOptions()
        options.init()
        title = options.title
        subtitle = options.subtitle
        image = options.image
    }
}


/**
 * Class for initializing and constructing MediaResponse.
 * @public
 */
class MediaResponse() : GoogleActionsV2MediaResponse, RichResponseItem() {
    override var mediaObjects: MutableList<GoogleActionsV2MediaObject>? = null

    override var mediaType: GoogleActionsV2MediaResponseMediaType? = null


    /**
     * @param objects MediaObjects
     * @public
     */
    constructor(vararg objects: MediaObject) : this() {
        this.mediaType = GoogleActionsV2MediaResponseMediaType.AUDIO
        this.mediaObjects = objects.toMutableList() //objects.map(o => toMediaObject (o))
    }

    constructor(vararg strings: String) : this() {
        this.mediaType = GoogleActionsV2MediaResponseMediaType.AUDIO
        this.mediaObjects = strings.map { it.toMediaObject() }.toMutableList()
    }

    /**
     * @param options MediaResponse options
     * @public
     */
    constructor(init: MediaResponseOptions.() -> Unit) : this() {
        val options = MediaResponseOptions()
        options.init()
        this.mediaType = GoogleActionsV2MediaResponseMediaType.AUDIO

        if (options != null) {
            this.mediaObjects = mutableListOf()
            return
        }



        this.mediaType = options.type ?: this.mediaType
        this.mediaObjects = options.objects?.toMutableList()

    }
}


/**
 * Class for initializing and constructing OrderUpdate
 * Delegates to MutableMap so dynamic fields may be added.
 * @public
 */
data class OrderUpdate(override var actionOrderId: String? = null,
                       override var cancellationInfo: GoogleActionsV2OrdersCancellationInfo? = null,
                       override var fulfillmentInfo: GoogleActionsV2OrdersFulfillmentInfo? = null,
                       override var googleOrderId: String? = null,
                       override var inTransitInfo: GoogleActionsV2OrdersInTransitInfo? = null,
                       override var infoExtension: ApiClientObjectMap<Any>? = null,
                       override var lineItemUpdates: ApiClientObjectMap<GoogleActionsV2OrdersLineItemUpdate>? = null,
                       override var orderManagementActions: MutableList<GoogleActionsV2OrdersOrderUpdateAction>? = null,
                       override var orderState: GoogleActionsV2OrdersOrderState? = null,
                       override var receipt: GoogleActionsV2OrdersReceipt? = null,
                       override var rejectionInfo: GoogleActionsV2OrdersRejectionInfo? = null,
                       override var returnInfo: GoogleActionsV2OrdersReturnInfo? = null,
                       override var totalPrice: GoogleActionsV2OrdersPrice? = null,
                       override var updateTime: GoogleTypeTimeOfDay? = null,
                       override var userNotification: GoogleActionsV2OrdersOrderUpdateUserNotification? = null,
                       override var orderDate: String? = null,
                       override var locale: String? = null) : GoogleActionsV2OrdersOrderUpdate, MutableMap<String, Any> by mutableMapOf(), RichResponseItem() {
    /**
     * @param options The raw {@link GoogleActionsV2OrdersOrderUpdate}
     * @public
     */
    constructor(options: GoogleActionsV2OrdersOrderUpdate? = null) : this(
            actionOrderId = options?.actionOrderId,
            cancellationInfo = options?.cancellationInfo,
            fulfillmentInfo = options?.fulfillmentInfo,
            googleOrderId = options?.googleOrderId,
            inTransitInfo = options?.inTransitInfo,
            infoExtension = options?.infoExtension,
            lineItemUpdates = options?.lineItemUpdates,
            orderManagementActions = options?.orderManagementActions,
            orderState = options?.orderState,
            receipt = options?.receipt,
            rejectionInfo = options?.rejectionInfo,
            returnInfo = options?.returnInfo,
            totalPrice = options?.totalPrice,
            updateTime = options?.updateTime,
            userNotification = options?.userNotification
    )

    constructor(init: OrderUpdate.() -> Unit) : this() {
        val options = OrderUpdate()
        options.init()
        actionOrderId = options.actionOrderId
        cancellationInfo = options.cancellationInfo
        fulfillmentInfo = options.fulfillmentInfo
        googleOrderId = options.googleOrderId
        inTransitInfo = options.inTransitInfo
        infoExtension = options.infoExtension
        lineItemUpdates = options.lineItemUpdates
        orderManagementActions = options.orderManagementActions
        orderState = options.orderState
        receipt = options.receipt
        rejectionInfo = options.rejectionInfo
        returnInfo = options.returnInfo
        totalPrice = options.totalPrice
        updateTime = options.updateTime
        userNotification = options.userNotification
    }

    fun orderState(init: GoogleActionsV2OrdersOrderState.() -> Unit) {
        this.orderState = GoogleActionsV2OrdersOrderState()
        this.orderState?.init()
    }

    fun receipt(init: GoogleActionsV2OrdersReceipt.() -> Unit) {
        this.receipt = GoogleActionsV2OrdersReceipt()
        this.receipt?.init()
    }

    fun orderManagementActions(vararg init: GoogleActionsV2OrdersOrderUpdateAction.() -> Unit) {
        this.orderManagementActions = init.map {
            val action = GoogleActionsV2OrdersOrderUpdateAction()
            action.it()
            action
        }.toMutableList()
    }

    fun userNotification(init: GoogleActionsV2OrdersOrderUpdateUserNotification.() -> Unit) {
        this.userNotification = GoogleActionsV2OrdersOrderUpdateUserNotification()
        this.userNotification?.init()
    }
}


/**
 * Link Out Suggestion.
 * Used in rich response as a suggestion chip which, when selected, links out to external URL.
 * @public
 */
data class LinkOutSuggestion(override var destinationName: String? = null,
                             override var openUrlAction: GoogleActionsV2UiElementsOpenUrlAction? = null,
                             override var url: String? = null) : GoogleActionsV2UiElementsLinkOutSuggestion, RichResponseItem() {
    /**
     * @param options LinkOutSuggestion options
     * @public
     */
    constructor(options: LinkOutSuggestionOptions) : this(
            destinationName = options.name,
            url = options.url)

    constructor(init: LinkOutSuggestionOptions.() -> Unit) : this({ val options = LinkOutSuggestionOptions(name = "", url = ""); options.init(); options }.invoke())
}


data class GoogleActionsV2RichResponseItem(
        /**
         * A basic card.
         */
        var basicCard: GoogleActionsV2UiElementsBasicCard? = null,
        /**
         * Carousel browse card.
         */
        var carouselBrowse: GoogleActionsV2UiElementsCarouselBrowse? = null,
        /**
         * Response indicating a set of media to be played.
         */
        var mediaResponse: GoogleActionsV2MediaResponse? = null,
        /**
         * Voice and text-only response.
         */
        var simpleResponse: GoogleActionsV2SimpleResponse? = null,
        /**
         * Structured payload to be processed by Google.
         */
        var structuredResponse: GoogleActionsV2StructuredResponse? = null,
        /**
         * Table card.
         */
        var tableCard: GoogleActionsV2UiElementsTableCard? = null
) : RichResponseItem()


/**
 * Class for initializing and constructing Browse Carousel.
 * @public
 */
class BrowseCarousel(override var imageDisplayOptions: GoogleActionsV2UiElementsCarouselBrowseImageDisplayOptions? = null,
                     override var items: MutableList<GoogleActionsV2UiElementsCarouselBrowseItem>? = null) : GoogleActionsV2UiElementsCarouselBrowse, RichResponseItem() {

    /**
     * @param items BrowseCarousel items
     * @public
     */
    constructor(vararg items: GoogleActionsV2UiElementsCarouselBrowseItem?) : this() {
        this.items = items.filterNotNull().toMutableList()
    }

    /**
     * @param item BrowseCarousel item
     * @public
     */
    constructor(item: GoogleActionsV2UiElementsCarouselBrowseItem?) : this() {
        if (item == null) {
            this.items = mutableListOf()
        } else {
            items?.add(item)
        }
    }

    /**
     * @param options BrowseCarouselOptions
     * @public
     */
    constructor(init: BrowseCarouselOptions.() -> Unit) : this() {
        val options = BrowseCarouselOptions()
        options.init()
        if (options == null) {
            this.items = mutableListOf()
            return
        }

        this.imageDisplayOptions = options.display
        this.items = options.items
        return
    }
}

class List(init: ListOptions2.() -> Unit) : Question(IntentEnum.OPTION) {

    init {
        val options = ListOptions2()
        options.init()
        this._data(InputValueSpec.OptionValueSpec) {
            listSelect = actions.service.actionssdk.api.GoogleActionsV2UiElementsListSelect(
                    title = options.title,
                    items = options.items.toGoogleActionsV2ListItem()
            )
        }
    }
}


/**
 * Asks to collect user"s input with a carousel.
 *
 * @example
 * ```javascript
 *
 * // Actions SDK
 * val app = actionssdk()
 *
 * app.intent("actions.intent.MAIN", conv => {
 *   conv.ask("Which of these looks good?")
 *   conv.ask(new Carousel({
 *     items: {
 *       [SELECTION_KEY_ONE]: {
 *         title: "Number one",
 *         description: "Description of number one",
 *         synonyms: ["synonym of KEY_ONE 1", "synonym of KEY_ONE 2"],
 *       },
 *       [SELECTION_KEY_TWO]: {
 *         title: "Number two",
 *         description: "Description of number one",
 *         synonyms: ["synonym of KEY_TWO 1", "synonym of KEY_TWO 2"],
 *       }
 *     }
 *   }))
 * })
 *
 * app.intent("actions.intent.OPTION", (conv, input, option) => {
 *   if (option === SELECTION_KEY_ONE) {
 *     conv.close("Number one is a great choice!")
 *   } else {
 *     conv.close("Number two is also a great choice!")
 *   }
 * })
 *
 * // Dialogflow
 * val app = dialogflow()
 *
 * app.intent("Default Welcome Intent", conv => {
 *   conv.ask("Which of these looks good?")
 *   conv.ask(new Carousel({
 *     items: {
 *       [SELECTION_KEY_ONE]: {
 *         title: "Number one",
 *         description: "Description of number one",
 *         synonyms: ["synonym of KEY_ONE 1", "synonym of KEY_ONE 2"],
 *       },
 *       [SELECTION_KEY_TWO]: {
 *         title: "Number two",
 *         description: "Description of number one",
 *         synonyms: ["synonym of KEY_TWO 1", "synonym of KEY_TWO 2"],
 *       }
 *     }
 *   }))
 * })
 *
 * // Create a Dialogflow intent with the `actions_intent_OPTION` event
 * app.intent("Get Option", (conv, input, option) => {
 *   if (option === SELECTION_KEY_ONE) {
 *     conv.close("Number one is a great choice!")
 *   } else {
 *     conv.close("Number two is also a great choice!")
 *   }
 * })
 * ```
 *
 * @public
 */
class Carousel(init: CarouselOptions.() -> Unit) : Question(IntentEnum.OPTION) {

    /**
     * @param options Carousel option
     * @public
     */
    init {
        val options = CarouselOptions()
        options.init()

        this._data(actions.service.actionssdk.conversation.InputValueSpec.OptionValueSpec) {
            carouselSelect = actions.service.actionssdk.api.GoogleActionsV2UiElementsCarouselSelect(
                    items = options.items.toGoogleActionsV2CarouselItem(),
                    imageDisplayOptions = options.display
            )
        }
    }
}