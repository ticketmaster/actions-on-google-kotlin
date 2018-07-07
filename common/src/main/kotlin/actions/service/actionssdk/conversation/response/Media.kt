package actions.service.actionssdk.conversation.response

import actions.service.actionssdk.api.GoogleActionsV2MediaResponseMediaType
import actions.service.actionssdk.api.GoogleActionsV2UiElementsImage
import actions.service.actionssdk.conversation.response.MediaObject

data class MediaObjectOptions(
    /**
     * MediaObject URL.
     * @public
     */
    var url: String? = null,

    var description: String? = null,

    /**
     * Name of the MediaObject.
     * @public
     */
    var name: String? = null,

    /**
     * Icon image.
     * @public
     */
    var icon: GoogleActionsV2UiElementsImage? = null,

    /**
     * Large image.
     * @public
     */
    var image: GoogleActionsV2UiElementsImage? = null
)

fun String.toMediaObject(): MediaObject = MediaObject(contentUrl = this)


//typealias MediaObjectString = GoogleActionsV2MediaObject | string

interface MediaResponseOptions {
    /**
     * Array of MediaObject held in the MediaResponse.
     * @public
     */
    var objects: MutableList<MediaObject>
    /**
     * Type of the media within this MediaResponse.
     * Defaults to 'AUDIO'
     * @public
     */
    var type: GoogleActionsV2MediaResponseMediaType?
}

//const isOptions = (
//options: MediaResponseOptions | MediaObjectString,
//): options is MediaResponseOptions => {
//    const test = options as MediaResponseOptions
//            return Array.isArray(test.objects)
//}


