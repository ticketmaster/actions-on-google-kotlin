package com.tmsdurham.actions.gui

data class BasicCard(
        var title: String = "",
        var imageUrl: String = "",
        var formattedText: String = "",
        var subtitle: String = "",
        var buttons: List<Button> = listOf()) {
    fun isEmpty() = this == emptyBasicCard
    fun isNotEmpty() = !isEmpty()

    fun basicCard(init: BasicCard.() -> Unit): BasicCard {
        val basicCard = BasicCard()
        basicCard.init()
        return basicCard
    }

    companion object {
        val emptyBasicCard = BasicCard()
    }
}

data class Button(
        val title: String = "",
        val openUrlAction: String = "") {

    fun isEmpty() = this == emptyButton

    companion object {
        val emptyButton = Button()
    }
}

