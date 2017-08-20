package com.tmsdurham.actions

import main.java.com.tmsdurham.apiai.sample.ActionsSdkAction
import java.util.logging.Logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// Copyright 2017, Google, Inc.
// Licensed under the Apache License, Version 2.0 (the "License")
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Constants for list and carousel selection
val logger = Logger.getAnonymousLogger()

val SELECTION_KEY_ONE = "title"
val SELECTION_KEY_GOOGLE_HOME = "googleHome"
val SELECTION_KEY_GOOGLE_PIXEL = "googlePixel"
val SELECTION_KEY_GOOGLE_ALLO = "googleAllo"

// Constant for image URLs
val IMG_URL_AOG = "https://developers.google.com/actions/images/badges" +
        "/XPM_BADGING_GoogleAssistant_VER.png"
val IMG_URL_GOOGLE_HOME = "https://lh3.googleusercontent.com" +
        "/Nu3a6F80WfixUqf_ec_vgXy_c0-0r4VLJRXjVFF_X_CIilEu8B9fT35qyTEj_PEsKw"
val IMG_URL_GOOGLE_PIXEL = "https://storage.googleapis.com/madebygoog/v1" +
        "/Pixel/Pixel_ColorPicker/Pixel_Device_Angled_Black-720w.png"
val IMG_URL_GOOGLE_ALLO = "https://allo.google.com/images/allo-logo.png"

// Welcome
fun welcome(app: ActionsSdkApp) =
        app.ask(app.buildRichResponse()
                .addSimpleResponse(speech = "Hi there!", displayText = "Hello there!")
                .addSimpleResponse(
                        speech = "I can show you basic cards, lists and carousels as well as " +
                                "suggestions on your phone",
                        displayText = "I can show you basic cards, lists and carousels as " +
                                "well as suggestions"
                )
                .addSuggestions(
                        "Basic Card", "List", "Carousel", "Suggestions"))

fun normalAsk(app: ActionsSdkApp) =
        app.ask("Ask me to show you a list, carousel, or basic card")

// Suggestions
fun suggestions(app: ActionsSdkApp) =
        app.ask(app
                .buildRichResponse()
                .addSimpleResponse("This is a simple response for suggestions")
                .addSuggestions("Suggestion Chips")
                .addSuggestions("Basic Card", "List", "Carousel")
                .addSuggestionLink("Suggestion Link", "https://assistant.google.com/"))

// Basic card
fun basicCard(app: ActionsSdkApp) {
    app.ask(app.buildRichResponse()
            .addSimpleResponse("This is the first simple response for a basic card")
            .addSuggestions(
                    "Basic Card", "List", "Carousel", "Suggestions")
            // Create a basic card and add it to the rich response
            .addBasicCard(app.buildBasicCard(""" This is a basic card . Text in a
                    basic card can include "quotes" and most other unicode characters
                    including emoji 📱.  Basic cards also support some markdown
                    formatting like * emphasis * or _italics_, ** strong * * or __bold__,
            and * * * bold itallic * * * or ___strong emphasis___ as well as other things
                    like line  \nbreaks """) // Note the two spaces before "\n" required for a
                    // line break to be rendered in the card
                    .setSubtitle("This is a subtitle")
                    .setTitle("Title: this is a title")
                    .addButton("This is a button", "https://assistant.google.com/")
                    .setImage(IMG_URL_AOG, "Image alternate text")
            )
            .addSimpleResponse(
                    speech = "This is the second simple response ",
                    displayText = "This is the 2nd simple response")
    )
}

// List
fun list(app: ActionsSdkApp) {
    app.askWithList(app.buildRichResponse()
            .addSimpleResponse("This is a simple response for a list")
            .addSuggestions(
                    "Basic Card", "List", "Carousel", "Suggestions"),
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
                            .setDescription(""" Google Home is a voice - activated speaker powered by
                    the Google Assistant.""")
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
fun carousel(app: ActionsSdkApp) {
    app.askWithCarousel(app.buildRichResponse()
            .addSimpleResponse("This is a simple response for a carousel")
            .addSuggestions(
                    "Basic Card", "List", "Carousel", "Suggestions"),
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
                            .setDescription(""" Google Home is a voice - activated speaker powered by
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
fun itemSelected(app: ActionsSdkApp) {
    val param = app.getSelectedOption()
    logger.info("USER SELECTED: " + param)
    when (param) {
        null -> app.ask("You did not select any item from the list or carousel")
        SELECTION_KEY_ONE -> app.ask("You selected the first item in the list or carousel")
        SELECTION_KEY_GOOGLE_HOME -> app.ask("You selected the Google Home!")
        SELECTION_KEY_GOOGLE_PIXEL -> app.ask("You selected the Google Pixel!")
        SELECTION_KEY_GOOGLE_ALLO -> app.ask("You selected Google Allo!")
        else -> app.ask("You selected an unknown item from the list or carousel")
    }
}

// Leave conversation with card
fun byeCard(app: ActionsSdkApp) {
    app.tell(app.buildRichResponse()
            .addSimpleResponse("Goodbye, World!")
            .addBasicCard(app.buildBasicCard("This is a goodbye card.")))
}

// Leave conversation with SimpleResponse
fun byeResponse(app: ActionsSdkApp) {
    app.tell(
            speech = "Okay see you later",
            displayText = "OK see you later!"
    )
}

// Leave conversation
fun normalBye(app: ActionsSdkApp) {
    app.tell("Okay see you later!")
}

fun actionsText(app: ActionsSdkApp) {
    val rawInput = app.getRawInput()
    logger.info("USER SAID " + rawInput)
    when (rawInput) {
        "Basic Card", "basic card" -> basicCard(app)
        "List", "list" -> list(app)
        "Carousel", "carousel" -> carousel(app)
        "normal ask" -> normalAsk(app)
        "normal bye" -> normalBye(app)
        "bye card" -> byeCard(app)
        "bye response" -> byeResponse(app)
        "Suggestions", "Suggestion Chips",
        "suggestions", "suggestions chips" ->
            suggestions(app)
        else ->
            normalAsk(app)
    }
}

@WebServlet("/conversation")
class ConversationComponentsSample : HttpServlet() {
    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {

        val app = ActionsSdkAction(req, resp)
        val actionMap = mutableMapOf(
                app.app.STANDARD_INTENTS.MAIN to ::welcome,
                app.app.STANDARD_INTENTS.TEXT to ::actionsText,
                app.app.STANDARD_INTENTS.OPTION to ::itemSelected)
        logger.info("app.STANDARD_INTENTS.MAIN: ${app.app.STANDARD_INTENTS.MAIN}")
        app.handleRequest(actionMap)
    }
}

