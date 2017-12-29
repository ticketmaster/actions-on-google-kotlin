package com.tmsdurham.actions

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.tmsdurham.dialogflow.*
import com.tmsdurham.actions.actions.Input
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

val gson = GsonBuilder()
        .serializeNulls()
        //needed for classes that extend map, such as OrderUpdate
        .registerTypeAdapter(OrderUpdate::class.java, OrderUpdateTypeAdapter(Gson()))
        .create()


val headerV1 = mapOf(
        "Content-Type" to "application/json",
        "Google-Assistant-API-version" to "v1"
)

val headerV2 = mapOf(
        "Content-Type" to "application/json",
        "Google-Actions-API-Version" to "2"
)
const val fakeTimeStamp = "2017-01-01T12:00:00"
const val fakeSessionId = "0123456789101112"
const val fakeIntentId = "1a2b3c4d-5e6f-7g8h-9i10-11j12k13l14m15n16o"
const val fakeDialogflowBodyRequestId = "1a2b3c4d-5e6f-7g8h-9i10-11j12k13l14m15n16o"
const val fakeUserId = "user123"
const val fakeConversationId = "0123456789"


object ActionsTest : Spek({

    debugFunction = defaultLogFunction

    fun requestFromJson(body: String) = gson.fromJson<DialogflowRequest>(body, DialogflowRequest::class.java)

    fun responseFromJson(body: String) = gson.fromJson<DialogflowResponse>(body, DialogflowResponse::class.java)


    // Body of the Dialogflow request that starts a new session
// new session is originalRequest.data.conversation.type == 1
    fun dialogflowAppRequestBodyNewSession(): DialogflowRequest {

        return requestFromJson("""{
        "lang": "en",
        "status": {
        "errorType": "success",
        "code": 200
    },
        "timestamp": fakeTimeStamp,
        "sessionId": fakeSessionId,
        "result": {
        "parameters": {
        "city": "Rome",
        "name": "Ana",
        "list": [ "one", "two" ],
        "nested": { "nestedField": "n1" }
        },
        "contexts": [],
        "resolvedQuery": "my name is Ana and I live in Rome",
        "source": "agent",
        "score": 1.0,
        "speech": "",
        "fulfillment": {
        "messages": [
        {
            "speech": "Hi Ana! Nice to meet you!",
            "type": 0
        }
        ],
        "speech": "Hi Ana! Nice to meet you!"
    },
        "actionIncomplete": false,
        "action": "greetings",
        "metadata": {
        "intentId": fakeIntentId,
        "webhookForSlotFillingUsed": "false",
        "intentName": "greetings",
        "webhookUsed": "true"
    }
    },
        "id": fakeDialogflowBodyRequestId,
        "originalRequest": {
        "source": "google",
        "data": {
        "inputs": [
        {
            "raw_inputs": [
            {
                "query": "my name is Ana and I live in Rome",
                "input_type": 2
            }
            ],
            "intent": "assistant.intent.action.TEXT",
            "arguments": [
            {
                "text_value": "my name is Ana and I live in Rome",
                "raw_text": "my name is Ana and I live in Rome",
                "name": "text"
            }
            ]
        }
        ],
        "user": {
        "user_id": fakeUserId,
        "locale": "en-US"
    },
        "conversation": {
        "conversation_id": fakeConversationId,
        "type": 1,
        "conversation_token": "[]"
    }
    }
    }
    }""")
    }

    fun createLiveSessionApiAppBody(): DialogflowRequest {
        var tmp = dialogflowAppRequestBodyNewSession()
        tmp.originalRequest?.data?.conversation?.type = "2"
        return tmp
    }

    // ---------------------------------------------------------------------------
    //                   App helpers
    // ---------------------------------------------------------------------------
    /**
     * Describes the behavior for Assistant isNotApiVersionOne_ method.
     */
    describe("DialogflowApp#isNotApiVersionOne") {
        var mockResponse = ResponseWrapper<DialogflowResponse>()

        val invalidHeader = mapOf(
                "Content-Type" to "application/json",
                "google-assistant-api-version" to "v1",
                "Google-Actions-API-Version" to "1"
        )
        val headerV1 = mapOf(
                "Content-Type" to "application/json",
                "Google-Assistant-API-Version" to "v1"
        )

        beforeEachTest {
            mockResponse = ResponseWrapper()
        }


        it("Should detect Proto2 when header is not present") {
            val mockRequest = RequestWrapper(headerV1, DialogflowRequest())

            val app = DialogflowApp(mockRequest, mockResponse)

            expect(app.isNotApiVersionOne()).to.equal(false)
        }

        it("Should detect v1 when header is present") {
            val mockRequest = RequestWrapper(invalidHeader, DialogflowRequest())
            val mockResponse = ResponseWrapper<DialogflowResponse>()
            val app = DialogflowApp(request = mockRequest, response = mockResponse)
            expect(app.isNotApiVersionOne()).to.equal(false)
        }

        it("Should detect v2 when version is present in Dialogflow req") {
            val mockRequest = RequestWrapper(headerV1, dialogflowRequest {
                result {
                    originalRequest {
                        version = "1"
                    }
                }
            })

            val mockResponse = ResponseWrapper<DialogflowResponse>()

            val app = DialogflowApp(request = mockRequest, response = mockResponse)

            expect(app.isNotApiVersionOne()).to.equal(false)
        }

        it("Should detect v2 when header is present") {
            val headers = HashMap(headerV1)
            headers["Google-Actions-API-Version"] = "2"

            val mockRequest = RequestWrapper(headers, DialogflowRequest())
            val mockResponse = ResponseWrapper<DialogflowResponse>()

            val app = DialogflowApp(request = mockRequest, response = mockResponse)

            expect(app.isNotApiVersionOne()).to.equal(true)
        }

        it("Should detect v2 when version is present in Dialogflow req") {
            val mockRequest = RequestWrapper(headerV1, dialogflowRequest {
                originalRequest {
                    version = "2"
                }
            })
            val mockResponse = ResponseWrapper<DialogflowResponse>()

            val app = DialogflowApp(request = mockRequest, response = mockResponse)

            expect(app.isNotApiVersionOne()).to.equal(true)
        }

        /**
         * Describes the behavior for AssistantApp isSsml_ method.
         */
        describe("DialogflowApp#isSsml_") {
            // Success case test, when the API returns a valid 200 response with the response object
            it("Should validate SSML syntax.") {
                val mockRequest = RequestWrapper(headerV1, dialogflowAppRequestBodyNewSession())
                val mockResponse = ResponseWrapper<DialogflowResponse>()
                val app = DialogflowApp(request = mockRequest, response = mockResponse)
                expect(app.isSsml("""<speak></speak>""")).to.equal(true)
                expect(app.isSsml("""<SPEAK></SPEAK>""")).to.equal(true)
                expect(app.isSsml("""  <speak></speak>  """)).to.equal(false)
                expect(app.isSsml("""<speak>  </speak>""")).to.equal(true)
                expect(app.isSsml("""<speak version="1.0"></speak>""")).to.equal(true)
                expect(app.isSsml("""<speak version="1.0">Hello world!</speak>""")).to.equal(true)
                expect(app.isSsml("""<speak>""")).to.equal(false)
                expect(app.isSsml("""</speak>""")).to.equal(false)
                expect(app.isSsml("""""")).to.equal(false)
                expect(app.isSsml("""bla bla bla""")).to.equal(false)
                expect(app.isSsml("""<html></html>""")).to.equal(false)
                expect(app.isSsml("""bla bla bla<speak></speak>""")).to.equal(false)
                expect(app.isSsml("""<speak></speak> bla bla bla""")).to.equal(false)
                expect(app.isSsml("""<speak>my SSML content</speak>""")).to.equal(true)
                expect(app.isSsml("""<speak>Line 1\nLine 2</speak>""")).to.equal(true)
                expect(app.isSsml("""<speak>Step 1, take a deep breath. <break time="2s" />Step 2, exhale.</speak>""")).to.equal(true)
                expect(app.isSsml("""<speak><say-as interpret-as="cardinal">12345</say-as></speak>""")).to.equal(true)
                expect(app.isSsml("""<speak><say-as interpret-as="ordinal">1</say-as></speak>""")).to.equal(true)
                expect(app.isSsml("""<speak><say-as interpret-as="characters">can</say-as></speak>""")).to.equal(true)
                expect(app.isSsml("""<speak><say-as interpret-as="date" format="ymd">1960-09-10</say-as></speak>""")).to.equal(true)
                expect(app.isSsml("""<speak><say-as interpret-as="date" format="yyyymmdd" detail="1">1960-09-10</say-as></speak>""")).to.equal(true)
                expect(app.isSsml("""<speak><say-as interpret-as="date" format="dm">10-9</say-as></speak>""")).to.equal(true)
                expect(app.isSsml("""<speak><say-as interpret-as="date" format="dmy" detail="2">10-9-1960</say-as></speak>""")).to.equal(true)
                expect(app.isSsml("""<speak><say-as interpret-as="time" format="hms12">2:30pm</say-as></speak>""")).to.equal(true)
                expect(app.isSsml("""<speak><audio src="https://somesite.bla/meow.mp3">a cat meowing</audio></speak>""")).to.equal(true)
                expect(app.isSsml("""<speak><p><s>This is sentence one.</s><s>This is sentence two.</s></p></speak>""")).to.equal(true)
                expect(app.isSsml("""<speak><sub alias="World Wide Web Consortium">W3C</sub></speak>""")).to.equal(true)
            }
        }
    }

    // ---------------------------------------------------------------------------
    //                   Dialogflow support
    // ---------------------------------------------------------------------------

    /**
     * Describes the behavior for DialogflowApp constructor method.
     */
    describe("DialogflowApp#constructor") {
        var mockResponse = ResponseWrapper<DialogflowResponse>()

        // Calls sessionStarted when provided
        it("Calls sessionStarted when new session") {
            var mockRequest = RequestWrapper(headerV1, dialogflowAppRequestBodyNewSession())

            val sessionStartedSpy = mock<(() -> Unit)> {}

            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse,
                    sessionStarted = sessionStartedSpy
            )

            app.handleRequest(handler = {})

            verify(sessionStartedSpy).invoke()
        }
    }

    // Does not call sessionStarted when not new sessoin
    it("Does not call sessionStarted when not new session") {
        val mockRequest = RequestWrapper(headerV1, createLiveSessionApiAppBody())
        val mockResponse = ResponseWrapper<DialogflowResponse>()

        val sessionStartedSpy = mock<(() -> Unit)> {}

        val app = DialogflowApp(
                request = mockRequest,
                response = mockResponse,
                sessionStarted = sessionStartedSpy
        )

        app.handleRequest(handler = {})

        verifyZeroInteractions(sessionStartedSpy)
    }


    //TODO 2 tests

    /**
     * Describes the behavior for DialogflowApp tell method.
     */
    describe("DialogflowApp#tell") {
        var mockResponse = ResponseWrapper<DialogflowResponse>()
        var body = DialogflowRequest()
        var mockRequest = RequestWrapper<DialogflowRequest>(body = body)
        var app = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper<DialogflowResponse>()
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV1, body)
            app = DialogflowApp(request = mockRequest, response = mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid JSON in the response object for the success case.", {
            app.tell("hello")

            // Validating the response object
            val expectedResponse = """{
                "speech": "hello",
                "data": {
                "google": {
                "expect_user_response": false,
                "is_ssml": false,
                "no_input_prompts": [

                ]
            }
            },
                contextOut: [

                ]
            }"""
            expect(mockResponse.body).to.equal(responseFromJson(expectedResponse))
        })

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid simple response JSON in the response object for the success case.") {
            app.tell(speech = "hello",
                    displayText = "hi")

            // Validating the response object
            var expectedResponse = """{
                "speech": "hello",
                "data": {
                "google": {
                "expectUserResponse": false,
                "richResponse": {
                "items": [
                {
                    "simpleResponse": {
                    "textToSpeech": "hello",
                    "displayText": "hi"
                }
                }
                ],
                "suggestions": []
            }
            }
            },
                "contextOut": []
            }"""
            expect(mockResponse.body).to.equal(responseFromJson(expectedResponse))
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid rich response JSON in the response object for the success case.") {

            app.tell(app.buildRichResponse()
                    .addSimpleResponse(
                            speech = "hello",
                            displayText = "hi"
                    )
                    .addSuggestions("Say this", "or this"))


            // Validating the response object
            val expectedResponse = responseFromJson("""{
            "speech": "hello",
            "data": {
            "google": {
            "expectUserResponse": false,
            "richResponse": {
            "items": [
            {
                "simpleResponse": {
                "textToSpeech": "hello",
                "displayText": "hi"
            }
            }
            ],
            "suggestions": [
            {
                "title": "Say this"
            },
            {
                "title": "or this"
            }
            ]
        }
        }
        },
            "contextOut": []
        }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }


        // Failure test, when the API returns a 400 response with the response object
        it("Should send failure response for rich response without simple response") {
            app.tell(app.buildRichResponse())

            expect(mockResponse.statusCode).to.equal(400)
        }

    }
    /**
     * Describes the behavior for DialogflowApp askWithList method.
     */
    describe("DialogflowApp#askWithList") {

        var mockResponse = ResponseWrapper<DialogflowResponse>()
        var body = DialogflowRequest()
        var mockRequest = RequestWrapper<DialogflowRequest>(body = body)
        var app = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
            body.originalRequest?.version = "2"
            mockRequest = RequestWrapper(headerV1, body)
            app = DialogflowApp(request = mockRequest, response = mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid list JSON in the response object for the success case.") {
            app.askWithList("Here is a list", app.buildList()
                    .addItems(
                            app.buildOptionItem("key_1", "key one"),
                            app.buildOptionItem("key_2", "key two")
                    ))

            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "speech": "Here is a list",
                "data": {
                "google": {
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.OPTION",
                "data": {
                "@type": "type.googleapis.com/google.actions.v2.OptionValueSpec",
                "listSelect": {
                "items": [
                {
                    "optionInfo": {
                    "key": "key_1",
                    "synonyms": [
                    "key one"
                    ]
                },
                    "title": ""
                },
                {
                    "optionInfo": {
                    "key": "key_2",
                    "synonyms": [
                    "key two"
                    ]
                },
                    "title": ""
                }
                ]
            }
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

        it("Should return the an error JSON in the response when list has <2 items.") {
            app.askWithList("Here is a list", app.buildList())
            expect(mockResponse.statusCode).to.equal(400)
        }
    }

    /**
     * Describes the behavior for DialogflowApp askWithCarousel method.
     */
    describe("DialogflowApp#askWithCarousel") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
            body.originalRequest?.version = "2"
            mockRequest = RequestWrapper(headerV1, body)
            app = DialogflowApp(request = mockRequest, response = mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid carousel JSON in the response object for the success case.") {
            app.askWithCarousel("Here is a carousel",
                    app.buildCarousel()
                            .addItems(
                                    app.buildOptionItem("key_1", "key one"),
                                    app.buildOptionItem("key_2", "key two")
                            )
            )

            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "speech": "Here is a carousel",
                "data": {
                "google": {
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.OPTION",
                "data": {
                "@type": "type.googleapis.com/google.actions.v2.OptionValueSpec",
                "carouselSelect": {
                "items": [
                {
                    "optionInfo": {
                    "key": "key_1",
                    "synonyms": [
                    "key one"
                    ]
                },
                    "title": ""
                },
                {
                    "optionInfo": {
                    "key": "key_2",
                    "synonyms": [
                    "key two"
                    ]
                },
                    "title": ""
                }
                ]
            }
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

        it("Should return the an error JSON in the response when carousel has <2 items.") {
            app.askWithCarousel("Here is a carousel",
                    app.buildCarousel()
            )
            expect(mockResponse.statusCode).to.equal(400)
        }
    }

    /**
     * Describes the behavior for DialogflowApp askForPermissions method in v2.
     */
    describe("DialogflowApp#askForPermissions") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper<DialogflowResponse>()
            body = createLiveSessionApiAppBody()
            body.originalRequest?.version = "2"
            mockRequest = RequestWrapper(headerV1, body)
            app = DialogflowApp(request = mockRequest, response = mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid JSON in the response object for the success case.") {
            app.askForPermissions("To test", "NAME", "DEVICE_PRECISE_LOCATION")
            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_PERMISSION",
                "data": {
                "google": {
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.PERMISSION",
                "data": {
                "@type": "type.googleapis.com/google.actions.v2.PermissionValueSpec",
                "optContext": "To test",
                "permissions": ["NAME", "DEVICE_PRECISE_LOCATION"]
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }

    /**
     * Describes the behavior for DialogflowApp getUser method.
     */
    describe("DialogflowApp#getUser") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
            body.originalRequest?.data?.user?.userId = "11112226094657824893"
            mockRequest = RequestWrapper(headerV1, body)
            app = DialogflowApp(request = mockRequest, response = mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user.") {
            // Test new and old API
//            expect(app.getUser().user_id).to.equal("11112226094657824893");
            expect(app.getUser()?.userId).to.equal("11112226094657824893")
        }
    }

    /**
     * Describes the behavior for DialogflowApp getUserName method.
     */
    describe("DialogflowApp#getUserName") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user.") {
            var mockRequest: RequestWrapper<DialogflowRequest>
            body.originalRequest?.data?.user = gson.fromJson("""{
                "userId": "11112226094657824893",
                "profile": {
                "displayName": "John Smith",
                "givenName": "John",
                "familyName": "Smith"
            }
            }""", User::class.java)
            mockRequest = RequestWrapper(headerV1, body)
            app = DialogflowApp(request = mockRequest, response = mockResponse)
            expect(app.getUserName()?.displayName).to.equal("John Smith")
            expect(app.getUserName()?.givenName).to.equal("John")
            expect(app.getUserName()?.familyName).to.equal("Smith")

            // Test the false case
            body.originalRequest?.data?.user?.profile = null
            mockRequest = RequestWrapper(headerV1, body)
            app = DialogflowApp(request = mockRequest, response = mockResponse)
            expect(app.getUserName()).to.equal(null)
        }
    }

    /**
     * Describes the behavior for DialogflowApp getUserLocale method.
     */
    describe("DialogflowApp#getUserLocale") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user with locale.") {
            var mockRequest: RequestWrapper<DialogflowRequest>
            val app: DialogflowApp
            mockRequest = RequestWrapper(headerV1, body)
            app = DialogflowApp(request = mockRequest, response = mockResponse)
            expect(app.getUserLocale()).to.equal("en-US")
        }

        // Failure case
        it("Should return null for missing locale.") {
            var mockRequest: RequestWrapper<DialogflowRequest>
            val app: DialogflowApp
            body.originalRequest?.data?.user?.locale = null
            mockRequest = RequestWrapper(headerV1, body)
            app = DialogflowApp(request = mockRequest, response = mockResponse)
            expect(app.getUserLocale()).to.equal(null)
        }
    }


    /**
     * Describes the behavior for DialogflowApp getDeviceLocation method.
     */
    describe("DialogflowApp#getDeviceLocation") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            body.originalRequest?.data?.device = gson.fromJson("""{
                "location": {
                "coordinates": {
                "latitude": 37.3861,
                "longitude": 122.0839
            },
                "formattedAddress": "123 Main St, Anytown, CA 12345, United States",
                "zipCode": "12345",
                "city": "Anytown"
            }
            }""", Device::class.java)
        }

        fun initMockApp() {
            mockRequest = RequestWrapper(headerV1, body)
            mockResponse = ResponseWrapper()
            app = DialogflowApp(request = mockRequest, response = mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user.") {
            initMockApp()
            expect(app.getDeviceLocation()?.coordinates).to.equal(Coordinates(
                    latitude = 37.3861,
                    longitude = 122.0839
            ))
            expect(app.getDeviceLocation()?.formattedAddress)
                    .to.equal("123 Main St, Anytown, CA 12345, United States")
            expect(app.getDeviceLocation()?.zipCode).to.equal("12345")
            expect(app.getDeviceLocation()?.city).to.equal("Anytown")
        }

        it("Should validate faulty assistant request user.") {
            // Test the false case
            body.originalRequest?.data?.device = null
            initMockApp()
            expect(app.getDeviceLocation()).to.equal(null)
        }
    }

    /**
     * Describes the behavior for DialogflowApp askForTransactionRequirements method.
     */
    describe("DialogflowApp#askForTransactionRequirements") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody();
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper()
            app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON transaction requirements with Google payment options") {
            val transactionConfig = gson.fromJson("""{
                deliveryAddressRequired: true,
                tokenizationParameters: {
                myParam: "myParam"
            },
                cardNetworks: [
                "VISA",
                "MASTERCARD"
                ],
                prepaidCardDisallowed: false
            }""", GooglePaymentTransactionConfig::class.java)

            app.askForTransactionRequirements(transactionConfig)

            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_TXN_REQUIREMENTS",
                "data": {
                "google": {
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.TRANSACTION_REQUIREMENTS_CHECK",
                "data": {
                "@type": "type.googleapis.com/google.actions.v2.TransactionRequirementsCheckSpec",
                "orderOptions": {
                "requestDeliveryAddress": true
            },
                "paymentOptions": {
                "googleProvidedOptions": {
                "tokenizationParameters": {
                "tokenizationType": "PAYMENT_GATEWAY",
                "parameters": {
                "myParam": "myParam"
            }
            },
                "supportedCardNetworks": [
                "VISA",
                "MASTERCARD"
                ],
                "prepaidCardDisallowed": false
            }
            }
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON transaction requirements with Action payment options") {
            val transactionConfig = ActionPaymentTransactionConfig(
                    deliveryAddressRequired = true,
                    type = "BANK",
                    displayName = "Checking-4773"
            )

            app.askForTransactionRequirements(transactionConfig)

            val expectedResponse = responseFromJson("""{
            "speech": "PLACEHOLDER_FOR_TXN_REQUIREMENTS",
            "data": {
            "google": {
            "expectUserResponse": true,
            "isSsml": false,
            "noInputPrompts": [],
            "systemIntent": {
            "intent": "actions.intent.TRANSACTION_REQUIREMENTS_CHECK",
            "data": {
            "@type": "type.googleapis.com/google.actions.v2.TransactionRequirementsCheckSpec",
            "orderOptions": {
            "requestDeliveryAddress": true
        },
            "paymentOptions": {
            "actionProvidedOptions": {
            "paymentType": "BANK",
            "displayName": "Checking-4773"
        }
        }
        }
        }
        }
        },
            "contextOut": [
            {
                "name": "_actions_on_google_",
                "lifespan": 100,
                "parameters": {}
            }
            ]
        }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }

    /**
     * Describes the behavior for DialogflowApp askForDeliveryAddress method.
     */
    describe("DialogflowApp#askForDeliveryAddress") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON delivery address") {
            val body = createLiveSessionApiAppBody()
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<DialogflowResponse>()

            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )

            app.askForDeliveryAddress("Just because")

            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_DELIVERY_ADDRESS",
                "data": {
                "google": {
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.DELIVERY_ADDRESS",
                "data": {
                "@type": "type.googleapis.com/google.actions.v2.DeliveryAddressValueSpec",
                "addressOptions": {
                "reason": "Just because"
            }
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }

    /**
     * Describes the behavior for DialogflowApp askForTransactionDecision method.
     */
    describe("DialogflowApp#askForTransactionDecision") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper()
            app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )
        }
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON transaction decision with Google payment options") {
            val transactionConfig = GooglePaymentTransactionConfig(
                    deliveryAddressRequired = true,
                    tokenizationParameters = mapOf("myParam" to "myParam"),
                    cardNetworks = mutableListOf(
                            TransactionValues.CardNetwork.VISA,
                            TransactionValues.CardNetwork.MASTERCARD)
                    ,
                    prepaidCardDisallowed = false,
                    customerInfoOptions = mutableListOf(
                            "EMAIL"
                    )
            )

            app.askForTransactionDecision(Order(id = "order_id"), transactionConfig)

            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_TXN_DECISION",
                "data": {
                "google": {
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.TRANSACTION_DECISION",
                "data": {
                "@type": "type.googleapis.com/google.actions.v2.TransactionDecisionValueSpec",
                "proposedOrder": { "id": "order_id" },
                "orderOptions": {
                "requestDeliveryAddress": true,
                "customerInfoOptions": [
                "EMAIL"
                ]
            },
                "paymentOptions": {
                "googleProvidedOptions": {
                "tokenizationParameters": {
                "tokenizationType": "PAYMENT_GATEWAY",
                "parameters": {
                "myParam": "myParam"
            }
            },
                "supportedCardNetworks": [
                "VISA",
                "MASTERCARD"
                ],
                "prepaidCardDisallowed": false
            }
            }
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON transaction decision with Action payment options") {
            val transactionConfig = ActionPaymentTransactionConfig(
                    deliveryAddressRequired = true,
                    type = "BANK",
                    displayName = "Checking-4773",
                    customerInfoOptions = mutableListOf(
                            "EMAIL"
                    )
            )

            app.askForTransactionDecision(app.buildOrder("order_id"), transactionConfig)

            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_TXN_DECISION",
                "data": {
                "google": {
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.TRANSACTION_DECISION",
                "data": {
                "@type": "type.googleapis.com/google.actions.v2.TransactionDecisionValueSpec",
                "proposedOrder": { "id": "order_id" },
                "orderOptions": {
                "requestDeliveryAddress": true,
                "customerInfoOptions": [
                "EMAIL"
                ]
            },
                "paymentOptions": {
                "actionProvidedOptions": {
                "paymentType": "BANK",
                "displayName": "Checking-4773"
            }
            }
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse);
        }

    }

    /**
     * Describes the behavior for DialogflowApp askForConfirmation method.
     */
    describe("DialogflowApp#askForConfirmation") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper()
            app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON confirmation request") {
            app.askForConfirmation("You want to do that?")
            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_CONFIRMATION",
                "data": {
                "google": {
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.CONFIRMATION",
                "data": {
                "@type": "type.googleapis.com/google.actions.v2.ConfirmationValueSpec",
                "dialogSpec": {
                "requestConfirmationText": "You want to do that?"
            }
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON confirmation request without prompt") {
            app.askForConfirmation()

            val expectedResponse = responseFromJson("""{
            "speech": "PLACEHOLDER_FOR_CONFIRMATION",
            "data": {
            "google": {
            "expectUserResponse": true,
            "isSsml": false,
            "noInputPrompts": [],
            "systemIntent": {
            "intent": "actions.intent.CONFIRMATION",
            "data": {
            "@type": "type.googleapis.com/google.actions.v2.ConfirmationValueSpec"
        }
        }
        }
        },
            "contextOut": [
            {
                "name": "_actions_on_google_",
                "lifespan": 100,
                "parameters": {}
            }
            ]
        }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }


        /**
         * Describes the behavior for DialogflowApp askForDateTime method.
         */
        describe("DialogflowApp#askForDateTime") {
            var body: DialogflowRequest = DialogflowRequest()
            var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
            var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
            var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

            beforeEachTest {
                body = createLiveSessionApiAppBody()
                mockRequest = RequestWrapper(headerV2, body)
                mockResponse = ResponseWrapper()
                app = DialogflowApp(
                        request = mockRequest,
                        response = mockResponse
                )
            }

            // Success case test, when the API returns a valid 200 response with the response object
            it("Should return valid JSON datetime request") {
                app.askForDateTime("When do you want to come in?",
                        "What is the best date for you?",
                        "What time of day works best for you?")

                val expectedResponse = responseFromJson("""{
                    "speech": "PLACEHOLDER_FOR_DATETIME",
                    "data": {
                    "google": {
                    "expectUserResponse": true,
                    "isSsml": false,
                    "noInputPrompts": [],
                    "systemIntent": {
                    "intent": "actions.intent.DATETIME",
                    "data": {
                    "@type": "type.googleapis.com/google.actions.v2.DateTimeValueSpec",
                    "dialogSpec": {
                    "requestDatetimeText": "When do you want to come in?",
                    "requestDateText": "What is the best date for you?",
                    "requestTimeText": "What time of day works best for you?"
                }
                }
                }
                }
                },
                    "contextOut": [
                    {
                        "name": "_actions_on_google_",
                        "lifespan": 100,
                        "parameters": {}
                    }
                    ]
                }""")

                expect(mockResponse.body).to.equal(expectedResponse);
            }

            // Success case test, when the API returns a valid 200 response with the response object
            it("Should return valid JSON datetime request with partial prompts") {
                app.askForDateTime("When do you want to come in?", null)
                val expectedResponse = responseFromJson("""{
                    "speech": "PLACEHOLDER_FOR_DATETIME",
                    "data": {
                    "google": {
                    "expectUserResponse": true,
                    "isSsml": false,
                    "noInputPrompts": [],
                    "systemIntent": {
                    "intent": "actions.intent.DATETIME",
                    "data": {
                    "@type": "type.googleapis.com/google.actions.v2.DateTimeValueSpec",
                    "dialogSpec": {
                    "requestDatetimeText": "When do you want to come in?"
                }
                }
                }
                }
                },
                    "contextOut": [
                    {
                        "name": "_actions_on_google_",
                        "lifespan": 100,
                        "parameters": {}
                    }
                    ]
                }""")

                expect(mockResponse.body).to.equal(expectedResponse)
            }

            // Success case test, when the API returns a valid 200 response with the response object
            it("Should return valid JSON datetime request withouts prompt") {
                app.askForDateTime()

                val expectedResponse = responseFromJson("""{
                    "speech": "PLACEHOLDER_FOR_DATETIME",
                    "data": {
                    "google": {
                    "expectUserResponse": true,
                    "isSsml": false,
                    "noInputPrompts": [],
                    "systemIntent": {
                    "intent": "actions.intent.DATETIME",
                    "data": {
                    "@type": "type.googleapis.com/google.actions.v2.DateTimeValueSpec"
                }
                }
                }
                },
                    "contextOut": [
                    {
                        "name": "_actions_on_google_",
                        "lifespan": 100,
                        "parameters": {}
                    }
                    ]
                }""")

                expect(mockResponse.body).to.equal(expectedResponse)
            }
        }

    }

    /**
     * Describes the behavior for DialogflowApp askForSignIn method.
     */
    describe("DialogflowApp#askForSignIn") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper()
            app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON sign in request") {
            app.askForSignIn()
            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_SIGN_IN",
                "data": {
                "google": {
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.SIGN_IN",
                "data": {
                    "@type": "type.googleapis.com/google.actions.v2.SignInValueSpec"
                }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }

    /**
     * Describes the behavior for DialogflowApp getAvailableSurfaces method.
     */
    describe("#getAvailableSurfaces", {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })
        var availableSurfaces: MutableList<Surface>? = null

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper()
            app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )
            availableSurfaces = gson.fromJson("""[
            {
                "capabilities": [
                {
                    "name": "cap_one"
                },
                {
                    "name": "cap_two"
                }
                ]
            },
            {
                "capabilities": [
                {
                    "name": "cap_three"
                },
                {
                    "name": "cap_four"
                }
                ]
            }
            ]""", arrayOf<Surface>().javaClass).toMutableList()
            body.originalRequest?.data?.availableSurfaces = availableSurfaces
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return assistant available surfaces", {
            body.originalRequest?.data?.availableSurfaces = availableSurfaces
            val mockRequest = RequestWrapper(headerV1, body)
            val app = DialogflowApp(
                    mockRequest,
                    mockResponse)

            expect(app.getAvailableSurfaces()).to.equal(availableSurfaces)
        })

        // Failure case test
        it("Should return empty assistant available surfaces", {
            body.originalRequest?.data?.availableSurfaces = mutableListOf()
            val mockRequest = RequestWrapper(headerV2, body)
            val app = DialogflowApp(
                    mockRequest,
                    mockResponse
            )
            expect(app.getAvailableSurfaces()).to.equal(mutableListOf<Surface>())
        })

    })
    /**
     * Describes the behavior for DialogflowApp hasAvailableSurfaceCapabilities method.
     */
    describe("#hasAvailableSurfaceCapabilities", {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })
        var availableSurfaces: MutableList<Surface>? = null

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper()
            app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )
            availableSurfaces = gson.fromJson("""[
            {
                "capabilities": [
                {
                    "name": "cap_one"
                },
                {
                    "name": "cap_two"
                }
                ]
            },
            {
                "capabilities": [
                {
                    "name": "cap_three"
                },
                {
                    "name": "cap_four"
                }
                ]
            }
            ]""", arrayOf<Surface>().javaClass).toMutableList()
            body.originalRequest?.data?.availableSurfaces = availableSurfaces
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return true for set of valid capabilities", {
            val mockRequest = RequestWrapper(headerV1, body)
            val app = DialogflowApp(
                    mockRequest,
                    mockResponse
            )

            expect(app.hasAvailableSurfaceCapabilities("cap_one", "cap_two")).to.be.`true`
        })

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return true for one valid capability", {
            val mockRequest = RequestWrapper(headerV1, body)
            val app = DialogflowApp(
                    mockRequest,
                    mockResponse
            )

            expect(app.hasAvailableSurfaceCapabilities("cap_one")).to.be.`true`
        })

        // Failure case test, when the API returns a valid 200 response with the response object
        it("Should return false for set of invalid capabilities", {
            val mockRequest = RequestWrapper(headerV1, body)
            val app = DialogflowApp(
                    mockRequest,
                    mockResponse)

            expect(app.hasAvailableSurfaceCapabilities("cap_one", "cap_three")).to.be.`false`
        })

        // Failure case test, when the API returns a valid 200 response with the response object
        it("Should return false for one invalid capability", {
            val mockRequest = RequestWrapper(headerV1, body)
            val app = DialogflowApp(
                    mockRequest,
                    mockResponse)

            expect(app.hasAvailableSurfaceCapabilities("cap_five")).to.be.`false`
        })

        // Failure case test
        it("Should return false for empty assistant available surfaces", {
            body.originalRequest?.data?.availableSurfaces = null
            val mockRequest = RequestWrapper(headerV2, body)
            val app = DialogflowApp(
                    mockRequest,
                    mockResponse
            )
            expect(app.hasAvailableSurfaceCapabilities()).to.be.`false`
        })
    })

    /**
     * Describes the behavior for DialogflowApp askForNewSurface method.
     */
    describe("#askForNewSurface", {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV2, body)
            app = DialogflowApp(
                mockRequest,
                mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON sign in request", {
            app.askForNewSurface("test context", "test title", mutableListOf("cap_one", "cap_two"))
            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_NEW_SURFACE",
                "data": {
                "google": {
                "userStorage": "{\"data\":{}}",
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.NEW_SURFACE",
                "data": {
                "context": "test context",
                "notificationTitle": "test title",
                "capabilities": ["cap_one", "cap_two"],
                "@type": "type.googleapis.com/google.actions.v2.NewSurfaceValueSpec"
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        })
    })

    /**
     * Describes the behavior for DialogflowApp isNewSurface method.
     */
    describe("#isNewSurface", {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV2, body)
            app = DialogflowApp(
                mockRequest,
                mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate when new surface was accepted.", {
            body?.originalRequest?.data?.inputs!![0]?.arguments = gson.fromJson("""[
            {
                "name": "NEW_SURFACE",
                "extension": {
                "status": "OK"
            }
            }
            ]""", arrayOf<Arguments>()::class.java).toMutableList()

            val mockRequest = RequestWrapper(headerV1, body)

            val app = DialogflowApp(
                mockRequest,
                mockResponse)

            expect(app.isNewSurface()).to.be.`true`
        })

        // Failure case test
        it("Should validate when new surface was denied.", {
            body?.originalRequest?.data?.inputs!![0]?.arguments = gson.fromJson("""[
            {
                "name": "NEW_SURFACE",
                "extension": {
                "status": "DENIED"
            }
            }
            ]""", arrayOf<Arguments>()::class.java).toMutableList()

            val mockRequest = RequestWrapper(headerV1, body)

            val app = DialogflowApp(
                mockRequest,
                mockResponse)

            expect(app.isNewSurface()).to.be.`false`
        })
    })

    /**
     * Describes the behavior for DialogflowApp isPermissionGranted method.
     */
    describe("DialogflowApp#isPermissionGranted") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        fun initMockApp() {
            mockRequest = RequestWrapper(headerV1, body)
            mockResponse = ResponseWrapper()
            app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user.") {
            body = createLiveSessionApiAppBody()
            body?.originalRequest?.data?.inputs?.get(0)?.arguments = mutableListOf(Arguments(
                    name = "permission_granted",
                    textValue = "true")
            )
            initMockApp()
            expect(app.isPermissionGranted()).to.equal(true)

            // Test the false case
            body.originalRequest?.data?.inputs?.get(0)?.arguments?.get(0)?.textValue = "false"
            initMockApp()
            expect(app.isPermissionGranted()).to.equal(false)
        }
    }

    /**
     * Describes the behavior for DialogflowApp isInSandbox method.
     */
    describe("DialogflowApp#isInSandbox") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        fun initMockApp() {
            mockRequest = RequestWrapper(headerV1, body)
            mockResponse = ResponseWrapper()
            app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user.") {
            body = createLiveSessionApiAppBody()
            body.originalRequest?.data?.isInSandbox = true
            initMockApp()
            expect(app.isInSandbox()).to.equal(true)

            // Test the false case
            body.originalRequest?.data?.isInSandbox = false
            initMockApp()
            expect(app.isInSandbox()).to.equal(false)
        }
    }

    /**
     * Describes the behavior for DialogflowApp getIntent method.
     */
    describe("DialogflowApp#getIntent") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should get the intent value for the success case.") {
            val body = createLiveSessionApiAppBody()
            body.result.action = "check_guess"
            val mockRequest = RequestWrapper(headerV1, body)
            val mockResponse = ResponseWrapper<DialogflowResponse>()

            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )

            expect(app.getIntent()).to.equal("check_guess")
        }
    }

    /**
     * Describes the behavior for DialogflowApp getArgument method.
     */
    describe("DialogflowApp#getArgument") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should get the argument value for the success case.") {
            val body = createLiveSessionApiAppBody()
            body.result?.parameters?.set("guess", "50")
            val t = TypeToken.get(Arguments::class.java).type
            val type = TypeToken.getParameterized(List::class.java, t).type
            /*
                    gson.fromJson("""[
      {
        "name": "number",
        "raw_text": "45",
        "text_value": "45"
      },
      {
        "name": "other_value",
        "raw_text": "45",
        "other_value": {
          "key": "value"
        }
      }
    ]""", type)
    */
            var arg = mutableListOf(Arguments(rawText = "raw text one", textValue = "text value one", name = "arg_value_one"),
                    Arguments(rawText = "45", name = "other_value"))
            body.originalRequest?.data?.inputs?.get(0)?.arguments = arg

            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<DialogflowResponse>()

            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )

            expect(app.getArgument("guess")).to.equal("50")
            expect(app.getArgument("arg_value_one")).to.equal("text value one")
            //below appears to be Argument with arbutary values.  Currently not supported.
            /*
            expect(app.getArgument("other_value")).to.equal(gson.fromJson("""{
                "name": "other_value",
                "rawText": "45",
                "other_value": {
                "key": "value"
            }}""", Arguments::class.java))
            */
        }
    }

    /**
     * Describes the behavior for DialogflowApp getContextArgument method.
     */
    describe("DialogflowApp#getContextArgument") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should get the context argument value for the success case.") {
            val body = createLiveSessionApiAppBody()
            body.result.contexts = mutableListOf(
                    Context(name = "game",
                            parameters = mutableMapOf(
                                    "guess.original" to "50",
                                    "guess" to "50"),
                            lifespan = 5),
                    Context(name = "previous_answer",
                            parameters = mutableMapOf(
                                    "answer" to "68",
                                    "guess.original" to "51",
                                    "guess" to "50"),
                            lifespan = 50
                    ))

            val mockRequest = RequestWrapper(headerV1, body)
            val mockResponse = ResponseWrapper<DialogflowResponse>()

            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )

            expect(app.getContextArgument("game", "guess")).to
                    .equal(DialogflowApp.ContextArgument(value = "50", original = "50"))
            expect(app.getContextArgument("previous_answer", "answer")).to
                    .equal(DialogflowApp.ContextArgument(value = "68"))
        }
    }

    /**
     * Describes the behavior for DialogflowApp getIncomingRichResponse method.
     */
    describe("DialogflowApp#getIncomingRichResponse") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should get the incoming rich response for the success case.") {
            val body = createLiveSessionApiAppBody()
            body.result?.fulfillment?.messages = gson.fromJson(
                    """[
            {
                "type": "simple_response",
                "platform": "google",
                "textToSpeech": "Simple response one"
            },
            {
                "type": "basic_card",
                "platform": "google",
                "formattedText": "my text",
                "buttons": []
            },
            {
                "type": "suggestion_chips",
                "platform": "google",
                "suggestions": [
                {
                    "title": "suggestion one"
                }
                ]
            },
            {
                "type": "link_out_chip",
                "platform": "google",
                "destinationName": "google",
                "url": "google.com"
            },
            {
                "type": 0,
                "speech": "Good day!"
            }
            ]""", arrayOf<Messages>().javaClass)?.toMutableList()

            val mockRequest = RequestWrapper(headerV1, body)
            val mockResponse = ResponseWrapper<DialogflowResponse>()

            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse)

            val expectedResponse = RichResponse()
                    .addSimpleResponse("Simple response one")
                    .addBasicCard(BasicCard()
                            .setBodyText("my text"))
                    .addSuggestions("suggestion one")
                    .addSuggestionLink("google", "google.com")

            expect(app.getIncomingRichResponse()).to
                    .equal(expectedResponse)
        }

        /**
         * Describes the behavior for DialogflowApp getIncomingList method.
         */
        describe("DialogflowApp#getIncomingList") {
            // Success case test, when the API returns a valid 200 response with the response object
            it("Should get the incoming list for the success case.") {
                val body = createLiveSessionApiAppBody()
                body.result.fulfillment?.messages?.add(gson.fromJson("""{
                    "type": "list_card",
                    "platform": "google",
                    "title": "list_title",
                    "items": [
                    {
                        "optionInfo": {
                        "key": "first_item",
                        "synonyms": []
                    },
                        "title": "first item"
                    },
                    {
                        "optionInfo": {
                        "key": "second_item",
                        "synonyms": []
                    },
                        "title": "second item"
                    }
                    ]
                }""", Messages::class.java))
                val mockRequest = RequestWrapper(headerV1, body)
                val mockResponse = ResponseWrapper<DialogflowResponse>()

                val app = DialogflowApp(
                        request = mockRequest,
                        response = mockResponse
                )

                val expectedResponse = List()
                        .setTitle("list_title")
                        .addItems(
                                OptionItem().setTitle("first item").setKey("first_item"),
                                OptionItem().setTitle("second item").setKey("second_item"))

                expect(app.getIncomingList()).to
                        .equal(expectedResponse)
            }
        }

        /**
         * Describes the behavior for DialogflowApp getIncomingCarousel method.
         */
        describe("DialogflowApp#getIncomingCarousel") {
            // Success case test, when the API returns a valid 200 response with the response object
            it("Should get the incoming list for the success case.") {
                val body = createLiveSessionApiAppBody()
                body.result.fulfillment?.messages?.add(gson.fromJson("""{
                    "type": "carousel_card",
                    "platform": "google",
                    "items": [
                    {
                        "optionInfo": {
                        "key": "first_item",
                        "synonyms": []
                    },
                        "title": "first item",
                        "description": "Your first choice"
                    },
                    {
                        "optionInfo": {
                        "key": "second_item",
                        "synonyms": []
                    },
                        "title": "second item",
                        "description": "Your second choice"
                    }
                    ]
                }""", Messages::class.java))

                val mockRequest = RequestWrapper(headerV1, body)
                val mockResponse = ResponseWrapper<DialogflowResponse>()

                val app = DialogflowApp(
                        request = mockRequest,
                        response = mockResponse
                )

                val expectedResponse = Carousel()
                        .addItems(
                                OptionItem().setTitle("first item").setKey("first_item")
                                        .setDescription("Your first choice"),
                                OptionItem().setTitle("second item").setKey("second_item")
                                        .setDescription("Your second choice")
                        )

                expect(app.getIncomingCarousel()).to
                        .equal(expectedResponse)
            }
        }

        /**
         * Describes the behavior for DialogflowApp getSelectedOption method.
         */
        describe("DialogflowApp#getSelectedOption") {
            var body: DialogflowRequest = DialogflowRequest()
            var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
            var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
            var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

            beforeEachTest {
                mockRequest = RequestWrapper(headerV1, body)
                mockResponse = ResponseWrapper()
            }


            // Success case test, when the API returns a valid 200 response with the response object
            it("Should get the selected option when given in Dialogflow context.") {
                val body = createLiveSessionApiAppBody()
                body.originalRequest?.data?.inputs?.add(0, gson.fromJson("""{
                    "arguments": [
                    {
                        "text_value": "first_item",
                        "name": "OPTION"
                    }
                    ],
                    "intent": "actions.intent.OPTION",
                    "raw_inputs": [
                    {
                        "query": "firstitem",
                        "input_type": 2,
                        "annotation_sets": []
                    }
                    ]
                }""", Input::class.java))
                body.result.contexts = gson.fromJson("""[
                {
                    "name": "actions_intent_option",
                    "parameters": {
                    "OPTION": "first_item"
                },
                    "lifespan": 0
                }
                ]""", arrayOf<Context>().javaClass).toList()
                mockRequest = mockRequest.copy(body = body)
                app = DialogflowApp(
                        request = mockRequest,
                        response = mockResponse
                )
                expect(app.getSelectedOption()).to.equal("first_item")
            }

            // Success case test, when the API returns a valid 200 response with the response object
            it("Should get the selected option when not given in Dialogflow context.") {
                val body = createLiveSessionApiAppBody()
                body.originalRequest?.data?.inputs?.add(gson.fromJson("""{
                "arguments": [
                {
                    "text_value": "first_item",
                    "name": "OPTION"
                }
                ],
                "intent": "actions.intent.OPTION",
                "raw_inputs": [
                {
                    "query": "firstitem",
                    "input_type": 2,
                    "annotation_sets": []
                }
                ]
            }""", Input::class.java))
                mockRequest = mockRequest.copy(body = body)
                app = DialogflowApp(
                        request = mockRequest,
                        response = mockResponse
                )
                expect(app.getSelectedOption()).to.equal("first_item")
            }
        }
        /**
         * Describes the behavior for DialogflowApp isRequestFromDialogflow method.
         */
        describe("DialogflowApp#isRequestFromDialogflow") {
            // Success case test, when the API returns a valid 200 response with the response object
            it("Should confirm request is from Dialogflow.") {
                val header = headerV1.toMutableMap()
                header["Google-Assistant-Signature"] = "YOUR_PRIVATE_KEY"
                val mockRequest = RequestWrapper(header, createLiveSessionApiAppBody())
                val mockResponse = ResponseWrapper<DialogflowResponse>()

                val app = DialogflowApp(
                        request = mockRequest,
                        response = mockResponse
                )

                val HEADER_KEY = "Google-Assistant-Signature"
                val HEADER_VALUE = "YOUR_PRIVATE_KEY"

                expect(app.isRequestFromDialogflow(HEADER_KEY, HEADER_VALUE)).to.equal(true)
            }

            it("Should confirm request is NOT from Dialogflow.") {
                val header = headerV1
                val body = createLiveSessionApiAppBody()
                val mockRequest = RequestWrapper(header, body)
                val mockResponse = ResponseWrapper<DialogflowResponse>()

                val app = DialogflowApp(
                        request = mockRequest,
                        response = mockResponse
                )

                val HEADER_KEY = "Google-Assistant-Signature"
                val HEADER_VALUE = "YOUR_PRIVATE_KEY"

                expect(app.isRequestFromDialogflow(HEADER_KEY, HEADER_VALUE)).to.equal(false)
            }
        }

    }

    /**
     * Describes the behavior for DialogflowApp hasSurfaceCapability method.
     */
    describe("DialogflowApp#hasSurfaceCapability") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return true for a valid capability from incoming JSON for the success case.") {
            val body = createLiveSessionApiAppBody()
            body.originalRequest?.data?.surface = gson.fromJson("""{
                "capabilities": [
                {
                    "name": "actions.capability.AUDIO_OUTPUT"
                },
                {
                    "name": "actions.capability.SCREEN_OUTPUT"
                }
                ]
            }""", Surface::class.java)

            val mockRequest = RequestWrapper(headerV1, body)
            val mockResponse = ResponseWrapper<DialogflowResponse>()

            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )

            val hasScreenOutput =
                    app.hasSurfaceCapability(app.SURFACE_CAPABILITIES.SCREEN_OUTPUT)
            val hasMagicPowers =
                    app.hasSurfaceCapability("MAGIC_POWERS")
            expect(hasScreenOutput).to.be.equal(true)
            expect(hasMagicPowers).to.be.equal(false)
        }
    }

    /**
     * Describes the behavior for DialogflowApp getSurfaceCapabilities method.
     */
    describe("DialogflowApp#getSurfaceCapabilities") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid list of capabilities from incoming JSON for the success case.") {
            val body = createLiveSessionApiAppBody()
            body.originalRequest?.data?.surface = gson.fromJson("""{
                "capabilities": [
                {
                    "name": "actions.capability.AUDIO_OUTPUT"
                },
                {
                    "name": "actions.capability.SCREEN_OUTPUT"
                }
                ]
            }""", Surface::class.java)

            val mockRequest = RequestWrapper(headerV1, body)
            val mockResponse = ResponseWrapper<DialogflowResponse>()

            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )

            val capabilities = app.getSurfaceCapabilities()
            expect(capabilities).to.equal(mutableListOf(
                    app.SURFACE_CAPABILITIES.AUDIO_OUTPUT,
                    app.SURFACE_CAPABILITIES.SCREEN_OUTPUT
            ))
        }
    }

    /**
     * Describes the behavior for DialogflowApp getInputType method.
     */
    describe("DialogflowApp#getInputType") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid input type from incoming JSON for the success case.") {
            val body = createLiveSessionApiAppBody()
            val KEYBOARD = 3
            body.originalRequest?.data?.inputs = gson.fromJson("""[
            {
                "rawInputs": [
                {
                    "inputType": $KEYBOARD
                }
                ]
            }
            ]""", arrayOf<Input>().javaClass).toMutableList()
            val mockRequest = RequestWrapper(headerV1, body)
            val mockResponse = ResponseWrapper<DialogflowResponse>()
            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )

            val inputType = app.getInputType()
            expect(inputType).to.equal(app.INPUT_TYPES.KEYBOARD)
        }
    }

    /**
     * Describes the behavior for DialogflowApp getRawInput method.
     */
    describe("DialogflowApp#getRawInput") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should raw input from Dialogflow.") {
            val body = createLiveSessionApiAppBody()
            body.result.resolvedQuery = "is it 667"

            val mockRequest = RequestWrapper(headerV1, body)
            val mockResponse = ResponseWrapper<DialogflowResponse>()

            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )

            expect(app.getRawInput()).to.equal("is it 667")
        }
    }

    /**
     * Describes the behavior for DialogflowApp setContext method.
     */
    describe("DialogflowApp#setContext") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid JSON in the response object for the success case.") {
            val body = createLiveSessionApiAppBody()
            val mockRequest = RequestWrapper(headerV1, body)
            val mockResponse = ResponseWrapper<DialogflowResponse>()
            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )

            val CONTEXT_NUMBER = "number"
            app.setContext(CONTEXT_NUMBER)
            app.ask("Welcome to action snippets! Say a number.")

            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "speech": "Welcome to action snippets! Say a number.",
                "data": {
                "google": {
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [

                ]
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {

                }
                },
                {
                    "name": $CONTEXT_NUMBER,
                    "lifespan": 1
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }

    /**
     * Describes the behavior for DialogflowApp getContexts method.
     */
    describe("DialogflowApp#getContexts") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        fun initMockApp() {
            app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )
        }

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV1, body)
            mockResponse = ResponseWrapper<DialogflowResponse>()
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the active contexts from incoming JSON for the success case.") {
            //             let body = createLiveSessionApiAppBody();
            mockRequest.body.result.contexts = gson.fromJson("""[
            {
                "name": "_actions_on_google_"
            },
            {
                "name": "number",
                "lifespan": 5,
                "parameters": {
                "parameterOne": "23",
                "parameterTwo": "24"
            }
            },
            {
                "name": "word",
                "lifespan": 1,
                "parameters": {
                "parameterOne": "wordOne",
                "parameterTwo": "wordTwo"
            }
            }
            ]""", arrayOf<Context>().javaClass).toMutableList()
            initMockApp()
            val mockContexts = app.getContexts()
            val expectedContexts = gson.fromJson("""[
                    {
                        "name": "number",
                        "lifespan": 5,
                        "parameters": {
                        "parameterOne": "23",
                        "parameterTwo": "24"
                    }
                    },
                    {
                        "name": "word",
                        "lifespan": 1,
                        "parameters": {
                        "parameterOne": "wordOne",
                        "parameterTwo": "wordTwo"
                    }
                    }
                    ]""", arrayOf<Context>().javaClass).toMutableList()
            expect(mockContexts).to.equal(expectedContexts)
        }
        it("Should return the active contexts from incoming JSON when only app.data incoming") {
            body.result.contexts = listOf(Context(name = "_actions_on_google_"))

            mockRequest = mockRequest.copy(body = body)
            initMockApp()
            val mockContexts = app.getContexts()
            val expectedContexts = mutableListOf<Context>()

            expect(mockContexts).to.equal(expectedContexts)
        }
        it("Should return the active contexts from incoming JSON when no contexts provided.") {
            // Check the empty case
            mockRequest.body.result.contexts = mutableListOf()
            initMockApp()
            val mockContexts = app.getContexts()
            val expectedContexts = mutableListOf<Context>()
            expect(mockContexts).to.equal(expectedContexts)
        }
    }

    /**
     * Describes the behavior for DialogflowApp getContext method.
     */
    describe("DialogflowApp#getContext") {
        var body: DialogflowRequest = DialogflowRequest()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV1, body)
            mockResponse = ResponseWrapper<DialogflowResponse>()
        }

        fun initMockApp() {
            app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the context by name from incoming JSON for the success case.") {
            mockRequest.body.result.contexts = gson.fromJson("""[{
                "name": "number",
                "lifespan": 5,
                "parameters": {
                "parameterOne": "23",
                "parameterTwo": "24"
            }
            }]""", arrayOf<Context>().javaClass).toMutableList()

            initMockApp()

            val mockContext = app.getContext("number")
            val expectedContext = gson.fromJson("""{
                "name": "number",
                "lifespan": 5,
                "parameters": {
                "parameterOne": "23",
                "parameterTwo": "24"
            }
            }""", Context::class.java)
            expect(mockContext).to.equal(expectedContext)
        }

        it("Should return the context by name from incoming JSON when no context provided.") {
            //  Check the empty case
            body.result.contexts = mutableListOf()
            mockRequest = mockRequest.copy(body = body)
            initMockApp()
            val mockContext = app.getContext("name")
            val expectedContext = null
            expect(mockContext).to.equal(expectedContext)
        }
    }

    /**
     * Describes the behavior for DialogflowApp ask with no inputs method.
     */
    describe("DialogflowApp#ask") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid JSON in the response object for the success case.") {
            val body = createLiveSessionApiAppBody()
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<DialogflowResponse>()
            val app = DialogflowApp(
                    request = mockRequest,
                    response = mockResponse
            )

            app.ask("Welcome to action snippets! Say a number.",
                    "Say any number", "Pick a number", "What is the number?")

            val expectedResponse = responseFromJson("""{
                "speech": "Welcome to action snippets! Say a number.",
                "data": {
                "google": {
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [
                {
                    "textToSpeech": "Say any number"
                },
                {
                    "textToSpeech": "Pick a number"
                },
                {
                    "textToSpeech": "What is the number?"
                }
                ]
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {

                }
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }



    /**
     * Describes the behavior for DialogflowApp askToRegisterDailyUpdate method.
     */
    describe("#askToRegisterDailyUpdate",  {
        val body = createLiveSessionApiAppBody()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockRequest = RequestWrapper(headerV2, body)
            app = DialogflowApp(
                 mockRequest,
                 mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON update registration request",  {
            app.askToRegisterDailyUpdate("test_intent", gson.fromJson("""[
                {
                    name: "intent_name",
                    textValue: "intent_value"
                }
            ]""", arrayOf<Arguments>().javaClass).toMutableList())
            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_REGISTER_UPDATE",
                "data": {
                "google": {
                "userStorage": "{\"data\":{}}",
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.REGISTER_UPDATE",
                "data": {
                "intent": "test_intent",
                "arguments": [
                {
                    "name": "intent_name",
                    "textValue": "intent_value"
                }
                ],
                "triggerContext": {
                "timeContext": {
                "frequency": "DAILY"
            }
            },
                "@type": "type.googleapis.com/google.actions.v2.RegisterUpdateValueSpec"
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        })

        // Success case test, when the API returns a valid 200 response
        // with the response object without arguments
        it("Should return valid JSON update registration request",  {
            app.askToRegisterDailyUpdate("test_intent")
            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_REGISTER_UPDATE",
                "data": {
                "google": {
                "userStorage": "{"data":{}}",
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.REGISTER_UPDATE",
                "data": {
                "intent": "test_intent",
                "triggerContext": {
                "timeContext": {
                "frequency": "DAILY"
            }
            },
                "@type": "type.googleapis.com/google.actions.v2.RegisterUpdateValueSpec"
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        })

        // Failure case test, when an invalid intent name is given
        it("Should return null",  {
            expect(app.askToRegisterDailyUpdate("", gson.fromJson("""[
                {
                    name: "intent_name",
                    textValue: "intent_value"
                }
            ]""", arrayOf<Arguments>().javaClass).toMutableList())).to.be.equal(null)
            expect(mockResponse.statusCode).to.equal(400)
        })
    })

    /**
     * Describes the behavior for DialogflowApp isUpdateRegistered method.
     */
    describe("#isUpdateRegistered",  {
        val body = createLiveSessionApiAppBody()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        fun initMockApp () {
            mockRequest = RequestWrapper(headerV1, body)
            app = DialogflowApp(mockRequest, mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate user registration status.",  {
            mockRequest.body?.originalRequest?.data?.inputs?.get(0)?.arguments = gson.fromJson("""[
                {
                    "name": "REGISTER_UPDATE",
                    "extension": {
                    "@type": "type.googleapis.com/google.actions.v2.RegisterUpdateValue",
                    "status": "OK"
                }
                }]""", arrayOf<Arguments>().javaClass).toMutableList()

            initMockApp()
            expect(app.isUpdateRegistered()).to.equal(true)

            // Test the false case
            mockRequest.body.originalRequest?.data?.inputs?.get(0)?.arguments?.get(0)?.extension?.status = "CANCELLED"
            initMockApp()
            expect(app.isPermissionGranted()).to.equal(false)
        })
    })

    /**
     * Describes the behavior for DialogflowApp askForUpdatePermission method in v1.
     */
    describe("#askForUpdatePermission",  {
        val body = createLiveSessionApiAppBody()
        var mockRequest: RequestWrapper<DialogflowRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<DialogflowResponse> = ResponseWrapper()
        var app: DialogflowApp = DialogflowApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockRequest = RequestWrapper(headerV2, body)
            app = DialogflowApp(mockRequest, mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid JSON in the response object for the success case.",  {
            app.askForUpdatePermission("test_intent", gson.fromJson("""[
                {
                    name: "intent_name",
                    textValue: "intent_value"
                }
            ]""", arrayOf<Arguments>().javaClass).toMutableList())
            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_PERMISSION",
                "data": {
                "google": {
                "userStorage": "{\"data\":{}}",
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.PERMISSION",
                "data": {
                "@type": "type.googleapis.com/google.actions.v2.PermissionValueSpec",
                "permissions": ["UPDATE"],
                "updatePermissionValueSpec": {
                "intent": "test_intent",
                "arguments": [
                {
                    "name": "intent_name",
                    "textValue": "intent_value"
                }
                ]
            }
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        })

        // Success case test, when the API returns a valid 200 response
        // with the response object without arguments
        it("Should return the valid JSON in the response object " +
                "without arguments for the success case.",  {
            app.askForUpdatePermission("test_intent")
            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "speech": "PLACEHOLDER_FOR_PERMISSION",
                "data": {
                "google": {
                "userStorage": "{\"data\":{}}",
                "expectUserResponse": true,
                "isSsml": false,
                "noInputPrompts": [],
                "systemIntent": {
                "intent": "actions.intent.PERMISSION",
                "data": {
                "@type": "type.googleapis.com/google.actions.v2.PermissionValueSpec",
                "permissions": ["UPDATE"],
                "updatePermissionValueSpec": {
                "intent": "test_intent"
            }
            }
            }
            }
            },
                "contextOut": [
                {
                    "name": "_actions_on_google_",
                    "lifespan": 100,
                    "parameters": {}
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        })

        // Failure case test, when an invalid intent name is given
        it("Should return null",  {
            expect(app.askForUpdatePermission("", gson.fromJson("""[
                {
                    name: "intent_name",
                    textValue: "intent_value"
                }
            ]""", arrayOf<Arguments>().javaClass).toMutableList())).to.be.equal(null)
            expect(mockResponse.statusCode).to.equal(400)
        })
    })


    /**
     * Tests parsing parameters with Gson and retrieving parameter values
     * NOTE: This are an addition to the official test suite.  These are needed due to the dynamic nature of
     * parameters.
     */
    describe("DialogflowResponse#Result#Parameters") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("fields should be accessible through map") {
            val body = createLiveSessionApiAppBody()
            val mockRequest = RequestWrapper(headerV1, body)

            expect(mockRequest.body.result.parameters?.get("city")).to.be.equal("Rome")
            expect(mockRequest.body.result.parameters?.get("list")).to.be.equal(listOf("one", "two"))
            expect(mockRequest.body.result.parameters?.get("nested")).to.be.equal(mutableMapOf("nestedField" to "n1"))
        }
    }

    it("Should allow extending with custom data") {
        val body = createLiveSessionApiAppBody()
        val mockRequest = RequestWrapper(headerV2, body)
        val mockResponse = ResponseWrapper<DialogflowResponse>()
        val app = DialogflowApp(
                request = mockRequest,
                response = mockResponse
        )

        app.data {
            this["customData"] = CustomData("test")
        }
        app.ask("Welcome to action snippets! Say a number.",
                "Say any number", "Pick a number", "What is the number?")


        val expectedResponse = """{"speech":"Welcome to action snippets! Say a number.","displayText":"","secondDisplayText":"","data":{"customData":{"testString":"test"}},"contextOut":[{"name":"_actions_on_google_","parameters":{},"lifespan":100}],"source":""}"""
        //json must remain unformatted to match gson output.
        // data class equals() will not match in this case because Data overrides MutableMap
        expect(gson.toJson(mockResponse.body)).to.equal(expectedResponse)
    }

})

data class CustomData(val testString: String? = null)
