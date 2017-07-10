package com.tmsdurham.actions


data class RequestWrapper<T>(val body: T, val headers: Map<String, String> = mapOf())

