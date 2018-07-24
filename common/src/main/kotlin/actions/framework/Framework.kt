package actions.framework

import actions.service.actionssdk.ActionsSdkIntentHandler4
import actions.service.dialogflow.DialogflowIntentHandler4

//import kotlinx.coroutines.experimental.Deferred


abstract class Frameworks<TUserStorage>: MutableList<Framework<TUserStorage>> by mutableListOf() {
}

interface OmniHandler {
    fun handle(vararg args: Any): Any
}

interface Framework<TUserStorage> {
    fun handle(base: StandardHandler<TUserStorage>): OmniHandler

    // tslint:disable-next-line:no-any detect if it is the correct framework from any parameter type
    fun check(vararg args: Any): Boolean
}

//typealias OmniHandler = StandardHandler/*, ExpressHandler, LambdaHandler*/ //{
    // tslint:disable-next-line:no-any allow any inputs and outputs depending on framework
//    (...args: any[]): any
//}


///** @hidden */
//val builtin: BuiltinFrameworks = {
//    express,
//    lambda,
//}

data class StandardResponse(
    var status: Int? = null,
    var body: JsonObject? = null,
    var headers: Headers? = null)

typealias Headers = MutableMap<String, MutableList<String>>
//interface Headers {
//    /** @public */
//    [header: string]: string | string[] | undefined
//}

//typealias StandardHandler =  (body: JsonObject, headers: Headers) -> StandardResponse //TODO Promise or deferred

interface StandardHandler<TUserStorage> {
    fun handle(body: Any, headers: Headers,
               overrideHandler: DialogflowIntentHandler4<TUserStorage>? = null,
               aogOverrideHandler: ActionsSdkIntentHandler4<TUserStorage>? = null): StandardResponse //TODO Promise or deferred
}

typealias JsonObject = Any
