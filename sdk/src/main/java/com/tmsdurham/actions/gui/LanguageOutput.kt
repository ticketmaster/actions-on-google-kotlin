package com.tmsdurham.actions.gui

data class LanguageOutput(val firstLine: String, val secondLine: String? = null) {

    fun full(): String {
        var speech = ""

        if (firstLine != null) {
            speech += firstLine
        }

        if (secondLine != null) {
            speech += " " + secondLine!!
        }
        return speech
    }

    companion object {
        val emptyLanguageOutput = LanguageOutput(firstLine = "")
    }

}
