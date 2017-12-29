package com.tmsdurham.actions

import com.tmsdurham.dialogflow.*
import com.tmsdurham.actions.actions.ActionRequest


/**
 * The Actions on Google client library RequestExtractor.
 *
 * This class contains the methods to extract data from the request object.
 */
class RequestExtractor<T, S>(val app: AssistantApp<T, S>) {


    /**
     * Gets the {@link User} object.
     * The user object contains information about the user, including
     * a string identifier and personal information (requires requesting permissions,
     * see {@link AssistantApp#askForPermissions|askForPermissions}).
     *
     * @example
     * val app = DialogflowApp(request = request, response = response)
     * or
     * val app = ActionsSdkApp(request = request, response = response)
     * val userId = app.getUser().userId
     *
     * @return {User} Null if no value.
     * @requestextractor
     */
    fun getUser(): User? {
        debug("getUser")
        when (app.request.body) {
            is DialogflowRequest -> {
                val data = app.request.body.originalRequest?.data
                if (data?.user == null) {
                    app.handleError("No user object")
                    return null
                }

                val requestUser = data.user

                // User object includes original API properties
                return requestUser
            }

            is ActionRequest -> {
                return app.request.body.user
            }
            else -> return null
        }
    }

    fun requestData(): ActionRequest? = when (app.request.body) {
        is DialogflowRequest -> { app.request.body.originalRequest?.data }
        is ActionRequest -> { app.request.body }
        else -> null
    }

    /**
     * If granted permission to device"s location in previous intent, returns device"s
     * location (see {@link AssistantApp#askForPermissions|askForPermissions}).
     * If device info is unavailable, returns null.
     *
     * @example
     * val app = DialogflowApp(request = req, response = res)
     * or
     * val app = ActionsSdkApp(request = req, response = res)
     * app.askForPermission("To get you a ride",
     *   app.SupportedPermissions.DEVICE_PRECISE_LOCATION)
     * // ...
     * // In response handler for permissions fallback intent:
     * if (app.isPermissionGranted()) {
     *   sendCarTo(app.getDeviceLocation().coordinates);
     * }
     *
     * @return {DeviceLocation} Null if location permission is not granted.
     * @requestextractor
     */
    fun getDeviceLocation(): DeviceLocation? {
        debug("getDeviceLocation")
        val data = requestData()
        if (data?.device?.location == null) {
            return null
        }
        val deviceLocation = data.device?.location
        deviceLocation?.address = deviceLocation?.formattedAddress
        return deviceLocation
    }

    /**
     * Find argument with requirements
     * @param {vararg <String>} targets Argument to find
     * @return {Arguments} The argument
     */
    fun findArgument(vararg targets: String): Arguments? {
        val data = requestData()
        data?.inputs?.forEach {
            it.arguments?.forEach {
                if (targets.contains(it.name)) {
                    return it
                }
            }
        }
        return null
    }

    /**
     * Get the argument value by name from the current intent.
     * If the argument is included in originalRequest, and is not a text argument,
     * the entire argument object is returned.
     *
     * Note: If incoming request is using an API version under 2 (e.g. "v1"),
     * the argument object will be in Proto2 format (snake_case, etc).
     *
     * @example
     * val app = DialogflowApp(request = request, response = response)
     * val WELCOME_INTENT = "input.welcome"
     * val NUMBER_INTENT = "input.number"
     *
     * fun welcomeIntent (app: DialogflowApp<T>) {
     *   app.ask("Welcome to action snippets! Say a number.")
     * }
     *
     * fun numberIntent (app: DialogflowApp<T>) {
     *   val number = app.getArgument(NUMBER_ARGUMENT)
     *   app.tell("You said " + number)
     * }
     *
     * val actionMap = mapOf(
     *  WELCOME_INTENT to welcomeIntent,
     *  NUMBER_INTENT to numberIntent)
     * app.handleRequest(actionMap)
     *
     * @param {String} argName Name of the argument.
     * @return {String} Argument value matching argName
     *     or null if no matching argument.
     * @requestextractor
     */
    fun getArgumentCommon(argName: String): Any? {
        debug("getArgumentCommon: argName=$argName")
        if (argName.isBlank()) {
            app.handleError("Invalid argument name")
            return null
        }
        val argument = findArgument(argName)
        if (argument == null) {
            debug("Failed to get argument value: $argName")
            return null
        } else if (argument.textValue != null) {
            return argument.textValue
        } else if (argument.text_value != null) {
            return argument.text_value
        } else {
            if (!this.app.isNotApiVersionOne()) {
                return argument
                //TODO version 1
//                return transformToSnakeCase(argument)
            } else {
                return argument
            }
        }
        return null
    }

    /**
     * Gets transactability of user. Only use after calling
     * askForTransactionRequirements. Null if no result given.
     *
     * @return {String?} One of Transactions.ResultType.
     * @requestextractor
     */
    fun getTransactionRequirementsResult(): TransactionValues.ResultType? {
        debug("getTransactionRequirementsResult")
        val argument = this.findArgument(app.BUILT_IN_ARG_NAMES.TRANSACTION_REQ_CHECK_RESULT)
        if (argument?.extension?.resultType != null) {
            return argument.extension.resultType
        }
        debug("Failed to get transaction requirements result")
        return null
    }

    /**
     * Gets order delivery address. Only use after calling askForDeliveryAddress.
     *
     * @return {DeliveryAddress} Delivery address information. Null if user
     *     denies permission, or no address given.
     * @requestextractor
     */
    fun getDeliveryAddress(): Location? {
        debug("getDeliveryAddress")
        val argument = findArgument(app.BUILT_IN_ARG_NAMES.DELIVERY_ADDRESS_VALUE, app.BUILT_IN_ARG_NAMES.TRANSACTION_DECISION_VALUE)
        if (argument?.extension != null) {
            if (argument.extension.userDecision == TransactionValues.DeliveryAddressDecision.ACCEPTED.value) {
                val location = argument.extension.location
                if (location?.postalAddress == null) {
                    debug("User accepted, but may not have configured address in app")
                    return null
                }
                return location
            } else {
                debug("User rejected giving delivery address")
                return null
            }
        }
        debug("Failed to get order delivery address")
        return null
    }

    /**
     * Gets transaction decision information. Only use after calling
     * askForTransactionDecision.
     *
     * @return {TransactionDecision} Transaction decision data. Returns object with
     *     userDecision only if user declines. userDecision will be one of
     *     Transactions.ConfirmationDecision. Null if no decision given.
     * @requestextractor
     */
    fun getTransactionDecision(): TransactionRequirementsCheckResult? {
        debug("getTransactionDecision")
        val argument = findArgument(app.BUILT_IN_ARG_NAMES.TRANSACTION_DECISION_VALUE)
        if (argument?.extension != null) {
            return argument.extension
        }
        debug("Failed to get order decision information")
        return null
    }

    /**
     * Gets confirmation decision. Use after askForConfirmation.
     *
     *     False if user replied with negative response. Null if no user
     *     confirmation decision given.
     * @requestextractor
     */
    fun getUserConfirmation(): Boolean? {
        debug("getUserConfirmation")
        val argument = findArgument(app.BUILT_IN_ARG_NAMES.CONFIRMATION)
        if (argument != null) {
            return argument.boolValue
        }
        debug("Failed to get confirmation decision information")
        return null
    }

    /**
     * Gets user provided date and time. Use after askForDateTime.
     *
     * @return {DateTime} Date and time given by the user. Null if no user
     *     date and time given.
     * @requestextractors
     */
    fun getDateTime(): DateTimeValue? {
        debug("getDateTime")
        val argument = findArgument(app.BUILT_IN_ARG_NAMES.DATETIME)
        if (argument != null) {
            return argument.datetimeValue
        }
        debug("Failed to get date/time information")
        return null
    }

    /**
     * Gets status of user sign in request.
     *
     * @return {String?} Result of user sign in request. One of
     * DialogflowApp.SignInStatus or ActionsSdkApp.SignInStatus
     * Null if no sign in status.
     * @requestextractor
     */
    fun getSignInStatus(): String? {
        debug("getSignInStatus")
        val argument = findArgument(app.BUILT_IN_ARG_NAMES.SIGN_IN)
        if (argument?.extension?.status != null) {
            return argument.extension.status
        }
        debug("Failed to get sign in status")
        return null
    }

    /**
     * Returns true if the app is being tested in sandbox mode. Enable sandbox
     * mode in the (Actions console)[console.actions.google.com] to test
     * transactions.
     *
     * @return {Boolean} True if app is being used in Sandbox mode.
     * @requestextractor
     */
    fun isInSandbox(): Boolean {
        val data = requestData()
        return data?.isInSandbox ?: false
    }

    /**
     * Gets surface capabilities of user device.
     *
     * @return {Array<string>} Supported surface capabilities, as defined in
     *     AssistantApp.SurfaceCapabilities.
     * @dialogflow
     */
    fun getSurfaceCapabilities(): MutableList<String>? {
        debug("getSurfaceCapabilities")
        val data = requestData()
        if (data?.surface?.capabilities == null) {
            error("No surface capabilities in incoming request")
            return null
        }
        if (data.surface?.capabilities != null) {
            return data.surface?.capabilities?.map { it.name }?.filterNotNull()?.toMutableList()
        } else {
            error("No surface capabilities in incoming request")
            return null
        }
    }

    /**
     * Gets type of input used for this request.
     *
     * @return {String?} One of DialogflowApp.InputTypes.
     *     Null if no input type given.
     * @requestextractor
     */
    fun getInputType(): String? {
        debug("getInputType")
        val data = requestData()
        debug(data.toString())
        data?.inputs?.forEach {
            it.rawInputs?.forEach {
                if (it.inputType != null) {
                    return it.inputType
                }
            }
        }
        error("No input type in incoming request")
        return null
    }

    /**
     * Returns true if the request follows a previous request asking for
     * permission from the user and the user granted the permission(s). Otherwise,
     * false. Use with {@link AssistantApp#askForPermissions|askForPermissions}.
     *
     * @example
     * val app = ActionsSdkApp(request = request, response = response)
     * or
     * val app = DialogflowApp(request = request, response = response)
     * app.askForPermissions("To get you a ride", [
     *   app.SupportedPermissions.NAME,
     *   app.SupportedPermissions.DEVICE_PRECISE_LOCATION
     * ])
     * // ...
     * // In response handler for subsequent intent:
     * if (app.isPermissionGranted()) {
     *  // Use the requested permission(s) to get the user a ride
     * }
     *
     * @return {Boolean} true if permissions granted.
     * @requestextractor
     */
    fun isPermissionGranted(): Boolean {
        debug("isPermissionGranted")
        return getArgumentCommon(app.BUILT_IN_ARG_NAMES.PERMISSION_GRANTED) ?: "" == "true"
    }

    /**
     * Returns the number of subsequent reprompts related to silent input from the
     * user. This should be used along with the NO_INPUT intent to reprompt the
     * user for input in cases where the Google Assistant could not pick up any
     * speech.
     *
     * @example
     * val app = ActionsSdkApp(request, response)
     *
     * func welcome(app: AsstistantApp) {
     *   app.ask("Welcome to your app!")
     * }
     *
     * fun noInput(app: AssistantApp) {
     *   if (app.getRepromptCount() == 0) {
     *     app.ask("What was that?")
     *   } else if (app.getRepromptCount() == 1) {
     *     app.ask("Sorry I didn"t catch that. Could you repeat yourself?")
     *   } else if (app.isFinalReprompt()) {
     *     app.tell("Okay let"s try this again later.")
     *   }
     * }
     *
     * val actionMap = mutableMapOf()
     * actionMap.set(app.StandardIntents.MAIN, welcome)
     * actionMap.set(app.StandardIntents.NO_INPUT, noInput)
     * app.handleRequest(actionMap)
     *
     * @return {Int?} The current reprompt count. Null if no reprompt count
     *     available (e.g. not in the NO_INPUT intent).
     * @dialogflow
     * @actionssdk
     */
    fun getRepromptCount(): Int? {
        debug("getRepromptCount")
        val repromptCount = getArgumentCommon(app.BUILT_IN_ARG_NAMES.REPROMPT_COUNT)
        return (repromptCount as String?)?.toInt()
    }


    /**
     * Returns true if it is the final reprompt related to silent input from the
     * user. This should be used along with the NO_INPUT intent to give the final
     * response to the user after multiple silences and should be an app.tell
     * which ends the conversation.
     *
     * @example
     * val app = ActionsSdkApp(request, response)
     *
     * fun welcome(app: AssistantApp) {
     *   app.ask("Welcome to your app!")
     * }
     *
     * fun noInput(app: AssistantApp) {
     *   if (app.getRepromptCount() == 0) {
     *     app.ask("What was that?")
     *   } else if (app.getRepromptCount() == 1) {
     *     app.ask("Sorry I didn't catch that. Could you repeat yourself?")
     *   } else if (app.isFinalReprompt()) {
     *     app.tell("Okay let's try this again later.")
     *   }
     * }
     *
     * val actionMap = mutableMapOf()
     * actionMap.set(app.STANDARD_INTENTS.MAIN, welcome)
     * actionMap.set(app.STANDARD_INTENTS.NO_INPUT, noInput)
     * app.handleRequest(actionMap)
     *
     * @return {Boolean} True if in a NO_INPUT intent and this is the final turn
     *     of dialog.
     * @dialogflow
     * @actionssdk
     */
    fun isFinalReprompt(): Boolean {
        debug("isFinalReprompt")
        val finalReprompt = getArgumentCommon(app.BUILT_IN_ARG_NAMES.IS_FINAL_REPROMPT)
        return finalReprompt == '1'
    }


    /**
     * Returns true if user accepted update registration request. Used with
     * {@link AssistantApp#askToRegisterDailyUpdate}
     *
     * @return {Boolean} True if user accepted update registration request.
     * @dialogflow
     * @actionssdk
     */
    fun isUpdateRegistered(): Boolean {
        debug("isUpdateRegistered");
        val argument = this.findArgument(app.BUILT_IN_ARG_NAMES.REGISTER_UPDATE)
        return argument?.extension?.status == "OK"
    }

}

