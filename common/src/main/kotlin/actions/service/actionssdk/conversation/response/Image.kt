package actions.service.actionssdk.conversation.response


interface ImageOptions {
    /**
     * Image source URL.
     * @public
     */
    var url: String

    /**
     * Text to replace for image for accessibility.
     * @public
     */
    var alt: String

    /**
     * Height of the image.
     * @public
     */
    var height: Int?

    /**
     * Width of the image.
     * @public
     */
    var width: Int?
}

