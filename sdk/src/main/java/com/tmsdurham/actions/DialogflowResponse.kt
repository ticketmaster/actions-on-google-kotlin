package com.tmsdurham.dialogflow

data class DialogflowResponse(var speech: String = "", var displayText: String? = null, var secondDisplayText: String? = null,
                              var data: Data = Data(), var contextOut: MutableList<Context> = mutableListOf(), var source: String = "") {
    fun data(init: Data.() -> Unit): Data {
        data.init()
        return data
    }
}
