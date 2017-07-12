package com.tmsdurham.actions


data class RequestWrapper<T>(val headers: Map<String, String> = mapOf(), val body: T) {
    fun get(headerName: String) = headers[headerName]
}

