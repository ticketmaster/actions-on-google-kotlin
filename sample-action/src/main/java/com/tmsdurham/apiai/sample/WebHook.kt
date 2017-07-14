package com.tmsdurham.apiai.sample

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ticketmaster.apiai.ApiAiRequest
import com.ticketmaster.apiai.ApiAiResponse
import com.tmsdurham.actions.ApiAiApp
import com.tmsdurham.actions.Handler
import com.tmsdurham.actions.RequestWrapper
import com.tmsdurham.actions.ResponseWrapper
import java.io.InputStreamReader
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
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
        action.tell(speech = "Ask me to show you a list, carousel, or basic card")

fun normalAsk(action: ApiAiApp<Parameters>) =
        action.tell(speech = "Ask me to show you a list, carousel, or basic card")

fun basicCard(action: ApiAiApp<Parameters>) {
    action.ask {}
//    action.ask(action.buildRichResponse())
    //{

//        addSimpleResponse {
//            speech = "test"
//            displayText = "test2"
//        }
//        addSimpleResponse {  }
//
//        displayList {
//        }

//    })

    action.ask {
        textToSpeech = "test"
        displayText = "test"
    }

}

// List
fun list(app: ApiAiApp<Parameters>) {
    app.askWithList(app.buildRichResponse()
            .addSimpleResponse("This is a simple response for a list")
            .addSuggestions("Basic Card", "List", "Carousel", "Suggestions"),
            // Build a list
            app.buildList("List Title")
                    // Add the first item to the list
                    .addItems(app.buildOptionItem(SELECTION_KEY_ONE,
                            "synonym of title 1", "synonym of title 2", "synonym of title 3")
                            .setTitle("Title of First List Item")
                            .setDescription("This is a description of a list item")
                            .setImage(IMG_URL_AOG, "Image alternate text"))
                    // Add the second item to the list
                    .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_HOME,
                            "Google Home Assistant", "Assistant on the Google Home")
                            .setTitle("Google Home")
                            .setDescription("Google Home is a voice-activated speaker powered by the Google Assistant.")
            .setImage(IMG_URL_GOOGLE_HOME, "Google Home")
    )
    // Add third item to the list
    .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_PIXEL,
            "Google Pixel XL", "Pixel", "Pixel XL")
            .setTitle("Google Pixel")
            .setDescription("Pixel. Phone by Google.")
            .setImage(IMG_URL_GOOGLE_PIXEL, "Google Pixel")
    )
            // Add last item of the list
            .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_ALLO)
                    .setTitle("Google Allo")
                    .setDescription("Introducing Google Allo, a smart messaging app" +
                            "that helps you say more and do more.")
                    .setImage(IMG_URL_GOOGLE_ALLO, "Google Allo Logo")
                    .addSynonyms("Allo")
            )
    )
}

fun byeResponse(action: ApiAiApp<Parameters>) =
        action.ask {
            textToSpeech = "Okay see you later"
            displayText = "OK see you later!"
        }

fun normalBye(action: ApiAiApp<Parameters>) = action.ask { textToSpeech = "Okay see you later!" }

val actionMap = mapOf(
        WELCOME to ::welcome,
        NORMAL_ASK to ::normalAsk,
        LIST to ::list,
        BYE_RESPONSE to ::byeResponse,
        NORMAL_BYE to ::normalBye)

@WebServlet(name = "ActionsWebhook", value = "/test")
class WebHook : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        GAction(req, resp, Parameters::class.java).handleRequest(actionMap)
    }
}

/**
 * Gson & Servlet Action - possibly move this into separate module for users of gson & servlet.
 * Intentionally not in sdk module so gson & servlet are not a dependency of the SDK.
 */
class GAction<T>(req: HttpServletRequest, resp: HttpServletResponse, clazz: Class<T>, val gson: Gson = Gson()) {
    val action: ApiAiApp<T>
    init {
        val t = TypeToken.get(clazz).type
        val type = TypeToken.getParameterized(ApiAiRequest::class.java, t)
        val request = gson.fromJson<ApiAiRequest<T>>(InputStreamReader(req.inputStream), type.type)
        action = ApiAiApp(RequestWrapper(body = request), ResponseWrapper(sendAction = {
            val bodyStr = gson.toJson(body)
            resp.writer.write(bodyStr)
        }))
    }

    fun handleRequest(handler: Map<*, *>) {
        action.handleRequest(handler)
    }
}
