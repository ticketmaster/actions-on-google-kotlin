package jvm

import actions.expected.ServletFramework
import actions.expected.logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/aog")
class SampleWebhook : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.writer.println("Working")
    }

    var hasAddedFramework = false

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        logger.info("here")
        if (!hasAddedFramework) {
            app.frameworks.add(ServletFramework<UserStorage>())
            hasAddedFramework = true
        }

        initActionsApp()

        app(req, resp)
    }


}


