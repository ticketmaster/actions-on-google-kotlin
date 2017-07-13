package com.ticketmaster.apiai

data class ApiAiResponse<T>(var speech: String = "", var displayText: String = "", var secondDisplayText: String = "",
                            var data: Data = Data.empty, var contextOut: MutableList<ContextOut<T>> = mutableListOf(), var source: String = "") {
    fun data(init: Data.() -> Unit): Data {
        val data = Data()
        data.init()
        return data
    }
}

data class ContextOut<T>(var name: String, var lifespan: Int, var parameters: T? = null) {
    override fun equals(other: Any?): Boolean {
        if (other is ContextOut<*>) {
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
