package actions.service.actionssdk.conversation.question

import actions.DialogSpecData
import actions.service.actionssdk.conversation.DialogSpec
import actions.service.actionssdk.conversation.InputValueSpec
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.response.Question


interface DeepLinkOptions {
    /**
     * The name of the link destination.
     * @public
     */
    var destination: String

    /**
     * URL of Android deep link.
     * @public
     */
    var url: String

    /**
     * Android app package name to which to link.
     * @public
     */
    var `package`: String

    /**
     * The reason to transfer the user. This may be appended to a Google-specified prompt.
     * @public
     */
    var reason: String?
}

/** @public */
typealias DeepLinkArgument = Unit

/**
 * Requests the user to transfer to a linked out Android app intent. Using this feature
 * requires verifying the linked app in the (Actions console)[console.actions.google.com].
 *
 * @example
 * ```javascript
 *
 * // Actions SDK
 * const app = actionssdk()
 *
 * app.intent('actions.intent.MAIN', conv => {
 *   conv.ask('Great! Looks like we can do that in the app.')
 *   conv.ask(new DeepLink({
 *     destination: 'Google',
 *     url: 'example://gizmos',
 *     package: 'com.example.gizmos',
 *     reason: 'handle this for you',
 *   }))
 * })
 *
 * app.intent('actions.intent.LINK', (conv, input, arg, status) => {
 *   // possibly do something with status
 *   conv.close('Okay maybe we can take care of that another time.')
 * })
 *
 * // Dialogflow
 * const app = actionssdk()
 *
 * app.intent('Default Welcome Intent', conv => {
 *   conv.ask('Great! Looks like we can do that in the app.')
 *   conv.ask(new DeepLink({
 *     destination: 'Google',
 *     url: 'example://gizmos',
 *     package: 'com.example.gizmos',
 *     reason: 'handle this for you',
 *   }))
 * })
 *
 * // Create a Dialogflow intent with the `actions_intent_LINK` event
 * app.intent('Get Link Status', (conv, input, arg, status) => {
 *   // possibly do something with status
 *   conv.close('Okay maybe we can take care of that another time.')
 * })
 * ```
 *
 * @public
 */
class DeepLink(options: DeepLinkOptions) : Question(IntentEnum.LINK) {
    /**
     * @param options DeepLink options
     * @public
     */
    init {
        val extension = DialogSpecData(
                `@type` = DialogSpec.LinkDialogSpec.value,
                destinationName = options.destination,
                requestLinkReason = options.reason)

        this._data(InputValueSpec.LinkValueSpec) {
            openUrlAction = actions.service.actionssdk.api.GoogleActionsV2UiElementsOpenUrlAction(
                    url = options.url,
                    androidApp = actions.service.actionssdk.api.GoogleActionsV2DevicesAndroidApp(
                            packageName = options.`package`)
            )
            dialogSpec = extension
        }
    }
}
