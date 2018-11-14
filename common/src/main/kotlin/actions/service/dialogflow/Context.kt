package actions.service.dialogflow

import actions.service.dialogflow.api.DialogflowV1Context
import actions.service.dialogflow.api.DialogflowV1Parameters
import actions.service.dialogflow.api.GoogleCloudDialogflowV2Context

/**
 * Copyright 2018 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/** @public */
//typealias Parameters = MutableMap<String, Any?>

/** @public */
class Contexts : MutableMap<String, DialogflowV1Context> by mutableMapOf() {}

//export interface Contexts {
//    /** @public */
//    [context: string]: Context<Parameters> | undefined
//}

/** @public */
class OutputContexts : MutableMap<String, OutputContext> by mutableMapOf() {
    /** @public */
//    [context: string]: OutputContext<Parameters> | undefined
}

/** @public */
class Context(
        /**
         * Full name of the context.
         * @public
         */
        var name: String? = null,

        /**
         * Remaining number of intents
         * @public
         */
        lifespan: Int? = null,

        /**
         * The context parameters from the current intent.
         * Context parameters include parameters collected in previous intents
         * during the lifespan of the given context.
         *
         * See {@link https://dialogflow.com/docs/concept-actions#section-extracting-values-from-contexts|
         *     here}.
         *
         * @example
         * ```javascript
         *
         * app.intent("Tell Greeting", conv => {
         *   val context1 = conv.contexts.get("context1")
         *   val parameters = context1.parameters
         *   val color = parameters.color
         *   val num = parameters.num
         * })
         *
         * // Using destructuring
         * app.intent("Tell Greeting", conv => {
         *   val context1 = conv.contexts.get("context1")
         *   val { color, num } = context1.parameters
         * })
         * ```
         *
         * @public
         */
        parameters: DialogflowV1Parameters? = null
) : OutputContext(lifespan = lifespan, parameters = parameters)

/** @public */
open class OutputContext(
        /** @public */
        var lifespan: Int?,

        /** @public */
        var parameters: DialogflowV1Parameters?
)

//typeof (context as ApiV1.DialogflowV1Context).lifespan === "number"

val contextNameRegex by lazy {   """([^/]+)?$""".toRegex() }

class ContextValues {
    /** @public */
    var input: Contexts? = null

    /** @public */
    var output: OutputContexts? = null

    private var _session: String? = null

    constructor(outputContexts: MutableList<GoogleCloudDialogflowV2Context>?, session: String? = null, flag: Boolean) {
        this.input = Contexts()
        this._session = session
        outputContexts?.forEach {
            val name = it.name
            val parameters = it.parameters
            val lifespanCount = it.lifespanCount
            //TODO test this regex
            val find = contextNameRegex.find(name)
            val first = find?.groups?.first()?.value ?: name
            this.input?.put(first, DialogflowV1Context(name = name,
                    lifespan = lifespanCount,
                    parameters = parameters))
        }

        this.output = OutputContexts()
    }

    /** @hidden */
    constructor(outputContexts: MutableList<DialogflowV1Context>?, session: String? = null) {
        this.input = Contexts()
        this._session = session
        outputContexts?.forEach {
            val name = it.name
            val parameters = it.parameters
            val lifespan = it.lifespan
            if (name != null) {
            this.input?.put(name, DialogflowV1Context(name = name,
                    lifespan = lifespan,
                    parameters = parameters))
            }

        }
        this.output = OutputContexts()
    }

    /** @hidden */
    fun _serialize(): MutableList<GoogleCloudDialogflowV2Context> {
        return this.output?.map {
            GoogleCloudDialogflowV2Context(
                    name = "${this._session}/contexts/${it.key}",
                    lifespanCount = it.value.lifespan ?: 0,
                    parameters = it.value.parameters
            )
        }?.toMutableList() ?: mutableListOf()
        /*).map((name): Api.GoogleCloudDialogflowV2Context => {
            val { lifespan, parameters } = this.output[name]!
            return {
                name: `${this._session}/contexts/${name}`,
                lifespanCount: lifespan,
                parameters,
            }
        })
        */
    }

    /** @hidden */
    fun _serializeV1(): MutableList<DialogflowV1Context> {
        return this.output?.map {
            DialogflowV1Context(
                    name = it.key,
                    lifespan = it.value.lifespan ?: 0,
                    parameters = it.value.parameters
            )
        }?.toMutableList() ?: mutableListOf()

        /*
            (name): ApiV1.DialogflowV1Context => {
            val { lifespan, parameters } = this.output[name]!
            return {
                name,
                lifespan,
                parameters,
            }
        })
        }
        */
    }

    /**
     * Returns the incoming context by name for this intent.
     *
     * @example
     * ```javascript
     *
     * val AppContexts = {
     *   NUMBER: "number",
     * }
     *
     * val app = dialogflow()
     *
     * app.intent("Default Welcome Intent", conv => {
     *   conv.contexts.set(AppContexts.NUMBER, 1)
     *   conv.ask("Welcome to action snippets! Say a number.")
     * })
     *
     * // Create intent with "number" context as requirement
     * app.intent("Number Input", conv => {
     *   val context = conv.contexts.get(AppContexts.NUMBER)
     * })
     * ```
     *
     * @param name The name of the Context to retrieve.
     * @return Context value matching name or undefined if no matching context.
     * @public
     */
    fun get(name: String): DialogflowV1Context? {
        return this.input?.get(name)
    }

    /**
     * Set a new context for the current intent.
     *
     * @example
     * ```javascript
     *
     * val AppContexts = {
     *   NUMBER: "number",
     * }
     *
     * val app = dialogflow()
     *
     * app.intent("Default Welcome Intent", conv => {
     *   conv.contexts.set(AppContexts.NUMBER, 1)
     *   conv.ask("Welcome to action snippets! Say a number.")
     * })
     *
     * // Create intent with "number" context as requirement
     * app.intent("Number Input", conv => {
     *   val context = conv.contexts.get(AppContexts.NUMBER)
     * })
     * ```
     *
     * @param name Name of the context. Dialogflow converts to lowercase.
     * @param lifespan Context lifespan.
     * @param parameters Context parameters.
     * @public
     */
    fun set(name: String, lifespan: Int, parameters: DialogflowV1Parameters? = null) {
        output?.put(name, OutputContext(
                lifespan,
                parameters))
    }

    /** @public */
    fun delete(name: String) {
        this.set(name, 0)
    }

    /**
     * Returns the incoming contexts for this intent as an iterator.
     *
     * @example
     * ```javascript
     *
     * val AppContexts = {
     *   NUMBER: "number",
     * }
     *
     * val app = dialogflow()
     *
     * app.intent("Default Welcome Intent", conv => {
     *   conv.contexts.set(AppContexts.NUMBER, 1)
     *   conv.ask("Welcome to action snippets! Say a number.")
     * })
     *
     * // Create intent with "number" context as requirement
     * app.intent("Number Input", conv => {
     *   for (val context of conv.contexts) {
     *     // do something with the contexts
     *   }
     * })
     * ```
     *
     * @public
     */

//todo see if we can implement 'in' function to closely match js.
    inline fun forEach(action: (ContextValues) -> Unit) {
//    [Symbol.iterator]() {
        TODO("iterate through Context - fix generic contexts through base class or intermediate types")
//        val contexts = values(this.input) as Context<Parameters>[]
//        return contexts[Symbol.iterator]()
        // suppose to be Array.prototype.values(), but can"t use because of bug:
        // https://bugs.chromium.org/p/chromium/issues/detail?id=615873
    }
}
