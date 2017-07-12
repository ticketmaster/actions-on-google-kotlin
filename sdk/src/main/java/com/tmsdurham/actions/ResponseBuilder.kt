package com.tmsdurham.actions

object ResponseBuilder {
    val ssmlRegex = "^<speak\\b[^>]*>(\\*?)</speak>".toRegex()
    fun isSsml(text: String): Boolean {
        return ssmlRegex.matches(text)
    }
}
