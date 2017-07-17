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


typealias MyAction = ApiAiApp<Parameters>

const val NORMAL_ASK = "normal.ask"
const val NORMAL_BYE = "normal.ask"
const val BYE_RESPONSE = "bye.response"
const val BYE_CARD = "bye.card"
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

fun welcome(app: MyAction) =
        app.ask(app.buildRichResponse()
                .addSimpleResponse(speech = "Hi there!", displayText = "Hello there!")
                .addSimpleResponse(
                        speech = """I can show you basic cards, lists and carousels as well as
                    "suggestions on your phone""",
                        displayText = """"I can show you basic cards, lists and carousels as
                    "well as suggestions"""")
                .addSuggestions("Basic Card", "List", "Carousel", "Suggestions"))

fun normalAsk(app: MyAction) = app.ask("Ask me to show you a list, carousel, or basic card")

// Suggestions
fun suggestions(app: MyAction) {
    app.ask(app
            .buildRichResponse()
            .addSimpleResponse("This is a simple response for suggestions")
            .addSuggestions("Suggestion Chips")
            .addSuggestions("Basic Card", "List", "Carousel")
            .addSuggestionLink("Suggestion Link", "https://assistant.google.com/"))
}

// Basic card
fun basicCard(app: MyAction) {
    app.ask(app.buildRichResponse()
            .addSimpleResponse("This is the first simple response for a basic card")
            .addSuggestions(
                    "Basic Card", "List", "Carousel", "Suggestions")
            // Create a basic card and add it to the rich response
            .addBasicCard(app.buildBasicCard("""This is a basic card.  Text in a
                    basic card can include "quotes" and most other unicode characters
                    including emoji 📱.  Basic cards also support some markdown
                    formatting like *emphasis* or _italics_, **strong** or __bold__,
            and ***bold itallic*** or ___strong emphasis___ as well as other things
                    like line  \nbreaks""") // Note the two spaces before "\n" required for a
                    // line break to be rendered in the card
                    .setSubtitle("This is a subtitle")
                    .setTitle("Title: this is a title")
                    .addButton("This is a button", "https://assistant.google.com/")
                    .setImage(IMG_URL_AOG, "Image alternate text"))
            .addSimpleResponse(speech = "This is the 2nd simple response ",
                    displayText = "This is the 2nd simple response")
    )
}


// List
fun list(app: MyAction) {
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

// Carousel
fun carousel(app: MyAction) {
    app.askWithCarousel(app.buildRichResponse()
            .addSimpleResponse("This is a simple response for a carousel")
            .addSuggestions("Basic Card", "List", "Carousel", "Suggestions"),
            app.buildCarousel()
                    // Add the first item to the carousel
                    .addItems(app.buildOptionItem(SELECTION_KEY_ONE,
                            "synonym of title 1", "synonym of title 2", "synonym of title 3")
                            .setTitle("Title of First List Item")
                            .setDescription("This is a description of a carousel item")
                            .setImage(IMG_URL_AOG, "Image alternate text"))
                    // Add the second item to the carousel
                    .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_HOME,
                            "Google Home Assistant", "Assistant on the Google Home")
                            .setTitle("Google Home")
                            .setDescription("""Google Home is a voice-activated speaker powered by
                    the Google Assistant.""")
                            .setImage(IMG_URL_GOOGLE_HOME, "Google Home")
                    )
                    // Add third item to the carousel
                    .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_PIXEL,
                            "Google Pixel XL", "Pixel", "Pixel XL")
                            .setTitle("Google Pixel")
                            .setDescription("Pixel. Phone by Google.")
                            .setImage(IMG_URL_GOOGLE_PIXEL, "Google Pixel")
                    )
                    // Add last item of the carousel
                    .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_ALLO)
                            .setTitle("Google Allo")
                            .setDescription("Introducing Google Allo, a smart messaging app" +
                                    "that helps you say more and do more.")
                            .setImage(IMG_URL_GOOGLE_ALLO, "Google Allo Logo")
                            .addSynonyms("Allo")
                    )
    )
}

// Leave conversation with card
fun byeCard(app: MyAction) {
    app.tell(app.buildRichResponse()
            .addSimpleResponse("Goodbye, World!")
            .addBasicCard(app.buildBasicCard("This is a goodbye card.")))
}

fun byeResponse(action: MyAction) =
        action.tell(speech = "Okay see you later",
                displayText = "OK see you later!")

fun normalBye(action: MyAction) = action.tell("Okay see you later!")

val actionMap = mapOf(
        WELCOME to ::welcome,
        NORMAL_ASK to ::normalAsk,
        BASIC_CARD to ::basicCard,
        LIST to ::list,
        CAROUSEL to ::carousel,
        SUGGESTIONS to ::suggestions,
        BYE_CARD to ::byeCard,
        BYE_RESPONSE to ::byeResponse,
        NORMAL_BYE to ::normalBye,
        BYE_RESPONSE to ::byeResponse)

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
            headers.forEach {
                resp.addHeader(it.key, it.value)
            }
            Logger.getAnonymousLogger().info(bodyStr)
            resp.writer.write(bodyStr)
        }))
    }

    fun handleRequest(handler: Map<*, *>) {
        action.handleRequest(handler)
    }
}
