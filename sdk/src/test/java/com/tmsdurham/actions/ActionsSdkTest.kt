package com.tmsdurham.actions

import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.tmsdurham.dialogflow.*
import com.tmsdurham.actions.actions.ActionRequest
import com.tmsdurham.actions.actions.ActionResponse
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

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
        "userId": $fakeUserId
    },
        "conversation": {
        "conversationId": "1480373842830",
        "type": 1
    },
        "inputs": [
        {
            "intent": "actions.intent.MAIN",
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
    }""")
    }

    fun createLiveSessionActionsSdkAppBody(): ActionRequest {
        val tmp = actionsSdkAppRequestBodyNewSession()
        tmp.conversation?.type = "2"
        return tmp
    }
    /**
     * Describes the behavior for DialogflowApp constructor method.
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

        /*
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
                    "intent": "actions.intent.MAIN",
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
*/
        // Test a change made for backwards compatibility with legacy sample code
        it("Does initialize StandardIntents without an options object") {
            val app = ActionsSdkApp(RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody()), mockResponse, serializer = serializer)

            expect(app.STANDARD_INTENTS.MAIN).to.equal("actions.intent.MAIN")
            expect(app.STANDARD_INTENTS.TEXT).to.equal("actions.intent.TEXT")
            expect(app.STANDARD_INTENTS.PERMISSION).to
                    .equal("actions.intent.PERMISSION")
        }
    }
    /**
     * Describes the behavior for ActionsSdkApp ask method.
     */
    describe("ActionsSdkApp#ask") {
        var mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
        var mockResponse = ResponseWrapper<ActionResponse>()
        var app = ActionsSdkApp(mockRequest, mockResponse, serializer = serializer)

        beforeEachTest {
            mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
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
                "conversationToken": "{\"state\":null,\"data\":{}}",
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
                        "intent": "actions.intent.TEXT"
                    }
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

        it("Should return the valid JSON in the response object for the success case when String text was asked w/o input prompts.") {
            app.ask("What can I help you with?")
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"state\":null,\"data\":{}}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "What can I help you with?"
                    }
                    ],
                    "noInputPrompts": [

                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.TEXT"
                    }
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

        it("Should return the valid JSON in the response object for the success case when SSML text was asked w/o input prompts.") {
            app.ask("<speak>What <break time=\"1\"/> can I help you with?</speak>")
            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"state\":null,\"data\":{}}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "ssml": "<speak>What <break time=\"1\"/> can I help you with?</speak>"
                    }
                    ],
                    "noInputPrompts": [

                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.TEXT"
                    }
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

        it("Should return the valid JSON in the response object for the advanced success case.") {
            val inputPrompt = app.buildInputPrompt(false, "Welcome to action snippets! Say a number.",
                    mutableListOf("Say any number", "Pick a number", "What is the number?"))
            app.ask(inputPrompt)
            // Validating the response object
            val expectedResponse = responseFromJson("""{
            "conversationToken": "{\"state\":null,\"data\":{}}",
            "expectUserResponse": true,
            "expectedInputs": [
            {
                "inputPrompt": {
                "initialPrompts": [
                {
                    "textToSpeech": "Welcome to action snippets! Say a number."
                }
                ],
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
            },
                "possibleIntents": [
                {
                    "intent": "actions.intent.TEXT"
                }
                ]
            }
            ]
        }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

        it("Should return the valid simple response JSON in the response object for the success case.") {
            app.ask {
                textToSpeech = "hello"
                displayText = "hi"
            }
            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"state\":null,\"data\":{}}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "richInitialPrompt": {
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
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.TEXT"
                    }
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid rich response JSON in the response object for the success case.") {
            app.ask(app.buildRichResponse()
                    .addSimpleResponse(speech = "hello", displayText = "hi")
                    .addSuggestions("Say this", "or this"))

            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"state\":null,\"data\":{}}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "richInitialPrompt": {
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
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.TEXT"
                    }
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

    }

    /**
     * Describes the behavior for ActionsSdkApp tell method.
     */
    describe("ActionsSdkApp#tell") {
        var mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
        var mockResponse = ResponseWrapper<ActionResponse>()
        var app = ActionsSdkApp(mockRequest, mockResponse, serializer = serializer)

        beforeEachTest {
            mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
            mockResponse = ResponseWrapper<ActionResponse>()
            debug("before test: ${mockResponse}")
            app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse, serializer = serializer)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid JSON in the response object for the success case.") {
            app.tell("Goodbye!")
            val expectedResponse = responseFromJson("""{
                "expectUserResponse": false,
                "finalResponse": {
                "speechResponse": {
                "textToSpeech": "Goodbye!"
            }
            }
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid simple rich response JSON in the response object for the success case.") {
            app.tell(speech = "hello", displayText = "hi")

            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "expectUserResponse": false,
                "finalResponse": {
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
            }""")
            expect(mockResponse.body)
                    .to.equal(expectedResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid rich response JSON in the response object for the success case.") {
            app.tell(app.buildRichResponse()
                    .addSimpleResponse(speech = "hello", displayText = "hi")
                    .addSuggestions("Say this", "or this"))

            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "expectUserResponse": false,
                "finalResponse": {
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
            }""")
            expect(mockResponse.body)
                    .to.equal(expectedResponse)
        }

        // Failure test, when the API returns a 400 response with the response object
        it("Should send failure response for rich response without simple response") {
            fun handler(app: ActionsSdkApp) = app.tell(app.buildRichResponse())

            val actionMap = mapOf("intent_name_not_present_in_the_body" to ::handler)

            app.handleRequest(actionMap)

            expect(mockResponse.statusCode).to.equal(400)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getRawInput method.
     */
    describe("ActionsSdkApp#getRawInput") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should get the raw user input for the success case.") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].rawInputs = mutableListOf(gson.fromJson("""
            {
                "inputType": 2,
                "query": "bye"
            }
            """, RawInput::class.java))
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            expect(app.getRawInput()).to.equal("bye")
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp askWithList method.
     */
    describe("ActionsSdkApp#askWithList") {
        var mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
        var mockResponse = ResponseWrapper<ActionResponse>()
        var app = ActionsSdkApp(mockRequest, mockResponse, serializer = serializer)

        beforeEachTest {
            mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
            mockResponse = ResponseWrapper<ActionResponse>()
            debug("before test: ${mockResponse}")
            app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse, serializer = serializer)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid list JSON in the response object for the success case.") {
            app.askWithList("Here is a list", app.buildList()
                    .addItems(
                            app.buildOptionItem("key_1", "key one"),
                            app.buildOptionItem("key_2", "key two")
                    ), mutableMapOf(
                    "optionType" to "list"))

            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"optionType\":\"list\"}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "Here is a list"
                    }
                    ],
                    "noInputPrompts": [
                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.OPTION",
                        "inputValueData": {
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
                    ]
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }

        it("Should return the an error JSON in the response when list has <2 items.") {
            app.askWithList("Here is a list", app.buildList(), mutableMapOf(
                    "optionType" to "list"))
            expect(mockResponse.statusCode).to.equal(400)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp askWithCarousel method.
     */
    describe("ActionsSdkApp#askWithCarousel") {
        var mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
        var mockResponse = ResponseWrapper<ActionResponse>()
        var app = ActionsSdkApp(mockRequest, mockResponse, serializer = serializer)

        beforeEachTest {
            mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
            mockResponse = ResponseWrapper<ActionResponse>()
            debug("before test: ${mockResponse}")
            app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse, serializer = serializer)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid carousel JSON in the response object for the success case.") {
            app.askWithCarousel("Here is a carousel", app.buildCarousel()
                    .addItems(
                            app.buildOptionItem("key_1", "key one"),
                            app.buildOptionItem("key_2", "key two")
                    ), mutableMapOf("optionType" to "carousel"))

            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"optionType\":\"carousel\"}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "Here is a carousel"
                    }
                    ],
                    "noInputPrompts": [
                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.OPTION",
                        "inputValueData": {
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
                    ]
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }

        it("Should return the an error JSON in the response when carousel has <2 items.") {
            app.askWithList("Here is a list", app.buildList(), mutableMapOf(
                    "optionType" to "list"))

            expect(mockResponse.statusCode).to.equal(400)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp askForPermissions method in v1.
     */
    describe("ActionsSdkApp#askForPermissions") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return the valid JSON in the response object for the success case.") {
            val mockRequest = RequestWrapper(headerV1, createLiveSessionActionsSdkAppBody())
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            app.askForPermissions("To get you a ride",
                    app.SUPPORTED_PERMISSIONS.NAME,
                    app.SUPPORTED_PERMISSIONS.DEVICE_PRECISE_LOCATION,
                    dialogState = mutableMapOf<String, Any?>(
                            "carType" to "big"))

            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"carType\":\"big\"}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_PERMISSION"
                    }
                    ],
                    "noInputPrompts": [
                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "assistant.intent.action.PERMISSION",
                        "inputValueSpec": {
                        "permissionValueSpec": {
                        "optContext": "To get you a ride",
                        "permissions": ["NAME", "DEVICE_PRECISE_LOCATION"]
                    }
                    }
                    }
                    ]
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }


        it("Should return the valid JSON in the response object for the success case in v2.") {
            val mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            app.askForPermissions("To get you a ride",
                    app.SUPPORTED_PERMISSIONS.NAME,
                    app.SUPPORTED_PERMISSIONS.DEVICE_PRECISE_LOCATION
                    , dialogState = mutableMapOf(
                    "carType" to "big"))
            // Validating the response object
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"carType\":\"big\"}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_PERMISSION"
                    }
                    ],
                    "noInputPrompts": [
                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.PERMISSION",
                        "inputValueData": {
                        "@type": "type.googleapis.com/google.actions.v2.PermissionValueSpec",
                        "optContext": "To get you a ride",
                        "permissions": ["NAME", "DEVICE_PRECISE_LOCATION"]
                    }
                    }
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

    }

    /**
     * Describes the behavior for ActionsSdkApp askForTransactionRequirements method.
     */
    describe("ActionsSdkApp#askForTransactionRequirements") {
        var mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
        var mockResponse = ResponseWrapper<ActionResponse>()
        var app = ActionsSdkApp(mockRequest, mockResponse, serializer = serializer)

        beforeEachTest {
            mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
            mockResponse = ResponseWrapper<ActionResponse>()
            debug("before test: ${mockResponse}")
            app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse, serializer = serializer)
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
            app.askForTransactionRequirements(transactionConfig, mutableMapOf("cartSize" to 2))
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"cartSize\":2}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_TXN_REQUIREMENTS"
                    }
                    ],
                    "noInputPrompts": [
                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.TRANSACTION_REQUIREMENTS_CHECK",
                        "inputValueData": {
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
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON transaction requirements with Action payment options") {
            val transactionConfig = gson.fromJson("""{
                deliveryAddressRequired: true,
                type: "BANK",
                displayName: "Checking-4773"
            }""", ActionPaymentTransactionConfig::class.java)
            app.askForTransactionRequirements(transactionConfig, mutableMapOf("cartSize" to 2))
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"cartSize\":2}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_TXN_REQUIREMENTS"
                    }
                    ],
                    "noInputPrompts": [
                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.TRANSACTION_REQUIREMENTS_CHECK",
                        "inputValueData": {
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
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp askForDeliveryAddress method.
     */
    describe("ActionsSdkApp#askForDeliveryAddress") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON delivery address") {
            val mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            app.askForDeliveryAddress("Just because", mutableMapOf("cartSize" to 2))
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"cartSize\":2}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_DELIVERY_ADDRESS"
                    }
                    ],
                    "noInputPrompts": [
                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.DELIVERY_ADDRESS",
                        "inputValueData": {
                        "@type": "type.googleapis.com/google.actions.v2.DeliveryAddressValueSpec",
                        "addressOptions": {
                        "reason": "Just because"
                    }
                    }
                    }
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp askForTransactionDecision method.
     */
    describe("ActionsSdkApp#askForTransactionDecision") {
        var mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
        var mockResponse = ResponseWrapper<ActionResponse>()
        var app = ActionsSdkApp(mockRequest, mockResponse, serializer = serializer)

        beforeEachTest {
            mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
            mockResponse = ResponseWrapper<ActionResponse>()
            debug("before test: ${mockResponse}")
            app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse, serializer = serializer)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON transaction decision with Google payment options") {
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
            app.askForTransactionDecision(Order(id = "order_id"), transactionConfig,
                    mutableMapOf("cartSize" to 2))
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"cartSize\":2}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_TXN_DECISION"
                    }
                    ],
                    "noInputPrompts": []
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.TRANSACTION_DECISION",
                        "inputValueData": {
                        "@type": "type.googleapis.com/google.actions.v2.TransactionDecisionValueSpec",
                        "proposedOrder": {"id": "order_id"},
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
                    ]
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
                    displayName = "Checking-4773")
            app.askForTransactionDecision(Order(id = "order_id"), transactionConfig,
                    mutableMapOf("cartSize" to 2))
            val expectedResponse = responseFromJson("""{
            "conversationToken": "{\"cartSize\":2}",
            "expectUserResponse": true,
            "expectedInputs": [
            {
                "inputPrompt": {
                "initialPrompts": [
                {
                    "textToSpeech": "PLACEHOLDER_FOR_TXN_DECISION"
                }
                ],
                "noInputPrompts": []
            },
                "possibleIntents": [
                {
                    "intent": "actions.intent.TRANSACTION_DECISION",
                    "inputValueData": {
                    "@type": "type.googleapis.com/google.actions.v2.TransactionDecisionValueSpec",
                    "proposedOrder": {"id": "order_id"},
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
                ]
            }
            ]
        }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp askForConfirmation method.
     */
    describe("ActionsSdkApp#askForConfirmation") {
        var mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
        var mockResponse = ResponseWrapper<ActionResponse>()
        var app = ActionsSdkApp(mockRequest, mockResponse, serializer = serializer)

        beforeEachTest {
            mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
            mockResponse = ResponseWrapper<ActionResponse>()
            debug("before test: ${mockResponse}")
            app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse, serializer = serializer)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON confirmation request") {
            app.askForConfirmation("You want to do that?", mutableMapOf("cartSize" to 2))
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"cartSize\":2}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_CONFIRMATION"
                    }
                    ],
                    "noInputPrompts": []
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.CONFIRMATION",
                        "inputValueData": {
                        "@type": "type.googleapis.com/google.actions.v2.ConfirmationValueSpec",
                        "dialogSpec": {
                        "requestConfirmationText": "You want to do that?"
                    }
                    }
                    }
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON confirmation request without prompt") {
            app.askForConfirmation()
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"state\":null,\"data\":{}}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_CONFIRMATION"
                    }
                    ],
                    "noInputPrompts": []
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.CONFIRMATION",
                        "inputValueData": {
                        "@type": "type.googleapis.com/google.actions.v2.ConfirmationValueSpec"
                    }
                    }
                    ]
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp askForDateTime method.
     */
    describe("ActionsSdkApp#askForDateTime") {
        var mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
        var mockResponse = ResponseWrapper<ActionResponse>()
        var app = ActionsSdkApp(mockRequest, mockResponse, serializer = serializer)

        beforeEachTest {
            mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
            mockResponse = ResponseWrapper<ActionResponse>()
            debug("before test: ${mockResponse}")
            app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse, serializer = serializer)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON datetime request") {
            app.askForDateTime("When do you want to come in?",
                    "What is the best date for you?",
                    "What time of day works best for you?", mutableMapOf("cartSize" to 2))

            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"cartSize\":2}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_DATETIME"
                    }
                    ],
                    "noInputPrompts": [
                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.DATETIME",
                        "inputValueData": {
                        "@type": "type.googleapis.com/google.actions.v2.DateTimeValueSpec",
                        "dialogSpec": {
                        "requestDatetimeText": "When do you want to come in?",
                        "requestDateText": "What is the best date for you?",
                        "requestTimeText": "What time of day works best for you?"
                    }
                    }
                    }
                    ]
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON datetime request with partial prompts") {
            app.askForDateTime("When do you want to come in?",
                    null)
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"state\":null,\"data\":{}}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_DATETIME"
                    }
                    ],
                    "noInputPrompts": [
                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.DATETIME",
                        "inputValueData": {
                        "@type": "type.googleapis.com/google.actions.v2.DateTimeValueSpec",
                        "dialogSpec": {
                        "requestDatetimeText": "When do you want to come in?"
                    }
                    }
                    }
                    ]
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON datetime request without prompts") {
            app.askForDateTime()
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"state\":null,\"data\":{}}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_DATETIME"
                    }
                    ],
                    "noInputPrompts": [
                    ]
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.DATETIME",
                        "inputValueData": {
                        "@type": "type.googleapis.com/google.actions.v2.DateTimeValueSpec"
                    }
                    }
                    ]
                }
                ]
            }""")

            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }


    /**
     * Describes the behavior for ActionsSdkApp askForSignIn method.
     */
    describe("ActionsSdkApp#askForSignIn") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid JSON sign in request") {
            val mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            app.askForSignIn(mutableMapOf("cartSize" to 2))
            val expectedResponse = responseFromJson("""{
                "conversationToken": "{\"cartSize\":2}",
                "expectUserResponse": true,
                "expectedInputs": [
                {
                    "inputPrompt": {
                    "initialPrompts": [
                    {
                        "textToSpeech": "PLACEHOLDER_FOR_SIGN_IN"
                    }
                    ],
                    "noInputPrompts": []
                },
                    "possibleIntents": [
                    {
                        "intent": "actions.intent.SIGN_IN",
                        "inputValueData": {}
                    }
                    ]
                }
                ]
            }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getUser method.
     */
    describe("ActionsSdkApp#getUser") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request info.") {
            val mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            // Test new and old API
            //TODO v1 api
//            expect(app.getUser()?.userId).to.equal(fakeUserId)
            expect(app.getUser()?.userId).to.equal(fakeUserId)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getUserName method.
     */
    describe("ActionsSdkApp#getUserName") {

        var mockRequest = RequestWrapper(headerV2, createLiveSessionActionsSdkAppBody())
        var mockResponse = ResponseWrapper<ActionResponse>()
        var app = ActionsSdkApp(mockRequest, mockResponse, serializer = serializer)
        var body = ActionRequest()

        fun initMockApp() {
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper<ActionResponse>()
            app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
        }

        it("Should validate assistant request user with sample user information.") {
            body = createLiveSessionActionsSdkAppBody()
            body.user?.profile = gson.fromJson("""{
                "displayName": "John Smith",
                "givenName": "John",
                "familyName": "Smith"
            }""", Profile::class.java)
            initMockApp()
            expect(app.getUserName()?.displayName).to.equal("John Smith")
            expect(app.getUserName()?.givenName).to.equal("John")
            expect(app.getUserName()?.familyName).to.equal("Smith")
        }

        it("Should validate assistant request with undefined user information.") {
            body = createLiveSessionActionsSdkAppBody()
            // Test the false case
            body.user?.profile = null
            initMockApp()
            expect(app.getUserName()).to.equal(null)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getUserLocale method.
     */
    describe("ActionsSdkApp#getUserLocale") {
        var mockRequest: RequestWrapper<ActionRequest>
        var mockResponse: ResponseWrapper<ActionResponse>
        var app: ActionsSdkApp? = null
        var body = ActionRequest()
        fun initMockApp() {
            mockRequest = RequestWrapper(headerV2, body)
            mockResponse = ResponseWrapper<ActionResponse>()
            app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
        }
        it("Should validate assistant request user with locale.") {
            body = createLiveSessionActionsSdkAppBody()
            body.user = User()
            body.user?.locale = "en-US"
            initMockApp()
            expect(app?.getUserLocale()).to.equal("en-US")
        }

        it("Should return null for missing locale.") {
            body = createLiveSessionActionsSdkAppBody()
            // Test the false case
            body.user = User()
            body.user?.locale = null
            initMockApp()
            expect(app?.getUserLocale()).to.equal(null)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getTransactionRequirementsResult method.
     */
    describe("ActionsSdkApp#getTransactionRequirementsResult") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request user.") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf(gson.fromJson("""
            {
                "extension": {
                "canTransact": true,
                "@type": "type.googleapis.com/google.actions.v2.TransactionRequirementsCheckResult",
                "resultType": "OK"
            },
                "name": "TRANSACTION_REQUIREMENTS_CHECK_RESULT"
            }
            """, Arguments::class.java))

            val mockRequest = RequestWrapper<ActionRequest>(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            expect(app.getTransactionRequirementsResult()?.value).to.equal("OK")
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getDeliveryAddress method.
     */
    describe("ActionsSdkApp#getDeliveryAddress") {
        var body = ActionRequest()

        beforeEachTest {
            body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf(gson.fromJson("""{
                "extension": {
                "userDecision": "ACCEPTED",
                "@type": "type.googleapis.com/google.actions.v2.TransactionDecisionValue",
                "location": {
                "zipCode": "94043",
                "postalAddress": {
                "regionCode": "US",
                "recipients": [
                "Jane Smith"
                ],
                "postalCode": "94043",
                "locality": "Mountain View",
                "addressLines": [
                "1600 Amphitheatre Parkway"
                ],
                "administrativeArea": "CA"
            },
                "phoneNumber": "+1 415-555-1234",
                "city": "Mountain View"
            }
            },
                "name": "TRANSACTION_DECISION_VALUE"
        }""", Arguments::class.java))
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request delivery address") {
            val mockRequest = RequestWrapper<ActionRequest>(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            expect(app.getDeliveryAddress()).to.equal(gson.fromJson("""{
                zipCode: "94043",
                postalAddress: {
                regionCode: "US",
                recipients: [
                "Jane Smith"
                ],
                postalCode: "94043",
                locality: "Mountain View",
                addressLines: [
                "1600 Amphitheatre Parkway"
                ],
                administrativeArea: "CA"
            },
                phoneNumber: "+1 415-555-1234",
                city: "Mountain View"
            }""", Location::class.java))
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request delivery address for txn decision") {
            body.inputs!![0].arguments!![0].name = "DELIVERY_ADDRESS_VALUE"
            val mockRequest = RequestWrapper<ActionRequest>(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            expect(app.getDeliveryAddress()).to.equal(gson.fromJson("""{
                zipCode: "94043",
                postalAddress: {
                regionCode: "US",
                recipients: [
                "Jane Smith"
                ],
                postalCode: "94043",
                locality: "Mountain View",
                addressLines: [
                "1600 Amphitheatre Parkway"
                ],
                administrativeArea: "CA"
            },
                phoneNumber: "+1 415-555-1234",
                city: "Mountain View"
            }""", Location::class.java))
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return null when user rejects") {
            body.inputs!![0].arguments!![0].extension?.userDecision = "REJECTED"
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            expect(app.getDeliveryAddress()).to.equal(null)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getTransactionDecision method.
     */
    describe("ActionsSdkApp#getTransactionDecision") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request delivery address") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf(gson.fromJson("""{
                "extension": {
                "userDecision": "ORDER_ACCEPTED",
                "checkResult": {
                "resultType": "OK",
                "order": {
                "finalOrder": { "fakeOrder": "fake_order" },
                "googleOrderId": "goog_123",
                "actionOrderId": "action_123",
                "orderDate": {
                "seconds": 40,
                "nanos": 880000000
            },
                "paymentInfo": { "fakePayment": "fake_payment" },
                "customerInfo": {
                "email": "username@example.com"
            }
            }
            }
            },
                "name": "TRANSACTION_DECISION_VALUE"
            }""", Arguments::class.java))

            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer
            )
            expect(app.getTransactionDecision()).to.equal(gson.fromJson("""{
                "userDecision": "ORDER_ACCEPTED",
                "checkResult": {
                "resultType": "OK",
                "order": {
                "finalOrder": { "fakeOrder": "fake_order" },
                "googleOrderId": "goog_123",
                "actionOrderId": "action_123",
                "orderDate": {
                "seconds": 40,
                "nanos": 880000000
            },
                "paymentInfo": { "fakePayment": "fake_payment" },
                "customerInfo": {
                "email": "username@example.com"
            }
            }
}
}
            """, TransactionRequirementsCheckResult::class.java))
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getUserConfirmation method.
     */
    describe("ActionsSdkApp#getUserConfirmation") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant positive confirmation decision") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf(gson.fromJson("""
            {
                "name": "CONFIRMATION",
                "boolValue": true
            }""", Arguments::class.java))

            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            expect(app.getUserConfirmation()).to.equal(true)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant negative confirmation decision") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf(gson.fromJson("""
            {
                "name": "CONFIRMATION",
                "boolValue": false
            }""", Arguments::class.java))

            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()

            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            expect(app.getUserConfirmation()).to.equal(false)
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant missing confirmation decision") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf()
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer
            )
            expect(app.getUserConfirmation()).to.equal(null)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getDateTime method.
     */
    describe("ActionsSdkApp#getDateTime") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant date time info") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf(gson.fromJson("""
            {
                "datetimeValue": {
                "date": {
                "month": 5,
                "year": 2017,
                "day": 26
            },
                "time": {
                "hours": 9
            }
            },
                "name": "DATETIME"
            }
            """, Arguments::class.java))
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            expect(app.getDateTime()).to.equal(gson.fromJson("""{
            date: {
            month: 5,
            year: 2017,
            day: 26
        },
            time: {
            hours: 9
        }
        }""", DateTimeValue::class.java))
        }

        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant missing date time info") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf()
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            expect(app.getDateTime()).to.equal(null)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getSignInStatus method.
     */
    describe("ActionsSdkApp#getSignInStatus") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant sign in status") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf(gson.fromJson("""
            {
                "name": "SIGN_IN",
                "extension": {
                "@type": "type.googleapis.com/google.actions.v2.SignInValue",
                "status": "foo_status"
            }
            }""", Arguments::class.java))

            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            expect(app.getSignInStatus()).to.equal("foo_status")
        }

// Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant missing sign in status") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf()
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            expect(app.getSignInStatus()).to.equal(null)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getDeviceLocation method.
     */
    describe("ActionsSdkApp#getDeviceLocation") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request for device location when location is provided.") {
            val body = createLiveSessionActionsSdkAppBody()
            body.device = gson.fromJson("""{
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
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            expect(app.getDeviceLocation()?.coordinates).to.equal(gson.fromJson("""{
            latitude: 37.3861,
            longitude: 122.0839
        }""", Coordinates::class.java))
            expect(app.getDeviceLocation()?.address)
                    .to.equal("123 Main St, Anytown, CA 12345, United States")
            expect(app.getDeviceLocation()?.zipCode).to.equal("12345")
            expect(app.getDeviceLocation()?.city).to.equal("Anytown")
        }

        it("Should validate assistant request for device location when location is undefined.") {
            // Test the false case
            val body = createLiveSessionActionsSdkAppBody()
            body.device = null
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            expect(app.getDeviceLocation()).to.equal(null)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp isPermissionGranted method.
     */
    describe("ActionsSdkApp#isPermissionGranted") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate when permissions were granted.") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf(gson.fromJson("""
            {
                "name": "permission_granted",
                "text_value": "true"
            }""", Arguments::class.java))


            val mockRequest = RequestWrapper(headerV1, body)
            val mockResponse = ResponseWrapper<ActionResponse>()

            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            expect(app.isPermissionGranted()).to.equal(true)
        }

        it("Should validate when permissions were not granted.") {
            // Test the false case
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf(gson.fromJson("""
        {
            "name": "permission_granted",
            "text_value": "false"
        }""", Arguments::class.java))
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            expect(app.isPermissionGranted()).to.equal(false)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp isInSandbox method.
     */
    describe("ActionsSdkApp#isInSandbox") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate when app is in sandbox mode.") {
            val body = createLiveSessionActionsSdkAppBody()
            body.isInSandbox = true
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer
            )
            expect(app.isInSandbox()).to.equal(true)
        }
        it("Should validate when app is not in sandbox mode.") {
            // Test the false case
            val body = createLiveSessionActionsSdkAppBody()
            body.isInSandbox = false
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            expect(app.isInSandbox()).to.equal(false)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp hasSurfaceCapability method.
     */
    describe("ActionsSdkApp#hasSurfaceCapability") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return true for a valid capability from incoming JSON for the success case.") {
            val body = createLiveSessionActionsSdkAppBody()
            body.surface = gson.fromJson("""{
            "capabilities": [
            {
                "name": "actions.capability.AUDIO_OUTPUT"
            },
            {
                "name": "actions.capability.SCREEN_OUTPUT"
            }
            ]
        }""", Surface::class.java)
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()

            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            val hasScreenOutput =
                    app.hasSurfaceCapability(app.SURFACE_CAPABILITIES.SCREEN_OUTPUT)
            val hasMagicPowers =
                    app.hasSurfaceCapability("MAGIC_POWERS")
            expect(hasScreenOutput).to.equal(true)
            expect(hasMagicPowers).to.equal(false)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getSurfaceCapabilities method.
     */
    describe("ActionsSdkApp#getSurfaceCapabilities") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid list of capabilities from incoming JSON for the success case.") {
            val body = createLiveSessionActionsSdkAppBody()
            body.surface = gson.fromJson("""{
                "capabilities": [
                {
                    "name": "actions.capability.AUDIO_OUTPUT"
                },
                {
                    "name": "actions.capability.SCREEN_OUTPUT"
                }
                ]
            }""", Surface::class.java)

            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            val capabilities = app.getSurfaceCapabilities()
            expect(capabilities).to.equal(mutableListOf(
                    app.SURFACE_CAPABILITIES.AUDIO_OUTPUT,
                    app.SURFACE_CAPABILITIES.SCREEN_OUTPUT
            ))
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getInputType method.
     */
    describe("ActionsSdkApp#getInputType") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should return valid input type from incoming JSON for the success case.") {
            val KEYBOARD = "KEYBOARD"
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].rawInputs = mutableListOf(gson.fromJson("""
            {
                "inputType": $KEYBOARD,
                "query": "talk to action snippets"
            }""", RawInput::class.java))

            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()

            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            val inputType = app.getInputType()
            expect(inputType).to.equal(app.INPUT_TYPES.KEYBOARD)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getApiVersion method.
     */
    describe("ActionsSdkApp#getApiVersion") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant request info.") {
            val headers = mapOf(
                    "Content-Type" to "application/json",
                    "Google-Assistant-API-Version" to "v1"
            )
            val mockRequest = RequestWrapper(headers, createLiveSessionActionsSdkAppBody())
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            expect(app.getApiVersion()).to.equal("v1")
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getDialogState method.
     */
    describe("ActionsSdkApp#getDialogState") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant dialog state info.") {
            val body = createLiveSessionActionsSdkAppBody()
            body.conversation?.conversationToken = gson.toJson(mutableMapOf("started" to true))
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            val dialogState = mutableMapOf("started" to true)
            expect(dialogState).to.equal(app.getDialogState() as Map<String, Boolean>?)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getActionVersionLabel method.
     */
    describe("ActionsSdkApp#getActionVersionLabel") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant action version label info.") {
            var headers = headerV1.toMutableMap()
            headers["Agent-Version-Label"] = "1.0.0"
            val mockRequest = RequestWrapper(headers, createLiveSessionActionsSdkAppBody())
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            expect(app.getActionVersionLabel()).to.equal("1.0.0")
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getConversationId method.
     */
    describe("ActionsSdkApp#getConversationId") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant conversation ID.") {
            val body = createLiveSessionActionsSdkAppBody()
            body.conversation?.conversationId = fakeConversationId
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            expect(app.getConversationId()).to.equal(fakeConversationId)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getArgument method.
     */
    describe("ActionsSdkApp#getArgument") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should validate assistant intent.") {
            val body = createLiveSessionActionsSdkAppBody()
            val type = object : TypeToken<java.util.List<Arguments>>() {}.getType()

            body.inputs!![0].arguments = gson.fromJson("""[
            {
                "name": "number",
                "rawText": "45",
                "textValue": "45"
            },
            {
                "name": "otherValue",
                "raw_text": "45",
                "otherValue": {
                "key": "value"
            }
            }]
            """, type)
            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)
            expect(app.getArgument("number")).to.equal("45")
            var tmp = app.getArgument("otherValue")
            var tmp2 = gson.fromJson("""{
            "name": "otherValue",
            "rawText": "45",
            "otherValue": {
            "key": "value"
        }
        }""", LinkedTreeMap::class.java)

            //below is for custom payloads in arguments.  Unclear is this is needed, and for now is not supported.
            /*
            expect(app.getArgument("otherValue")).to.equal(gson.fromJson("""{
            "name": "otherValue",
            "rawText": "45",
            "otherValue": {
            "key": "value"
        }
        }""", LinkedTreeMap::class.java))
        */
            app.tell("You said " + app.getArgument("number"))
            val expectedResponse = responseFromJson("""{
            "expectUserResponse": false,
            "finalResponse": {
            "speechResponse": {
            "textToSpeech": "You said 45"
        }
        }
        }""")
            expect(mockResponse.body).to.equal(expectedResponse)
        }
    }

    /**
     * Describes the behavior for ActionsSdkApp getSelectedOption method.
     */
    describe("ActionsSdkApp#getSelectedOption") {
        // Success case test, when the API returns a valid 200 response with the response object
        it("Should get the selected option when given in Dialogflow context.") {
            val body = createLiveSessionActionsSdkAppBody()
            body.inputs!![0].arguments = mutableListOf(gson.fromJson("""
        {
            "name": "OPTION",
            "text_value": "first_item"
        }""", Arguments::class.java))

            val mockRequest = RequestWrapper(headerV2, body)
            val mockResponse = ResponseWrapper<ActionResponse>()
            val app = ActionsSdkApp(
                    request = mockRequest,
                    response = mockResponse,
                    serializer = serializer)

            expect(app.getSelectedOption()).to.equal("first_item")
        }


        /**
         * Describes the behavior for ActionsSdkApp tell with SSML method.
         */
        describe("ActionsSdkApp#tell") {
            // Success case test, when the API returns a valid 200 response with the response object
            it("Should validate assistant tell SSML.") {
                val body = createLiveSessionActionsSdkAppBody()
                body.inputs!![0].rawInputs = mutableListOf(gson.fromJson("""
        {
            "input_type": 2,
            "query": "45"
        }""", RawInput::class.java))

                val mockRequest = RequestWrapper(headerV2, body)
                val mockResponse = ResponseWrapper<ActionResponse>()
                val app = ActionsSdkApp(
                        request = mockRequest,
                        response = mockResponse,
                        serializer = serializer)

                app.tell("<speak>You said <break time=\"2\"/>" + app.getRawInput() + "</speak>")
                val expectedResponse = responseFromJson("""{
        "expectUserResponse": false,
        "finalResponse": {
        "speechResponse": {
        "ssml": "<speak>You said <break time=\"2\"/>45</speak>"
    }
    }
    }""")
                expect(mockResponse.body).to.equal(expectedResponse)
            }
        }
    }
})
