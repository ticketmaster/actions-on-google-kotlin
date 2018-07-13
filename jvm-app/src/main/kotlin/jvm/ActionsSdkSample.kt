package jvm

import actions.expected.logger
import actions.service.actionssdk.conversation.response.List
import actions.service.actionssdk.actionssdk
import actions.service.actionssdk.conversation.Conversation
import actions.service.actionssdk.conversation.ConversationApp
import actions.service.actionssdk.conversation.question.option.OptionItem
import actions.service.actionssdk.conversation.question.option.item
import actions.service.actionssdk.conversation.response.*
import actions.service.actionssdk.conversation.response.card.Button
import actions.service.dialogflow.dialogflow
import javax.swing.text.html.Option


// const valants for list and carousel selection
const val SELECTION_KEY_GOOGLE_ALLO = "googleAllo"
const val SELECTION_KEY_GOOGLE_HOME = "googleHome"
const val SELECTION_KEY_GOOGLE_PIXEL = "googlePixel"
const val SELECTION_KEY_ONE = "title"

// const valants for image URLs
const val IMG_URL_AOG = "https://developers.google.com/actions/images/badges" +
        "/XPM_BADGING_GoogleAssistant_VER.png"
const val IMG_URL_GOOGLE_ALLO = "https://allo.google.com/images/allo-logo.png"
const val IMG_URL_GOOGLE_HOME = "https://lh3.googleusercontent.com" +
        "/Nu3a6F80WfixUqf_ec_vgXy_c0-0r4VLJRXjVFF_X_CIilEu8B9fT35qyTEj_PEsKw"
const val IMG_URL_GOOGLE_PIXEL = "https://storage.googleapis.com/madebygoog/v1" +
        "/Pixel/Pixel_ColorPicker/Pixel_Device_Angled_Black-720w.png"

// const valants for selected item responses
val SELECTED_ITEM_RESPONSES = mapOf(
        SELECTION_KEY_ONE to "You selected the first item in the list or carousel",
        SELECTION_KEY_GOOGLE_HOME to "You selected the Google Home!",
        SELECTION_KEY_GOOGLE_PIXEL to "You selected the Google Home!",
        SELECTION_KEY_GOOGLE_PIXEL to "You selected the Google Pixel!",
        SELECTION_KEY_GOOGLE_ALLO to "You selected Google Allo!")

private val intentSuggestions = arrayOf(
        "Basic Card",
        "Browse Carousel",
        "Carousel",
        "List",
        "Media",
        "Suggestions")

val app = actionssdk<UserStorage>({ debug = true })

data class MyConversation(val temp: String? = null)
data class MyArgument(val temp: String? = null,
                      var resultType: String? = null,
                      var userDescision: String? = null)


fun initActionsApp() {
//    app.middleware((conv) => {
//        conv.hasScreen =
//                conv.surface.capabilities.has("actions.capability.SCREEN_OUTPUT")
//        conv.hasAudioPlayback =
//                conv.surface.capabilities.has("actions.capability.AUDIO_OUTPUT")
//    })

// Welcome
    app.intent("actions.intent.MAIN", "input.unknown") { conv ->
        conv.ask(SimpleResponse {
            speech = "Hi there!"
            text = "Hello there!"
        })
        conv.ask(SimpleResponse {
            speech = "I can show you basic cards, lists and carousels " +
                    "as well as suggestions on your phone."
            text = "I can show you basic cards, lists and carousels as " +
                    "well as suggestions."
        })
        conv.ask(Suggestions(*intentSuggestions))
    }

// React to a text intent
    app.intent("actions.intent.TEXT") { conv, input ->
        val rawInput = (input as String).toLowerCase()
        logger.info("USER SAID " + rawInput)
        when (rawInput) {
            "basic card" -> basicCard(conv)
            "list" -> list(conv)
            "carousel" -> carousel(conv)
            "normal ask" -> normalAsk(conv)
            "normal bye" -> normalBye(conv)
            "bye card" -> byeCard(conv)
            "bye response" -> byeResponse(conv)
            "suggestions", "suggestion chips" -> suggestions(conv)
            "test" -> test(conv)
            else -> normalAsk(conv)

        }


// React to list or carousel selection
        app.intent("actions.intent.OPTION") { conv, params, option ->
            val response = if (option != null && SELECTED_ITEM_RESPONSES.containsKey(option.textValue)) {
                SELECTED_ITEM_RESPONSES[option.textValue] ?: ""
            } else {
                "You selected an unknown item from the list or carousel"
            }
            conv.ask(response)
        }
    }
}

/**
 * Test
 */
fun <TUserStorage> test(conv: Conversation<TUserStorage>) {
//    conv.data.lastResponse = "This was the last response"
//    conv.user.storage.name = "fred"
    conv.ask("Can you hear me?")
}

/**
 * Normal Ask
 * @param {object} conv - The conversation object.
 */
fun <TUserStorage> normalAsk(conv: Conversation<TUserStorage>) {
    conv.ask("Ask me to show you a list, carousel, or basic card.")
}

/**
 * Suggestions
 * @param {object} conv - The conversation object.
 */
fun <TUserStorage> suggestions(conv: Conversation<TUserStorage>) {
    if (!conv.hasScreen()) {
        conv.ask("Sorry, try this on a screen device or select the " +
                "phone surface in the simulator.")
        return
    }
    conv.ask("This is a simple response for suggestions.")
    conv.ask(Suggestions("Suggestion Chips"))
    conv.ask(Suggestions(*intentSuggestions))
    conv.ask(LinkOutSuggestion {
        name = "Suggestion Link"
        url = "https://assistant.google.com/"
    })
}

/**
 * Basic Card
 * @param {object} conv - The conversation object.
 */
fun <TUserStorage> basicCard(conv: Conversation<TUserStorage>) {
    if (!conv.hasScreen()) {
        conv.ask("Sorry, try this on a screen device or select the " +
                "phone surface in the simulator.")
        return
    }
    conv.ask("This is the first simple response for a basic card.")
    conv.ask(Suggestions(*intentSuggestions))
    // Create a basic card
    conv.ask(BasicCard {
        text = """This is a basic card.  Text in a basic card can include "quotes" and
    most other unicode characters including emoji 📱.  Basic cards also support
            some markdown formatting like * emphasis * or _italics_, **strong** or
    __bold__, and ***bold itallic*** or ___strong emphasis___ as well as other
    things like line  \nbreaks""" // Note the two spaces before "\n" required for
        // a line break to be rendered in the card.
        subtitle = "This is a subtitle"
        title = "Title: this is a title"
        buttons = mutableListOf(Button {
            title = "This is a button"
            url = "https://assistant.google.com/"
        })
        image = Image {
            url = IMG_URL_AOG
            alt = "Image alternate text"
        }
    })
    conv.ask(SimpleResponse {
        speech = "This is the second simple response."
        text = "This is the 2nd simple response."
    })
}

/**
 * List
 * @param {object} conv - The conversation object.
 */
fun <TUserStorage> list(conv: Conversation<TUserStorage>) {
    if (!conv.hasScreen()) {
        conv.ask("Sorry, try this on a screen device or select the " +
                "phone surface in the simulator.")
        return
    }
    conv.ask("This is a simple response for a list.")
    conv.ask(Suggestions(*intentSuggestions))
    // Create a list
    conv.ask(List {
        title = "List Title"
        items = mutableMapOf(
                // Add the first item to the list
                SELECTION_KEY_ONE to OptionItem(
                        synonyms = mutableListOf(
                                "synonym of title 1",
                                "synonym of title 2",
                                "synonym of title 3"),

                        title = "Title of First List Item",
                        description = "This is a description of a list item.",
                        image = Image {
                            url = IMG_URL_AOG
                            alt = "Image alternate text"
                        }),

                // Add the second item to the list
                SELECTION_KEY_GOOGLE_HOME to OptionItem(
                        synonyms = mutableListOf(
                                "Google Home Assistant",
                                "Assistant on the Google Home"),

                        title = "Google Home",
                        description = "Google Home is a voice-activated speaker powered by " +
                                "the Google Assistant.",
                        image = Image {
                            url = IMG_URL_GOOGLE_HOME
                            alt = "Google Home"
                        }),
                // Add the third item to the list
                SELECTION_KEY_GOOGLE_PIXEL to OptionItem(
                        synonyms = mutableListOf(
                                "Google Pixel XL",
                                "Pixel",
                                "Pixel XL"),
                        title = "Google Pixel",
                        description = "Pixel. Phone by Google.",
                        image = Image {
                            url = IMG_URL_GOOGLE_PIXEL
                            alt = "Google Pixel"
                        }),
                // Add the last item to the list
                SELECTION_KEY_GOOGLE_ALLO to OptionItem(
                        title = "Google Allo",
                        synonyms = mutableListOf("Allo"),
                        description = "Introducing Google Allo, a smart messaging app that " +
                                "helps you say more and do more.",
                        image = Image {
                            url = IMG_URL_GOOGLE_ALLO
                            alt = "Google Allo Logo"
                        })
        )

    })
}

/**
 * Carousel
 * @param {object} conv - The conversation object.
 */
fun <TUserStorage> carousel(conv: Conversation<TUserStorage>) {
    if (!conv.hasScreen()) {
        conv.ask("Sorry, try this on a screen device or select the " +
                "phone surface in the simulator.")
        return
    }
    conv.ask("This is a simple response for a carousel.")
    conv.ask(Suggestions(*intentSuggestions))
    // Create a carousel
    conv.ask(Carousel {
        items = mutableMapOf(
                // Add the first item to the carousel
                SELECTION_KEY_ONE to OptionItem(
                        synonyms = mutableListOf(
                                "synonym of title 1",
                                "synonym of title 2",
                                "synonym of title 3"),

                        title = "Title of First Carousel Item",
                        description = "This is a description of a carousel item.",
                        image = Image {
                            url = IMG_URL_AOG
                            alt = "Image alternate text"
                        }),
                // Add the second item to the carousel
                SELECTION_KEY_GOOGLE_HOME to OptionItem(
                        synonyms = mutableListOf(
                                "Google Home Assistant",
                                "Assistant on the Google Home"),
                        title = "Google Home",
                        description = "Google Home is a voice-activated speaker powered by " +
                                "the Google Assistant.",
                        image = Image {
                            url = IMG_URL_GOOGLE_HOME
                            alt = "Google Home"
                        }),
                // Add third item to the carousel
                SELECTION_KEY_GOOGLE_PIXEL to OptionItem(
                        synonyms = mutableListOf(
                                "Google Pixel XL",
                                "Pixel",
                                "Pixel XL"),
                        title = "Google Pixel",
                        description = "Pixel. Phone by Google.",
                        image = Image {
                            url = IMG_URL_GOOGLE_PIXEL
                            alt = "Google Pixel"
                        }),
                // Add last item of the carousel
                SELECTION_KEY_GOOGLE_ALLO to OptionItem(
                        title = "Google Allo",
                        synonyms = mutableListOf(
                                "Allo"),
                        description = "Introducing Google Allo, a smart messaging app that " +
                                "helps you say more and do more.",
                        image = Image {
                            url = IMG_URL_GOOGLE_ALLO
                            alt = "Google Allo Logo"
                        })
        )
    })
}

/**
 * Leave conversation with card
 * @param {object} conv - The conversation object.
 */
fun <TUserStorage> byeCard(conv: Conversation<TUserStorage>) {
    if (!conv.hasScreen()) {
        conv.ask("Sorry, try this on a screen device or select the phone " +
                "surface in the simulator.")
        return
    }
    conv.ask("Goodbye, World!")
    conv.close(BasicCard {
        text = "This is a goodbye card."
    })
}

/**
 * Leave conversation with SimpleResponse
 * @param {object} conv - The conversation object.
 */
fun <TUserStorage> byeResponse(conv: Conversation<TUserStorage>) {
    conv.close(SimpleResponse {
        speech = "Okay see you later"
        text = "OK see you later!"
    })
}

/**
 * Leave conversation
 * @param {object} conv - The conversation object.
 */
fun <TUserStorage> normalBye(conv: Conversation<TUserStorage>) {
    conv.close("Okay see you later!")
}

//fun wrapper(arg1, arg2) {
//    console.log("arg1: ", JSON.stringify(arg1.body))
//    app(arg1, arg2)
//    console.log("arg2: ", arg2)
//    logResponseBody(arg1, arg2)
//}
