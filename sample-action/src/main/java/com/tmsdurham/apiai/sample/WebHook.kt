package com.tmsdurham.apiai.sample

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ticketmaster.apiai.ApiAiRequest
import com.ticketmaster.apiai.apiAiResponse
import java.io.InputStreamReader
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "webhook", value = "/test")
class WebHook : HttpServlet() {
    val gson = Gson()
    val logger = Logger.getAnonymousLogger()

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val t = TypeToken.get(Parameters::class.java).type
        val type = TypeToken.getParameterized(ApiAiRequest::class.java, t)
        val request = gson.fromJson<ApiAiRequest<Parameters>>(InputStreamReader(req.inputStream), type.type)
        logger.warning(gson.toJson(request))
        val response = apiAiResponse {
            speech = "test"
            displayText = "display test"
            source = "kotlinsdk"
        }
        resp.writer.write(gson.toJson(response))
    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.writer.write("Hello")

    }

}