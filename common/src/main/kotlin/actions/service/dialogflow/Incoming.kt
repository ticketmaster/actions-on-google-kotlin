package actions.service.dialogflow

import actions.service.actionssdk.conversation.response.*
import actions.service.actionssdk.push
import actions.service.dialogflow.api.DialogflowV1Fulfillment
import actions.service.dialogflow.api.GoogleCloudDialogflowV2IntentMessage

/**
 * Copyright 2018 Google Inc. All Rights Reserved.d
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*

type IncomingMessage =
string |
Image |
Suggestions |
BasicCard |
SimpleResponse |
LinkOutSuggestion |
List |
Carousel |
JsonObject
*/

fun toImage(imageUri: String?): Image? {
    if (!imageUri.isNullOrBlank()) {
        return Image {
            url = imageUri!!
            alt = ""
        }
    }
    return null
}

class Incoming {
    /** @public */
    var parsed: MutableList<Any>? = null //IncomingMessage can be multiple types.  See above

    constructor(fulfillment: DialogflowV1Fulfillment) {
        val speech = fulfillment.speech
        val messages = fulfillment.messages
        parsed = mutableListOf()

        if (speech != null) {
            this.parsed?.push(speech)
        }
        if (messages != null) {
            for (message in messages) {
                val platform = message.platform
                val type = message.type
                if (platform != null && platform != "google") {
                    continue
                }
                if (type == "0") {
                    val assumed = message //as DialogflowV1MessageText
                    this.parsed?.push(assumed.speech ?: "")
                    continue
                }
                if (type == "3") {
                    val assumed = message //as DialogflowV1MessageImage
//                    this.parsed?.push(toImage(assumed.imageUrl))
                    continue
                }
                if (type == "1") {
                    val assumed = message //as DialogflowV1MessageCard
                    /*
                    val buttons = assumed.buttons
                    this.parsed?.push(BasicCard(
                            title = assumed.title,
                            subtitle = assumed.subtitle,
                            image = toImage(assumed.imageUrl),
                            buttons = buttons?.map {
                                Button {
                                    title = it.text ?: ""
                                    url = it.postback
                                }
                            }?.toMutableList()))
                            */
                    continue
                }
                if (type == "2") {
                    val assumed = message //as DialogflowV1MessageQuickReplies
                    //TODO find cleaner way for Suggestions creation
                    /*
                    if (assumed?.replies != null)
                        this.parsed?.push(Suggestions(*assumed.replies!!.toTypedArray()))
                        */
                    continue
                }
                if (type == "4") {
                    val assumed = message //as DialogflowV1MessageCustomPayload
                    this.parsed?.push(assumed.payload ?: "")
                    continue
                }
                if (type == "simple_response") {
                    val assumed = message //as DialogflowV1MessageSimpleResponse
                    this.parsed?.push(SimpleResponse {
                        text = assumed.displayText
                        this.speech = assumed.textToSpeech ?: ""
                    })
                    continue
                }
                if (type == "basic_card") {
                    val assumed = message //as DialogflowV1MessageBasicCard
                    val image = assumed.image
                    val buttons = assumed.buttons
                    /*
                    this.parsed?.push(BasicCard(
                        title = assumed.title,
                        subtitle = assumed.subtitle,
                        text = assumed.formattedText,
                        image = toImage(image.imageUrl),
                        buttons = buttons?.map {
                            Button {
                                title = it.title
                                url = it.openUrlAction.url
                            }
                        }.toMutableList()
                    ))
                    */
                    continue
                }
                if (type == "list_card") {
                    val assumed = message //as DialogflowV1MessageList
                    /*
                    this.parsed?.push(List ({ title: assumed.title,
                                                 items: assumed.items!,
                    }))
                    */
                    continue
                }
                if (type == "suggestion_chips") {
                    val assumed = message //as DialogflowV1MessageSuggestions
//                    this.parsed?.push(Suggestions (assumed.suggestions!.map(s => s . title !)))
                    continue
                }
                if (type == "carousel_card") {
                    val assumed = message //as DialogflowV1MessageCarousel
                    this.parsed?.push(Carousel {
                        //                        items = assumed.items
                    })
                    continue
                }
                if (type == "link_out_chip") {
                    val assumed = message //as DialogflowV1MessageLinkOut
                    this.parsed?.push(LinkOutSuggestion {
                        name = assumed.destinationName ?: ""
                        url = assumed.url ?: ""
                    })
                    continue
                }
                if (type == "custom_payload") {
                    val assumed = message //as DialogflowV1MessageGooglePayload
                    this.parsed?.push(assumed.payload ?: "")
                    continue
                }
            }
        }
    }


    /** @hidden */
    constructor(fulfillment: MutableList<GoogleCloudDialogflowV2IntentMessage>) {
        /*
        // Dialogflow v2
        for (val message of fulfillment) {
            val { text,
                  image,
                  quickReplies,
                  card,
                  simpleResponses,
                  basicCard,
                  suggestions,
                  linkOutSuggestion,
                  listSelect,
                  carouselSelect,
                  platform,
                  payload,
        } = message
            if (platform && platform !== "ACTIONS_ON_GOOGLE" && platform !== "PLATFORM_UNSPECIFIED") {
                continue
            }
            if (text) {
                this.parsed.push(... text . text !)
                continue
            }
            if (image) {
                this.parsed.push(new Image ({ url: image.imageUri!,
                                              alt: image.accessibilityText!,
                }))
                continue
            }
            if (quickReplies) {
                this.parsed.push(new Suggestions (quickReplies.quickReplies!))
                continue
            }
            if (card) {
                val { buttons } = card
                this.parsed.push(new BasicCard ({
                    title: card.title,
                    subtitle: card.subtitle,
                    image: toImage(card.imageUri),
                    buttons: buttons ? buttons.map(b => new Button({ title: b.text!,
                                                                     url: b.postback,
                })) : undefined,
                }))
                continue
            }
            if (simpleResponses) {
                this.parsed.push(... simpleResponses . simpleResponses !. map (s => new SimpleResponse({
                    speech: s.textToSpeech || s.ssml!,
                    text: s.displayText,
                })))
                continue
            }
            if (basicCard) {
                val { image, buttons } = basicCard
                this.parsed.push(new BasicCard ({
                    title: basicCard.title,
                    subtitle: basicCard.subtitle,
                    text: basicCard.formattedText,
                    image: image ? new Image({ url: image.imageUri!,
                                               alt: image.accessibilityText!,
                }) : undefined,
                    buttons: buttons ? buttons.map(b => new Button({ title: b.title!,
                                                                     url: b.openUriAction!.uri,
                })) : undefined,
                }))
                continue
            }
            if (suggestions) {
                this.parsed.push(new Suggestions (suggestions.suggestions!.map(s => s . title !)))
                continue
            }
            if (linkOutSuggestion) {
                this.parsed.push(new LinkOutSuggestion ({ name: linkOutSuggestion.destinationName!,
                                                          url: linkOutSuggestion.uri!,
                }))
                continue
            }
            if (listSelect) {
                this.parsed.push(new List ({ title: listSelect.title,
                                             items: listSelect.items!,
                }))
                continue
            }
            if (carouselSelect) {
                this.parsed.push(new Carousel ({ items: carouselSelect.items!,
                }))
                continue
            }
            if (payload) {
                this.parsed.push(payload)
                continue
            }
        }
        */
    }

    /**
     * Gets the first Dialogflow incoming message with the given type.
     * Messages are converted into client library class instances or a string.
     *
     * Only messages with the platform field unlabeled (for generic use)
     * or labeled `ACTIONS_ON_GOOGLE` (`google` in v1) will be converted and read.
     *
     * The conversation is detailed below for a specific message oneof:
     * * Generic Platform Response
     *   * `text` -> `typeof string`
     *   * `image` -> `Image`
     *   * `quickReplies` -> `Suggestions`
     *   * `card` -> `BasicCard`
     * * Actions on Google Response
     *   * `simpleResponses` -> `SimpleResponse[]`
     *   * `basicCard` -> `BasicCard`
     *   * `suggestions` -> `Suggestions`
     *   * `linkOutSuggestion` -> `LinkOutSuggestion`
     *   * `listSelect` -> `List`
     *   * `carouselSelect` -> `Carousel`
     *   * `payload` -> `typeof object`
     *
     * Dialogflow v1:
     * * Generic Platform Response
     *   * `0` (text) -> `typeof string`
     *   * `3` (image) -> `Image`
     *   * `1` (card) -> `BasicCard`
     *   * `2` (quick replies) -> `Suggestions`
     *   * `4` (custom payload) -> `typeof object`
     * * Actions on Google Response
     *   * `simple_response` -> `SimpleResponse`
     *   * `basic_card` -> `BasicCard`
     *   * `list_card` -> `List`
     *   * `suggestion_chips` -> `Suggestions`
     *   * `carousel_card` -> `Carousel`
     *   * `link_out_chip` -> `LinkOutSuggestion`
     *   * `custom_payload` -> `typeof object`
     *
     * @example
     * ```javascript
     *
     * // Dialogflow
     * val { dialogflow, BasicCard } = require("actions-on-google")
     *
     * val app = dialogflow()
     *
     * // Create an Actions on Google Basic Card in the Dialogflow Console Intent Responses section
     * app.intent("Default Welcome Intent", conv => {
     *   val str = conv.incoming.get("string") // get the first text response
     *   val card = conv.incoming.get(BasicCard) // gets the instance of BasicCard
     *   // Do something with the Basic Card
     * })
     * ```
     *
     * @param type A string checking for the typeof message or a class checking for instanceof message
     * @public
     */
// tslint:disable-next-line:no-any allow constructors with any type of arguments
    /*
    get<TMessage extends IncomingMessage>(type: new (...args: any[]) => TMessage): TMessage
    /** @public */
    get(type: "string"): string
// tslint:disable-next-line:no-any allow constructors with any type of arguments
    get<TMessage extends IncomingMessage>(type: "string" | (new (...args: any[]) => TMessage))
    {
        for (val message of this) {
        if (typeof type === "string") {
        if (typeof message === type) {
        return message
    }
        continue
    }
        if (message instanceof type) {
            return message
        }
    }
        return null
    }

    /**
     * Gets the Dialogflow incoming messages as an iterator.
     * Messages are converted into client library class instances or a string.
     * See {@link Incoming#get|conv.incoming.get} for details on how the conversion works.
     *
     * @example
     * ```javascript
     *
     * // Dialogflow
     * val app = dialogflow()
     *
     * // Create messages in the Dialogflow Console Intent Responses section
     * app.intent("Default Welcome Intent", conv => {
     *   val messages = [...conv.incoming]
     *   // do something with the messages
     *   // or just spread them out back to the user
     *   conv.ask(`Here"s what was set in the Dialogflow console`)
     *   conv.ask(...conv.incoming)
     * }
     * ```
     *
     * @public
     */
    [Symbol.iterator]()
    {
        return this.parsed[Symbol.iterator]()
        // suppose to be Array.prototype.values(), but can"t use because of bug:
        // https://bugs.chromium.org/p/chromium/issues/detail?id=615873
    }
    */
}

