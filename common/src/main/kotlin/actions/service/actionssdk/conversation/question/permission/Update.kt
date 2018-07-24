package actions.service.actionssdk.conversation.question.permission

import actions.service.actionssdk.api.GoogleActionsV2Argument
import actions.service.actionssdk.api.GoogleActionsV2PermissionValueSpec
import actions.service.actionssdk.api.GoogleActionsV2PermissionValueSpecPermissions
import actions.service.actionssdk.api.GoogleActionsV2UpdatePermissionValueSpec

/** @public */
typealias UpdatePermissionUserIdArgument = String

/** @public */
class UpdatePermissionOptions {
    /**
     * The Dialogflow/Actions SDK intent name to be triggered when the update is received.
     * @public
     */
    var intent: String? = null

    /**
     * The necessary arguments to fulfill the intent triggered on update.
     * These can be retrieved using {@link Arguments#get|conv.arguments.get}.
     * @public
     */
    var arguments: MutableList<GoogleActionsV2Argument>? = null
}

/**
 * Prompts the user for permission to send proactive updates at any time.
 *
 * @example
 * ```javascript
 *
 * // Actions SDK
 * const app = actionssdk()
 *
 * app.intent('actions.intent.MAIN', conv => {
 *   conv.ask(new UpdatePermission({
 *     intent: 'show.image',
 *     arguments: [{
 *       name: 'image_to_show',
 *       textValue: 'image_type_1',
 *     }
 *   ))
 * })
 *
 * app.intent('actions.intent.PERMISSION', conv => {
 *   const granted = conv.arguments.get('PERMISSION')
 *   if (granted) {
 *     conv.close(`Great, I'll send an update whenever I notice a change`)
 *   } else {
 *     // Response shows that user did not grant permission
 *     conv.close('Alright, just let me know whenever you need the weather!')
 *   }
 * })
 *
 * app.intent('show.image', conv => {
 *   const arg = conv.arguments.get('image_to_show') // will be 'image_type_1'
 *   // do something with arg
 * })
 *
 * // Dialogflow
 * const app = dialogflow()
 *
 * app.intent('Default Welcome Intent', conv => {
 *   conv.ask(new UpdatePermission({
 *     intent: 'Show Image',
 *     arguments: [{
 *       name: 'image_to_show',
 *       textValue: 'image_type_1',
 *     }
 *   ))
 * })
 *
 * // Create a Dialogflow intent with the `actions_intent_PERMISSION` event
 * app.intent('Get Permission', conv => {
 *   const granted = conv.arguments.get('PERMISSION')
 *   if (granted) {
 *     conv.close(`Great, I'll send an update whenever I notice a change`)
 *   } else {
 *     // Response shows that user did not grant permission
 *     conv.close('Alright, just let me know whenever you need the weather!')
 *   }
 * })
 *
 * app.intent('Show Image', conv => {
 *   const arg = conv.arguments.get('image_to_show') // will be 'image_type_1'
 *   // do something with arg
 * })
 * ```
 *
 * @public
 */
/**
 * @param options UpdatePermission options
 * @public
 */
class UpdatePermission(options: UpdatePermissionOptions? = null) : Permission({
            permissions = mutableListOf(GoogleActionsV2PermissionValueSpecPermissions.UPDATE)
            extra = GoogleActionsV2PermissionValueSpec(
                    updatePermissionValueSpec = GoogleActionsV2UpdatePermissionValueSpec(arguments = options?.arguments, intent = options?.intent))
        })

