package com.ticketmaster.apiai

data class ApiAiResponse<T>(var speech: String = "", var displayText: String = "", var secondDisplayText: String = "",
                         var data: Data = Data.empty, var contextOut: MutableList<ContextOut<T>> = mutableListOf(), var source: String = "") {
    fun data(init: Data.() -> Unit): Data {
        val data = Data()
        data.init()
        return data
    }
}

class ContextOut<T>(var name: String, var lifespan: Int, var parameters: T? = null)

fun <T> apiAiResponse(init: ApiAiResponse<T>.() -> Unit): ApiAiResponse<T> {
    val r = ApiAiResponse<T>()
    r.init()
    return r
}

fun test() {
    apiAiResponse<Any> {
        data {
            google {

            }
        }
    }
}
