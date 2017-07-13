package com.tmsdurham.actions

open class ResponseWrapper<T>(var statusCode: Int = 200,
                              var body: T? = null,
                              val headers: MutableMap<String, String> = mutableMapOf(),
                              val sendAction: (ResponseWrapper<T>.() -> Unit)? = null) {

    var errorMessage: String = ""

    fun status(statusCode: Int): ResponseWrapper<T> {
        this.statusCode = statusCode
        return this
    }

    fun send(body: T): ResponseWrapper<T> {
        this.body = body
        sendAction?.invoke(this)
        return this
    }

    fun send(errorMessage: String): ResponseWrapper<T> {
        this.errorMessage = errorMessage
        sendAction?.invoke(this)
        return this
    }

    fun append(header: String, value: String): ResponseWrapper<T> {
        headers.put(header, value)
        return this
    }
}
