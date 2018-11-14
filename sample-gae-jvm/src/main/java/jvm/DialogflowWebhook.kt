package jvm

import actions.expected.ServletFramework
import actions.expected.logger
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/df")
class DialogflowWebhook : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.writer.println("Working")
    }

    var hasAddedFramework = false

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        logger.info("here")
        if (!hasAddedFramework) {
            dfApp.frameworks.add(ServletFramework())
            hasAddedFramework = true
        }

        initDf()

        dfApp(req, resp)
    }


}


