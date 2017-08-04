package com.tmsdurham.actions

import com.tmsdurham.actions.actions.ActionRequest
import com.tmsdurham.actions.actions.ActionResponse
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

typealias MockActionHandler = Handler<ActionRequest, ActionResponse>

//Serializer to handle serialization of conversation token
val serializer: Serializer = object : Serializer {
    override fun <T> serialize(obj: T) = gson.toJson(obj)

    override fun <T> deserialize(str: String, clazz: Class<T>) = gson.fromJson(str, clazz)
}

object ActionsSdkTest : Spek({
    fun requestFromJson(body: String) = gson.fromJson<ActionRequest>(body, ActionRequest::class.java)
    fun responseFromJson(body: String) = gson.fromJson(body, ActionResponse::class.java)

    fun actionsSdkAppRequestBodyNewSession(): ActionRequest {
        return requestFromJson("""{
        "user": {
        "user_id": fakeUserId
    },
        "conversation": {
        "conversation_id": "1480373842830",
        "type": 1
    },
        "inputs": [
        {
            "intent": "assistant.intent.action.MAIN",
            "raw_inputs": [
            {
                "input_type": 2,
                "query": "talk to hello action"
            }
            ],
            "arguments": [
            {
                "name": "agent_info"
            }
            ]
        }
        ]
    }""")
    }

    fun createLiveSessionActionsSdkAppBody(): ActionRequest {
        val tmp = actionsSdkAppRequestBodyNewSession()
        tmp.conversation?.type = "2"
        return tmp
    }
    /**
     * Describes the behavior for ApiAiApp constructor method.
     */
    describe("ActionsSdkApp#constructor") {
        var mockResponse = ResponseWrapper<ActionResponse>()

        beforeEachTest {
            mockResponse = ResponseWrapper<ActionResponse>()
        }

        /*
        // Calls sessionStarted when provided
        it("Calls sessionStarted when new session") {
            val mockRequest = RequestWrapper(headerV1, actionsSdkAppRequestBodyNewSession())

            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    sessionStarted = sessionStartedSpy)
            app.handleRequest({})
            expect(sessionStartedSpy).to.have.been.called()
        }
        */

        // Does transform to Proto3
        it("Does not detect v2 and transform body when version not present") {
            val mockRequest = RequestWrapper(headerV1, createLiveSessionActionsSdkAppBody())
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer
            )
            app.handleRequest({})
            expect(app.request.body).to.equal(requestFromJson("""{
                "user": {
                "userId": fakeUserId
            },
                "conversation": {
                "conversationId": "1480373842830",
                "type": 2
            },
                "inputs": [
                {
                    "intent": "assistant.intent.action.MAIN",
                    "rawInputs": [
                    {
                        "inputType": 2,
                        "query": "talk to hello action"
                    }
                    ],
                    "arguments": [
                    {
                        "name": "agent_info"
                    }
                    ]
                }
                ]
            }"""))
//            expect(app.request.body).to.not.equal(createLiveSessionActionsSdkAppBody())
        }

        // Test a change made for backwards compatibility with legacy sample code
        it("Does initialize StandardIntents without an options object") {
            val app = ActionsSdkApp(RequestWrapper(headerV1, createLiveSessionActionsSdkAppBody()), mockResponse, serializer = serializer)

            expect(app.STANDARD_INTENTS.MAIN).to.equal("assistant.intent.action.MAIN")
            expect(app.STANDARD_INTENTS.TEXT).to.equal("assistant.intent.action.TEXT")
            expect(app.STANDARD_INTENTS.PERMISSION).to
                    .equal("assistant.intent.action.PERMISSION")
        }
    }
    /**
     * Describes the behavior for ActionsSdkApp ask method.
     */
    describe("ActionsSdkApp#ask") {
        var mockRequest = RequestWrapper(headerV1, createLiveSessionActionsSdkAppBody())
        var mockResponse = ResponseWrapper<ActionResponse>()
        var app = ActionsSdkApp(mockRequest, mockResponse, serializer = serializer)

        beforeEachTest {
            mockRequest = RequestWrapper(headerV1, createLiveSessionActionsSdkAppBody())
            mockResponse = ResponseWrapper<ActionResponse>()
            debug("before test: ${mockResponse}")
            app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse, serializer = serializer)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid JSON in the response object for the success case.") {
            debug("here 44 ${mockResponse}")
            val inputPrompt = app.buildInputPrompt(true, """<speak>Hi! <break time='1'/> """ +
                    "I can read out an ordinal like " +
                    """<say-as interpret-as='ordinal'>123</say-as>. Say a number.</speak>""",
                    mutableListOf("I didn\'t hear a number", "If you\'re still there, what\'s the number?", "What is the number?"))
            app.ask(inputPrompt)

            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"data\":{}}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "ssml": "<speak>Hi! <break time='1'/> I can read out an ordinal like <say-as interpret-as='ordinal'>123</say-as>. Say a number.</speak>"
                    }
                    ],
                    "noInputPrompts": [
                    {
                        "ssml": "I didn\'t hear a number"
                    },
                    {
                        "ssml": "If you\'re still there, what\'s the number?"
                    },
                    {
                        "ssml": "What is the number?"
                    }
                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "assistant.intent.action.TEXT"
                    }
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }


})
