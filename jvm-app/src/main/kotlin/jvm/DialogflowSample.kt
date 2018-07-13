package jvm

import actions.service.actionssdk.conversation.question.option.OptionItem
import actions.service.actionssdk.conversation.response.List
import actions.service.actionssdk.conversation.response.*
import actions.service.actionssdk.conversation.response.card.Button
import actions.service.dialogflow.dialogflow


// Constant for image URLs
val IMG_URL_MEDIA = "http://storage.googleapis.com/automotive-media/album_art.jpg"
val MEDIA_SOURCE = "http://storage.googleapis.com/automotive-media/Jazz_In_Paris.mp3"

private val intentSuggestions = arrayOf(
        "Basic Card",
        "Browse Carousel",
        "Carousel",
        "List",
        "Media",
        "Suggestions",
        "Table",
        "Test")

val dfApp = dialogflow<UserStorage, MyConversation, MyArgument>({ debug = true })

fun initDf() {

//    app.middleware {(conv) -> {
//        conv.hasScreen =
//                conv.surface.capabilities.has("actions.capability.SCREEN_OUTPUT")
//        conv.hasAudioPlayback =
//                conv.surface.capabilities.has("actions.capability.AUDIO_OUTPUT")
//    } }

// Test
    dfApp.intent("test") { conv ->
        conv.ask("Can you hear me?")
    }

// Welcome
    dfApp.intent("input.welcome") { conv ->
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

    dfApp.intent("normal.ask") { conv ->
        conv.ask("Ask me to show you a list, carousel, or basic card.")
    }

// Suggestions
    dfApp.intent("suggestions") { conv ->
        if (!conv.hasScreen()) {
            conv.ask("Sorry, try this on a screen device or select the " +
                    "phone surface in the simulator.")
        } else {
            conv.ask("This is a simple response for suggestions.")
            conv.ask(Suggestions("Suggestion Chips"))
            conv.ask(Suggestions(*intentSuggestions))
            conv.ask(LinkOutSuggestion {
                name = "Suggestion Link"
                url = "https://assistant.google.com/"
            })
        }
    }

// Basic card
    dfApp.intent("basic.card") { conv ->
        if (!conv.hasScreen()) {
            conv.ask("Sorry, try this on a screen device or select the " +
                    "phone surface in the simulator.")
        } else {
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
    }

// List
    dfApp.intent("list") { conv ->
        if (!conv.hasScreen()) {
            conv.ask("Sorry, try this on a screen device or select the " +
                    "phone surface in the simulator.")
        } else {
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
                                        "synonym of title 3"
                                ),
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
                                        "Assistant on the Google Home"
                                ),
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
                                        "Pixel XL"
                                ),
                                title = "Google Pixel",
                                description = "Pixel. Phone by Google.",
                                image = Image {
                                    url = IMG_URL_GOOGLE_PIXEL
                                    alt = "Google Pixel"
                                }),
                        // Add the last item to the list
                        SELECTION_KEY_GOOGLE_ALLO to OptionItem(
                                title = "Google Allo",
                                synonyms = mutableListOf(
                                        "Allo"
                                ),
                                description = "Introducing Google Allo, a smart messaging app that " +
                                        "helps you say more and do more.",
                                image = Image {
                                    url = IMG_URL_GOOGLE_ALLO
                                    alt = "Google Allo Logo"
                                }))
            })
        }
    }

// Carousel
    dfApp.intent("carousel") { conv ->
        if (!conv.hasScreen()) {
            conv.ask("Sorry, try this on a screen device or select the " +
                    "phone surface in the simulator.")
        } else {
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
                                        "synonym of title 3"
                                ),
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
                                        "Assistant on the Google Home"
                                ),
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
                                        "Allo"
                                ),
                                description = "Introducing Google Allo, a smart messaging app that " +
                                        "helps you say more and do more.",
                                image = Image {
                                    url = IMG_URL_GOOGLE_ALLO
                                    alt = "Google Allo Logo"
                                }))
            })
        }
    }

// Browse Carousel
    dfApp.intent("browse.carousel") { conv ->
        val a11yText = "Google Assistant Bubbles"
        val googleUrl = "https://google.com"
        if (!conv.hasScreen()) {
            conv.ask("Sorry, try this on a screen device or select the " +
                    "phone surface in the simulator.")
        } else {
            conv.ask("""This is an example of a "Browse Carousel"""")
            // Create a browse carousel
            conv.ask(BrowseCarousel {
                items = mutableListOf(
                        BrowseCarouselItem {
                            title = "Title of item 1"
                            url = googleUrl
                            description = "Description of item 1"
                            image = Image {
                                url = IMG_URL_AOG
                                alt = a11yText
                            }
                            footer = "Item 1 footer"
                        },
                        BrowseCarouselItem {
                            title = "Title of item 2"
                            url = googleUrl
                            description = "Description of item 2"
                            image = Image {
                                url = IMG_URL_AOG
                                alt = a11yText
                            }
                            footer = "Item 2 footer"
                        })
            })
            conv.ask(Suggestions(*intentSuggestions))
        }
    }

// Media response
    dfApp.intent("media.response") { conv ->
        if (!conv.hasAudioPlayback()) {
            conv.ask("Sorry, this device does not support audio playback.")
        } else {
            conv.ask("This is the first simple response for a media response")
            conv.ask(MediaObject {
                name = "Jazz in Paris"
                url = MEDIA_SOURCE
                description = "A funky Jazz tune"
                icon = Image {
                    url = IMG_URL_MEDIA
                    alt = "Media icon"
                }
            })
            conv.ask(Suggestions(*intentSuggestions))
        }
    }

// Handle a media status event
    dfApp.intent("media.status") { conv ->
        val mediaStatus = conv.arguments.get("MEDIA_STATUS")
        var response = "Unknown media status received."
        if (mediaStatus != null && mediaStatus.status?.message == "FINISHED") {
            response = "Hope you enjoyed the tunes!"
        }
        conv.ask(response)
        conv.ask(Suggestions(*intentSuggestions))
    }

// React to list or carousel selection
    dfApp.intent("item.selected") { conv, params, option ->
        {
            var response = "You did not select any item from the list or carousel"
            response = if (option != null && SELECTED_ITEM_RESPONSES.containsKey(option.textValue)) {
                SELECTED_ITEM_RESPONSES[option.textValue]!!
            } else {
                "You selected an unknown item from the list or carousel"
            }
            conv.ask(response)
        }
    }

    dfApp.intent("card.builder") { conv ->
        if (!conv.hasScreen()) {
            conv.ask("Sorry, try this on a screen device or select the " +
                    "phone surface in the simulator.")
        } else {
            //TODO implement incoming
//            conv.ask(conv.incoming)

            conv.ask(BasicCard {
                text = """Actions on Google let you build for
                            the Google Assistant.Reach users right when they need you . Users don’t
                            need to pre - enable skills or install new apps .  \n  \nThis was written
                            in the fulfillment webhook!"""
                subtitle = "Engage users through the Google Assistant"
                title = "Actions on Google"
                buttons = mutableListOf(Button {
                    title = "Developer Site"
                    url = "https://developers.google.com/actions/"
                })
                image = Image {
                    url = IMG_URL_AOG
                    alt = "Actions on Google"
                }
            })
        }
    }

    dfApp.intent("table.builder") { conv ->
        {
            if (!conv.hasScreen()) {
                conv.ask("Sorry, try this on a screen device or select the " +
                        "phone surface in the simulator.")
            } else {
                conv.ask("You can include table data like this")
                conv.ask(Table {
                    dividers = true
//                    columns = mutableListOf(TableColumn("header 1", "header 2", "header 3"))
//                    rows = mutableListOf(
//                            mutableListOf("row 1 item 1", "row 1 item 2", "row 1 item 3"),
//                            mutableListOf("row 2 item 1", "row 2 item 2", "row 2 item 3")
//                    )
                })
            }
        }
    }

// Leave conversation with card
    app.intent("bye.card") { conv ->
        if (!conv.hasScreen()) {
            conv.ask("Sorry, try this on a screen device or select the phone " +
                    "surface in the simulator.")
        }
        conv.ask("Goodbye, World!")
        conv.close(BasicCard {
            text = "This is a goodbye card."
        })
    }

// Leave conversation with SimpleResponse
    app.intent("bye.response") { conv ->
        conv.close(SimpleResponse {
            speech = "Okay see you later"
            text = "OK see you later!"
        })
    }

// Leave conversation
    app.intent("normal.bye") { conv ->
        conv.close("Okay see you later!")
    }

}
