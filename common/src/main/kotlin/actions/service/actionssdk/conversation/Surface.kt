package actions.service.actionssdk.conversation

import actions.service.actionssdk.api.GoogleActionsV2Capability
import actions.service.actionssdk.api.GoogleActionsV2Surface


enum class SurfaceCapability(val value: String) {
    ACTIONS_CAPABILITY_AUDIO_OUTPUT("actions.capability.AUDIO_OUTPUT"),
    ACTIONS_CAPABILITY_SCREEN_OUTPUT("actions.capability.SCREEN_OUTPUT"),
    ACTIONS_CAPABILITY_MEDIA_RESPONSE_AUDIO("actions.capability.MEDIA_RESPONSE_AUDIO"),
    ACTIONS_CAPABILITY_WEB_BROWSER("actions.capability.WEB_BROWSER")
}

class Surface(surface: GoogleActionsV2Surface? = null) {
    /** @public */
    var capabilities: Capabilities? = null

    init {
        this.capabilities = Capabilities(surface?.capabilities)
    }
}

class Capabilities(list: List<GoogleActionsV2Capability>? = null) {
    /**
     * List of surface capabilities of user device.
     * @public
     */
    var list: List<GoogleActionsV2Capability>? = null

    init {
        this.list = list
    }

    /**
     * Returns true if user device has a given surface capability.
     * @public
     */
    fun has(capability: SurfaceCapability): Boolean =
        list?.find { it.name == capability.value } != null
}

class AvailableSurfacesCapabilities(surfaces: List<Surface>? = null) {
    /** @public */
    var surfaces: List<Surface>? = null

    init{
        this.surfaces = surfaces
    }

    /**
     * Returns true if user has an available surface which includes all given
     * capabilities. Available surfaces capabilities may exist on surfaces other
     * than that used for an ongoing conversation.
     * @public
     */
    fun has(capability: SurfaceCapability) =
        surfaces?.find {it.capabilities?.has(capability) == true } != null

}

class AvailableSurfaces(list: List<GoogleActionsV2Surface>? = null) {
    /** @public */
    var list: List<Surface>? = null

    /** @public */
    var capabilities: AvailableSurfacesCapabilities? = null

    init {
        this.list = list?.map { Surface(it) }
        this.capabilities = AvailableSurfacesCapabilities(this.list)
    }
}

class Available(surfaces: List<GoogleActionsV2Surface>? = null) {
    /** @public */
    var surfaces: AvailableSurfaces? = null

    init {
        this.surfaces = AvailableSurfaces(surfaces)
    }
}
