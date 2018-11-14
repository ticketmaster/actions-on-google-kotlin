package jvm

import actions.AogMockFactory
import actions.MockResponses
import actions.expected.ServletFramework
import actions.expected.logger
import actions.service.actionssdk.actionssdk
import actions.service.actionssdk.api.*
import actions.service.actionssdk.conversation.Conversation
import actions.service.actionssdk.conversation.SurfaceCapability
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

data class UserStorage(var name: String? = null)

fun <T> Conversation<T>.hasScreen(): Boolean = surface.capabilities?.has(SurfaceCapability.ACTIONS_CAPABILITY_SCREEN_OUTPUT)
        ?: false

fun <T> Conversation<T>.hasAudioPlayback(): Boolean = surface.capabilities?.has(SurfaceCapability.ACTIONS_CAPABILITY_AUDIO_OUTPUT)
        ?: false

fun servlet() {
    val app = actionssdk<UserStorage>() {}
    app.frameworks.add(ServletFramework<UserStorage>())

    app.intent("actions.intent.MAIN", "actions.intent.TEXT") { conv ->
        conv.data["lastResponse"] = "This was the last response"
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

    AogMockFactory.mockWelcome()
    val req: HttpServletRequest = getMockHttpReq(MockResponses.basicCard)
    val resp: HttpServletResponse = getMockHttpResp()
    app(req, resp)
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


