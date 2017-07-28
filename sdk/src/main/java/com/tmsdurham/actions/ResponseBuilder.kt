package com.tmsdurham.actions


var LIST_ITEM_LIMIT = 30
var CAROUSEL_ITEM_LIMIT = 10


data class RichResponse(
        var items: MutableList<RichResponseItem>? = null,
        var suggestions: MutableList<Suggestions>? = null,
        var altLinkSuggestion: AltLinkSuggestion? = null,
        var linkOutSuggestion: LinkOutSuggestion? = null) {

    fun isEmpty() = ((items == null || items!!.isEmpty()) &&
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
        if (items?.count { it?.simpleResponse != null } ?: 0 >= 2) {
            error("Cannot include >2 SimpleResponses in RichResponse")
            return this
        }
        val simpleResponseObj = RichResponseItem(simpleResponse = simpleResponse)

        // Check first if needs to replace BasicCard at beginning of items list
        if (items == null) {
            items = mutableListOf()
        }
        if (items!!.size > 0 && (items!![0].basicCard != null ||
                items!![0].structuredResponse != null)) {
            items!!.add(simpleResponseObj)
        } else {
            items!!.add(0, simpleResponseObj)
        }
        return this
    }
//
//    fun addSimpleResponse(speech: String, displayText: String? = null) = addSimpleResponse {
//        textToSpeech = speech
//        this.displayText = displayText
//    }

    fun addSimpleResponse(simpleResponse: SimpleResponse): RichResponse {
        return addSimpleResponse(
                speech = simpleResponse.textToSpeech ?: "",
                displayText = simpleResponse.displayText ?: "")
    }

    /**
     * Adds a BasicCard to list of items.
     *
     * @param {BasicCard} basicCard Basic card to include in response.
     * @return {RichResponse} Returns current constructed RichResponse.
     */
    fun addBasicCard(basicCard: BasicCard): RichResponse {
        if (basicCard == null) {
            error("Invalid basicCard")
            return this
        }
        // Validate if basic card is already present
        if (items?.count { it.basicCard != null } ?: 0 > 0) {
            error("Cannot include >1 BasicCard in RichResponse")
            return this
        }
        if (items == null) {
            items = mutableListOf()
        }
        val item = RichResponseItem()
        item.basicCard = basicCard
        if (items?.size == 0) {
            items?.add(item)
        } else {
            items?.add(0, item)
        }

        return this
    }

    /**
     * Adds a single suggestion or list of suggestions to list of items.
     *
     * @param {string|Array<string>} suggestions Either a single string suggestion
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


}

data class LinkOutSuggestion(
        var destinationName: String? = null,
        var url: String? = null)

//weird structure here.  This is Wrapper for ONE of the objects below.
data class RichResponseItem(
        var simpleResponse: SimpleResponse? = null,
        var basicCard: BasicCard? = null,
        var structuredResponse: StructuredResponse? = null)

data class StructuredResponse(var orderUpdate: OrderUpdate)

data class OrderState(var state: TransactionValues.OrderState, var label: String)

data class AltLinkSuggestion(var url: String? = null)

data class Suggestions(var title: String? = null)


data class Buttons(
        var title: String? = null,
        var openUrlAction: OpenUrlAction? = null)

data class OpenUrlAction(var url: String? = null)

data class Image(var url: String? = null, var accessibilityText: String? = null, var width: Int? = null, var height: Int? = null)

data class Items(
        var optionInfo: OptionInfo? = null,
        var title: String? = null,
        var description: String? = null,
        var image: Image? = null)

data class ListSelect(
        var title: String? = null,
        var items: MutableList<Items>? = null)

data class SimpleResponse(
        var textToSpeech: String? = null,
        var ssml: String? = null,
        var displayText: String? = null) {
    fun isEmpty() = textToSpeech.isNullOrBlank() && ssml.isNullOrBlank() && displayText.isNullOrBlank()
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
data class BasicCard(var title: String = "",
                var formattedText: String? = null,
                var subtitle: String? = null,
                var image: Image? = null,
                var buttons: MutableList<Buttons> = mutableListOf()) {

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
        if (buttons == null) {
            buttons = mutableListOf()
        }
        this.buttons!!.add(Buttons(
                title = text,
                openUrlAction = OpenUrlAction(
                        url = url))
        )
        return this
    }
}


/**
 * Class for initializing and constructing Lists with chainable interface.
 */
data class List(var title: String? = null, var items: MutableList<OptionItem>? = null) {

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
        if (items == null) {
            items = mutableListOf()
        }
        items?.addAll(optionItems)

        if (items?.size ?: 0 > LIST_ITEM_LIMIT) {
            items = items?.slice(0..LIST_ITEM_LIMIT)?.toMutableList()
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
            items = items.slice(0..CAROUSEL_ITEM_LIMIT).toMutableList()
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

    var title: String? = null
    var description: String? = null
    var image: Image? = null

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
        this.optionInfo?.key = key
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
        if (synonyms == null || synonyms.isEmpty()) {
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
