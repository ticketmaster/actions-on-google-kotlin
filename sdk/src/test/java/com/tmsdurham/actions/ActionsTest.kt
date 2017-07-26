package com.tmsdurham.actions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.ticketmaster.apiai.*
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

val gson = Gson()

class MockParameters

typealias MockHandler = Handler<ApiAiRequest<MockParameters>, ApiAiResponse<MockParameters>, MockParameters>

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
fun apiAiAppRequestBodyNewSession(): ApiAiRequest<MockParameters> {
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
        "name": "Ana"
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

fun createLiveSessionApiAppBody(): ApiAiRequest<MockParameters> {
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
        var mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()

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
            val mockRequest = RequestWrapper(headerV1, ApiAiRequest<MockParameters>())

            val app = ApiAiApp(mockRequest, mockResponse)

            expect(app.isNotApiVersionOne()).to.equal(false)
        }

        it("Should detect v1 when header is present") {
            val mockRequest = RequestWrapper(invalidHeader, ApiAiRequest<MockParameters>())
            val mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()
            val app = ApiAiApp(request = mockRequest, response = mockResponse)
            expect(app.isNotApiVersionOne()).to.equal(false)
        }

        it("Should detect v2 when version is present in APIAI req") {
            val mockRequest = RequestWrapper(headerV1, apiAiRequest<MockParameters> {
                result {
                    originalRequest {
                        version = "1"
                    }
                }
            })

            val mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()

            val app = ApiAiApp(request = mockRequest, response = mockResponse)
            debug(mockRequest.toString())

            expect(app.isNotApiVersionOne()).to.equal(false)
        }

        it("Should detect v2 when header is present") {
            val headers = HashMap(headerV1)
            headers["Google-Actions-API-Version"] = "2"

            val mockRequest = RequestWrapper<ApiAiRequest<MockParameters>>(headers, ApiAiRequest())
            val mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()

            val app = ApiAiApp(request = mockRequest, response = mockResponse)

            expect(app.isNotApiVersionOne()).to.equal(true)
        }

        it("Should detect v2 when version is present in APIAI req") {
            val mockRequest = RequestWrapper(headerV1, apiAiRequest<MockParameters> {
                originalRequest {
                    version = "2"
                }
            })
            val mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()

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
                val mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()
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
        var mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()

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
        val mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()

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
        var mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()
        var body = ApiAiRequest<MockParameters>()
        var mockRequest = RequestWrapper<ApiAiRequest<MockParameters>>(body = body)
        var app = ApiAiApp<MockParameters>(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()
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

        var mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()
        var body = ApiAiRequest<MockParameters>()
        var mockRequest = RequestWrapper<ApiAiRequest<MockParameters>>(body = body)
        var app = ApiAiApp<MockParameters>(mockRequest, mockResponse, { false })

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
        var body: ApiAiRequest<MockParameters> = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest<MockParameters>> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse<MockParameters>> = ResponseWrapper()
        var app: ApiAiApp<MockParameters> = ApiAiApp<MockParameters>(mockRequest, mockResponse, { false })

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
        var body: ApiAiRequest<MockParameters> = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest<MockParameters>> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse<MockParameters>> = ResponseWrapper()
        var app: ApiAiApp<MockParameters> = ApiAiApp<MockParameters>(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper<ApiAiResponse<MockParameters>>()
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
        var body: ApiAiRequest<MockParameters> = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest<MockParameters>> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse<MockParameters>> = ResponseWrapper()
        var app: ApiAiApp<MockParameters> = ApiAiApp<MockParameters>(mockRequest, mockResponse, { false })

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
        var body: ApiAiRequest<MockParameters> = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest<MockParameters>> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse<MockParameters>> = ResponseWrapper()
        var app: ApiAiApp<MockParameters> = ApiAiApp<MockParameters>(mockRequest, mockResponse, { false })

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user.") {
            var mockRequest: RequestWrapper<ApiAiRequest<MockParameters>>
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
        var body: ApiAiRequest<MockParameters> = ApiAiRequest()
        var mockResponse: ResponseWrapper<ApiAiResponse<MockParameters>> = ResponseWrapper()

        beforeEachTest {
            mockResponse = ResponseWrapper()
            body = createLiveSessionApiAppBody()
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user with locale.") {
            var mockRequest: RequestWrapper<ApiAiRequest<MockParameters>>
            val app: ApiAiApp<MockParameters>
            mockRequest = RequestWrapper(headerV1, body)
            app = ApiAiApp(request = mockRequest, response = mockResponse)
            expect(app.getUserLocale()).to.equal("en-US")
        }

        // Failure case
        it("Should return null for missing locale.") {
            var mockRequest: RequestWrapper<ApiAiRequest<MockParameters>>
            val app: ApiAiApp<MockParameters>
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
        var body: ApiAiRequest<MockParameters> = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest<MockParameters>> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse<MockParameters>> = ResponseWrapper()
        var app: ApiAiApp<MockParameters> = ApiAiApp<MockParameters>(mockRequest, mockResponse, { false })

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
        var body: ApiAiRequest<MockParameters> = ApiAiRequest()
        var mockRequest: RequestWrapper<ApiAiRequest<MockParameters>> = RequestWrapper(body = body)
        var mockResponse: ResponseWrapper<ApiAiResponse<MockParameters>> = ResponseWrapper()
        var app: ApiAiApp<MockParameters> = ApiAiApp<MockParameters>(mockRequest, mockResponse, { false })

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
    }


})


fun requestFromJson(body: String): ApiAiRequest<MockParameters> {
    val t = TypeToken.get(MockParameters::class.java).type
    val type = TypeToken.getParameterized(ApiAiRequest::class.java, t)
    return gson.fromJson<ApiAiRequest<MockParameters>>(body, type.type)
}

fun responseFromJson(body: String): ApiAiResponse<MockParameters> {
    val t = TypeToken.get(MockParameters::class.java).type
    val responseType = TypeToken.getParameterized(ApiAiResponse::class.java, t)
    return gson.fromJson<ApiAiResponse<MockParameters>>(body, responseType.type)
}