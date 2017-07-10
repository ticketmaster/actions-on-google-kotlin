package com.tmsdurham.actions.gui


data class DisplayList(
        val title: String = "",
        val items: List<Item> = listOf()) {

    fun isEmpty() = this == emptyDisplayList
    fun isNotEmpty() = !isEmpty()

    companion object {
        val emptyDisplayList = DisplayList()
    }
}

data class Item(
        val key: String = "",
        val synonyms: List<String> = listOf(),
        val title: String = "",
        val desc: String = "",
        val imageUrl: String = "") : Comparable<Item> {

    override fun compareTo(o: Item): Int {
        return title.compareTo(o.title)
    }

    companion object {
        val emptyItem = Item()
    }
}

