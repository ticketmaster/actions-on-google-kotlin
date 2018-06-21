package actions.expected

import actions.framework.*
import actions.service.actionssdk.ActionsSdkConversation
import actions.service.actionssdk.ActionsSdkConversationOptions
import actions.service.actionssdk.ActionsSdkOptions
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.actionssdk.api.GoogleActionsV2AppResponse
import com.google.gson.Gson
import java.io.InputStream
import java.io.InputStreamReader
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

actual class BuiltinFrameworks<TUserStorage> : Frameworks<TUserStorage>() {
    init {
//        add(ServletFramework())
        //add frameworks
    }
}

val gson = Gson() //TODO make param or other solution

class ServletHandler<TConvData, TUserStorage>(val standardHandler: StandardHandler<TUserStorage>) : OmniHandler {

    override fun handle(vararg args: Any): Any {
        log("Servlet Handler: handle $args")
        val req = args[0] as HttpServletRequest
        val headers = req.getAoGHeaders()
        //TODO check for debug flag for logging
        val copies = ServletUtils.copyBuffer(req.inputStream)
        logger.info(ServletUtils.getBody(copies[0], null))

        val body: GoogleActionsV2AppRequest = requestToActionsSdkRequest(copies[1])
        log("Request: $body")
        val resp = args[1] as HttpServletResponse


        val standardResponse = standardHandler.handle(body, headers)
        resp.status = standardResponse.status!!
        standardResponse.headers?.forEach {
            val name = it.key
            it.value.forEach {
                resp.setHeader(name, it)
            }
        }

        val respJson = standardResponse.serialize()
        log("Response: $respJson")
        resp.writer.write(respJson)
        return this
    }

    fun requestToActionsSdkRequest(inputStream: InputStream): GoogleActionsV2AppRequest {
//        val options = ActionsSdkConversationOptions<TConvData, TUserStorage>(body = GoogleActionsV2AppRequest(), headers = null)
        return gson.fromJson(InputStreamReader(inputStream), GoogleActionsV2AppRequest::class.java)
    }

    fun StandardResponse.serialize(): String? {
        return gson.toJson(this.body, GoogleActionsV2AppResponse::class.java)
    }

    fun HttpServletRequest.getAoGHeaders(): Headers =
            headerNames.asSequence().associate { it to getHeaders(it) }.toMutableMap() as Headers
}
//typealias ServletHandler = (HttpServletRequest, HttpServletResponse) -> Unit

class ServletFramework<TConvData, TUserStorage> : Framework<TUserStorage> {

    override fun handle(base: StandardHandler<TUserStorage>): OmniHandler {
//      base.handle(body = ,
//              headers = )
        return ServletHandler<TConvData, TUserStorage>(base)
    }

    override fun check(vararg args: Any): Boolean = (args.size > 1 && args.first() is HttpServletRequest)


}

