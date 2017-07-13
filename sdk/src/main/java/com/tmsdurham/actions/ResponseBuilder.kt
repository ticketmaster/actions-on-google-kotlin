package com.tmsdurham.actions

import com.ticketmaster.apiai.google.GoogleData

var LIST_ITEM_LIMIT = 30
var CAROUSEL_ITEM_LIMIT = 10

/**
 * Class for initializing and constructing Lists with chainable interface.
 */
data class List(var title: String? = null, var items: MutableList<GoogleData.OptionInfo>? = null) {

    /**
     * Sets the title for this List.
     *
     * @param {string} title Title to show on list.
     * @return {List} Returns current constructed List.
     */
    fun setTitle (title: String): List {
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
    fun addItems (vararg optionItems: GoogleData.OptionInfo): List {
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

object ResponseBuilder {
    
    
    val ssmlRegex = "^<speak\\b[^>]*>(\\*?)</speak>".toRegex()
    fun isSsml(text: String): Boolean {
        return ssmlRegex.matches(text)
    }
}
