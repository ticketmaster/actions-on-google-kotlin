package com.ticketmaster.apiai

data class ApiAiResponse(var speech: String = "", var displayText: String = "", var secondDisplayText: String = "",
                         var data: Data = Data.empty, var contextOut: MutableList<ContextOut> = mutableListOf(), var source: String = "") {
    fun data(init: Data.() -> Unit): Data {
        val data = Data()
        data.init()
        return data
    }
}

class ContextOut(var name: String, var lifespan: Int)

fun apiAiResponse(init: ApiAiResponse.() -> Unit): ApiAiResponse {
    val r = ApiAiResponse()
    r.init()
    return r
}

fun test() {
    apiAiResponse {
        data {
            google {

            }
        }
    }
}
