package com.tmsdurham.actions

import com.ticketmaster.apiai.google.GoogleData

var LIST_ITEM_LIMIT = 30
var CAROUSEL_ITEM_LIMIT = 10

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
        if (title.isNullOrBlank()) {
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

data class OptionInfo(var key: String = "", var synonyms: MutableList<String> = mutableListOf(), var description: String? = null, var image: GoogleData.Image? = null)

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
    var image: GoogleData.Image? = null

    /**
     * Sets the title for this Option Item.
     *
     * @param {string} title Title to show on item.
     * @return {OptionItem} Returns current constructed OptionItem.
     */
    fun setTitle (title: String): OptionItem {
        if (!title.isNotBlank()) {
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
    fun setDescription (description: String): OptionItem {
        if (description.isNotBlank()) {
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
    fun setImage (url: String, accessibilityText: String, width: Int? = null, height: Int? = null): OptionItem {
        if (url.isNotBlank()) {
            error("url cannot be empty")
            return this
        }
        if (accessibilityText.isNotBlank()) {
            error("accessibilityText cannot be empty")
            return this
        }
        image = GoogleData.Image(url = url, accessibilityText = accessibilityText)
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
    fun setKey (key: String): OptionItem {
        if (key.isNotBlank()) {
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
    fun addSynonyms (vararg synonyms: String?): OptionItem {
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


    val ssmlRegex = "^<speak\\b[^>]*>(\\*?)</speak>".toRegex()
    fun isSsml(text: String): Boolean {
        return ssmlRegex.matches(text)
    }
}
