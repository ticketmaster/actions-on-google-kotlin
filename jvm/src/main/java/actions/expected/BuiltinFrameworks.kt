package actions.expected

import actions.framework.*
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.dialogflow.api.DialogflowV1WebhookRequest
import actions.service.dialogflow.api.GoogleCloudDialogflowV2WebhookRequest
import actions.service.dialogflow.api.GoogleCloudDialogflowV2WebhookResponse
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


class ServletHandler<TUserStorage>(val standardHandler: StandardHandler<TUserStorage>) : OmniHandler {

    override fun handle(vararg args: Any): Any {
        log("Servlet Handler: handle $args")
        val req = args[0] as HttpServletRequest
        val headers = req.getAoGHeaders()
        //TODO check for debug flag for logging
        val copies = ServletUtils.copyBuffer(req.inputStream)
        logger.info(ServletUtils.getBody(copies[0], null))
        log(headers.toString())
        val body: GoogleCloudDialogflowV2WebhookRequest = requestToDialogflowV2Request(copies[1])
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
        return Serializer.aogGson.fromJson(InputStreamReader(inputStream), GoogleActionsV2AppRequest::class.java)
    }

    fun requestToDialogflowV1Request(inputStream: InputStream): DialogflowV1WebhookRequest {
//        val options = ActionsSdkConversationOptions<TConvData, TUserStorage>(body = GoogleActionsV2AppRequest(), headers = null)
        return Serializer.aogGson.fromJson(InputStreamReader(inputStream), DialogflowV1WebhookRequest::class.java)
    }

    fun requestToDialogflowV2Request(inputStream: InputStream): GoogleCloudDialogflowV2WebhookRequest {
//        val options = ActionsSdkConversationOptions<TConvData, TUserStorage>(body = GoogleActionsV2AppRequest(), headers = null)
        return Serializer.aogGson.fromJson(InputStreamReader(inputStream), GoogleCloudDialogflowV2WebhookRequest::class.java)
    }

    fun StandardResponse.serialize(): String? {
        return Serializer.aogGson.toJson(this.body, GoogleCloudDialogflowV2WebhookResponse::class.java)
    }

    fun HttpServletRequest.getAoGHeaders(): Headers =
            headerNames.asSequence().associate { it to getHeaders(it) }.toMutableMap() as Headers
}
//typealias ServletHandler = (HttpServletRequest, HttpServletResponse) -> Unit

class ServletFramework<TUserStorage> : Framework<TUserStorage> {

    override fun handle(base: StandardHandler<TUserStorage>): OmniHandler {
//      base.handle(body = ,
//              headers = )
        return ServletHandler<TUserStorage>(base)
    }

    override fun check(vararg args: Any): Boolean = (args.size > 1 && args.first() is HttpServletRequest)


}

