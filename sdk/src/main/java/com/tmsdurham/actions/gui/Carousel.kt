package com.tmsdurham.actions.gui

data class Carousel(val items: List<CarouselItem> = listOf()) {
    fun isEmpty() = this == emptyCarousel
    fun isNotEmpty() = !isEmpty()

    companion object {
        val emptyCarousel = Carousel()
    }
}

data class CarouselItem(
        val key: String,
        val synonyms: List<String> = listOf(),
        val title: String,
        val desc: String? = null,
        val imageUrl: String = "")
