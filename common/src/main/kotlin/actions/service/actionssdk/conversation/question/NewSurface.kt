package actions.service.actionssdk.conversation.question

import actions.service.actionssdk.api.GoogleActionsV2NewSurfaceValue
import actions.service.actionssdk.conversation.InputValueSpec
import actions.service.actionssdk.conversation.IntentEnum
import actions.service.actionssdk.conversation.SurfaceCapability
import actions.service.actionssdk.conversation.response.SoloQuestion


typealias NewSurfaceArgument = GoogleActionsV2NewSurfaceValue

data class NewSurfaceOptions(
    /**
     * Context why new surface is requested.
     * It's the TTS prompt prefix (action phrase) we ask the user.
     * @public
     */
    var context: String? = null,

    /**
     * Title of the notification appearing on new surface device.
     * @public
     */
    var notification: String? = null,

    /**
     * The list of capabilities required in the surface.
     * @public
     */
    var capabilities: MutableList<SurfaceCapability>? = null
)

/**
 * Requests the user to switch to another surface during the conversation.
 * Works only for en-* locales.
 *
 * @example
 * ```javascript
 *
 * // Actions SDK
 * const app = actionssdk()
 *
 * const imageResponses = [
 *   `Here's an image of Google`,
 *   new Image({
 *     url: 'https://storage.googleapis.com/gweb-uniblog-publish-prod/images/' +
 *       'Search_GSA.2e16d0ba.fill-300x300.png',
 *     alt: 'Google Logo',
 *   })
 * ]
 *
 * app.intent('actions.intent.MAIN', conv => {
 *   const capability = 'actions.capability.SCREEN_OUTPUT'
 *   if (conv.surface.capabilities.has(capability)) {
 *     conv.close(...imageResponses)
 *   } else {
 *     conv.ask(new NewSurface({
 *       capabilities: capability,
 *       context: 'To show you an image',
 *       notification: 'Check out this image',
 *     }))
 *   }
 * })
 *
 * app.intent('actions.intent.NEW_SURFACE', (conv, input, newSurface) => {
 *   if (newSurface.status === 'OK') {
 *     conv.close(...imageResponses)
 *   } else {
 *     conv.close(`Ok, I understand. You don't want to see pictures. Bye`)
 *   }
 * })
 *
 * // Dialogflow
 * const app = dialogflow()
 *
 * const imageResponses = [
 *   `Here's an image of Google`,
 *   new Image({
 *     url: 'https://storage.googleapis.com/gweb-uniblog-publish-prod/images/' +
 *       'Search_GSA.2e16d0ba.fill-300x300.png',
 *     alt: 'Google Logo',
 *   })
 * ]
 *
 * app.intent('Default Welcome Intent', conv => {
 *   const capability = 'actions.capability.SCREEN_OUTPUT'
 *   if (conv.surface.capabilities.has(capability)) {
 *     conv.close(...imageResponses)
 *   } else {
 *     conv.ask(new NewSurface({
 *       capabilities: capability,
 *       context: 'To show you an image',
 *       notification: 'Check out this image',
 *     }))
 *   }
 * })
 *
 * // Create a Dialogflow intent with the `actions_intent_NEW_SURFACE` event
 * app.intent('Get New Surface', (conv, input, newSurface) => {
 *   if (newSurface.status === 'OK') {
 *     conv.close(...imageResponses)
 *   } else {
 *     conv.close(`Ok, I understand. You don't want to see pictures. Bye`)
 *   }
 * })
 * ```
 *
 * @public
 */
class NewSurface(init: NewSurfaceOptions.() -> Unit): SoloQuestion(IntentEnum.NEW_SURFACE){
    /**
     * @param options NewSurface options
     * @public
     */
    init {
        val options = NewSurfaceOptions()
        options.init()
        this._data(InputValueSpec.NewSurfaceValueSpec) {
            capabilities = options.capabilities
            context = options.context
            notificationTitle = options.notification
        }
    }
}
