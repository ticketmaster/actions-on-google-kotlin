package com.ticketmaster.apiai

data class ApiAiResponse(var speech: String = "", var displayText: String = "", var secondDisplayText: String = "",
                            var data: Data = Data.empty, var contextOut: MutableList<ContextOut> = mutableListOf(), var source: String = "") {
    fun data(init: Data.() -> Unit): Data {
        if (data == null) {
            this.data = Data()
        }
        data.init()
        return data
    }
}

data class ContextOut(var name: String, var lifespan: Int, var parameters: MutableMap<String, Any>? = null) {
    override fun equals(other: Any?): Boolean {
        if (other is ContextOut) {
            if (parameters != null) {
                return super.equals(other)
            } else {
                return (this.name == other.name) && (this.lifespan == other.lifespan)
            }
        } else {
            return false
        }

    }
}

fun <T> apiAiResponse(init: ApiAiResponse.() -> Unit): ApiAiResponse {
    val r = ApiAiResponse()
    r.init()
    return r
}

fun test() {
    apiAiResponse<Any> {
        displayText = "display Text"
        data {
            google {

            }
        }
    }
}
