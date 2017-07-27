package com.ticketmaster.apiai

data class ApiAiResponse(var speech: String = "", var displayText: String = "", var secondDisplayText: String = "",
                            var data: Data = Data.empty, var contextOut: MutableList<Context> = mutableListOf(), var source: String = "") {
    fun data(init: Data.() -> Unit): Data {
        if (data == null) {
            this.data = Data()
        }
        data.init()
        return data
    }
}

fun apiAiResponse(init: ApiAiResponse.() -> Unit): ApiAiResponse {
    val r = ApiAiResponse()
    r.init()
    return r
}

fun test() {
    apiAiResponse {
        displayText = "display Text"
        data {
            google {

            }
        }
    }
}
