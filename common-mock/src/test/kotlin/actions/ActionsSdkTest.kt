package actions

import actions.expected.Serializer
import actions.expected.deserialize
import actions.framework.Headers
import actions.service.actionssdk.actionssdk
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test setup for Tests.
 * Other solutions - 1) use official javascipt tests against the javascript version of this lib
 *                   2) Use Spek library once v2 & multiplatform support lands and test common module
 */
class ActionsSdkTest {
    data class TestConvData(var ct: Int? = null)
    data class TestUserStorage(var name: String? = null)

    @Test
    fun returnEmptyConvData() {
        val app = actionssdk<TestUserStorage>({})
        app.intent("test") { conv ->
            conv.data["ct"] = 5
            conv.ask("This is a test")
        }

        val result = app(deserialize<GoogleActionsV2AppRequest>(testWelcomeRequestStr)!!)
    }
}


val testWelcomeRequestStr = """{
"user":{
"userId":"APhe68EmFG8L689xcinHdNbpSadP",
"locale":"en-US",
"lastSeen":"2018-05-22T01:38:28Z",
"userStorage":"{\"data\":{}}"
},
"conversation":{
"conversationId":"1526953174329",
"type":"ACTIVE",
"conversationToken":"{\"data\":{}}"
},
"inputs":[
{
"intent":"actions.intent.TEXT",
"rawInputs":[
{
"inputType":"TOUCH",
"query":"Basic Card"
}
],
"arguments":[
{
"name":"text",
"rawText":"Basic Card",
"textValue":"Basic Card"
}
]
}
],
"surface":{
"capabilities":[
{
"name":"actions.capability.MEDIA_RESPONSE_AUDIO"
},
{
"name":"actions.capability.WEB_BROWSER"
},
{
"name":"actions.capability.AUDIO_OUTPUT"
},
{
"name":"actions.capability.SCREEN_OUTPUT"
}
]
},
"isInSandbox":true,
"availableSurfaces":[
{
"capabilities":[
{
"name":"actions.capability.WEB_BROWSER"
},
{
"name":"actions.capability.AUDIO_OUTPUT"
},
{
"name":"actions.capability.SCREEN_OUTPUT"
}
]
}
]
}"""