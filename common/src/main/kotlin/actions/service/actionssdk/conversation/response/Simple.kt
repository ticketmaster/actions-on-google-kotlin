package actions.service.actionssdk.conversation.response


data class SimpleResponseOptions(
    /**
     * Speech to be spoken to user. SSML allowed.
     * @public
     */
    var speech: String,

    /**
     * Optional text to be shown to user
     * @public
     */
    var text: String? = null
)
