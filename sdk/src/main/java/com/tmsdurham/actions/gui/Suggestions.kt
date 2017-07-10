package com.tmsdurham.actions.gui


data class Suggestions(var chips: List<Chip> = mutableListOf()) {

    fun isEmpty() = this == emptySuggestion
    fun isNotEmpty() = !isEmpty()

    companion object {
        val emptySuggestion = Suggestions()
    }
}

data class Chip(val title: String)
