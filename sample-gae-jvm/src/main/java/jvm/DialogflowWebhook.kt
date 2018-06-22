package jvm

import actions.expected.ServletFramework
import actions.expected.logger
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class DialogflowWebhook : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.writer.println("Working")
    }

    var hasAddedFramework = false

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        logger.info("here")
        if (!hasAddedFramework) {
            app.frameworks.add(ServletFramework<ConversationData, UserStorage>())
            hasAddedFramework = true
        }

        toBeMoved()

        app(req, resp)
    }


}


