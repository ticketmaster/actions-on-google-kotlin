package actions.service.actionssdk.conversation.argument

import actions.ApiClientObjectMap
import actions.service.actionssdk.api.*
import actions.service.actionssdk.conversation.question.*
import actions.service.actionssdk.conversation.question.option.OptionArgument
import actions.service.actionssdk.conversation.question.permission.PermissionArgument
import actions.service.actionssdk.conversation.question.permission.UpdatePermissionUserIdArgument
import actions.service.actionssdk.conversation.question.transaction.DeliveryAddressArgument
import actions.service.actionssdk.conversation.question.transaction.TransactionDecisionArgument
import actions.service.actionssdk.conversation.question.transaction.TransactionRequirementsArgument

/*  commented out because this is duplicate of GoogleActionsV2Argument
data class Argument(
        var boolValue: Boolean? = null,
        var datetimeValue: GoogleActionsV2DateTime? = null,
//        var extension: ArgumentExtension? = null,
        var extension: ApiClientObjectMap<Any>? = null,
        var floatValue: Float? = null,
        var intValue: Int? = null,
        var name: String? = null,
        var placeValue: GoogleActionsV2Location? = null,
        var rawText: String? = null,
        var status: GoogleRpcStatus? = null,
        var structuredValue: ApiClientObjectMap<Any>? = null,
        var textValue: String? = null)
        */

class ArgumentsNamed {
    /**
     * True if the request follows a previous request asking for
     * permission from the user and the user granted the permission(s).
     * Otherwise, false.
     * Only use after calling {@link Permission|conv.ask(new Permission)}
     * or {@link UpdatePermission|conv.ask(new UpdatePermission)}.
     * @public
     */
    var PERMISSION: PermissionArgument? = null

    /**
     * The option key user chose from options response.
     * Only use after calling {@link List|conv.ask(new List)}
     * or {@link Carousel|conv.ask(new Carousel)}.
     * @public
     */
    var OPTION: OptionArgument? = null

    /**
     * The transactability of user.
     * Only use after calling {@link TransactionRequirements|conv.ask(new TransactionRequirements)}.
     * Undefined if no result given.
     * @public
     */
    var TRANSACTION_REQUIREMENTS_CHECK_RESULT: TransactionRequirementsArgument? = null

    /**
     * The order delivery address.
     * Only use after calling {@link DeliveryAddress|conv.ask(new DeliveryAddress)}.
     * @public
     */
    var DELIVERY_ADDRESS_VALUE: DeliveryAddressArgument? = null

    /**
     * The transaction decision information.
     * Is object with userDecision only if user declines.
     * userDecision will be one of {@link GoogleActionsV2TransactionDecisionValueUserDecision}.
     * Only use after calling {@link TransactionDecision|conv.ask(new TransactionDecision)}.
     * @public
     */
    var TRANSACTION_DECISION_VALUE: TransactionDecisionArgument? = null

    /**
     * The confirmation decision.
     * Use after {@link Confirmation|conv.ask(new Confirmation)}
     * @public
     */
    var CONFIRMATION: ConfirmationArgument? = null

    /**
     * The user provided date and time.
     * Use after {@link DateTime|conv.ask(new DateTime)}
     * @public
     */
    var DATETIME: DateTimeArgument? = null

    /**
     * The status of user sign in request.
     * Use after {@link SignIn|conv.ask(new SignIn)}
     * @public
     */
    var SIGN_IN: SignInArgument? = null

    /**
     * The number of subsequent reprompts related to silent input from the user.
     * This should be used along with the `actions.intent.NO_INPUT` intent to reprompt the
     * user for input in cases where the Google Assistant could not pick up any speech.
     * @public
     */
    var REPROMPT_COUNT: RepromptArgument? = null

    /**
     * True if it is the final reprompt related to silent input from the user.
     * This should be used along with the `actions.intent.NO_INPUT` intent to give the final
     * response to the user after multiple silences and should be an `conv.close`
     * which ends the conversation.
     * @public
     */
    var IS_FINAL_REPROMPT: FinalRepromptArgument? = null

    /**
     * The result of {@link NewSurface|conv.ask(new NewSurface)}
     * True if user has triggered conversation on a new device following the
     * `actions.intent.NEW_SURFACE` intent.
     * @public
     */
    var NEW_SURFACE: NewSurfaceArgument? = null

    /**
     * True if user accepted update registration request.
     * Used with {@link RegisterUpdate|conv.ask(new RegisterUpdate)}
     * @public
     */
    var REGISTER_UPDATE: RegisterUpdateArgument? = null

    /**
     * The updates user id.
     * Only use after calling {@link UpdatePermission|conv.ask(new UpdatePermission)}.
     * @public
     */
    var UPDATES_USER_ID: UpdatePermissionUserIdArgument? = null

    /**
     * The user provided place.
     * Use after {@link Place|conv.ask(new Place)}.
     * @public
     */
    var PLACE: PlaceArgument? = null

    /**
     * The link non status argument.
     * Is undefined as a noop.
     * Use {@link Status#get|conv.arguments.status.get('LINK')} to explicitly get the status.
     * @public
     */
    var LINK: DeepLinkArgument? = null

    /**
     * The status of MEDIA_STATUS intent.
     * @public
     */
    var MEDIA_STATUS: MediaStatusArgument? = null
}

class ArgumentsParsed : MutableMap<String, GoogleActionsV2Argument> by mutableMapOf() {
}

class ArgumentsIndexable : MutableMap<String, GoogleActionsV2Argument> by mutableMapOf() {
}

class ArgumentsStatus : MutableMap<String, GoogleRpcStatus> by mutableMapOf() {
}

class ArgumentsRaw : MutableMap<String, GoogleActionsV2Argument> by mutableMapOf() {
    /** @public */
//    [name: string]: Api.GoogleActionsV2Argument
}

fun getValue(arg: GoogleActionsV2Argument): GoogleActionsV2Argument? {
    return arg

//    for (key in arg) {
//        if (key === 'name' || key === 'textValue' || key === 'status') {
//            continue
//        }
//        return (arg as ArgumentsIndexable)[key]
//    }
//     Manually handle the PERMISSION argument because of a bug not returning boolValue
//    if (arg.name === 'PERMISSION') {
//        return !!arg.boolValue
//    }
//    return arg.textValue
//    return null

}

class Parsed(raw: MutableList<GoogleActionsV2Argument>? = null) {
    /** @public */
    var list: MutableList<GoogleActionsV2Argument>? = null

    /** @public */
    var input: ArgumentsParsed? = null

    init {
        input = ArgumentsParsed()
//        raw?.forEach {
//            input?.put(it.name ?: "", it)
//        }
    }

    /** @public */
//    get<TName extends keyof ArgumentsNamed>(name: TName): ArgumentsNamed[TName]
    /** @public */
//    get(name: string): Argument
    fun get(name: String): GoogleActionsV2Argument? {
        return this.input?.get(name)
    }
}

class Status(raw: MutableList<GoogleActionsV2Argument>? = null) {
    /** @public */
    var list: MutableList<GoogleRpcStatus>? = null

    /** @public */
    var input: ArgumentsStatus? = null

    init {
        input = ArgumentsStatus()
        this.list = raw?.mapNotNull {
            val name = it.name
            val status = it.status
            if (name != null && status != null)
                this.input?.put(name, status)
            status
        }?.toMutableList()
    }

    fun get(name: String): GoogleRpcStatus? {
        return this.input?.get(name)
    }
}

class Raw(list: MutableList<GoogleActionsV2Argument>? = null) {
    /** @public */
    var input: ArgumentsRaw? = null

    init {
        input = ArgumentsRaw()
        list?.forEach {
            if (it.extension?.resultType != null) {
                it.resultType = it.extension?.resultType
            }
            if (it.extension?.userDecision != null) {
                it.userDecision = it.extension?.userDecision
            }
            if (it.extension?.location != null) {
                it.location = it.extension?.location
            }
            input?.put(it.name ?: "", it)
        }
    }

    fun get(name: String): GoogleActionsV2Argument? {
        return this.input?.get(name)
    }
}

class Arguments(raw: MutableList<GoogleActionsV2Argument>? = null) {
    /** @public */
    var parsed: Parsed? = null

    /** @public */
    var status: Status? = null

    /** @public */
    var raw: Raw? = null

    init {
        this.parsed = Parsed(raw)
        this.status = Status(raw)
        this.raw = Raw(raw)
    }

    /**
     * Get the argument value by name from the current intent.
     * The first property value not named `name` or `status` will be returned.
     * Will retrieve `textValue` last.
     * If there is no other properties, return undefined.
     *
     * @example
     * ```javascript
     *
     * // Actions SDK
     * app.intent('actions.intent.PERMISSION', conv => {
     *   const granted = conv.arguments.get('PERMISSION') // boolean true if granted, false if not
     * })
     *
     * // Dialogflow
     * // Create a Dialogflow intent with the `actions_intent_PERMISSION` event
     * app.intent('Get Permission', conv => {
     *   const granted = conv.arguments.get('PERMISSION') // boolean true if granted, false if not
     * })
     * ```
     *
     * @param argument Name of the argument.
     * @return First property not named 'name' or 'status' with 'textValue' given last priority
     *     or undefined if no other properties.
     *
     * @public
     */
//    get<TName extends keyof ArgumentsNamed>(name: TName): ArgumentsNamed[TName]
    /** @public */
//    get(name: string): Argument
    fun get(name: String): GoogleActionsV2Argument? {
        return this.raw?.get(name)
    }

//    /** @public */
//    [Symbol.iterator]()
//    {
//        return this.raw.list[Symbol.iterator]()
//         suppose to be Array.prototype.values(), but can't use because of bug:
//         https://bugs.chromium.org/p/chromium/issues/detail?id=615873
//    }
}
