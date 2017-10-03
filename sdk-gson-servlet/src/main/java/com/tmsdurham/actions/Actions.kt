package main.java.com.tmsdurham.apiai.sample

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ticketmaster.apiai.ApiAiRequest
import com.ticketmaster.apiai.ApiAiResponse
import com.tmsdurham.actions.*
import com.tmsdurham.actions.actions.ActionRequest
import com.tmsdurham.actions.actions.ActionResponse
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Gson & Servlet Action for Api.Ai
 * Intentionally not in sdk module so gson & servlet are not a dependency of the SDK.
 */
class ApiAiAction(req: HttpServletRequest,
                  resp: HttpServletResponse,
                  val gson: Gson,
                  val beforeSending: ((ApiAiResponse, String) -> Unit)? = null) {
    val app: ApiAiApp

    //needed for 2 arg constructor from Java
    constructor(req: HttpServletRequest, resp: HttpServletResponse) : this(req, resp, Gson())

    init {
        val jsonStr = convertStreamToString(req.inputStream)
        Logger.getAnonymousLogger().info(jsonStr)
        val request = gson.fromJson<ApiAiRequest>(jsonStr, ApiAiRequest::class.java)
        val headers = resp.headerNames.associate { it to resp.getHeader(it) }
        app = ApiAiApp(RequestWrapper(body = request, headers = headers), ResponseWrapper(sendAction = {
            val bodyStr = gson.toJson(body)
            headers.forEach {
                resp.addHeader(it.key, it.value)
            }
            beforeSending?.invoke(this.body ?: ApiAiResponse(), bodyStr)
            debug(bodyStr)
            resp.contentType = "application/json"
            resp.characterEncoding = "UTF-8"
            resp.writer.write(bodyStr)
        }))
    }

    fun convertStreamToString(input: java.io.InputStream): String {
        val s = java.util.Scanner(input).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    fun handleRequest(handler: Map<*, *>) {
        app.handleRequest(handler)
    }
}

/**
 * Gson & Servlet Action for Actions SDK (direct integration)
 * Intentionally not in sdk module so gson & servlet are not a dependency of the SDK.
 */
class ActionsSdkAction(req: HttpServletRequest,
                       resp: HttpServletResponse,
                       val gson: Gson = GsonBuilder()
                               .registerTypeAdapter(OrderUpdate::class.java, OrderUpdateTypeAdapter(Gson()))
                               .create(),
                       val beforeSending: ((ActionResponse, String) -> Unit)? = null) {
    val app: ActionsSdkApp

    init {
        val jsonStr = convertStreamToString(req.inputStream)
        Logger.getAnonymousLogger().info(jsonStr)
        val request = gson.fromJson<ActionRequest>(jsonStr, ActionRequest::class.java)
        val headers = resp.headerNames.associate { it to resp.getHeader(it) }

        app = ActionsSdkApp(RequestWrapper(body = request, headers = headers), ResponseWrapper(sendAction = {
            val bodyStr = gson.toJson(body)
            headers.forEach {
                resp.addHeader(it.key, it.value)
            }
            beforeSending?.invoke(this.body ?: ActionResponse(), bodyStr)
            debug(bodyStr)
            resp.contentType = "application/json"
            resp.characterEncoding = "UTF-8"
            resp.writer.write(bodyStr)
        }), serializer = object : Serializer {
            override fun <T> serialize(obj: T) = gson.toJson(obj)

            override fun <T> deserialize(str: String, clazz: Class<T>) = gson.fromJson(str, clazz)

        })
    }

    fun convertStreamToString(input: java.io.InputStream): String {
        val s = java.util.Scanner(input).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else ""
    }

    fun handleRequest(handler: Map<*, *>) {
        app.handleRequest(handler)
    }
}