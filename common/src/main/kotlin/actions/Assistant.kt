package actions

import actions.framework.*


interface AppHandler: BaseApp, OmniHandler

interface AppOptions {
    var debug: Boolean?
}

interface ServiceBaseApp {
    var handler: StandardHandler
}

//interface Plugin<PluginApp<TService, TApp>, PluginResult<TService, TApp, TPlugin>> {
//    <TApp>(app: AppHandler & TService & TApp): (AppHandler & TService & TApp & TPlugin) | void
//}

data class SerivcePlugin<TService, TPlugin>(val service: TService,
                                            val pluging: TPlugin)

data class BaseAppPlugin<TPlugin>(val baseApp: BaseApp,
                         val pluging: TPlugin)


typealias Plugin<PluginApp, PluginResult> = ((PluginApp) -> PluginResult)

data class PluginApp<TService, TApp>(val app: AppHandler,
                                     val service: TService,
                                     val tApp: TApp)

data class PluginResult<TService, TApp, TPlugin>(val app: AppHandler,
                                                 val service: TService,
                                                 val tApp: TApp,
                                                 val plugin: TPlugin)

interface BaseApp: ServiceBaseApp {
    var frameworks: BuiltinFrameworks

    fun <TService, TPlugin> use(plugin: Plugin<TService, TPlugin>): BaseAppPlugin<TPlugin>

    var debug: Boolean
}

fun create(options: AppOptions? = null): BaseApp {
//    BaseApp(
//            frameworks: Object. assign ({}, builtin),
//    handler: () => Promise.reject(new Error('StandardHandler not set')),
//    use(plugin) {
//        return plugin(this) || this
//    },
//    debug:!!(options && options.debug))
    return object: BaseApp {
        override var handler: StandardHandler
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            set(value) {}
        override var frameworks: BuiltinFrameworks
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            set(value) {}

        override fun <TService, TPlugin> use(plugin: Plugin<TService, TPlugin>): BaseAppPlugin<TPlugin> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override var debug: Boolean
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            set(value) {}

    }
}

//equivalent of (AppHandler & TService) mixin
data class AttachedAppHandlerService<TService>(val handler: AppHandler,
                           val service: TService)

data class AttachedBaseAppService<TService>(val baseApp: BaseApp,
                           val service: TService)

data class AttachResult<TService>(val omni: OmniHandler,
                        val baseApp: BaseApp,
                        val handler: StandardHandler,
                        val service: TService)

interface AppResult: OmniHandler, BaseApp, StandardHandler, ServiceBaseApp {

}

fun <TService: ServiceBaseApp> attach(
        service: TService,
        options: AppOptions? = null): AppResult {

    val baseApp = create(options)
    val omni = object: OmniHandler {
        override fun handle(body: JsonObject, headers: Headers): StandardResponse {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    val standardHandler = object: StandardHandler {
        override fun handle(body: JsonObject, headers: Headers): StandardResponse {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

//    var appResult = object: OmniHandler by omni, actions.BaseApp by baseApp, actions.framework.StandardHandler by standardHandler, actions.ServiceBaseApp by service {
//
//    }

//    var attachedResult = AttachResult(
//            baseApp = baseApp,
//            service = service,
//            omni = omni,
//            handler = standardHandler)

    return object: AppResult {
        override var handler: StandardHandler
            get() = standardHandler
            set(value) {handler = value}

        override fun <TService, TPlugin> use(plugin: Plugin<TService, TPlugin>): BaseAppPlugin<TPlugin> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun handle(body: JsonObject, headers: Headers): StandardResponse = standardHandler.handle(body, headers)

        override var frameworks: BuiltinFrameworks
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            set(value) {}

        override var debug: Boolean
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
            set(value) {}
    }
}
