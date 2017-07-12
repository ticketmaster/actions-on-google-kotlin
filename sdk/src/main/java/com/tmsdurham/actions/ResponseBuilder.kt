package com.tmsdurham.actions

object ResponseBuilder {
    val ssmlRegex = "<speak>(.+?)</speak>".toRegex()
    fun isSsml(text: String): Boolean {
        return ssmlRegex.matches(text)
    }
}
