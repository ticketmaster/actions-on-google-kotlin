package actions.expected

import actions.framework.Framework
import actions.framework.Frameworks
import actions.framework.JsonObject
import actions.framework.StandardHandler
import actions.service.actionssdk.api.GoogleActionsV2AppRequest
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

actual class BuiltinFrameworks<THandler> : Frameworks<THandler>() {
   init {
       //add frameworks
   }
}

class ServletFramework<THandler>(val request: HttpServletRequest,
                                 val response: HttpServletResponse,
                                 val s): Framework<THandler> {
   override fun handle(base: StandardHandler): THandler {
      base.handle(body = ,
              headers = )
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
   }

   override fun check(vararg args: Any): Boolean = (args.size > 1 && args.first() is HttpServletRequest )


   fun requestToActionsSdkRequest(request: GoogleActionsV2AppRequest): JsonObject {

   }
}