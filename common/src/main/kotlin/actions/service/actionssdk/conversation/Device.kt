package actions.service.actionssdk.conversation

import actions.service.actionssdk.api.GoogleActionsV2Device
import actions.service.actionssdk.api.GoogleActionsV2Location


class Device(device: GoogleActionsV2Device? = null) {
        /**
         * If granted permission to device's location in previous intent, returns device's
         * location (see {@link Permission|conv.ask(new Permission)}).
         * @public
         */
        var location: GoogleActionsV2Location? = null

        init {
            location = device?.location
        }

}
