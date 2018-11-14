package jvm

import actions.expected.ServletFramework
import actions.expected.logger
import actions.service.dialogflow.dialogflow
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet("/df/transactions")
class DfTransactionsWebhook : HttpServlet() {

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.writer.println("Working")
    }

    private val dfApp = dialogflow<UserStorage, MyConversation, MyArgument>({ debug = true })

    var hasAddedFramework = false

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        logger.info("here")
        if (!hasAddedFramework) {
            dfApp.frameworks.add(ServletFramework<UserStorage>())
            hasAddedFramework = true
        }

        initTransactionSample(dfApp)

        dfApp(req, resp)
    }


}


