package jvm

import actions.ActionsSdkTest
import actions.expected.BuiltinFrameworks
import actions.expected.ServletFramework
import actions.expected.framework.TestHttpServletRequest
import actions.expected.framework.TestHttpServletResponse
import actions.expected.gson
import actions.expected.logger
import actions.service.actionssdk.actionssdk
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import actions.service.actionssdk.api.GoogleActionsV2UiElementsOpenUrlAction
import actions.service.actionssdk.api.GoogleActionsV2User
import actions.service.actionssdk.conversation.Conversation
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.SurfaceCapability
import actions.service.actionssdk.conversation.response.BasicCard
import actions.service.actionssdk.conversation.response.Image
import actions.service.actionssdk.conversation.response.SimpleResponse
import actions.service.actionssdk.conversation.response.Suggestions
import actions.service.actionssdk.conversation.response.card.BasicCardOptions
import actions.service.actionssdk.conversation.response.card.Button
import actions.service.actionssdk.conversation.response.card.ButtonOptions
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

data class UserStorage(var name: String? = null)
data class ConversationData(var lastResponse: String? = null)

// Constants for image URLs
const val IMG_URL_AOG = "https://developers.google.com/actions/images/badges" +
        "/XPM_BADGING_GoogleAssistant_VER.png"
const val IMG_URL_GOOGLE_ALLO = "https://allo.google.com/images/allo-logo.png"
const val IMG_URL_GOOGLE_HOME = "https://lh3.googleusercontent.com" +
        "/Nu3a6F80WfixUqf_ec_vgXy_c0-0r4VLJRXjVFF_X_CIilEu8B9fT35qyTEj_PEsKw"
const val IMG_URL_GOOGLE_PIXEL = "https://storage.googleapis.com/madebygoog/v1" +
        "/Pixel/Pixel_ColorPicker/Pixel_Device_Angled_Black-720w.png"

val intentSuggestions = arrayOf(
        "Basic Card",
        "Browse Carousel",
        "Carousel",
        "List",
        "Media",
        "Suggestions")

fun <T> Conversation<T>.hasScreen(): Boolean = surface.capabilities?.has(SurfaceCapability.ACTIONS_CAPABILITY_SCREEN_OUTPUT)
        ?: false

fun <T> Conversation<T>.hasAudioPlayback(): Boolean = surface.capabilities?.has(SurfaceCapability.ACTIONS_CAPABILITY_AUDIO_OUTPUT)
        ?: false

fun servlet() {
    val app = actionssdk<ConversationData, UserStorage>()
    app.frameworks.add(ServletFramework<ConversationData, UserStorage>())

    app.intent("actions.intent.MAIN", "actions.intent.TEXT") { conv ->
                conv.data = ConversationData(lastResponse = "This was the last response")
        conv.user.storage = UserStorage(name = "fred")
        conv.ask("Can you hear me?")

    }

    app.intent("actions.intent.TEXT") { conv, input ->
        val rawInput = (input as String).toLowerCase()
        logger.info("USER SAID $rawInput")
        when (rawInput) {
            "basic card" -> basicCard(conv)
            else -> normalAsk(conv)
        }

    }

    val req: HttpServletRequest = getMockHttpReq(testWelcomeRequestStr)
    val resp: HttpServletResponse = getMockHttpResp()
    app(req, resp)
}

fun <TUserStorage> normalAsk(conv: Conversation<TUserStorage>) {
    conv.ask("Ask me to show you a list, carousel, or basic card.")
}

fun <TUserStorage> basicCard(conv: Conversation<TUserStorage>) {
    if (!conv.hasScreen()) {
        conv.ask("Sorry, try this on a screen device of select the phone surface in the simulator")
        return
    }
    conv.ask("This is the first simple response for a basic card.")
    conv.ask(Suggestions(*intentSuggestions))
    // Create a basic card

    conv.ask(BasicCard({
        text = """This is a basic card.  Text in a basic card can include "quotes" and
                most other unicode characters including emoji 📱.  Basic cards also support
                some markdown formatting like *emphasis* or _italics_, **strong** or
        __bold__, and ***bold itallic*** or ___strong emphasis___ as well as other
        things like line  \nbreaks""" // Note the two spaces before "\n" required for
        // a line break to be rendered in the card.
        subtitle = "This is a subtitle"
        title = "Title: this is a title"
        buttons = mutableListOf(Button({
            title = "This is a button"
            url = "https://assistant.google.com/"
        }))
        image = Image({
            url = IMG_URL_AOG
            alt = "Image alternate text"
        })
    }))
    conv.ask(SimpleResponse({
        speech = "This is the second simple response"
        text = "This is the second simple response"
    }))
}

fun getMockHttpReq(body: String): HttpServletRequest {
    val headers = mutableMapOf("Content-Type" to "json/application")
    val iterator = headers.keys.iterator()
    val headerNames = object : Enumeration<String> {
        override fun hasMoreElements(): Boolean = iterator.hasNext()
        override fun nextElement(): String = iterator.next()
    }

    val req = mock(HttpServletRequest::class.java)
    `when`(req.headerNames).thenReturn(headerNames)

    `when`(req.inputStream).thenReturn(DelegatingServletInputStream(ByteArrayInputStream(body.toByteArray())))

    return req
}

fun getMockHttpResp(): HttpServletResponse {
    val resp = mock(HttpServletResponse::class.java)
    `when`(resp.writer).thenReturn(PrintWriter(StringWriter()))

    return resp
}

fun testBody(): GoogleActionsV2AppRequest {
    val user = GoogleActionsV2User(userId = "test1234")
    val req = GoogleActionsV2AppRequest()
    return req
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
