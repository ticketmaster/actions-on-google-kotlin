package com.tmsdurham.actions

import main.java.com.tmsdurham.apiai.sample.ApiAiAction
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


val logger: Logger = Logger.getAnonymousLogger()

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

fun welcome(app: ApiAiApp) =
        app.ask(app.buildRichResponse()
                .addSimpleResponse(speech = "Hi there!", displayText = "Hello there!")
                .addSimpleResponse(
                        speech = """I can show you basic cards, lists and carousels as well as suggestions on your phone""",
                        displayText = """I can show you basic cards, lists and carousels as well as suggestions""")
                .addSuggestions("Basic Card", "List", "Carousel", "Suggestions"))

fun normalAsk(app: ApiAiApp) = app.ask("Ask me to show you a list, carousel, or basic card")

fun suggestions(app: ApiAiApp) =
        app.ask(app
                .buildRichResponse()
                .addSimpleResponse("This is a simple response for suggestions")
                .addSuggestions("Suggestion Chips")
                .addSuggestions("Basic Card", "List", "Carousel")
                .addSuggestionLink("Suggestion Link", "https://assistant.google.com/"))

fun basicCard(app: ApiAiApp) =
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
                    like line""" + "  \nbreaks") // Note the two spaces before "\n" required for a
                        // line break to be rendered in the card
                        .setSubtitle("This is a subtitle")
                        .setTitle("Title: this is a title")
                        .addButton("This is a button", "https://assistant.google.com/")
                        .setImage(IMG_URL_AOG, "Image alternate text"))
                .addSimpleResponse(speech = "This is the 2nd simple response ",
                        displayText = "This is the 2nd simple response")
        )


fun list(app: ApiAiApp) {
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
fun carousel(app: ApiAiApp) {
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

// React to list or carousel selection
fun itemSelected(app: ApiAiApp) {
    app.getIntent()
    val param = app.getSelectedOption()
    logger.info("USER SELECTED: $param")
    when (param) {
        null -> app.ask("You did not select any item from the list or carousel")
        SELECTION_KEY_ONE -> app.ask("You selected the first item in the list or carousel")
        SELECTION_KEY_GOOGLE_HOME -> app.ask("You selected the Google Home!")
        SELECTION_KEY_GOOGLE_PIXEL -> app.ask("You selected the Google Pixel!")
        SELECTION_KEY_GOOGLE_ALLO -> app.ask("You selected Google Allo!")
        else -> app.ask("You selected an unknown item from the list or carousel")
    }
}

// Receive a rich response from API.AI and modify it
fun cardBuilder(app: ApiAiApp) =
        app.ask(app.getIncomingRichResponse()
                .addBasicCard(app.buildBasicCard("""Actions on Google let you build for
            the Google Assistant. Reach users right when they need you. Users don’t
            need to pre-enable skills or install new apps.""" + "  \n  \nThis was written in the fulfillment webhook!")
                        .setSubtitle("Engage users through the Google Assistant")
                        .setTitle("Actions on Google")
                        .addButton("Developer Site", "https://developers.google.com/actions/")
                        .setImage("https://lh3.googleusercontent.com/Z7LtU6hhrhA-5iiO1foAfGB" +
                                "75OsO2O7phVesY81gH0rgQFI79sjx9aRmraUnyDUF_p5_bnBdWcXaRxVm2D1Rub92" +
                                "L6uxdLBl=s1376", "Actions on Google")))

// Leave conversation with card
fun byeCard(app: ApiAiApp) =
        app.tell(app.buildRichResponse()
                .addSimpleResponse("Goodbye, World!")
                .addBasicCard(app.buildBasicCard("This is a goodbye card.")))

fun byeResponse(action: ApiAiApp) =
        action.tell(speech = "Okay see you later",
                displayText = "OK see you later!")

fun normalBye(action: ApiAiApp) = action.tell("Okay see you later!")

val actionMap = mapOf(
        WELCOME to ::welcome,
        NORMAL_ASK to ::normalAsk,
        BASIC_CARD to ::basicCard,
        LIST to ::list,
        ITEM_SELECTED to ::itemSelected,
        CAROUSEL to ::carousel,
        SUGGESTIONS to ::suggestions,
        BYE_CARD to ::byeCard,
        NORMAL_BYE to ::normalBye,
        BYE_RESPONSE to ::byeResponse,
        CARD_BUILDER to ::cardBuilder)

@WebServlet(name = "ActionsWebhook", value = "/conversation")
class WebHook : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        ApiAiAction(req, resp).handleRequest(actionMap)
    }
}

