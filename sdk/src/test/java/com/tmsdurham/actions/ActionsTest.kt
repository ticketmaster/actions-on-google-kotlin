package com.tmsdurham.actions

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.ticketmaster.apiai.*
import com.ticketmaster.apiai.google.GoogleData
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

val gson = GsonBuilder().setPrettyPrinting().create()

//data class MockParameters(var guess: String? = null)

typealias MockHandler = Handler<ApiAiRequest, ApiAiResponse>

val headerV1 = mapOf(
        "Content-Type" to "application/json",
        "google-assistant-api-version" to "v1"
)

val headerV2 = mapOf(
        "Content-Type" to "application/json",
        "Google-Actions-API-Version" to "2"
)
const val fakeTimeStamp = "2017-01-01T12:00:00"
const val fakeSessionId = "0123456789101112"
const val fakeIntentId = "1a2b3c4d-5e6f-7g8h-9i10-11j12k13l14m15n16o"
const val fakeApiAiBodyRequestId = "1a2b3c4d-5e6f-7g8h-9i10-11j12k13l14m15n16o"
const val fakeUserId = "user123"
const val fakeConversationId = "0123456789"

// Body of the ApiAi request that starts a new session
// new session is originalRequest.data.conversation.type == 1
fun apiAiAppRequestBodyNewSession(): ApiAiRequest {
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
        "id": fakeApiAiBodyRequestId,
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

fun createLiveSessionApiAppBody(): ApiAiRequest {
    var tmp = apiAiAppRequestBodyNewSession()
    tmp.originalRequest?.data?.conversation?.type = "2"
    return tmp
}


object ActionsTest : Spek({
    // ---------------------------------------------------------------------------
    //                   App helpers
    // ---------------------------------------------------------------------------
    /**
     * Describes the behavior for Assistant isNotApiVersionOne_ method.
     */
    describe("ApiAiApp#isNotApiVersionOne") {
        var mockResponse = ResponseWrapper<ApiAiResponse>()

        val invalidHeader = mapOf(
                "Content-Type" to "application/json",
                "google-assistant-api-version" to "v1",
                "Google-Actions-API-Version" to "1"
        )
        val headerV1 = mapOf(
                "Content-Type" to "application/json",
                "google-assistant-api-version" to "v1"
        )

        beforeEachTest {
            mockResponse = ResponseWrapper()
        }


        it("Should detect Proto2 when header is not present") {
            val mockRequest = RequestWrapper(headerV1, ApiAiRequest())

            val app = ApiAiApp(mockRequest, mockResponse)

            expect(app.isNotApiVersionOne()).to.equal(false)
        }

        it("Should detect v1 when header is present") {
            val mockRequest = RequestWrapper(invalidHeader, ApiAiRequest())
            val mockResponse = ResponseWrapper<ApiAiResponse>()
            val app = ApiAiApp(request = mockRequest, response = mockResponse)
            expect(app.isNotApiVersionOne()).to.equal(false)
        }

        it("Should detect v2 when version is present in APIAI req") {
            val mockRequest = RequestWrapper(headerV1, apiAiRequest {
                result {
                    originalRequest {
                        version = "1"
                    }
                }
            })

            val mockResponse = ResponseWrapper<ApiAiResponse>()

            val app = ApiAiApp(request = mockRequest, response = mockResponse)
            debug(mockRequest.toString())

            expect(app.isNotApiVersionOne()).to.equal(false)
        }

        it("Should detect v2 when header is present") {
            val headers = HashMap(headerV1)
            headers["Google-Actions-API-Version"] = "2"

            val mockRequest = RequestWrapper<ApiAiRequest>(headers, ApiAiRequest())
            val mockResponse = ResponseWrapper<ApiAiResponse>()

            val app = ApiAiApp(request = mockRequest, response = mockResponse)

            expect(app.isNotApiVersionOne()).to.equal(true)
        }

        it("Should detect v2 when version is present in APIAI req") {
            val mockRequest = RequestWrapper(headerV1, apiAiRequest {
                originalRequest {
                    version = "2"
                }
            })
            val mockResponse = ResponseWrapper<ApiAiResponse>()

            val app = ApiAiApp(request = mockRequest, response = mockResponse)

            expect(app.isNotApiVersionOne()).to.equal(true)
        }

        /**
         * Describes the behavior for AssistantApp isSsml_ method.
         */
        describe("ApiAiApp#isSsml_") {
            // Success case test, when the API returns a valid 200 response with the response object
            it("Should validate SSML syntax.") {
                val mockRequest = RequestWrapper(headerV1, apiAiAppRequestBodyNewSession())
                val mockResponse = ResponseWrapper<ApiAiResponse>()
                val app = ApiAiApp(request = mockRequest, response = mockResponse)
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
    //                   API.ai support
    // ---------------------------------------------------------------------------

    /**
     * Describes the behavior for ApiAiApp constructor method.
     */
    describe("ApiAiApp#constructor") {
        var mockResponse = ResponseWrapper<ApiAiResponse>()

        // Calls sessionStarted when provided
        it("Calls sessionStarted when new session") {
            var mockRequest = RequestWrapper(headerV1, apiAiAppRequestBodyNewSession())

            val sessionStartedSpy = mock<(() -> Unit)> {}

            val app = ApiAiApp(
                    request = mockRequest,
                    response = mockResponse,
                    sessionStarted = sessionStartedSpy
            )
            debug(mockRequest.toString())

            app.handleRequest(handler = {})

            verify(sessionStartedSpy).invoke()
        }
    }

    // Does not call sessionStarted when not new sessoin
    it("Does not call sessionStarted when not new session") {
        val mockRequest = RequestWrapper(headerV1, createLiveSessionApiAppBody())
        val mockResponse = ResponseWrapper<ApiAiResponse>()

        val sessionStartedSpy = mock<(() -> Unit)> {}

        val app = ApiAiApp(
                request = mockRequest,
                response = mockResponse,
                sessionStarted = sessionStartedSpy
        )

        app.handleRequest(handler = {})

        verifyZeroInteractions(sessionStartedSpy)
    }


    //TODO 2 tests

    /**
     * Describes the behavior for ApiAiApp tell method.
     */
    describe("ApiAiApp#tell") {
        var mockResponse = ResponseWrapper<ApiAiResponse>()
        var body = ApiAiRequest()
        var mockRequest = RequestWrapper<ApiAiRequest>(body = body)
        var app = ApiAiApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper<ApiAiResponse>()
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV1, body)
            app = ApiAiApp(request = mockRequest, response = mockResponse)
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
                    displayText = "Hi")

            // Validating the response object
            var expectedResponse = """{
                "speech": "hello",
                "data": {
                "google": {
                "expect_user_response": false,
                "rich_response": {
                "items": [
                {
                    "simple_response": {
                    "text_to_speech": "hello",
                    "display_text": "hi"
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
            "expect_user_response": false,
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
     * Describes the behavior for ApiAiApp askWithList method.
     */
    describe("ApiAiApp#askWithList") {

        var mockResponse = ResponseWrapper<ApiAiResponse>()
        var body = ApiAiRequest()
        var mockRequest = RequestWrapper<ApiAiRequest>(body = body)
        var app = ApiAiApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
            body.originalRequest?.version = "2"
            mockRequest = RequestWrapper(headerV1, body)
            app = ApiAiApp(request = mockRequest, response = mockResponse)
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
     * Describes the behavior for ApiAiApp askWithCarousel method.
     */
    describe("ApiAiApp#askWithCarousel") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
        var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
            body.originalRequest?.version = "2"
            mockRequest = RequestWrapper(headerV1, body)
            app = ApiAiApp(request = mockRequest, response = mockResponse)
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
     * Describes the behavior for ApiAiApp askForPermissions method in v2.
     */
    describe("ApiAiApp#askForPermissions") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
        var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper<ApiAiResponse>()
            body = createLiveSessionApiAppBody()
            body.originalRequest?.version = "2"
            mockRequest = RequestWrapper(headerV1, body)
            app = ApiAiApp(request = mockRequest, response = mockResponse)
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
     * Describes the behavior for ApiAiApp getUser method.
     */
    describe("ApiAiApp#getUser") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
        var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
            body.originalRequest?.data?.user?.userId = "11112226094657824893"
            mockRequest = RequestWrapper(headerV1, body)
            app = ApiAiApp(request = mockRequest, response = mockResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user.") {
            // Test new and old API
//            expect(app.getUser().user_id).to.equal("11112226094657824893");
            expect(app.getUser()?.userId).to.equal("11112226094657824893")
        }
    }

    /**
     * Describes the behavior for ApiAiApp getUserName method.
     */
    describe("ApiAiApp#getUserName") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
        var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user.") {
            var mockRequest: RequestWrapper<ApiAiRequest>
            body.originalRequest?.data?.user = gson.fromJson("""{
                "userId": "11112226094657824893",
                "profile": {
                "displayName": "John Smith",
                "givenName": "John",
                "familyName": "Smith"
            }
            }""", User::class.java)
            mockRequest = RequestWrapper(headerV1, body)
            app = ApiAiApp(request = mockRequest, response = mockResponse)
            expect(app.getUserName()?.displayName).to.equal("John Smith")
            expect(app.getUserName()?.givenName).to.equal("John")
            expect(app.getUserName()?.familyName).to.equal("Smith")

            // Test the false case
            body.originalRequest?.data?.user?.profile = null
            mockRequest = RequestWrapper(headerV1, body)
            app = ApiAiApp(request = mockRequest, response = mockResponse)
            expect(app.getUserName()).to.equal(null)
        }
    }

    /**
     * Describes the behavior for ApiAiApp getUserLocale method.
     */
    describe("ApiAiApp#getUserLocale") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user with locale.") {
            var mockRequest: RequestWrapper<ApiAiRequest>
            val app: ApiAiApp
            mockRequest = RequestWrapper(headerV1, body)
            app = ApiAiApp(request = mockRequest, response = mockResponse)
            expect(app.getUserLocale()).to.equal("en-US")
        }

        // Failure case
        it("Should return null for missing locale.") {
            var mockRequest: RequestWrapper<ApiAiRequest>
            val app: ApiAiApp
            body.originalRequest?.data?.user?.locale = null
            mockRequest = RequestWrapper(headerV1, body)
            app = ApiAiApp(request = mockRequest, response = mockResponse)
            expect(app.getUserLocale()).to.equal(null)
        }
    }


    /**
     * Describes the behavior for ApiAiApp getDeviceLocation method.
     */
    describe("ApiAiApp#getDeviceLocation") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
        var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

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
            app = ApiAiApp(request = mockRequest, response = mockResponse)
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
     * Describes the behavior for ApiAiApp askForTransactionRequirements method.
     */
    describe("ApiAiApp#askForTransactionRequirements") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
        var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody();
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper()
            app = ApiAiApp(
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
     * Describes the behavior for ApiAiApp askForDeliveryAddress method.
     */
    describe("ApiAiApp#askForDeliveryAddress") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON delivery address") {
            val body = createLiveSessionApiAppBody()
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ApiAiResponse>()

            val app = ApiAiApp(
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
     * Describes the behavior for ApiAiApp askForTransactionDecision method.
     */
    describe("ApiAiApp#askForTransactionDecision") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
        var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper()
            app = ApiAiApp(
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
                            "VISA",
                            "MASTERCARD")
                    ,
                    prepaidCardDisallowed = false,
                    customerInfoOptions = mutableListOf(
                            "EMAIL"
                    )
            )

            app.askForTransactionDecision(GoogleData.Order(id = "order_id"), transactionConfig)

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
     * Describes the behavior for ApiAiApp askForConfirmation method.
     */
    describe("ApiAiApp#askForConfirmation") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
        var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper()
            app = ApiAiApp(
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
         * Describes the behavior for ApiAiApp askForDateTime method.
         */
        describe("ApiAiApp#askForDateTime") {
            var body: ApiAiRequest = ApiAiRequest()
            var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
            var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
            var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

            beforeEachTest {
                body = createLiveSessionApiAppBody()
                mockRequest = RequestWrapper(headerV2, body)
                mockResponse = ResponseWrapper()
                app = ApiAiApp(
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
     * Describes the behavior for ApiAiApp askForSignIn method.
     */
    describe("ApiAiApp#askForSignIn") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
        var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

        beforeEachTest {
            body = createLiveSessionApiAppBody()
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper()
            app = ApiAiApp(
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
                "data": {}
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
     * Describes the behavior for ApiAiApp isPermissionGranted method.
     */
    describe("ApiAiApp#isPermissionGranted") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
        var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

        fun initMockApp() {
            mockRequest = RequestWrapper(headerV1, body)
            mockResponse = ResponseWrapper()
            app = ApiAiApp(
                    request = mockRequest,
                    response = mockResponse
            )
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user.") {
            body = createLiveSessionApiAppBody()
            body?.originalRequest?.data?.inputs?.get(0)?.arguments = listOf(Arguments(
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
     * Describes the behavior for ApiAiApp isInSandbox method.
     */
    describe("ApiAiApp#isInSandbox") {
        var body: ApiAiRequest = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
        var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

        fun initMockApp() {
            mockRequest = RequestWrapper(headerV1, body)
            mockResponse = ResponseWrapper()
            app = ApiAiApp(
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
     * Describes the behavior for ApiAiApp getIntent method.
     */
    describe("ApiAiApp#getIntent") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should get the intent value for the success case.") {
            val body = createLiveSessionApiAppBody()
            body.result.action = "check_guess"
            val mockRequest = RequestWrapper(headerV1, body)
            val mockResponse = ResponseWrapper<ApiAiResponse>()

            val app = ApiAiApp(
                    request = mockRequest,
                    response = mockResponse
            )

            expect(app.getIntent()).to.equal("check_guess")
        }
    }

    /**
     * Describes the behavior for ApiAiApp getArgument method.
     */
    describe("ApiAiApp#getArgument") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should get the argument value for the success case.") {
            val body = createLiveSessionApiAppBody()
            body.result?.parameters?.set("guess", "50")
            val t = TypeToken.get(Arguments::class.java).type
            val type = TypeToken.getParameterized(List::class.java, t)
            body.originalRequest?.data?.inputs?.get(0)?.arguments =
                    listOf(Arguments(rawText = "raw text one", textValue = "text value one", name = "arg_value_one"),
                            Arguments(rawText = "45", name = "other_value", otherValue = mapOf("key" to "value")))

            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ApiAiResponse>()

            val app = ApiAiApp(
                    request = mockRequest,
                    response = mockResponse
            )

            expect(app.getArgument("guess")).to.equal("50")
            expect(app.getArgument("arg_value_one")).to.equal("text value one")
            expect(app.getArgument("other_value")).to.equal(gson.fromJson("""{
                "name": "other_value",
                "rawText": "45",
                "otherValue": {
                "key": "value"
            }}""", Arguments::class.java))
        }
    }

    /**
     * Describes the behavior for ApiAiApp getContextArgument method.
     */
    describe("ApiAiApp#getContextArgument") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should get the context argument value for the success case.") {
            val body = createLiveSessionApiAppBody()
            body.result.contexts = mutableListOf(
                    Contexts(name = "game",
                            parameters = mutableMapOf(
                                    "guess.original" to "50",
                                    "guess" to "50"),
                            lifespan = 5),
                    Contexts(name = "previous_answer",
                            parameters = mutableMapOf(
                                    "answer" to "68",
                                    "guess.original" to "51",
                                    "guess" to "50"),
                            lifespan = 50
                    ))

            val mockRequest = RequestWrapper(headerV1, body)
            val mockResponse = ResponseWrapper<ApiAiResponse>()

            val app = ApiAiApp(
                    request = mockRequest,
                    response = mockResponse
            )

            expect(app.getContextArgument("game", "guess")).to
                    .equal(ApiAiApp.ContextArgument(value = "50", original = "50"))
            expect(app.getContextArgument("previous_answer", "answer")).to
                    .equal(ApiAiApp.ContextArgument(value = "68"))
        }
    }

    /**
     * Describes the behavior for ApiAiApp getIncomingRichResponse method.
     */
    describe("ApiAiApp#getIncomingRichResponse") {
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
            val mockResponse = ResponseWrapper<ApiAiResponse>()

            val app = ApiAiApp(
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
         * Describes the behavior for ApiAiApp getIncomingList method.
         */
        describe("ApiAiApp#getIncomingList") {
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
                val mockResponse = ResponseWrapper<ApiAiResponse>()

                val app = ApiAiApp(
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
         * Describes the behavior for ApiAiApp getIncomingCarousel method.
         */
        describe("ApiAiApp#getIncomingCarousel") {
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
                val mockResponse = ResponseWrapper<ApiAiResponse>()

                val app = ApiAiApp(
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
         * Describes the behavior for ApiAiApp getSelectedOption method.
         */
        describe("ApiAiApp#getSelectedOption") {
            var body: ApiAiRequest = ApiAiRequest()
            var mockRequest: RequestWrapper<ApiAiRequest> = RequestWrapper(body = body)
            var mockResponse: ResponseWrapper<ApiAiResponse> = ResponseWrapper()
            var app: ApiAiApp = ApiAiApp(mockRequest, mockResponse, { false })

            beforeEachTest {
                mockRequest = RequestWrapper(headerV1, body)
                mockResponse = ResponseWrapper()
            }


            // Success case test, when the API returns a valid 200 response with the response object
            it("Should get the selected option when given in APIAI context.") {
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
                }""", Inputs::class.java))
                body.result.contexts = gson.fromJson("""[
                {
                    "name": "actions_intent_option",
                    "parameters": {
                    "OPTION": "first_item"
                },
                    "lifespan": 0
                }
                ]""", arrayOf<Contexts>().javaClass).toList()
                mockRequest = mockRequest.copy(body = body)
                app = ApiAiApp(
                        request = mockRequest,
                        response = mockResponse
                )
                expect(app.getSelectedOption()).to.equal("first_item")
            }
        }
    }


    /**
     * Tests parsing parameters with Gson and retrieving parameter values
     * NOTE: This are an addition to the official test suite.  These are needed due to the dynamic nature of
     * parameters.
     */
    describe("ApiAiResponse#Result#Parameters") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("fields should be accessible through map") {
            val body = createLiveSessionApiAppBody()
            val mockRequest = RequestWrapper(headerV1, body)

            expect(mockRequest.body.result.parameters?.get("city")).to.be.equal("Rome")
            expect(mockRequest.body.result.parameters?.get("list")).to.be.equal(listOf("one", "two"))
            expect(mockRequest.body.result.parameters?.get("nested")).to.be.equal(mutableMapOf("nestedField" to "n1"))
        }
    }
})


fun requestFromJson(body: String): ApiAiRequest {
//    val t = TypeToken.get(MockParameters::class.java).type
//    val type = TypeToken.getParameterized(ApiAiRequest::class.java, t)
    return gson.fromJson<ApiAiRequest>(body, ApiAiRequest::class.java)
}

fun responseFromJson(body: String): ApiAiResponse {
//    val t = TypeToken.get(MockParameters::class.java).type
//    val responseType = TypeToken.getParameterized(ApiAiResponse::class.java, t)
    return gson.fromJson<ApiAiResponse>(body, ApiAiResponse::class.java)
}