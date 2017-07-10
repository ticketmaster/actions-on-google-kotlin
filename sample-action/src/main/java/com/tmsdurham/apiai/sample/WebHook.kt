package com.tmsdurham.apiai.sample

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ticketmaster.apiai.ApiAiRequest
import com.tmsdurham.actions.ApiAiApp
import com.tmsdurham.actions.RequestWrapper
import com.tmsdurham.actions.ResponseWrapper
import java.io.InputStreamReader
import java.util.logging.Logger
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


const val NORMAL_ASK = "normal.ask"
const val NORMAL_BYE = "normal.ask"
const val BYE_RESPONSE = "bye.response"
const val WELCOME = "input.welcome"
const val BASIC_CARD = "basic.card"
const val LIST = "list"
const val CAROUSEL = "carousel"
const val SUGGESTIONS = "suggestions"
const val ITEM_SELECTED = "item.selected"
const val CARD_BUILDER = "card.builder"


// Constants for list and carousel selection
const val SELECTION_KEY_ONE = "title"
const val SELECTION_KEY_GOOGLE_HOME = "googleHome"
const val SELECTION_KEY_GOOGLE_PIXEL = "googlePixel"
const val SELECTION_KEY_GOOGLE_ALLO = "googleAllo"

// Constant for image URLs
const val IMG_URL_AOG = "https://developers.google.com/actions/images/badges" +
        "/XPM_BADGING_GoogleAssistant_VER.png"
const val IMG_URL_GOOGLE_HOME = "https://lh3.googleusercontent.com" +
        "/Nu3a6F80WfixUqf_ec_vgXy_c0-0r4VLJRXjVFF_X_CIilEu8B9fT35qyTEj_PEsKw"
const val IMG_URL_GOOGLE_PIXEL = "https://storage.googleapis.com/madebygoog/v1" +
        "/Pixel/Pixel_ColorPicker/Pixel_Device_Angled_Black-720w.png"
const val IMG_URL_GOOGLE_ALLO = "https://allo.google.com/images/allo-logo.png"

fun welcome(action: ApiAiApp<Parameters>) =
        action.ask { speech = "Ask me to show you a list, carousel, or basic card" }

fun normalAsk(action: ApiAiApp<Parameters>) =
        action.ask { speech = "Ask me to show you a list, carousel, or basic card" }

fun basicCard(action: ApiAiApp<Parameters>) {
    action.ask {}
    action.ask(action.buildRichResponse {
//        addSimpleResponse {
//            speech = "test"
//            displayText = "test2"
//        }
//        addSimpleResponse {  }
//
//        displayList {
//        }

    })

    action.ask {
        speech = "test"
        displayText = "test"
    }

}

fun byeResponse(action: ApiAiApp<Parameters>) =
        action.ask { speech = "Okay see you later"
            displayText = "OK see you later!" }

fun normalBye(action: ApiAiApp<Parameters>) = action.ask { speech = "Okay see you later!" }

val actionMap = mapOf(
        WELCOME to ::welcome,
        NORMAL_ASK to ::normalAsk,
        BYE_RESPONSE to ::byeResponse,
        NORMAL_BYE to ::normalBye)

class WebHook : HttpServlet() {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val logger = Logger.getAnonymousLogger()

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val t = TypeToken.get(Parameters::class.java).type
        val type = TypeToken.getParameterized(ApiAiRequest::class.java, t)
        val request = gson.fromJson<ApiAiRequest<Parameters>>(InputStreamReader(req.inputStream), type.type)
        val action = ApiAiApp(RequestWrapper(request), ResponseWrapper())
        logger.warning(gson.toJson(request))

        val response = action.handleRequest(actionMap)
        resp.writer.write(gson.toJson(response))
    }
}
