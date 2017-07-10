package com.tmsdurham.actions

class ResponseWrapper<T>(var statusCode: Int = 200, var body: T? = null, val headers: MutableMap<String, String> = mutableMapOf()) {

   fun status(statusCode: Int): ResponseWrapper<T> {
      this.statusCode = statusCode
      return this
   }

   fun send(body: T): ResponseWrapper<T> {
      this.body = body
       return this
   }

   fun append(header: String, value: String): ResponseWrapper<T> {
      headers.put(header, value)
      return this
   }
}
