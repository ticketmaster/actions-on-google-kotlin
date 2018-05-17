package actions.framework

//import kotlinx.coroutines.experimental.Deferred


abstract class Frameworks<THandler>: MutableMap<String, Framework<THandler>> by mutableMapOf() {
}

interface Framework<THandler> {
    fun handle(base: StandardHandler): THandler

    // tslint:disable-next-line:no-any detect if it is the correct framework from any parameter type
    fun check(vararg args: Any): Boolean
}

interface OmniHandler: StandardHandler/*, ExpressHandler, LambdaHandler*/ //{

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

typealias Headers = Map<String, List<String>>
//interface Headers {
//    /** @public */
//    [header: string]: string | string[] | undefined
//}

//typealias StandardHandler =  (body: JsonObject, headers: Headers) -> StandardResponse //TODO Promise or deferred

interface StandardHandler {
    fun handle(body: JsonObject, headers: Headers): StandardResponse //TODO Promise or deferred
}

typealias JsonObject = Any
