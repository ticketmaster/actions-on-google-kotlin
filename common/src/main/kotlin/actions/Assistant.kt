package actions

import actions.framework.*
import actions.expected.BuiltinFrameworks
import actions.expected.Serializer
import actions.expected.log
import actions.service.actionssdk.ActionsSdk
import actions.service.actionssdk.ActionsSdkIntentHandler4
import actions.service.actionssdk.conversation.Conversation
import actions.service.dialogflow.DialogflowIntentHandler4


abstract class AppHandler<THandler>: BaseApp<THandler>()

interface AppOptions {
    var debug: Boolean?
}

abstract class ServiceBaseApp<TUserStorage> {
    operator fun invoke(vararg args: Any) {
        omni?.handle(*args)
    }

    var omni: OmniHandler? = null

    abstract var handler: StandardHandler<TUserStorage>
}

//interface Plugin<PluginApp<TService, TApp>, PluginResult<TService, TApp, TPlugin>> {
//    <TApp>(app: AppHandler & TService & TApp): (AppHandler & TService & TApp & TPlugin) | void
//}

data class SerivcePlugin<TService, TPlugin>(val service: TService,
                                            val pluging: TPlugin)

data class BaseAppPlugin<TPlugin, TUserStorage>(val baseApp: BaseApp<TUserStorage>,
                         val pluging: TPlugin)


typealias Plugin<PluginApp, PluginResult> = ((PluginApp) -> PluginResult)

data class PluginApp<TService, TApp, TUserStorage>(val app: AppHandler<TUserStorage>,
                                     val service: TService,
                                     val tApp: TApp)

data class PluginResult<TService, TApp, TPlugin, TUserStorage>(val app: AppHandler<TUserStorage>,
                                                 val service: TService,
                                                 val tApp: TApp,
                                                 val plugin: TPlugin)

abstract class BaseApp<TUserStorage>: ServiceBaseApp<TUserStorage>() {
    abstract var frameworks: BuiltinFrameworks<TUserStorage>


    abstract fun <TService, TPlugin> use(plugin: Plugin<TService, TPlugin>): BaseAppPlugin<TPlugin, TUserStorage>

    abstract var debug: Boolean
}

fun <TUserStorage>create(options: AppOptions? = null): BaseApp<TUserStorage> {
//    BaseApp(
//            frameworks: Object. assign ({}, builtin),
//    handler: () => Promise.reject(new Error('StandardHandler not set')),
//    use(plugin) {
//        return plugin(this) || this
//    },
//    debug:!!(options && options.debug))
    return object: BaseApp<TUserStorage>() {
        override var handler: StandardHandler<TUserStorage>
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            set(value) {}
        override var frameworks: BuiltinFrameworks<TUserStorage>
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            set(value) {}

        override fun <TService, TPlugin> use(plugin: Plugin<TService, TPlugin>): BaseAppPlugin<TPlugin, TUserStorage> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override var debug: Boolean
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            set(value) {}

    }
}

//equivalent of (AppHandler & TService) mixin
data class AttachedAppHandlerService<TService, TUserStorage>(val handler: AppHandler<TUserStorage>,
                           val service: TService)

data class AttachedBaseAppService<TService, TUserStorage>(val baseApp: BaseApp<TUserStorage>,
                           val service: TService)

data class AttachResult<TService, TUserStorage>(
                        val baseApp: BaseApp<TUserStorage>,
                        val handler: StandardHandler<TUserStorage>,
                        val service: TService)

abstract class AppResult<TUserStorage>: BaseApp<TUserStorage>(), StandardHandler<TUserStorage> {

}

fun <TService: ServiceBaseApp<TUserStorage>, TUserStorage> attach(
        service: ActionsSdk<TUserStorage>,
        options: AppOptions? = null): AppResult<TUserStorage> {

    val baseApp = create<TUserStorage>(options)
    val omni = object: OmniHandler {
        override fun handle(vararg args: Any): Any {
            for (framework in baseApp.frameworks) {
                if(framework.check(args)) {
                    return framework.handle(baseApp.handler).handle(args)
                }
            }
            return baseApp.handler.handle(args[0] as Conversation<TUserStorage>, args[1] as Headers)
        }
    }

    var handler = baseApp.handler
    val standard = object: StandardHandler<TUserStorage> {
        override fun handle(body: Any, headers: Headers, overrideHandler: DialogflowIntentHandler4<TUserStorage>?, aogOverrideHandler: ActionsSdkIntentHandler4<TUserStorage>?): StandardResponse {
            log("Request", Serializer.serialize(body))
            log("Headers", Serializer.serialize(headers))
            val response = /* await */ handler.handle(body, headers)
            response.headers?.get("content-type")?.add("application/json; charset=utf-8")
            log("Response", Serializer.serialize(response))
            return response
        }
    }
    baseApp.omni = omni
    baseApp.handler = standard

//    var appResult = object: OmniHandler by omni, actions.BaseApp by baseApp, actions.framework.StandardHandler by standardHandler, actions.ServiceBaseApp by service {
//
//    }

//    var attachedResult = AttachResult(
//            baseApp = baseApp,
//            service = service,
//            omni = omni,
//            handler = standardHandler)

    return object: AppResult<TUserStorage>() {
        override var handler: StandardHandler<TUserStorage>
            get() = standard
            set(value) {handler = value}

        override fun <TService, TPlugin> use(plugin: Plugin<TService, TPlugin>): BaseAppPlugin<TPlugin, TUserStorage> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun handle(body: Any, headers: Headers, overrideHandler: DialogflowIntentHandler4<TUserStorage>?, aogOverrideHandler: ActionsSdkIntentHandler4<TUserStorage>?): StandardResponse = standard.handle(body, headers)

        override var frameworks: BuiltinFrameworks<TUserStorage> = baseApp.frameworks

        override var debug: Boolean = baseApp.debug
    }
}
