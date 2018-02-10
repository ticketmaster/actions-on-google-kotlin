package com.tmsdurham.actions


var LIST_ITEM_LIMIT = 30
var CAROUSEL_ITEM_LIMIT = 10


/**
 * List of possible options to display the image in a BasicCard.
 * When the aspect ratio of an image is not the same as the surface,
 * this attribute changes how the image is displayed in the card.
 */
enum class ImageDisplays {
    /**
     * Pads the gaps between the image and image frame with a blurred copy of the
     * same image.
     */
    DEFAULT,

    /**
     * Fill the gap between the image and image container with white bars.
     */
    WHITE,

    /**
     * Image is scaled such that the image width matches the container width. This may crop the top
     * and bottom of the image if the scaled image height is greater than the container height. This
     * is similar to "Zoom Mode" on a widescreen TV when playing a 4:3 video..
     */
    CROPPED
}

/**
 * Simple Response type.
 * @property {String} speech - Speech to be spoken to user. SSML allowed.
 * @property {String} displayText - Optional text to be shown to user
 */
data class SimpleResponse(
        var textToSpeech: String? = null,
        var ssml: String? = null,
        var displayText: String? = null) {
    fun isEmpty() = textToSpeech.isNullOrBlank() && ssml.isNullOrBlank() && displayText.isNullOrBlank()
}

/**
 * Suggestions to show with response.
 * @property {String} title - Text of the suggestion.
 */
data class Suggestions(var title: String? = null)

/**
 * Link Out Suggestion. Used in rich response as a suggestion chip which, when
 * selected, links out to external URL.
 * @property {String} title - Text shown on the suggestion chip.
 * @property {String} url - String URL to open.
 */
data class LinkOutSuggestion(
        var destinationName: String? = null,
        var url: String? = null)

/**
 * Image type shown on visual elements.
 * @property {String} url - Image source URL.
 * @property {String} accessibilityText - Text to replace for image for
 *     accessibility.
 * @property {Int?} width - Width of the image.
 * @property {Int?} height - Height of the image.
 */
data class Image(var url: String? = null, var accessibilityText: String? = null, var width: Int? = null, var height: Int? = null)

/**
 * Basic Card Button. Shown below basic cards. Open a URL when selected.
 * @property {string} title - Text shown on the button.
 * @property {Object} openUrlAction - Action to take when selected.
 * @property {string} openUrlAction.url - String URL to open.
 */
data class Button(
        var title: String? = null,
        var openUrlAction: OpenUrlAction? = null)

//weird structure here.  This is Wrapper for ONE of the objects below.
data class RichResponseItem(
        var simpleResponse: SimpleResponse? = null,
        var basicCard: BasicCard? = null,
        var structuredResponse: StructuredResponse? = null)

data class StructuredResponse(var orderUpdate: OrderUpdate)

data class OrderState(var state: TransactionValues.OrderState, var label: String)

data class AltLinkSuggestion(var url: String? = null)


data class OpenUrlAction(var url: String? = null)

data class Items(
        var optionInfo: OptionInfo? = null,
        var title: String? = null,
        var description: String? = null,
        var image: Image? = null)

data class ListSelect(
        var title: String? = null,
        var items: MutableList<Items>? = null)

data class RichResponse(
        var items: MutableList<RichResponseItem> = mutableListOf(),
        var suggestions: MutableList<Suggestions>? = mutableListOf(),
        var altLinkSuggestion: AltLinkSuggestion? = null,
        var linkOutSuggestion: LinkOutSuggestion? = null) {

    fun isEmpty() = (items.isEmpty() &&
            (suggestions == null || suggestions!!.isEmpty()) &&
            (altLinkSuggestion == null) &&
            (linkOutSuggestion == null))

    /**
     * Adds a SimpleResponse to list of items.
     *
     * @param {string|SimpleResponse} simpleResponse Simple response to present to
     *     user. If just a string, display text will not be set.
     * @return {RichResponse} Returns current constructed RichResponse.
     */
    fun addSimpleResponse(speech: String, displayText: String? = null): RichResponse {
        val simpleResponse = SimpleResponse(textToSpeech = speech, displayText = displayText)
        if (simpleResponse.isEmpty()) {
            error("Invalid simpleResponse")
            return this
        }
        // Validate if RichResponse already contains two SimpleResponse objects
        if (items.count { it.simpleResponse != null } >= 2) {
            error("Cannot include >2 SimpleResponses in RichResponse")
            return this
        }
        val simpleResponseObj = RichResponseItem(simpleResponse = buildSimpleResponseHelper(simpleResponse))

        if (items.size > 0 && items.get(0).simpleResponse == null) {
            items.add(0, simpleResponseObj)
        } else {
            items.add(simpleResponseObj)
        }
        return this
    }

    /**
     * One arg function for convenience when calling from Java
     */
    fun addSimpleResponse(speech: String) = addSimpleResponse(speech, null)

    fun addSimpleResponse(simpleResponse: SimpleResponse) =
        addSimpleResponse(
                speech = simpleResponse.textToSpeech ?: "",
                displayText = simpleResponse.displayText ?: "")


    /**
     * Adds a BasicCard to list of items.
     *
     * @param {BasicCard} basicCard Basic card to include in response.
     * @return {RichResponse} Returns current constructed RichResponse.
     */
    fun addBasicCard(basicCard: BasicCard): RichResponse {
        // Validate if basic card is already present
        if (items.count { it.basicCard != null } > 0) {
            error("Cannot include >1 BasicCard in RichResponse")
            return this
        }

        val item = RichResponseItem()
        item.basicCard = basicCard
        items.add(item)

        return this
    }

    /**
     * Adds a single suggestion or list of suggestions to list of items.
     *
     * @param {varard String} suggestions Either a single string suggestion
     *     or list of suggestions to add.
     * @return {RichResponse} Returns current constructed RichResponse.
     */
    fun addSuggestions(vararg suggestions: String): RichResponse {
        if (suggestions.isEmpty()) {
            error("Invalid suggestions")
            return this
        }
        if (this.suggestions == null) {
            this.suggestions = mutableListOf()
        }
        this.suggestions?.addAll(suggestions.map { Suggestions(it) })
        return this
    }

    /**
     * Sets the suggestion link for this rich response.
     *
     * @param {string} destinationName Name of the link out destination.
     * @param {string} suggestionUrl - String URL to open when suggestion is used.
     * @return {RichResponse} Returns current constructed RichResponse.
     */
    fun addSuggestionLink(destinationName: String, suggestionUrl: String): RichResponse {
        if (destinationName.isBlank()) {
            error("destinationName cannot be empty")
            return this
        }
        if (suggestionUrl.isBlank()) {
            error("suggestionUrl cannot be empty")
            return this
        }
        this.linkOutSuggestion = LinkOutSuggestion(
                destinationName = destinationName,
                url = suggestionUrl)
        return this
    }

    /**
     * Adds an order update to this response. Use after a successful transaction
     * decision to confirm the order.
     *
     * @param {OrderUpdate} orderUpdate
     * @return {RichResponse} Returns current constructed RichResponse.
     */
    fun addOrderUpdate(orderUpdate: OrderUpdate): RichResponse {
        // Validate if RichResponse already contains StructuredResponse object
        items.forEach {
            if (it.structuredResponse != null) {
                debug("Cannot include >1 StructuredResponses in RichResponse")
                return this
            }
        }

        items.add(RichResponseItem(
                structuredResponse = StructuredResponse(
                        orderUpdate = orderUpdate
                ))
        )
        return this
    }

    /**
     * Helper to build SimpleResponse from speech and display text.
     *
     * @param {SimpleResponse} response String to speak, or SimpleResponse.
     *     SSML allowed.
     * @param {String} response.speech If using SimpleResponse, speech to be spoken
     *     to user.
     * @param {String=} response.displayText If using SimpleResponse, text to be shown
     *     to user.
     * @return {Object} Appropriate SimpleResponse object.
     * @private
     */
    fun buildSimpleResponseHelper(response: SimpleResponse): SimpleResponse? {
        debug("buildSimpleResponseHelper: response=$response")
        var simpleResponseObj: SimpleResponse
        if (response.textToSpeech?.isNotBlank() ?: false) {
            simpleResponseObj = if (ResponseBuilder.isSsml(response.textToSpeech!!))
                SimpleResponse(ssml = response.textToSpeech) else SimpleResponse(textToSpeech = response.textToSpeech)
            simpleResponseObj.displayText = response.displayText
        } else {
            error("SimpleResponse requires a speech parameter.")
            return null
        }
        return simpleResponseObj
    }
}


/**
 * Class for initializing and constructing Basic Cards with chainable interface.
 */
/**
 * Title of the card. Optional.
 * @type {string}
 *
 * Body text to show on the card. Required, unless image is present.
 * @type {string}
 *
 * Subtitle of the card. Optional.
 * @type {string}
 *
 * Image to show on the card. Optional.
 * @type {Image}
 *
 * Ordered list of buttons to show below card. Optional.
 * @type {Array<Button>}
 */
data class BasicCard(internal var title: String = "",
                     internal var formattedText: String = "",
                     internal var subtitle: String? = null,
                     internal var image: Image? = null,
                     internal var imageDisplayOptions: ImageDisplays? = null,
                     internal var buttons: MutableList<Button> = mutableListOf()) {

    /**
     * Sets the title for this Basic Card.
     *
     * @param {string} title Title to show on card.
     * @return {BasicCard} Returns current constructed BasicCard.
     */
    fun setTitle(title: String): BasicCard {
        if (title.isBlank()) {
            error("title cannot be empty")
            return this
        }
        this.title = title
        return this
    }

    /**
     * Sets the subtitle for this Basic Card.
     *
     * @param {string} subtitle Subtitle to show on card.
     * @return {BasicCard} Returns current constructed BasicCard.
     */
    fun setSubtitle(subtitle: String): BasicCard {
        if (subtitle.isBlank()) {
            error("subtitle cannot be empty")
            return this
        }
        this.subtitle = subtitle
        return this
    }

    /**
     * Sets the body text for this Basic Card.
     *
     * @param {string} bodyText Body text to show on card.
     * @return {BasicCard} Returns current constructed BasicCard.
     */
    fun setBodyText(bodyText: String): BasicCard {
        if (bodyText.isBlank()) {
            error("bodyText cannot be empty")
            return this
        }
        this.formattedText = bodyText
        return this
    }

    /**
     * Sets the image for this Basic Card.
     *
     * @param {string} url Image source URL.
     * @param {string} accessibilityText Text to replace for image for
     *     accessibility.
     * @param {number=} width Width of the image.
     * @param {number=} height Height of the image.
     * @return {BasicCard} Returns current constructed BasicCard.
     */
    fun setImage(url: String, accessibilityText: String, width: Int? = null, height: Int? = null): BasicCard {
        if (url.isBlank()) {
            error("url cannot be empty")
            return this
        }
        if (accessibilityText.isBlank()) {
            error("accessibilityText cannot be empty")
            return this
        }
        this.image = Image(url = url, accessibilityText = accessibilityText, width = width, height = height)
        return this
    }

    /**
     * Two arg function for convenience when calling from Java
     */
    fun setImage(url: String, accessibilityText: String) = setImage(url, accessibilityText, null, null)
    /**
     * Adds a button below card.
     *
     * @param {string} text Text to show on button.
     * @param {string} url URL to open when button is selected.
     * @return {BasicCard} Returns current constructed BasicCard.
     */
    fun addButton(text: String, url: String): BasicCard {
        if (text.isBlank()) {
            error("text cannot be empty")
            return this
        }
        if (url.isBlank()) {
            error("url cannot be empty")
            return this
        }

        this.buttons.add(Button(
                title = text,
                openUrlAction = OpenUrlAction(
                        url = url))
        )
        return this
    }

    /**
     * Sets the display options for the image in this Basic Card.
     * Use one of the image display constants. If none is chosen,
     * ImageDisplays.DEFAULT will be enforced.
     *
     * @param {ImageDisplays} option The option for displaying the image.
     * @return {BasicCard} Returns current constructed BasicCard.
     */
    fun setImageDisplay(imageDisplayOption: ImageDisplays): BasicCard {
        this.imageDisplayOptions = imageDisplayOption
        return this
    }
}


/**
 * Class for initializing and constructing Lists with chainable interface.
 */
data class List(var title: String? = null, var items: MutableList<OptionItem> = mutableListOf()) {

    /**
     * Sets the title for this List.
     *
     * @param {string} title Title to show on list.
     * @return {List} Returns current constructed List.
     */
    fun setTitle(title: String): List {
        if (title.isBlank()) {
            error("title cannot be empty")
            return this
        }
        this.title = title
        return this
    }

    /**
     * Adds a single item or list of items to the list.
     *
     * @param {OptionItem|Array<OptionItem>} optionItems OptionItems to add.
     * @return {List} Returns current constructed List.
     */
    fun addItems(vararg optionItems: OptionItem): List {
        if (optionItems.isEmpty()) {
            error("optionItems cannot be empty")
            return this
        }
        items.addAll(optionItems)

        if (items.size > LIST_ITEM_LIMIT) {
            items = items.slice(0..LIST_ITEM_LIMIT - 1).toMutableList()
            error("Carousel can have no more than " + LIST_ITEM_LIMIT +
                    " items")
        }
        return this
    }
}

/**
 * Class for initializing and constructing Carousel with chainable interface.
 */

/**
 * Constructor for Carousel. Accepts optional Carousel to clone or list of
 * items to copy.
 *
 * @param {Carousel|Array<OptionItem>} carousel Either a carousel to clone, a
 *     or an array of OptionItem to initialize a new carousel
 */
data class Carousel(var items: MutableList<OptionItem> = mutableListOf()) {
    /**
     * Adds a single item or list of items to the carousel.
     *
     * @param {OptionItem|Array<OptionItem>} optionItems OptionItems to add.
     * @return {Carousel} Returns current constructed Carousel.
     */
    fun addItems(vararg optionItems: OptionItem): Carousel {
        if (optionItems.isEmpty()) {
            error("optionItems cannot be empty")
            return this
        }
        items.addAll(optionItems)

        if (items.size > CAROUSEL_ITEM_LIMIT) {
            items = items.slice(0..CAROUSEL_ITEM_LIMIT - 1).toMutableList()
            error("Carousel can have no more than $CAROUSEL_ITEM_LIMIT items")
        }
        return this
    }

}

data class OptionInfo(var key: String = "", var synonyms: MutableList<String> = mutableListOf(), var description: String? = null, var image: Image? = null)

/**
 * Class for initializing and constructing Option Items with chainable interface.
 *
 * Option info of the option item. Required.
 * @type {OptionInfo}
 *
 * Title of the option item. Required.
 * @type {string}
 *
 * Description text of the item. Optional.
 * @type {string}
 *
 * Image to show on item. Optional.
 * @type {Image}
 */
data class OptionItem(var optionInfo: OptionInfo = OptionInfo()) {

    private var title: String? = null
    private var description: String? = null
    private var image: Image? = null

    /**
     * Sets the title for this Option Item.
     *
     * @param {string} title Title to show on item.
     * @return {OptionItem} Returns current constructed OptionItem.
     */
    fun setTitle(title: String): OptionItem {
        if (title.isBlank()) {
            error("title cannot be empty")
            return this
        }
        this.title = title
        return this
    }

    /**
     * Sets the description for this Option Item.
     *
     * @param {string} description Description to show on item.
     * @return {OptionItem} Returns current constructed OptionItem.
     */
    fun setDescription(description: String): OptionItem {
        if (description.isBlank()) {
            error("descriptions cannot be empty")
            return this
        }
        this.description = description
        return this
    }

    /**
     * Sets the image for this Option Item.
     *
     * @param {string} url Image source URL.
     * @param {string} accessibilityText Text to replace for image for
     *     accessibility.
     * @param {number=} width Width of the image.
     * @param {number=} height Height of the image.
     * @return {OptionItem} Returns current constructed OptionItem.
     */
    fun setImage(url: String, accessibilityText: String, width: Int? = null, height: Int? = null): OptionItem {
        if (url.isBlank()) {
            error("url cannot be empty")
            return this
        }
        if (accessibilityText.isBlank()) {
            error("accessibilityText cannot be empty")
            return this
        }
        image = Image(url = url, accessibilityText = accessibilityText)
        if (width != null) {
            image?.width = width
        }
        if (height != null) {
            image?.height = height
        }
        return this
    }

    /**
     * Two arg function for convenience when calling from Java
     */
    fun setImage(url: String, accessibilityText: String): OptionItem {
        return setImage(url, accessibilityText, null, null)
    }

    /**
     * Sets the key for the OptionInfo of this Option Item. This will be returned
     * as an argument in the resulting actions.intent.OPTION intent.
     *
     * @param {string} key Key to uniquely identify this item.
     * @return {OptionItem} Returns current constructed OptionItem.
     */
    fun setKey(key: String): OptionItem {
        if (key.isBlank()) {
            error("key cannot be empty")
            return this
        }
        this.optionInfo.key = key
        return this
    }

    /**
     * Adds a single synonym or list of synonyms to item.
     *
     * @param {string|Array<string>} synonyms Either a single string synonyms
     *     or list of synonyms to add.
     * @return {OptionItem} Returns current constructed OptionItem.
     */
    fun addSynonyms(vararg synonyms: String?): OptionItem {
        if (synonyms.isEmpty()) {
            error("Invalid synonyms")
            return this
        }
        synonyms.forEach {
            if (it != null) {
                optionInfo.synonyms.add(it)
            }
        }
        return this
    }
}


object ResponseBuilder {
    val ssmlRegex = "^(?i)<speak\\b[^>]*>(.*?)</speak>$".toRegex()

    fun isSsml(text: String): Boolean {
        return ssmlRegex.containsMatchIn(text)
    }
}
