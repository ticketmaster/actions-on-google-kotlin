package com.tmsdurham.actions

import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

/**
 * Test suite for the actions client library.
 */

fun richResponse(json: String) = gson.fromJson(json, RichResponse::class.java)

fun richResponseItem(json: String) = gson.fromJson(json, RichResponseItem::class.java)
fun basicCard(json: String) = gson.fromJson(json, BasicCard::class.java)
fun optionItem(json: String) = gson.fromJson(json, OptionItem::class.java)
fun list(json: String) = gson.fromJson(json, List::class.java)
fun carousel(json: String) = gson.fromJson(json, Carousel::class.java)

/**
 * Describes the behavior for RichResponse interface.
 */
object ResponseBuilderTest : Spek({
    describe("RichResponse") {
        describe("#constructor") {
            it("should create valid object") {
                var richResponse = RichResponse()
                expect(richResponse).to.equal(richResponse("""{
                items: [],
                suggestions: []
            }"""))
            }
        }

        describe("#addSimpleResponse") {
            var richResponse = RichResponse()

            beforeEachTest {
                richResponse = RichResponse()
            }

            it("should add a simple response w/ just speech") {
                richResponse.addSimpleResponse("This is speech")
                expect(richResponse).to.equal(richResponse("""{
                items: [
                {
                    simpleResponse: {
                    textToSpeech: "This is speech"
                }
                }
                ],
                suggestions: []
            }"""))
            }

            it("should add a simple response w/ just SSML speech") {
                richResponse.addSimpleResponse("<speak>This is speech</speak>")
                expect(richResponse).to.equal(richResponse("""{
                items: [
                {
                    simpleResponse: {
                    ssml: "<speak>This is speech</speak>"
                }
                }
                ],
                suggestions: []
            }"""))
            }

            it("should add a simple response w/ speech and display text") {
                richResponse.addSimpleResponse(
                        speech = "This is speech",
                        displayText = "This is display text"
                )
                expect(richResponse).to.equal(richResponse("""{
                items: [
                {
                    simpleResponse: {
                    textToSpeech: "This is speech",
                    displayText: "This is display text"
                }
                }
                ],
                suggestions: []
            }"""))
            }

            it("should add a simple response w/ SSML speech and display text") {
                richResponse.addSimpleResponse(
                        speech = "<speak>This is speech</speak>",
                        displayText = "This is display text"
                )
                expect(richResponse).to.equal(richResponse("""{
                items: [
                {
                    simpleResponse: {
                    ssml: "<speak>This is speech</speak>",
                    displayText: "This is display text"
                }
                }
                ],
                suggestions: []
            }"""))
            }

            it("not add more than two simple responses") {
                richResponse.addSimpleResponse("text")
                richResponse.addSimpleResponse("text")
                richResponse.addSimpleResponse("text")
                expect(richResponse).to.equal(richResponse("""{
                items: [
                {
                    simpleResponse: {
                    textToSpeech: "text"
                }
                },
                {
                    simpleResponse: {
                    textToSpeech: "text"
                }
                }
                ],
                suggestions: []
            }"""))
            }

            it("should replace a basic card at first position") {
                richResponse.addBasicCard(BasicCard())
                richResponse.addSimpleResponse("text")
                expect(richResponse.items[0]).to.equal(richResponseItem("""
                    {
                        simpleResponse: {
                        textToSpeech: "text"
                    }
                    }"""))
            }

            it("should replace a structured response at first position") {
                richResponse.addOrderUpdate(OrderUpdate())
                richResponse.addSimpleResponse("text")
                expect(richResponse.items[0]).to.equal(richResponseItem("""
                    {
                        simpleResponse: {
                        textToSpeech: "text"
                    }
                    }"""))
            }
        }

        describe("#addOrderUpdate") {
            var richResponse = RichResponse()

            beforeEachTest {
                richResponse = RichResponse()
            }

            it("should add an order update") {
                richResponse.addOrderUpdate(OrderUpdate())
                expect(richResponse.items[0].structuredResponse?.orderUpdate).to.not.equal(null)
            }

            it("should not add more than one order update") {
                richResponse.addOrderUpdate(OrderUpdate())
                richResponse.addOrderUpdate(OrderUpdate())
                expect(richResponse.items.size).to.equal(1)
            }
        }

        describe("#addBasicCard") {
            var richResponse = RichResponse()

            beforeEachTest {
                richResponse = RichResponse()
            }

            it("should add a basic card") {
                richResponse.addBasicCard(BasicCard())
                expect(richResponse.items[0].basicCard).to.not.equal(null)
            }

            it("should not add more than one basic card") {
                richResponse.addBasicCard(BasicCard())
                richResponse.addBasicCard(BasicCard())
                expect(richResponse.items.size).to.equal(1)
            }
        }

        describe("#addSuggestions") {
            var richResponse = RichResponse()

            beforeEachTest {
                richResponse = RichResponse()
            }

            it("should add a single suggestion") {
                richResponse.addSuggestions("suggestion")
                expect(richResponse).to.equal(richResponse("""{
                items: [],
                suggestions: [{
                title: "suggestion"
            }]
            }"""))
            }

            it("should add multiple suggestions") {
                richResponse.addSuggestions("suggestion one", "suggestion two")
                expect(richResponse).to.equal(richResponse("""{
                items: [],
                suggestions: [
                {
                    title: "suggestion one"
                },
                {
                    title: "suggestion two"
                }]
            }"""))
            }
        }

        describe("#addSuggestionLink") {
            var richResponse = RichResponse()

            beforeEachTest {
                richResponse = RichResponse()
            }

            it("should add a single suggestion link") {
                richResponse.addSuggestionLink("title", "url")
                expect(richResponse).to.equal(richResponse("""{
                items: [],
                suggestions: [],
                linkOutSuggestion: {
                destinationName: "title",
                url: "url"
            }
            }"""))
            }

            it("should replace existing suggestion link") {
                richResponse.addSuggestionLink("title", "url")
                richResponse.addSuggestionLink("replacement", "replacement url")
                expect(richResponse).to.equal(richResponse("""{
                items: [],
                suggestions: [],
                linkOutSuggestion: {
                destinationName: "replacement",
                url: "replacement url"
            }
            }"""))
            }
        }
    }

    /**
     * Describes the behavior for BasicCard interface.
     */
    describe("BasicCard") {
        describe("#constructor") {
            it("should create valid object") {
                var basicCard = BasicCard()
                expect(basicCard).to.equal(basicCard("""{
                formattedText: "",
                buttons: []
            }"""))
            }
        }

        describe("#setTitle") {
            var basicCard = BasicCard()

            beforeEachTest {
                basicCard = BasicCard()
            }

            it("should set title") {
                basicCard.setTitle("Title")
                expect(basicCard).to.equal(basicCard("""{
                title: "Title",
                formattedText: "",
                buttons: []
            }"""))
            }

            it("should overwrite previously set title") {
                basicCard.setTitle("Title")
                basicCard.setTitle("New title")
                expect(basicCard).to.equal(basicCard("""{
                title: "New title",
                formattedText: "",
                buttons: []
            }"""))
            }
        }

        describe("#setSubtitle") {
            var basicCard = BasicCard()

            beforeEachTest {
                basicCard = BasicCard()
            }

            it("should set subtitle") {
                basicCard.setSubtitle("Subtitle")
                expect(basicCard).to.equal(basicCard("""{
                subtitle: "Subtitle",
                formattedText: "",
                buttons: []
            }"""))
            }

            it("should overwrite previously set subtitle") {
                basicCard.setSubtitle("Subtitle")
                basicCard.setSubtitle("New Subtitle")
                expect(basicCard).to.equal(basicCard("""{
                subtitle: "New Subtitle",
                formattedText: "",
                buttons: []
            }"""))
            }
        }

        describe("#setBodyText") {
            var basicCard = BasicCard()

            beforeEachTest {
                basicCard = BasicCard()
            }

            it("should set body text") {
                basicCard.setBodyText("body text")
                expect(basicCard).to.equal(basicCard("""{
                formattedText: "body text",
                buttons: []
            }"""))
            }

            it("should overwrite previously set body text") {
                basicCard.setBodyText("body text")
                basicCard.setBodyText("New body text")
                expect(basicCard).to.equal(basicCard("""{
                formattedText: "New body text",
                buttons: []
            }"""))
            }
        }

        describe("#setImage") {
            var basicCard = BasicCard()

            beforeEachTest {
                basicCard = BasicCard()
            }

            it("should set image") {
                basicCard.setImage("url", "accessibilityText")
                expect(basicCard).to.equal(basicCard("""{
                formattedText: "",
                buttons: [],
                image: {
                url: "url",
                accessibilityText: "accessibilityText"
            }
            }"""))
            }

            it("should overwrite previously set image") {
                basicCard.setImage("url", "accessibilityText")
                basicCard.setImage("new.url", "new_accessibilityText")
                expect(basicCard).to.equal(basicCard("""{
                formattedText: "",
                buttons: [],
                image: {
                url: "new.url",
                accessibilityText: "new_accessibilityText"
            }
            }"""))
            }
        }

        describe("#addButton") {
            var basicCard = BasicCard()

            beforeEachTest {
                basicCard = BasicCard()
            }

            it("should add a single button") {
                basicCard.addButton("button", "url")
                expect(basicCard).to.equal(basicCard("""{
                formattedText: "",
                buttons: [{
                title: "button",
                openUrlAction: {
                url: "url"
            }
            }]
            }"""))
            }

            it("should add multiple buttons") {
                basicCard.addButton("button one", "url.one")
                basicCard.addButton("button two", "url.two")
                basicCard.addButton("button three", "url.three")
                expect(basicCard).to.equal(basicCard("""{
                formattedText: "",
                buttons: [
                {
                    title: "button one",
                    openUrlAction: {
                    url: "url.one"
                }
                },
                {
                    title: "button two",
                    openUrlAction: {
                    url: "url.two"
                }
                },
                {
                    title: "button three",
                    openUrlAction: {
                    url: "url.three"
                }
                }
                ]
            }"""))
            }
        }
    }

    /**
     * Describes the behavior for List interface.
     */
    describe("List") {
        describe("#constructor") {
            it("should create valid object") {
                var list = List()
                expect(list).to.equal(list("""{
                items: []
            }"""))
            }
        }

        describe("#setTitle") {
            var list = List()

            beforeEachTest {
                list = List()
            }

            it("should set title") {
                list.setTitle("Title")
                expect(list).to.equal(list("""{
                title: "Title",
                items: []
            }"""))
            }

            it("should overwrite previously set title") {
                list.setTitle("Title")
                list.setTitle("New title")
                expect(list).to.equal(list("""{
                title: "New title",
                items: []
            }"""))
            }
        }

        describe("#addItems") {
            var list = List()

            beforeEachTest {
                list = List()
            }

            it("should add a single item") {
                list.addItems(OptionItem())
                expect(list).to.equal(list("""{
                items: [{
                title: "",
                optionInfo: {
                key: "",
                synonyms: []
            }
            }]
            }"""))
            }

            it("should add multiple items") {
                list.addItems(OptionItem(), OptionItem(), OptionItem())
                expect(list).to.equal(list("""{
                items: [
                {
                    title: "",
                    optionInfo: {
                    key: "",
                    synonyms: []
                }
                },
                {
                    title: "",
                    optionInfo: {
                    key: "",
                    synonyms: []
                }
                },
                {
                    title: "",
                    optionInfo: {
                    key: "",
                    synonyms: []
                }
                }
                ]
            }"""))
            }
        }

        it("should add no more than 30 items") {
            var list = List()
            var optionItems = mutableListOf<OptionItem>()
            (0..35).forEach {
                var optionItem = OptionItem().setKey(it.toString())
                optionItems.add(optionItem)
            }
            list.addItems(*optionItems.toTypedArray())
            expect(list.items.size).to.equal(30)
            var key = 0
            list.items.forEach {
                expect(it.optionInfo.key).to.equal(key.toString())
                key++
            }
        }
    }
})

object CarouselTests : Spek({
    /**
     * Describes the behavior for Carousel interface.
     */
    describe("Carousel") {
        describe("#constructor") {
            it("should create valid object") {
                var carousel = Carousel()
                expect(carousel).to.equal(carousel("""{
                        items: []
                    }"""))
            }
        }

        describe("#addItems") {
            var carousel = Carousel()

            beforeEachTest {
                carousel = Carousel()
            }

            it("should add a single item") {
                carousel.addItems(OptionItem())
                expect(carousel).to.equal(carousel("""{
                        items: [{
                        title: "",
                        optionInfo: {
                        key: "",
                        synonyms: []
                    }
                    }]
                    }"""))
            }

            it("should add multiple items") {
                carousel.addItems(OptionItem(), OptionItem(), OptionItem())
                expect(carousel).to.equal(carousel("""{
                        items: [
                        {
                            title: "",
                            optionInfo: {
                            key: "",
                            synonyms: []
                        }
                        },
                        {
                            title: "",
                            optionInfo: {
                            key: "",
                            synonyms: []
                        }
                        },
                        {
                            title: "",
                            optionInfo: {
                            key: "",
                            synonyms: []
                        }
                        }
                        ]
                    }"""))
            }

            it("should add no more than 10 items") {
                var optionItems = mutableListOf<OptionItem>()
                (0..15).forEach {
                    var optionItem = OptionItem().setKey(it.toString())
                    optionItems.add(optionItem)
                }
                carousel.addItems(*optionItems.toTypedArray())
                expect(carousel.items.size).to.equal(10)
                (0..carousel.items.size - 1).forEach {
                    expect(carousel.items[it].optionInfo.key).to.equal(it.toString())
                }
            }
        }
    }
})

object OptionItemTests : Spek({
    /**
     * Describes the behavior for OptionItem interface.
     */
    describe("OptionItem") {
        describe("#constructor") {
            it("should create valid object") {
                var optionItem = OptionItem()
                expect(optionItem).to.equal(optionItem("""{
                        title: "",
                        optionInfo: {
                        key: "",
                        synonyms: []
                    }
                    }"""))
            }
        }

        describe("#setTitle") {
            var optionItem = OptionItem()

            beforeEachTest {
                optionItem = OptionItem()
            }

            it("should set title") {
                optionItem.setTitle("Title")
                expect(optionItem).to.equal(optionItem("""{
                        title: "Title",
                        optionInfo: {
                        key: "",
                        synonyms: []
                    }
                    }"""))
            }

            it("should overwrite previously set title") {
                optionItem.setTitle("Title")
                optionItem.setTitle("New title")
                expect(optionItem).to.equal(optionItem("""{
                        title: "New title",
                        optionInfo: {
                        key: "",
                        synonyms: []
                    }
                    }"""))
            }
        }

        describe("#setDescription") {
            var optionItem = OptionItem()

            beforeEachTest {
                optionItem = OptionItem()
            }

            it("should set subtitle") {
                optionItem.setDescription("Description")
                expect(optionItem).to.equal(optionItem("""{
                        title: "",
                        description: "Description",
                        optionInfo: {
                        key: "",
                        synonyms: []
                    }
                    }"""))
            }

            it("should overwrite previously set description") {
                optionItem.setDescription("Description")
                optionItem.setDescription("New Description")
                expect(optionItem).to.equal(optionItem("""{
                        title: "",
                        description: "New Description",
                        optionInfo: {
                        key: "",
                        synonyms: []
                    }
                    }"""))
            }
        }

        describe("#setImage") {
            var optionItem = OptionItem()

            beforeEachTest {
                optionItem = OptionItem()
            }

            it("should set image") {
                optionItem.setImage("url", "accessibilityText")
                expect(optionItem).to.equal(optionItem("""{
                        title: "",
                        optionInfo: {
                        key: "",
                        synonyms: []
                    },
                        image: {
                        url: "url",
                        accessibilityText: "accessibilityText"
                    }
                    }"""))
            }

            it("should overwrite previously set image") {
                optionItem.setImage("url", "accessibilityText")
                optionItem.setImage("new.url", "new_accessibilityText")
                expect(optionItem).to.equal(optionItem("""{
                title: "",
                optionInfo: {
                key: "",
                synonyms: []
            },
                image: {
                url: "new.url",
                accessibilityText: "new_accessibilityText"
            }
            }"""))
            }
        }
    }
})
