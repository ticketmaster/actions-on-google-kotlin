package actions.service.actionssdk.api

import actions.ApiClientObjectMap
import actions.ProtoAny
import actions.service.actionssdk.conversation.response.GoogleActionsV2RichResponseItem
import actions.service.actionssdk.conversation.response.Image
import actions.service.actionssdk.conversation.response.OrderUpdate
import actions.service.actionssdk.conversation.response.SimpleResponse
import actions.service.actionssdk.conversation.response.card.Button


enum class GoogleActionsV2ConversationType {
    TYPE_UNSPECIFIED,
    NEW,
    ACTIVE
}


enum class GoogleActionsV2DeliveryAddressValueUserDecision { UNKNOWN_USER_DECISION, ACCEPTED, REJECTED }

enum class GoogleActionsV2EntitlementSkuType { TYPE_UNSPECIFIED, IN_APP, SUBSCRIPTION, APP }

enum class GoogleActionsV2MediaResponseMediaType { MEDIA_TYPE_UNSPECIFIED, AUDIO }

enum class GoogleActionsV2MediaStatusStatus { STATUS_UNSPECIFIED, FINISHED }

enum class GoogleActionsV2NewSurfaceValueStatus { NEW_SURFACE_STATUS_UNSPECIFIED, CANCELLED, OK }

enum class GoogleActionsV2OrdersActionProvidedPaymentOptionsPaymentType { PAYMENT_TYPE_UNSPECIFIED, PAYMENT_CARD, BANK, LOYALTY_PROGRAM, ON_FULFILLMENT, GIFT_CARD }

enum class GoogleActionsV2OrdersCustomerInfoOptionsCustomerInfoProperties { CUSTOMER_INFO_PROPERTY_UNSPECIFIED, EMAIL }

enum class GoogleActionsV2OrdersGoogleProvidedPaymentOptionsSupportedCardNetworks { UNSPECIFIED_CARD_NETWORK, AMEX, DISCOVER, MASTERCARD, VISA, JCB }

enum class GoogleActionsV2OrdersLineItemType { UNSPECIFIED, REGULAR, TAX, DISCOUNT, GRATUITY, DELIVERY, SUBTOTAL, FEE }

enum class GoogleActionsV2OrdersOrderLocationType { UNKNOWN, DELIVERY, BUSINESS, ORIGIN, DESTINATION, PICK_UP }

enum class GoogleActionsV2OrdersOrderUpdateActionType { UNKNOWN, VIEW_DETAILS, MODIFY, CANCEL, RETURN, EXCHANGE, EMAIL, CALL, REORDER, REVIEW, CUSTOMER_SERVICE, FIX_ISSUE }

enum class GoogleActionsV2OrdersPaymentInfoPaymentType { PAYMENT_TYPE_UNSPECIFIED, PAYMENT_CARD, BANK, LOYALTY_PROGRAM, ON_FULFILLMENT, GIFT_CARD }

enum class GoogleActionsV2OrdersPaymentMethodTokenizationParametersTokenizationType { UNSPECIFIED_TOKENIZATION_TYPE, PAYMENT_GATEWAY, DIRECT }

enum class GoogleActionsV2OrdersPriceType { UNKNOWN, ESTIMATE, ACTUAL }

enum class GoogleActionsV2OrdersRejectionInfoType { UNKNOWN, PAYMENT_DECLINED, INELIGIBLE, PROMO_NOT_APPLICABLE, UNAVAILABLE_SLOT }

enum class GoogleActionsV2OrdersTimeType { UNKNOWN, DELIVERY_DATE, ETA, RESERVATION_SLOT }

enum class GoogleActionsV2PermissionValueSpecPermissions { UNSPECIFIED_PERMISSION, NAME, DEVICE_PRECISE_LOCATION, DEVICE_COARSE_LOCATION, UPDATE }

enum class GoogleActionsV2RawInputInputType { UNSPECIFIED_INPUT_TYPE, TOUCH, VOICE, KEYBOARD }

enum class GoogleActionsV2RegisterUpdateValueStatus { REGISTER_UPDATE_STATUS_UNSPECIFIED, OK, CANCELLED }

enum class GoogleActionsV2SignInValueStatus { SIGN_IN_STATUS_UNSPECIFIED, OK, CANCELLED, ERROR }

enum class GoogleActionsV2TransactionDecisionValueUserDecision { UNKNOWN_USER_DECISION, ORDER_ACCEPTED, ORDER_REJECTED, DELIVERY_ADDRESS_UPDATED, CART_CHANGE_REQUESTED }

enum class GoogleActionsV2TransactionRequirementsCheckResultResultType { RESULT_TYPE_UNSPECIFIED, OK, USER_ACTION_REQUIRED, ASSISTANT_SURFACE_NOT_SUPPORTED, REGION_NOT_SUPPORTED }

enum class GoogleActionsV2TriggerContextTimeContextFrequency { FREQUENCY_UNSPECIFIED, DAILY, ROUTINES }

enum class GoogleActionsV2UiElementsBasicCardImageDisplayOptions { DEFAULT, WHITE, CROPPED }

enum class GoogleActionsV2UiElementsCarouselBrowseImageDisplayOptions { DEFAULT, WHITE, CROPPED }

enum class GoogleActionsV2UiElementsCarouselSelectImageDisplayOptions { DEFAULT, WHITE, CROPPED }

enum class GoogleActionsV2UiElementsOpenUrlActionUrlTypeHint { URL_TYPE_HINT_UNSPECIFIED, AMP_CONTENT }

enum class GoogleActionsV2UiElementsTableCardColumnPropertiesHorizontalAlignment { LEADING, CENTER, TRAILING }

enum class GoogleActionsV2UserPermissions { UNSPECIFIED_PERMISSION, NAME, DEVICE_PRECISE_LOCATION, DEVICE_COARSE_LOCATION, UPDATE }


open class GoogleActionsV2AppRequest {
    /**
     * Surfaces available for cross surface handoff.
     */
    var availableSurfaces: MutableList<GoogleActionsV2Surface>? = null
    /**
     * Holds session data like the conversation ID and conversation token.
     */
    var conversation: GoogleActionsV2Conversation? = null
    /**
     * Information about the device the user is using to interact with the app.
     */
    var device: GoogleActionsV2Device? = null
    /**
     * List of inputs corresponding to the expected inputs specified by the app.
     * For the initial conversation trigger, the input contains information on
     * how the user triggered the conversation.
     */
    var inputs: MutableList<GoogleActionsV2Input>? = null
    /**
     * Indicates whether the request should be handled in sandbox mode.
     */
    var isInSandbox: Boolean? = null
    /**
     * Information about the surface the user is interacting with, e.g. whether it
     * can output audio or has a screen.
     */
    var surface: GoogleActionsV2Surface? = null
    /**
     * User who initiated the conversation.
     */
    var user: GoogleActionsV2User? = null

    var sender: Sender? = null
}

data class Sender(val id: String? = null)

data class GoogleActionsV2AppResponse(
        /**
         * An opaque token that is recirculated to the app every conversation
         * turn.
         */
        var conversationToken: String? = null,
        /**
         * Custom Push Message allows developers to send structured data to Google
         * for interactions on the Assistant.
         */
        var customPushMessage: GoogleActionsV2CustomPushMessage? = null,
        /**
         * Indicates whether the app is expecting a user response. This is true when
         * the conversation is ongoing, false when the conversation is done.
         */
        var expectUserResponse: Boolean? = null,
        /**
         * List of inputs the app expects, each input can be a built-in intent, or an
         * input taking list of possible intents. Only one input is supported for now.
         */
        var expectedInputs: MutableList<GoogleActionsV2ExpectedInput>? = null,
        /**
         * Final response when the app does not expect user's input.
         */
        var finalResponse: GoogleActionsV2FinalResponse? = null,
        /**
         * Indicates whether the response should be handled in sandbox mode. This
         * bit is needed to push structured data to Google in sandbox mode.
         */
        var isInSandbox: Boolean? = null,
        /**
         * Whether to clear the persisted user_storage. If set to true, then in the
         * next interaction with the user, the user_storage field will be empty.
         */
        var resetUserStorage: Boolean? = null,
        /**
         * An opaque token controlled by the application that is persisted across
         * conversations for a particular user. If empty or unspecified, the
         * existing persisted token will be unchanged.
         * The maximum size of the string is 10k bytes.
         * If multiple dialogs are occurring concurrently for the same user, then
         * updates to this token can overwrite each other unexpectedly.
         */
        var userStorage: String? = null)

/**
 * Hold data for TransactionCheckComplete, SignInStatus, TransactionDecision, NewSurfaceResult
 */
data class ArgumentExtension(
        val `@type`: String = "",
//        val resultType: TransactionValues.ResultType = TransactionValues.ResultType.UNSPECIFIED,
        val resultType: String? = null,
        var userDecision: String = "",
        var status: String = "",
        var location: GoogleActionsV2Location? = null,
        val order: OrderUpdate? = null)


data class GoogleActionsV2Argument(
        /**
         * Specified when query pattern includes a `$org.schema.type.YesNo` type or
         * expected input has a built-in intent: `actions.intent.CONFIRMATION`.
         * NOTE: if the boolean value is missing, it represents `false`.
         */
        var boolValue: Boolean? = null,
        /**
         * Specified for the built-in intent: `actions.intent.DATETIME`.
         */
        var datetimeValue: GoogleActionsV2DateTime? = null,
        /**
         * Extension whose type depends on the argument.
         * For example, if the argument name is `SIGN_IN` for the
         * `actions.intent.SIGN_IN` intent, then this extension will
         * contain a SignInValue value.
         */
//        var extension: ApiClientObjectMap<Any>? = null,
        var extension: ArgumentExtension? = null,
        /**
         * Specified for built-in intent: \"actions.intent.NUMBER\"
         */
        var floatValue: Float? = null,
        /**
         * Specified when query pattern includes a $org.schema.type.Number type or
         * expected input has a built-in intent: \"assistant.intent.action.NUMBER\".
         */
        var intValue: String? = null,
        /**
         * Name of the argument being provided for the input.
         */
        var name: String? = null,
        /**
         * Specified when query pattern includes a $org.schema.type.Location type or
         * expected input has a built-in intent: \"actions.intent.PLACE\".
         */
        var placeValue: GoogleActionsV2Location? = null,
        /**
         * The raw text, typed or spoken, that provided the value for the argument.
         */
        var rawText: String? = null,
        /**
         * Specified when an error was encountered while computing the argument. For
         * example, the built-in intent \"actions.intent.PLACE\" can return an error
         * status if the user denied the permission to access their device location.
         */
        var status: GoogleRpcStatus? = null,
        /**
         * Specified when Google needs to pass data value in JSON format.
         */
        var structuredValue: ApiClientObjectMap<Any>? = null,
        /**
         * Specified when query pattern includes a `$org.schema.type.Text` type or
         * expected input has a built-in intent: `actions.intent.TEXT`, or
         * `actions.intent.OPTION`. Note that for the `OPTION` intent, we set the
         * `text_value` as option key, the `raw_text` above will indicate the raw
         * span in user's query.
         */
        var textValue: String? = null,

        /**** ADDED FOR KOTLIN ***/
        var resultType: String? = null,

        var userDecision: String? = null,

        var location: GoogleActionsV2Location? = null,

        var order: GoogleActionsV2OrdersOrder? = null
)

data class GoogleActionsV2Capability(
        /**
         * The name of the capability, e.g. `actions.capability.AUDIO_OUTPUT`
         */
        var name: String? = null
)

data class GoogleActionsV2ConfirmationValueSpec(
        /**
         * Configures dialog that asks for confirmation.
         */
        var dialogSpec: GoogleActionsV2ConfirmationValueSpecConfirmationDialogSpec? = null
)

data class GoogleActionsV2ConfirmationValueSpecConfirmationDialogSpec(
        /**
         * This is the question asked by confirmation sub-dialog. For example \"Are
         * you sure about that?\"
         */
        var requestConfirmationText: String? = null
)

data class GoogleActionsV2Conversation(
        /**
         * Unique ID for the multi-turn conversation. It's assigned for the first
         * turn. After that it remains the same for subsequent conversation turns
         * until the conversation is terminated.
         */
        var conversationId: String? = null,
        /**
         * Opaque token specified by the app in the last conversation turn. It can
         * be used by an app to track the conversation or to store conversation
         * related data.
         */
        var conversationToken: String? = null,
        /**
         * Type indicates the state of the conversation in its life cycle.
         */
        var type: GoogleActionsV2ConversationType? = null
)

data class GoogleActionsV2CustomPushMessage(
        /**
         * An order update updating orders placed through transaction APIs.
         */
        var orderUpdate: GoogleActionsV2OrdersOrderUpdate? = null,
        /**
         * The specified target for the push request.
         */
        var target: GoogleActionsV2CustomPushMessageTarget? = null,
        /**
         * If specified, displays a notification to the user with specified title
         * and text.
         */
        var userNotification: GoogleActionsV2UserNotification? = null
)

data class GoogleActionsV2CustomPushMessageTarget(
        /**
         * The argument to target for an intent. For V1, only one Argument is
         * supported.
         */
        var argument: GoogleActionsV2Argument? = null,
        /**
         * The intent to target.
         */
        var intent: String? = null,
        /**
         * The locale to target. Follows IETF BCP-47 language code.
         * Can be used by a multi-lingual app to target a user on a specified
         * localized app. If not specified, it will default to en-US.
         */
        var locale: String? = null,
        /**
         * The user to target.
         */
        var userId: String? = null
)

data class GoogleActionsV2DateTime(
        /**
         * Date value
         */
        var date: GoogleTypeDate? = null,
        /**
         * Time value
         */
        var time: GoogleTypeTimeOfDay? = null
)

data class GoogleActionsV2DateTimeValueSpec(
        /**
         * Control datetime prompts.
         */
        var dialogSpec: GoogleActionsV2DateTimeValueSpecDateTimeDialogSpec? = null
)

data class GoogleActionsV2DateTimeValueSpecDateTimeDialogSpec(
        /**
         * This is used to create prompt to ask for date only.
         * For example: What date are you looking for?
         */
        var requestDateText: String? = null,
        /**
         * This is used to create initial prompt by datetime sub-dialog.
         * Example question: \"What date and time do you want?\"
         */
        var requestDatetimeText: String? = null,
        /**
         * This is used to create prompt to ask for time only.
         * For example: What time?
         */
        var requestTimeText: String? = null
)

data class GoogleActionsV2DeliveryAddressValue(
        /**
         * Contains delivery address only when user agrees to share the delivery
         * address.
         */
        var location: GoogleActionsV2Location? = null,
        /**
         * User's decision regarding the request.
         */
        var userDecision: GoogleActionsV2DeliveryAddressValueUserDecision? = null
)

data class GoogleActionsV2DeliveryAddressValueSpec(
        /**
         * Configuration for delivery address dialog.
         */
        var addressOptions: GoogleActionsV2DeliveryAddressValueSpecAddressOptions? = null
) {
    fun addressOptions(init: GoogleActionsV2DeliveryAddressValueSpecAddressOptions.() -> Unit) {
        this.addressOptions = GoogleActionsV2DeliveryAddressValueSpecAddressOptions()
        this.addressOptions?.init()
    }
}

data class GoogleActionsV2DeliveryAddressValueSpecAddressOptions(
        /**
         * App can optionally pass a short text giving user a hint why delivery
         * address is requested. For example, \"Grubhub is asking your address for
         * [determining the actions.service area].\", the text in `[]` is the custom TTS
         * that should be populated here.
         */
        var reason: String? = null
)

data class GoogleActionsV2Device(
        /**
         * Represents actual device location such as lat, lng, and formatted address.
         * Requires the
         * DEVICE_COARSE_LOCATION
         * or
         * DEVICE_PRECISE_LOCATION
         * permission.
         */
        var location: GoogleActionsV2Location? = null
)

data class GoogleActionsV2DevicesAndroidApp(
        /**
         * Package name
         * Package name must be specified when specifing Android Fulfillment.
         */
        var packageName: String? = null,
        /**
         * When multiple filters are specified, any filter match will trigger the app.
         */
        var versions: MutableList<GoogleActionsV2DevicesAndroidAppVersionFilter>? = null
)

data class GoogleActionsV2DevicesAndroidAppVersionFilter(
        /**
         * Max version code, inclusive.
         * The range considered is [min_version:max_version].
         * A null range implies any version.
         * Examples:
         * To specify a single version use: [target_version:target_version].
         * To specify any version leave min_version and max_version unspecified.
         * To specify all versions until max_version, leave min_version unspecified.
         * To specify all versions from min_version, leave max_version unspecified.
         */
        var maxVersion: Float? = null,
        /**
         * Min version code or 0, inclusive.
         */
        var minVersion: Float? = null
)

data class GoogleActionsV2DialogSpec(
        /**
         * Holds helper specific dialog specs if any. For example:
         * ConfirmationDialogSpec for confirmation helper.
         */
        var extension: ApiClientObjectMap<Any>? = null
)

data class GoogleActionsV2Entitlement(
        /**
         * Only present for in-app purchase and in-app subs.
         */
        var inAppDetails: GoogleActionsV2SignedData? = null,
        /**
         * Product sku. Package name for paid app, suffix of Finsky docid for
         * in-app purchase and in-app subscription.
         * Match getSku() in Play InApp Billing API.
         */
        var sku: String? = null,
        var skuType: GoogleActionsV2EntitlementSkuType? = null
)

data class GoogleActionsV2ExpectedInput(
        /**
         * The customized prompt used to ask user for input.
         */
        var inputPrompt: GoogleActionsV2InputPrompt? = null,
        /**
         * List of intents that can be used to fulfill this input.
         * To have the Google Assistant just return the raw user input, the app
         * should ask for the `actions.intent.TEXT` intent.
         */
        var possibleIntents: MutableList<GoogleActionsV2ExpectedIntent>? = null,
        /**
         * List of phrases the app wants Google to use for speech biasing.
         * Up to 1000 phrases are allowed.
         */
        var speechBiasingHints: MutableList<String>? = null
)

interface GoogleActionsV2ExpectedIntent {
    /**
     * Additional configuration data required by a built-in intent. Possible
     * values for the built-in intents: `actions.intent.OPTION ->`
     * [google.actions.v2.OptionValueSpec], `actions.intent.CONFIRMATION ->`
     * [google.actions.v2.ConfirmationValueSpec],
     * `actions.intent.TRANSACTION_REQUIREMENTS_CHECK ->`
     * [google.actions.v2.TransactionRequirementsCheckSpec],
     * `actions.intent.DELIVERY_ADDRESS ->`
     * [google.actions.v2.DeliveryAddressValueSpec],
     * `actions.intent.TRANSACTION_DECISION ->`
     * [google.actions.v2.TransactionDecisionValueSpec],
     * `actions.intent.PLACE ->`
     * [google.actions.v2.PlaceValueSpec],
     * `actions.intent.Link ->`
     * [google.actions.v2.LinkValueSpec]
     */
    var inputValueData: ProtoAny?
    /**
     * The built-in intent name, e.g. `actions.intent.TEXT`, or intents
     * defined in the action package. If the intent specified is not a built-in
     * intent, it is only used for speech biasing and the input provided by the
     * Google Assistant will be the `actions.intent.TEXT` intent.
     */
    var intent: String?
    /**
     * Optionally, a parameter of the intent that is being requested. Only valid
     * for requested intents. Used for speech biasing.
     */
    var parameterName: String?
}

data class GoogleActionsV2ExpectedIntentData(override var inputValueData: ProtoAny? = null,
                                             override var intent: String? = null,
                                             override var parameterName: String? = null) : GoogleActionsV2ExpectedIntent

data class GoogleActionsV2FinalResponse(
        /**
         * Rich response when user is not required to provide an input.
         */
        var richResponse: GoogleActionsV2RichResponse? = null,
        /**
         * Spoken response when user is not required to provide an input.
         */
        var speechResponse: GoogleActionsV2SpeechResponse? = null
)

data class GoogleActionsV2Input(
        /**
         * A list of provided argument values for the input requested by the app.
         */
        var arguments: MutableList<GoogleActionsV2Argument>? = null,
        /**
         * Indicates the user's intent. For the first conversation turn, the intent
         * will refer to the intent of the action that is being triggered. For
         * subsequent conversation turns, the intent will be a built-in intent.
         * For example, if the expected input is `actions.intent.OPTION`, then the
         * the intent specified here will either be `actions.intent.OPTION` if the
         * Google Assistant was able to satisfy that intent, or
         * `actions.intent.TEXT` if the user provided other information.
         */
        var intent: String? = null,
        /**
         * Raw input transcription from each turn of conversation that was used to
         * provide this input.
         * Multiple conversation turns that don't involve the app may be required
         * for the assistant to provide some types of input.
         */
        var rawInputs: MutableList<GoogleActionsV2RawInput>? = null
)

data class GoogleActionsV2InputPrompt(
        /**
         * Initial prompts asking user to provide an input.
         * Only a single initial_prompt is supported.
         */
        var initialPrompts: MutableList<GoogleActionsV2SpeechResponse>? = null,
        /**
         * Prompt used to ask user when there is no input from user.
         */
        var noInputPrompts: MutableList<SimpleResponse>? = null,
        /**
         * Prompt payload.
         */
        var richInitialPrompt: GoogleActionsV2RichResponse? = null
)

data class GoogleActionsV2LinkValueSpec(
        var dialogSpec: GoogleActionsV2DialogSpec? = null,
        /**
         * Destination that the app should link to. Could be a web URL, a
         * conversational link or an Android intent. A web URL is used to handoff the
         * flow to some website. A conversational link is used to provide a deep link
         * into another AoG app. An Android intent URI is used to trigger an Android
         * intent. This requires the package_name to be specified.
         */
        var openUrlAction: GoogleActionsV2UiElementsOpenUrlAction? = null
)

data class GoogleActionsV2LinkValueSpecLinkDialogSpec(
        /**
         * The name of the app or site this request wishes to linking to.
         * The TTS will be created with the title \"Open <destination_name>\". Also
         * used during confirmation, \"Can I send you to <destination_name>?\" If we
         * know the actual title of the link that is being handed off to, we will
         * ignore this field and use the appropriate title.
         * Max 20 chars.
         */
        var destinationName: String? = null,
        /**
         * A string that is added to the end of the confirmation prompt to explain
         * why we need to link out. Example: \"navigate to pick up your coffee?\" This
         * can be appended to the confirmation prompt like \"Can I send you to Google
         * Maps to navigate to pick up your coffee?\"
         */
        var requestLinkReason: String? = null
)

data class GoogleActionsV2Location(
        /**
         * City.
         * Requires the DEVICE_PRECISE_LOCATION or
         * DEVICE_COARSE_LOCATION permission.
         */
        var city: String? = null,
        /**
         * Geo coordinates.
         * Requires the DEVICE_PRECISE_LOCATION permission.
         */
        var coordinates: GoogleTypeLatLng? = null,
        /**
         * Display address, e.g., \"1600 Amphitheatre Pkwy, Mountain View, CA 94043\".
         * Requires the DEVICE_PRECISE_LOCATION permission.
         */
        var formattedAddress: String? = null,
        /**
         * Name of the place.
         */
        var name: String? = null,
        /**
         * Notes about the location.
         */
        var notes: String? = null,
        /**
         * Phone number of the location, e.g. contact number of business location or
         * phone number for delivery location.
         */
        var phoneNumber: String? = null,
        /**
         * Postal address.
         * Requires the DEVICE_PRECISE_LOCATION or
         * DEVICE_COARSE_LOCATION permission.
         */
        var postalAddress: GoogleTypePostalAddress? = null,
        /**
         * Zip code.
         * Requires the DEVICE_PRECISE_LOCATION or
         * DEVICE_COARSE_LOCATION permission.
         */
        var zipCode: String? = null,

        var address: String? = null
)

interface GoogleActionsV2MediaObject {
    /**
     * The url pointing to the media content.
     */
    var contentUrl: String?
    /**
     * Description of this media object.
     */
    var description: String?
    /**
     * A small image icon displayed on the right from the title.
     * It's resized to 36x36 dp.
     */
    var icon: GoogleActionsV2UiElementsImage?
    /**
     * A large image, such as the cover of the album, etc.
     */
    var largeImage: GoogleActionsV2UiElementsImage?
    /**
     * Name of this media object.
     */
    var name: String?
}

interface GoogleActionsV2MediaResponse {
    /**
     * The list of media objects.
     */
    var mediaObjects: MutableList<GoogleActionsV2MediaObject>?
    /**
     * Type of the media within this response.
     */
    var mediaType: GoogleActionsV2MediaResponseMediaType?
}

data class GoogleActionsV2MediaStatus(
        /**
         * The status of the media
         */
        var status: GoogleActionsV2MediaStatusStatus? = null
)

data class GoogleActionsV2NewSurfaceValue(
        var status: GoogleActionsV2NewSurfaceValueStatus? = null
)

data class GoogleActionsV2NewSurfaceValueSpec(
        /**
         * The list of capabilities required from the surface. Eg,
         * [\"actions.capability.SCREEN_OUTPUT\"]
         */
        var capabilities: MutableList<String>? = null,
        /**
         * Context describing the content the user will receive on the new surface.
         * Eg, \"[Sure, I know of 10 that are really popular. The highest-rated one is
         * at Mount Marcy.] Is it okay if I send that to your phone?\"
         */
        var context: String? = null,
        /**
         * Title of the notification which prompts the user to continue on the new
         * surface.
         */
        var notificationTitle: String? = null
)

data class GoogleActionsV2OptionInfo(
        /**
         * A unique key that will be sent back to the agent if this response is given.
         */
        var key: String? = null,
        /**
         * A list of synonyms that can also be used to trigger this item in dialog.
         */
        var synonyms: MutableList<String>? = null
)

data class GoogleActionsV2OptionValueSpec(
        /**
         * A select with a card carousel GUI
         */
        var carouselSelect: GoogleActionsV2UiElementsCarouselSelect? = null,
        /**
         * A select with a list card GUI
         */
        var listSelect: GoogleActionsV2UiElementsListSelect? = null,
        /**
         * A simple select with no associated GUI
         */
        var simpleSelect: GoogleActionsV2SimpleSelect? = null
)

data class GoogleActionsV2OrdersActionProvidedPaymentOptions(
        /**
         * Name of the instrument displayed on the receipt.
         * Required for action-provided payment info.
         * For `PAYMENT_CARD`, this could be \"VISA-1234\".
         * For `BANK`, this could be \"Chase Checking-1234\".
         * For `LOYALTY_PROGRAM`, this could be \"Starbuck's points\".
         * For `ON_FULFILLMENT`, this could be something like \"pay on delivery\".
         */
        var displayName: String? = null,
        /**
         * Type of payment.
         * Required.
         */
        var paymentType: GoogleActionsV2OrdersActionProvidedPaymentOptionsPaymentType? = null
)

data class GoogleActionsV2OrdersCancellationInfo(
        /**
         * Reason for cancellation.
         */
        var reason: String? = null
)

data class GoogleActionsV2OrdersCart(
        /**
         * Extension to the cart based on the type of order.
         */
        var extension: ApiClientObjectMap<Any>? = null,
        /**
         * Optional id for this cart. Included as part of the
         * Cart returned back to the integrator at confirmation time.
         */
        var id: String? = null,
        /**
         * The good(s) or actions.service(s) the user is ordering. There must be at least
         * one line item.
         */
        var lineItems: MutableList<GoogleActionsV2OrdersLineItem>? = null,
        /**
         * Merchant for the cart, if different from the caller.
         */
        var merchant: GoogleActionsV2OrdersMerchant? = null,
        /**
         * Notes about this cart.
         */
        var notes: String? = null,
        /**
         * Adjustments entered by the user, e.g. gratuity.
         */
        var otherItems: MutableList<GoogleActionsV2OrdersLineItem>? = null,
        /**
         * Optional. Promotional coupons added to the cart. Eligible promotions will
         * be sent back as discount line items in proposed order.
         */
        var promotions: MutableList<GoogleActionsV2OrdersPromotion>? = null
) {

    fun merchant(init: GoogleActionsV2OrdersMerchant.() -> Unit) {
        merchant = GoogleActionsV2OrdersMerchant()
        merchant?.init()
    }

    fun lineItems(vararg init: GoogleActionsV2OrdersLineItem.() -> Unit) {
        this.lineItems = init.map {
            val item = GoogleActionsV2OrdersLineItem()
            item.it()
            item
        }.toMutableList()
    }

    fun otherItems(vararg init: GoogleActionsV2OrdersLineItem.() -> Unit) {
        this.otherItems = init.map {
            val item = GoogleActionsV2OrdersLineItem()
            item.it()
            item
        }.toMutableList()
    }
}

data class GoogleActionsV2OrdersCustomerInfo(
        /**
         * Customer email will be included and returned to the app if
         * CustomerInfoProperty.EMAIL specified in CustomerInfoOptions.
         */
        var email: String? = null
)

data class GoogleActionsV2OrdersCustomerInfoOptions(
        /**
         * List of customer info properties.
         */
        var customerInfoProperties: MutableList<GoogleActionsV2OrdersCustomerInfoOptionsCustomerInfoProperties>? = null
)

data class GoogleActionsV2OrdersFulfillmentInfo(
        /**
         * When the order will be fulfilled.
         */
        var deliveryTime: String? = null
)

data class GoogleActionsV2OrdersGenericExtension(
        /**
         * Locations associated with the order. Up to 2 locations.
         */
        var locations: MutableList<GoogleActionsV2OrdersOrderLocation>? = null,
        /**
         * Time indicator associated with the proposed order.
         */
        var time: GoogleActionsV2OrdersTime? = null
)

data class GoogleActionsV2OrdersGoogleProvidedPaymentOptions(
        /**
         * If true, billing address will be returned.
         */
        var billingAddressRequired: Boolean? = null,
        /**
         * If true, disallow prepaid cards from being used in the transaction.
         */
        var prepaidCardDisallowed: Boolean? = null,
        /**
         * The app allows cards from any card network listed here being used in
         * transaction.
         * By default, Amex, Visa, MC and Discover are supported.
         */
        var supportedCardNetworks: MutableList<GoogleActionsV2OrdersGoogleProvidedPaymentOptionsSupportedCardNetworks>? = null,
        /**
         * Required field for requesting Google provided payment instrument.
         * These tokenization parameters  will be used for generating payment token
         * for use in transaction. The app should get these parameters from their
         * payment gateway.
         */
        var tokenizationParameters: GoogleActionsV2OrdersPaymentMethodTokenizationParameters? = null
)

data class GoogleActionsV2OrdersInTransitInfo(
        /**
         * Last updated time for in transit.
         */
        var updatedTime: String? = null
)

data class GoogleActionsV2OrdersLineItem(
        /**
         * Description of the item.
         */
        var description: String? = null,
        /**
         * Extension to the line item based on its type.
         */
        var extension: ApiClientObjectMap<Any>? = null,
        /**
         * Unique id of the line item within the Cart/Order. Required.
         */
        var id: String? = null,
        /**
         * Small image associated with this item.
         */
        var image: Image? = null,
        /**
         * Name of the line item as displayed in the receipt. Required.
         */
        var name: String? = null,
        /**
         * Optional product or offer id for this item.
         */
        var offerId: String? = null,
        /**
         * Each line item should have a price, even if the price is 0. Required.
         * This is the total price as displayed on the receipt for this line
         * (i.e. unit price * quantity).
         */
        var price: GoogleActionsV2OrdersPrice? = null,
        /**
         * Number of items included.
         */
        var quantity: Int? = null,
        /**
         * Sub-line item(s). Only valid if type is `REGULAR`.
         */
        var subLines: MutableList<GoogleActionsV2OrdersLineItemSubLine>? = null,
        /**
         * Type of line item.
         */
        var type: GoogleActionsV2OrdersLineItemType? = null
) {
    fun price(init: GoogleActionsV2OrdersPrice.() -> Unit) {
        price = GoogleActionsV2OrdersPrice()
        price?.init()
    }

    fun subLines(vararg init: GoogleActionsV2OrdersLineItemSubLine.() -> Unit) {
        this.subLines = init.map {
            val subLine = GoogleActionsV2OrdersLineItemSubLine()
            subLine.it()
            subLine
        }?.toMutableList()
    }
}

data class GoogleActionsV2OrdersLineItemSubLine(
        /**
         * A generic line item (e.g. add-on).
         */
        var lineItem: GoogleActionsV2OrdersLineItem? = null,
        /**
         * A note associated with the line item.
         */
        var note: String? = null
) {
    fun lineItem(init: GoogleActionsV2OrdersLineItem.() -> Unit) {
        val item = GoogleActionsV2OrdersLineItem()
        item.init()
        this.lineItem = item
    }
}

data class GoogleActionsV2OrdersLineItemUpdate(
        /**
         * Update to the line item extension. Type must match the item's
         * existing extension type.
         */
        var extension: ApiClientObjectMap<Any>? = null,
        /**
         * New line item-level state.
         */
        var orderState: GoogleActionsV2OrdersOrderState? = null,
        /**
         * New price for the line item.
         */
        var price: GoogleActionsV2OrdersPrice? = null,
        /**
         * Reason for the change. Required for price changes.
         */
        var reason: String? = null
)

data class GoogleActionsV2OrdersMerchant(
        /**
         * Id of the merchant.
         */
        var id: String? = null,
        /**
         * User-visible name of the merchant. Required.
         */
        var name: String? = null
)

data class GoogleActionsV2OrdersOrder(
        /**
         * User-visible order id. Must be set on the initial synchronous
         * OrderUpdate/confirmation.
         */
        var actionOrderId: String? = null,
        /**
         * If requested, customer info e.g. email will be passed back to the app.
         */
        var customerInfo: GoogleActionsV2OrdersCustomerInfo? = null,
        /**
         * Reflect back the proposed order that caused the order.
         */
        var finalOrder: GoogleActionsV2OrdersProposedOrder? = null,
        /**
         * Order id assigned by Google.
         */
        var googleOrderId: String? = null,
        /**
         * Date and time the order was created.
         */
        var orderDate: String? = null,
        /**
         * Payment related info for the order.
         */
        var paymentInfo: GoogleActionsV2OrdersPaymentInfo? = null
)

data class GoogleActionsV2OrdersOrderLocation(
        /**
         * Contains actual location info.
         */
        var location: GoogleActionsV2Location? = null,
        /**
         * Address type. Determines icon and placement. Required.
         */
        var type: GoogleActionsV2OrdersOrderLocationType? = null
) {
    fun location(init: GoogleActionsV2Location.() -> Unit) {
        this.location = GoogleActionsV2Location()
        this.location?.init()
    }
}

data class GoogleActionsV2OrdersOrderOptions(
        /**
         * The app can request customer info by setting this field.
         * If set, the corresponding field will show up in ProposedOrderCard for
         * user's confirmation.
         */
        var customerInfoOptions: GoogleActionsV2OrdersCustomerInfoOptions? = null,
        /**
         * If true, delivery address is required for the associated Order.
         */
        var requestDeliveryAddress: Boolean? = null
)

data class GoogleActionsV2OrdersOrderState(
        /**
         * The user-visible string for the state. Required.
         */
        var label: String? = null,
        /**
         * State can be one of the following values:
         *
         * `CREATED`: Order was created at integrator's system.
         * `REJECTED`: Order was rejected by integrator.
         * `CONFIRMED`: Order was confirmed by the integrator and is active.
         * `CANCELLED`: User cancelled the order.
         * `IN_TRANSIT`: Order is being delivered.
         * `RETURNED`: User did a return.
         * `FULFILLED`: User received what was ordered.
         * 'CHANGE_REQUESTED': User has requested a change to the order, and
         *           the integrator is processing this change. The
         *           order should be moved to another state after the
         *           request is handled.
         *
         * Required.
         */
        var state: String? = null
)

interface GoogleActionsV2OrdersOrderUpdate {
    /**
     * Required.
     * The canonical order id referencing this order.
     * If integrators don't generate the canonical order id in their system,
     * they can simply copy over google_order_id included in order.
     */
    var actionOrderId: String?
    /**
     * Information about cancellation state.
     */
    var cancellationInfo: GoogleActionsV2OrdersCancellationInfo?
    /**
     * Information about fulfillment state.
     */
    var fulfillmentInfo: GoogleActionsV2OrdersFulfillmentInfo?
    /**
     * Id of the order is the Google-issued id.
     */
    var googleOrderId: String?
    /**
     * Information about in transit state.
     */
    var inTransitInfo: GoogleActionsV2OrdersInTransitInfo?
    /**
     * Extra data based on a custom order state or in addition to info of a
     * standard state.
     */
    var infoExtension: ApiClientObjectMap<Any>?
    /**
     * Map of line item-level changes, keyed by item id. Optional.
     */
    var lineItemUpdates: ApiClientObjectMap<GoogleActionsV2OrdersLineItemUpdate>?
    /**
     * Updated applicable management actions for the order, e.g. manage, modify,
     * contact support.
     */
    var orderManagementActions: MutableList<GoogleActionsV2OrdersOrderUpdateAction>?
    /**
     * The new state of the order.
     */
    var orderState: GoogleActionsV2OrdersOrderState?
    /**
     * Receipt for order.
     */
    var receipt: GoogleActionsV2OrdersReceipt?
    /**
     * Information about rejection state.
     */
    var rejectionInfo: GoogleActionsV2OrdersRejectionInfo?
    /**
     * Information about returned state.
     */
    var returnInfo: GoogleActionsV2OrdersReturnInfo?
    /**
     * New total price of the order
     */
    var totalPrice: GoogleActionsV2OrdersPrice?
    /**
     * When the order was updated from the app's perspective.
     */
    var updateTime: GoogleTypeTimeOfDay?
    /**
     * If specified, displays a notification to the user with the specified
     * title and text. Specifying a notification is a suggestion to
     * notify and is not guaranteed to result in a notification.
     */
    var userNotification: GoogleActionsV2OrdersOrderUpdateUserNotification?

    /**
     * Added in AoG-Kotlin
     */
    var orderDate: String?

    var locale: String?
}


data class GoogleActionsV2OrdersOrderUpdateAction(
        /**
         * Button label and link.
         */
        var button: Button? = null,
        /**
         * Type of action.
         */
        var type: GoogleActionsV2OrdersOrderUpdateActionType? = null
) {
    fun button(init: GoogleActionsV2UiElementsButton.() -> Unit) {
        this.button = Button()
        this.button?.init()
    }
}

data class GoogleActionsV2OrdersOrderUpdateUserNotification(
        /**
         * The contents of the notification.
         */
        var text: String? = null,
        /**
         * The title for the user notification.
         */
        var title: String? = null
)

data class GoogleActionsV2OrdersPaymentInfo(
        /**
         * Name of the instrument displayed on the receipt.
         */
        var displayName: String? = null,
        /**
         * Google provided payment instrument.
         */
        var googleProvidedPaymentInstrument: GoogleActionsV2OrdersPaymentInfoGoogleProvidedPaymentInstrument? = null,
        /**
         * Type of payment.
         * Required.
         */
        var paymentType: GoogleActionsV2OrdersPaymentInfoPaymentType? = null
)

data class GoogleActionsV2OrdersPaymentInfoGoogleProvidedPaymentInstrument(
        /**
         * If requested by integrator, billing address for the instrument in use
         * will be included.
         */
        var billingAddress: GoogleTypePostalAddress? = null,
        /**
         * Google provided payment instrument.
         */
        var instrumentToken: String? = null
)

data class GoogleActionsV2OrdersPaymentMethodTokenizationParameters(
        /**
         * If tokenization_type is set to `PAYMENT_GATEWAY` then the list of
         * parameters should contain payment gateway specific parameters required to
         * tokenize payment method as well as parameter with the name \"gateway\" with
         * the value set to one of the gateways that we support e.g. \"stripe\" or
         * \"braintree\".
         * A sample tokenization configuration used for Stripe in JSON format.
         * `{
         *   \"gateway\" : \"stripe\",
         *   \"stripe:publishableKey\" : \"pk_1234\",
         *   \"stripe:version\" : \"1.5\"
         * }`
         * A sample tokenization configuration used for Braintree in JSON format.
         * `{
         *   \"gateway\" : \"braintree\",
         *   \"braintree:merchantId\" : \"abc\"
         *   \"braintree:sdkVersion\" : \"1.4.0\"
         *   \"braintree:apiVersion\" : \"v1\"
         *   \"braintree:clientKey\" : \"production_a12b34\"
         *   \"braintree:authorizationFingerprint\" : \"production_a12b34\"
         * }`
         * A sample configuration used for Adyen in JSON format.
         * `{
         *   \"gateway\" : \"adyen\",
         *   \"gatewayMerchantId\" : \"gateway-merchant-id\"
         * }`
         * If tokenization_type is set to DIRECT, integrators must specify a parameter
         * named \"publicKey\" which will contain an Elliptic Curve public key using
         * the uncompressed point format and base64 encoded. This publicKey will be
         * used by Google to encrypt the payment information.
         * Example of the parameter in JSON format:
         * {
         *   \"publicKey\": \"base64encoded...\"
         * }
         */
        var parameters: ApiClientObjectMap<String>? = null,
        /**
         * Required.
         */
        var tokenizationType: GoogleActionsV2OrdersPaymentMethodTokenizationParametersTokenizationType? = null
)

data class GoogleActionsV2OrdersPaymentOptions(
        /**
         * Info for an Action-provided payment instrument for display on receipt.
         */
        var actionProvidedOptions: GoogleActionsV2OrdersActionProvidedPaymentOptions? = null,
        /**
         * Requirements for Google provided payment instrument.
         */
        var googleProvidedOptions: GoogleActionsV2OrdersGoogleProvidedPaymentOptions? = null
) {
    fun actionProvidedOptions(init: GoogleActionsV2OrdersActionProvidedPaymentOptions.() -> Unit) {
        this.actionProvidedOptions = GoogleActionsV2OrdersActionProvidedPaymentOptions()
        this.actionProvidedOptions?.init()
    }

    fun googleProvidedOptions(init: GoogleActionsV2OrdersGoogleProvidedPaymentOptions.() -> Unit) {
        this.googleProvidedOptions = GoogleActionsV2OrdersGoogleProvidedPaymentOptions()
        this.googleProvidedOptions?.init()

    }
}

data class GoogleActionsV2OrdersPresentationOptions(
        /**
         * call_to_action can be one of the following values:
         *
         * `PLACE_ORDER`: Used for placing an order.
         * `PAY`: Used for a payment.
         * `BUY`: Used for a purchase.
         * `SEND`: Used for a money transfer.
         * `BOOK`: Used for a booking.
         * `RESERVE`: Used for reservation.
         * `SCHEDULE`: Used for scheduling an appointment.
         * `SUBSCRIBE`: Used for subscription.
         *
         * call_to_action refers to the action verb which best describes this order.
         * This will be used in various places like prompt, suggestion chip etc while
         * proposing the order to the user.
         */
        var callToAction: String? = null
)

data class GoogleActionsV2OrdersPrice(
        /**
         * Monetary amount. Required.
         */
        var amount: GoogleTypeMoney? = null,
        /**
         * Type of price. Required.
         */
        var type: GoogleActionsV2OrdersPriceType? = null
) {
    fun amount(init: GoogleTypeMoney.() -> Unit) {
        amount = GoogleTypeMoney()
        amount?.init()
    }
}

data class GoogleActionsV2OrdersPromotion(
        /**
         * Required. Coupon code understood by 3P. For ex: GOOGLE10.
         */
        var coupon: String? = null
)

data class GoogleActionsV2OrdersProposedOrder(
        /**
         * User's items.
         */
        var cart: GoogleActionsV2OrdersCart? = null,
        /**
         * Extension to the proposed order based on the kind of order.
         * For example, if the order includes a location then this extension will
         * contain a OrderLocation value.
         */
        var extension: ProtoAny? = null, //ApiClientObjectMap<Any>? = null,
        /**
         * Optional id for this ProposedOrder. Included as part of the
         * ProposedOrder returned back to the integrator at confirmation time.
         */
        var id: String? = null,
        /**
         * Image associated with the proposed order.
         */
        var image: Image? = null,
        /**
         * Fees, adjustments, subtotals, etc.
         */
        var otherItems: MutableList<GoogleActionsV2OrdersLineItem>? = null,
        /**
         * A link to the terms of actions.service that apply to this proposed order.
         */
        var termsOfServiceUrl: String? = null,
        /**
         * Total price of the proposed order. If of type `ACTUAL`, this is the amount
         * the caller will charge when the user confirms the proposed order.
         */
        var totalPrice: GoogleActionsV2OrdersPrice? = null
) {
    fun cart(init: GoogleActionsV2OrdersCart.() -> Unit) {
        if (cart == null) {
            cart = GoogleActionsV2OrdersCart()
        }
        cart?.init()
    }

    fun totalPrice(init: GoogleActionsV2OrdersPrice.() -> Unit) {
        if (totalPrice == null) {
            totalPrice = GoogleActionsV2OrdersPrice()
        }
        totalPrice?.init()
    }
    fun extension(init: ProtoAny.() -> Unit) {
        if (extension == null) {
            extension = ProtoAny()
        }
        extension?.init()
    }
    fun image(init: Image.() -> Unit) {
        if (image == null) {
            image = Image()
        }
        image?.init()
    }
}

fun order(init: GoogleActionsV2OrdersProposedOrder.() -> Unit): GoogleActionsV2OrdersProposedOrder {
    val order = GoogleActionsV2OrdersProposedOrder()
    order.init()
    return order
}

data class GoogleActionsV2OrdersReceipt(
        /**
         * Confirmed order id when order has been received by the integrator. This is
         * the canonical order id used in integrator's system referencing the order
         * and may subsequently be used to identify the order as `action_order_id`.
         */
        var confirmedActionOrderId: String? = null,
        /**
         * Optional.
         * The user facing id referencing to current order, which will show up in the
         * receipt card if present. This should be the id that usually appears on
         * a printed receipt or receipt sent to user's email.
         * User should be able to use this id referencing her order for customer
         * actions.service provided by integrators.
         * Note that this field must be populated if integrator does generate
         * user facing id for an order with a printed receipt / email receipt.
         */
        var userVisibleOrderId: String? = null
)

data class GoogleActionsV2OrdersRejectionInfo(
        /**
         * Reason for the error.
         */
        var reason: String? = null,
        /**
         * Rejection type.
         */
        var type: GoogleActionsV2OrdersRejectionInfoType? = null
)

data class GoogleActionsV2OrdersReturnInfo(
        /**
         * Reason for return.
         */
        var reason: String? = null
)

data class GoogleActionsV2OrdersTime(
        /**
         * ISO 8601 representation of time indicator: could be a duration, date or
         * exact datetime.
         */
        var timeIso8601: String? = null,
        /**
         * Type of time indicator.
         */
        var type: GoogleActionsV2OrdersTimeType? = null
)

data class GoogleActionsV2PackageEntitlement(
        /**
         * List of entitlements for a given app
         */
        var entitlements: MutableList<GoogleActionsV2Entitlement>? = null,
        /**
         * Should match the package name in action package
         */
        var packageName: String? = null
)

data class GoogleActionsV2PermissionValueSpec(
        /**
         * The context why agent needs to request permission.
         */
        var optContext: String? = null,
        /**
         * List of permissions requested by the agent.
         */
        var permissions: MutableList<GoogleActionsV2PermissionValueSpecPermissions>? = null,
        /**
         * Additional information needed to fulfill update permission request.
         */
        var updatePermissionValueSpec: GoogleActionsV2UpdatePermissionValueSpec? = null
)

data class GoogleActionsV2PlaceValueSpec(
        /**
         * Speech configuration for askForPlace dialog. The extension should be used
         * to define the PlaceDialogSpec configuration.
         */
        var dialogSpec: GoogleActionsV2DialogSpec? = null
)

data class GoogleActionsV2PlaceValueSpecPlaceDialogSpec(
        /**
         * This is the context for seeking permission to access various user related
         * data if the user prompts for personal location during the sub-dialog like
         * \"Home\", \"Work\" or \"Dad's house\". For example \"*To help you find
         * juice stores*, I just need to check your location. Can I get that from
         * Google?\". The first part of this permission prompt is configurable.
         */
        var permissionContext: String? = null,
        /**
         * This is the initial prompt by AskForPlace sub-dialog. For example \"What
         * place do you want?\"
         */
        var requestPrompt: String? = null
)

data class GoogleActionsV2RawInput(
        /**
         * Indicates how the user provided this input: a typed response, a voice
         * response, unspecified, etc.
         */
        var inputType: GoogleActionsV2RawInputInputType? = null,
        /**
         * Typed or spoken input from the end user.
         */
        var query: String? = null
)

data class GoogleActionsV2RegisterUpdateValue(
        /**
         * The status of the registering the update requested by the app.
         */
        var status: GoogleActionsV2RegisterUpdateValueStatus? = null
)

data class GoogleActionsV2RegisterUpdateValueSpec(
        /**
         * The list of arguments to necessary to fulfill an update.
         */
        var arguments: MutableList<GoogleActionsV2Argument>? = null,
        /**
         * The intent that the user wants to get updates from.
         */
        var intent: String? = null,
        /**
         * The trigger context that defines how the update will be triggered.
         * This may modify the dialog in order to narrow down the user's preferences
         * for getting his or her updates.
         */
        var triggerContext: GoogleActionsV2TriggerContext? = null
)

interface GoogleActionsV2RichResponse {
    /**
     * A list of UI elements which compose the response
     * The items must meet the following requirements:
     * 1. The first item must be a SimpleResponse
     * 2. At most two SimpleResponse
     * 3. At most one card (e.g. BasicCard or
     *  StructuredResponse or
     *  MediaResponse
     * 4. Cards may not be used if an actions.intent.OPTION intent is used
     *  ie ListSelect or
     *     CarouselSelect
     */
    var items: MutableList<GoogleActionsV2RichResponseItem>?
    /**
     * An additional suggestion chip that can link out to the associated app
     * or site.
     */
    var linkOutSuggestion: GoogleActionsV2UiElementsLinkOutSuggestion?
    /**
     * A list of suggested replies. These will always appear at the end of the
     * response. If used in a FinalResponse,
     * they will be ignored.
     */
    var suggestions: MutableList<GoogleActionsV2UiElementsSuggestion>?
}

data class GoogleActionsV2SignInValue(
        /**
         * The status of the sign in requested by the app.
         */
        var status: GoogleActionsV2SignInValueStatus? = null
)

data class GoogleActionsV2SignInValueSpec(
        /**
         * The optional context why the app needs to ask the user to sign in, as a
         * prefix of a prompt for user consent, e.g. \"To track your exercise\", or
         * \"To check your account balance\".
         */
        var optContext: String? = null
)

data class GoogleActionsV2SignedData(
        /**
         * Matches IN_APP_DATA_SIGNATURE from getPurchases() method in Play InApp
         * Billing API.
         */
        var inAppDataSignature: String? = null,
        /**
         * Match INAPP_PURCHASE_DATA
         * from getPurchases() method. Contains all inapp purchase data in JSON format
         * See details in table 6 of
         * https://developer.android.com/google/play/billing/billing_reference.html.
         */
        var inAppPurchaseData: ApiClientObjectMap<Any>? = null
)

interface GoogleActionsV2SimpleResponse {
    /**
     * Optional text to display in the chat bubble. If not given, a display
     * rendering of the text_to_speech or ssml above will be used. Limited to 640
     * chars.
     */
    var displayText: String?
    /**
     * Structured spoken response to the user in the SSML format, e.g.
     * `<speak> Say animal name after the sound.  <audio src =
     * 'https://www.pullstring.com/moo.mps' />, what’s the animal?  </speak>`.
     * Mutually exclusive with text_to_speech.
     */
    var ssml: String?
    /**
     * Plain text of the speech output, e.g., \"where do you want to go?\"
     * Mutually exclusive with ssml.
     */
    var textToSpeech: String?
}

data class GoogleActionsV2SimpleSelect(
        /**
         * List of items users should select from.
         */
        var items: MutableList<GoogleActionsV2SimpleSelectItem>? = null
)

data class GoogleActionsV2SimpleSelectItem(
        /**
         * Item key and synonyms.
         */
        var optionInfo: GoogleActionsV2OptionInfo? = null,
        /**
         * Title of the item. It will act as synonym if it's provided.
         * Optional
         */
        var title: String? = null
)

data class GoogleActionsV2SpeechResponse(
        /**
         * Structured spoken response to the user in the SSML format, e.g.
         * \"<speak> Say animal name after the sound.  <audio src =
         * 'https://www.pullstring.com/moo.mps' />, what’s the animal?  </speak>\".
         * Mutually exclusive with text_to_speech.
         */
        var ssml: String? = null,
        /**
         * Plain text of the speech output, e.g., \"where do you want to go?\"/
         */
        var textToSpeech: String? = null
)

data class GoogleActionsV2StructuredResponse(
        /**
         * App provides an order update (e.g.
         * Receipt) after receiving the order.
         */
        var orderUpdate: GoogleActionsV2OrdersOrderUpdate? = null
)

data class GoogleActionsV2Surface(
        /**
         * A list of capabilities the surface supports at the time of the request
         * e.g. `actions.capability.AUDIO_OUTPUT`
         */
        var capabilities: MutableList<GoogleActionsV2Capability>? = null
)

data class GoogleActionsV2TransactionDecisionValue(
        /**
         * If `check_result` is NOT `ResultType.OK`, the rest of the fields in
         * this message should be ignored.
         */
        var checkResult: MutableList<GoogleActionsV2TransactionRequirementsCheckResult>? = null,
        /**
         * If user requests for delivery address update, this field includes the
         * new delivery address. This field will be present only when `user_decision`
         * is `DELIVERY_ADDRESS_UPDATED`.
         */
        var deliveryAddress: GoogleActionsV2Location? = null,
        /**
         * The order that user has approved. This field will be present only when
         * `user_decision` is `ORDER_ACCEPTED`.
         */
        var order: GoogleActionsV2OrdersOrder? = null,
        /**
         * User decision regarding the proposed order.
         */
        var userDecision: GoogleActionsV2TransactionDecisionValueUserDecision? = null
)

data class GoogleActionsV2TransactionDecisionValueSpec(
        /**
         * Options associated with the order.
         */
        var orderOptions: GoogleActionsV2OrdersOrderOptions? = null,
        /**
         * Payment options for this order, or empty if no payment
         * is associated with the order.
         */
        var paymentOptions: GoogleActionsV2OrdersPaymentOptions? = null,
        /**
         * Options used to customize order presentation to the user.
         */
        var presentationOptions: GoogleActionsV2OrdersPresentationOptions? = null,
        /**
         * The proposed order that's ready for user to approve.
         */
        var proposedOrder: GoogleActionsV2OrdersProposedOrder? = null
) {

    fun orderOptions(init: GoogleActionsV2OrdersOrderOptions.() -> Unit) {
        this.orderOptions = GoogleActionsV2OrdersOrderOptions()
        this.orderOptions?.init()
    }

    fun paymentOptions(init: GoogleActionsV2OrdersPaymentOptions.() -> Unit) {
        this.paymentOptions = GoogleActionsV2OrdersPaymentOptions()
        this.paymentOptions?.init()
    }
}

data class GoogleActionsV2TransactionRequirementsCheckResult(
        /**
         * Result of the operation.
         */
        var resultType: GoogleActionsV2TransactionRequirementsCheckResultResultType? = null
)

data class GoogleActionsV2TransactionRequirementsCheckSpec(
        /**
         * Options associated with the order.
         */
        var orderOptions: GoogleActionsV2OrdersOrderOptions? = null,
        /**
         * Payment options for this Order, or empty if no payment
         * is associated with the Order.
         */
        var paymentOptions: GoogleActionsV2OrdersPaymentOptions? = null
)

data class GoogleActionsV2TriggerContext(
        /**
         * The time context for which the update can be triggered.
         */
        var timeContext: GoogleActionsV2TriggerContextTimeContext? = null
)

data class GoogleActionsV2TriggerContextTimeContext(
        /**
         * The high-level frequency of the recurring update.
         */
        var frequency: GoogleActionsV2TriggerContextTimeContextFrequency? = null
)

interface GoogleActionsV2UiElementsBasicCard {
    /**
     * Buttons.
     * Currently at most 1 button is supported.
     * Optional.
     */
    var buttons: MutableList<GoogleActionsV2UiElementsButton>?
    /**
     * Body text of the card.
     * Supports a limited set of markdown syntax for formatting.
     * Required, unless image is present.
     */
    var formattedText: String?
    /**
     * A hero image for the card. The height is fixed to 192dp.
     * Optional.
     */
    var image: GoogleActionsV2UiElementsImage?
    /**
     * Type of image display option. Optional.
     */
    var imageDisplayOptions: GoogleActionsV2UiElementsBasicCardImageDisplayOptions?
    /**
     * Optional.
     */
    var subtitle: String?
    /**
     * Overall title of the card.
     * Optional.
     */
    var title: String?
}

interface GoogleActionsV2UiElementsButton {
    /**
     * Action to take when a user taps on the button.
     * Required.
     */
    var openUrlAction: GoogleActionsV2UiElementsOpenUrlAction?
    /**
     * Title of the button.
     * Required.
     */
    var title: String?

    fun openUrlAction(init: GoogleActionsV2UiElementsOpenUrlAction.() -> Unit) {
        this.openUrlAction = GoogleActionsV2UiElementsOpenUrlAction()
        this.openUrlAction?.init()
    }
}

interface GoogleActionsV2UiElementsCarouselBrowse {
    /**
     * Type of image display option.
     * Optional.
     */
    var imageDisplayOptions: GoogleActionsV2UiElementsCarouselBrowseImageDisplayOptions?
    /**
     * Min: 2. Max: 10.
     */
    var items: MutableList<GoogleActionsV2UiElementsCarouselBrowseItem>?
}

interface GoogleActionsV2UiElementsCarouselBrowseItem {
    /**
     * Description of the carousel item.
     * Optional.
     */
    var description: String?
    /**
     * Footer text for the carousel item, displayed below the description.
     * Single line of text, truncated with an ellipsis.
     * Optional.
     */
    var footer: String?
    /**
     * Hero image for the carousel item.
     * Optional.
     */
    var image: GoogleActionsV2UiElementsImage?
    /**
     * URL of the document associated with the carousel item.
     * The document can contain HTML content or, if \"url_type_hint\" is set to
     * AMP_CONTENT, AMP content.
     * Required.
     */
    var openUrlAction: GoogleActionsV2UiElementsOpenUrlAction?
    /**
     * Title of the carousel item.
     * Required.
     */
    var title: String?
}

data class GoogleActionsV2UiElementsCarouselSelect(
        /**
         * Type of image display option. Optional.
         */
        var imageDisplayOptions: GoogleActionsV2UiElementsCarouselSelectImageDisplayOptions? = null,
        /**
         * min: 2 max: 10
         */
        var items: MutableList<GoogleActionsV2UiElementsCarouselSelectCarouselItem>? = null
)

data class GoogleActionsV2UiElementsCarouselSelectCarouselItem(
        /**
         * Body text of the card.
         */
        var description: String? = null,
        /**
         * Optional.
         */
        var image: GoogleActionsV2UiElementsImage? = null,
        /**
         * See google.actions.v2.OptionInfo
         * for details.
         * Required.
         */
        var optionInfo: GoogleActionsV2OptionInfo? = null,
        /**
         * Title of the carousel item. When tapped, this text will be
         * posted back to the conversation verbatim as if the user had typed it.
         * Each title must be unique among the set of carousel items.
         * Required.
         */
        var title: String? = null
)

interface GoogleActionsV2UiElementsImage {
    /**
     * A text description of the image to be used for accessibility, e.g. screen
     * readers.
     * Required.
     */
    var accessibilityText: String?
    /**
     * The height of the image in pixels.
     * Optional.
     */
    var height: Int?
    /**
     * The source url of the image. Images can be JPG, PNG and GIF (animated and
     * non-animated). For example,`https://www.agentx.com/logo.png`. Required.
     */
    var url: String?
    /**
     * The width of the image in pixels.
     * Optional.
     */
    var width: Int?
}

interface GoogleActionsV2UiElementsLinkOutSuggestion {
    /**
     * The name of the app or site this chip is linking to. The chip will be
     * rendered with the title \"Open <destination_name>\". Max 20 chars.
     * Required.
     */
    var destinationName: String?
    /**
     * The URL of the App or Site to open when the user taps the suggestion chip.
     * Ownership of this App/URL must be validated in the Actions on Google
     * developer  console, or the suggestion will not be shown to the user.
     * Open URL Action supports http, https and intent URLs.
     * For Intent URLs refer to:
     * https://developer.chrome.com/multidevice/android/intents
     */
    var openUrlAction: GoogleActionsV2UiElementsOpenUrlAction?
    /**
     * Deprecated. Use OpenUrlAction instead.
     */
    var url: String?
}

data class GoogleActionsV2UiElementsListSelect(
        /**
         * min: 2 max: 30
         */
        var items: MutableList<GoogleActionsV2UiElementsListSelectListItem>? = null,
        /**
         * Overall title of the list.
         * Optional.
         */
        var title: String? = null
)

data class GoogleActionsV2UiElementsListSelectListItem(
        /**
         * Main text describing the item.
         * Optional.
         */
        var description: String? = null,
        /**
         * Square image.
         * Optional.
         */
        var image: GoogleActionsV2UiElementsImage? = null,
        /**
         * Information about this option. See google.actions.v2.OptionInfo
         * for details.
         * Required.
         */
        var optionInfo: GoogleActionsV2OptionInfo? = null,
        /**
         * Title of the list item. When tapped, this text will be
         * posted back to the conversation verbatim as if the user had typed it.
         * Each title must be unique among the set of list items.
         * Required.
         */
        var title: String? = null
)

data class GoogleActionsV2UiElementsOpenUrlAction(
    /**
     * Information about the Android App if the URL is expected to be
     * fulfilled by an Android App.
     */
    var androidApp: GoogleActionsV2DevicesAndroidApp? = null,
    /**
     * The url field which could be any of:
     * - http/https urls for opening an App-linked App or a webpage
     */
    var url: String? = null,
    /**
     * Indicates a hint for the url type.
     */
    var urlTypeHint: GoogleActionsV2UiElementsOpenUrlActionUrlTypeHint? = null
)

data class GoogleActionsV2UiElementsSuggestion(
        /**
         * The text shown the in the suggestion chip. When tapped, this text will be
         * posted back to the conversation verbatim as if the user had typed it.
         * Each title must be unique among the set of suggestion chips.
         * Max 25 chars
         * Required
         */
        var title: String? = null
)

interface GoogleActionsV2UiElementsTableCard {
    /**
     * Buttons.
     * Currently at most 1 button is supported.
     * Optional.
     */
    var buttons: MutableList<GoogleActionsV2UiElementsButton>?
    /**
     * Headers and alignment of columns.
     */
    var columnProperties: MutableList<GoogleActionsV2UiElementsTableCardColumnProperties>?
    /**
     * Image associated with the table. Optional.
     */
    var image: GoogleActionsV2UiElementsImage?
    /**
     * Row data of the table. The first 3 rows are guaranteed to be shown but
     * others might be cut on certain surfaces. Please test with the simulator to
     * see which rows will be shown for a given surface. On surfaces that support
     * the WEB_BROWSER capability, you can point the user to
     * a web page with more data.
     */
    var rows: MutableList<GoogleActionsV2UiElementsTableCardRow>?
    /**
     * Subtitle for the table. Optional.
     */
    var subtitle: String?
    /**
     * Overall title of the table. Optional but must be set if subtitle is set.
     */
    var title: String?
}

data class GoogleActionsV2UiElementsTableCardCell(
        /**
         * Text content of the cell.
         */
        var text: String? = null
)

interface GoogleActionsV2UiElementsTableCardColumnProperties {
    /**
     * Header text for the column.
     */
    var header: String?
    /**
     * Horizontal alignment of content w.r.t column. If unspecified, content
     * will be aligned to the leading edge.
     */
    var horizontalAlignment: GoogleActionsV2UiElementsTableCardColumnPropertiesHorizontalAlignment?
}

data class GoogleActionsV2UiElementsTableCardRow(
        /**
         * Cells in this row. The first 3 cells are guaranteed to be shown but
         * others might be cut on certain surfaces. Please test with the simulator
         * to see which cells will be shown for a given surface.
         */
        var cells: MutableList<GoogleActionsV2UiElementsTableCardCell>? = null,
        /**
         * Indicates whether there should be a divider after each row.
         */
        var dividerAfter: Boolean? = null
)

data class GoogleActionsV2UpdatePermissionValueSpec(
        /**
         * The list of arguments necessary to fulfill an update.
         */
        var arguments: MutableList<GoogleActionsV2Argument>? = null,
        /**
         * The intent that the user wants to get updates from.
         */
        var intent: String? = null
)

data class GoogleActionsV2User(
        /**
         * An OAuth2 token that identifies the user in your system. Only
         * available if Account Linking
         * configuration is defined in the action package and the user links their
         * account.
         */
        var accessToken: String? = null,
        /**
         * Token representing the user's identity.
         * This is a Json web token including encoded profile. The definition is at
         * https://developers.google.com/identity/protocols/OpenIDConnect#obtainuserinfo.
         */
        var idToken: String? = null,
        /**
         * The timestamp of the last interaction with this user.
         * This field will be omitted if the user has not interacted with the agent
         * before.
         */
        var lastSeen: String? = null,
        /**
         * Primary locale setting of the user making the request.
         * Follows IETF BCP-47 language code
         * http://www.rfc-editor.org/rfc/bcp/bcp47.txt
         * However, the script subtag is not included.
         */
        var locale: String? = null,
        /**
         * List of user entitlements for every package name listed in action package,
         * if any.
         */
        var packageEntitlements: MutableList<GoogleActionsV2PackageEntitlement>? = null,
        /**
         * Contains permissions granted by user to this app.
         */
        var permissions: MutableList<GoogleActionsV2UserPermissions>? = null,
        /**
         * Information about the end user. Some fields are only available if the user
         * has given permission to provide this information to the app.
         */
        var profile: GoogleActionsV2UserProfile? = null,
        /**
         * Unique ID for the end user.
         */
        var userId: String? = null,
        /**
         * An opaque token supplied by the application that is persisted across
         * conversations for a particular user.
         * The maximum size of the string is 10k characters.
         */
        var userStorage: String? = null
)


data class GoogleActionsV2UserNotification(
        /**
         * The content of the notification.
         */
        var text: String? = null,
        /**
         * The title for the notification.
         */
        var title: String? = null
)

data class GoogleActionsV2UserProfile(
        /**
         * The user's full name as specified in their Google account.
         * Requires the NAME permission.
         */
        var displayName: String? = null,
        /**
         * The user's last name as specified in their Google account.
         * Note that this field could be empty.
         * Requires the NAME permission.
         */
        var familyName: String? = null,
        /**
         * The user's first name as specified in their Google account.
         * Requires the NAME permission.
         */
        var givenName: String? = null
)


data class GoogleRpcStatus(
        /**
         * The status code, which should be an enum value of google.rpc.Code.
         */
        var code: Int? = null,
        /**
         * A list of messages that carry the error details.  There is a common set of
         * message types for APIs to use.
         */
        var details: MutableList<ApiClientObjectMap<Any>>? = null,
        /**
         * A developer-facing error message, which should be in English. Any
         * user-facing error message should be localized and sent in the
         * google.rpc.Status.details field, or localized by the client.
         */
        var message: String? = null
)

data class GoogleTypeDate(
        /**
         * Day of month. Must be from 1 to 31 and valid for the year and month, or 0
         * if specifying a year/month where the day is not significant.
         */
        var day: Int? = null,
        /**
         * Month of year. Must be from 1 to 12, or 0 if specifying a date without a
         * month.
         */
        var month: Int? = null,
        /**
         * Year of date. Must be from 1 to 9999, or 0 if specifying a date without
         * a year.
         */
        var year: Int? = null
)

data class GoogleTypeLatLng(
        /**
         * The latitude in degrees. It must be in the range [-90.0, +90.0].
         */
        var latitude: Double? = null,
        /**
         * The longitude in degrees. It must be in the range [-180.0, +180.0].
         */
        var longitude: Double? = null
)

data class GoogleTypeMoney(
        /**
         * The 3-letter currency code defined in ISO 4217.
         */
        var currencyCode: String? = null,
        /**
         * Number of nano (10^-9) units of the amount.
         * The value must be between -999,999,999 and +999,999,999 inclusive.
         * If `units` is positive, `nanos` must be positive or zero.
         * If `units` is zero, `nanos` can be positive, zero, or negative.
         * If `units` is negative, `nanos` must be negative or zero.
         * For example $-1.75 is represented as `units`=-1 and `nanos`=-750,000,000.
         */
        var nanos: Int? = null,
        /**
         * The whole units of the amount.
         * For example if `currencyCode` is `\"USD\"`, then 1 unit is one US dollar.
         */
        var units: Int? = null
)

data class GoogleTypePostalAddress(
        /**
         * Unstructured address lines describing the lower levels of an address.
         *
         * Because values in address_lines do not have type information and may
         * sometimes contain multiple values in a single field (e.g.
         * \"Austin, TX\"), it is important that the line order is clear. The order of
         * address lines should be \"envelope order\" for the country/region of the
         * address. In places where this can vary (e.g. Japan), address_language is
         * used to make it explicit (e.g. \"ja\" for large-to-small ordering and
         * \"ja-Latn\" or \"en\" for small-to-large). This way, the most specific line
         * of an address can be selected based on the language.
         *
         * The minimum permitted structural representation of an address consists
         * of a region_code with all remaining information placed in the
         * address_lines. It would be possible to format such an address very
         * approximately without geocoding, but no semantic reasoning could be
         * made about any of the address components until it was at least
         * partially resolved.
         *
         * Creating an address only containing a region_code and address_lines, and
         * then geocoding is the recommended way to handle completely unstructured
         * addresses (as opposed to guessing which parts of the address should be
         * localities or administrative areas).
         */
        var addressLines: MutableList<String>? = null,
        /**
         * Optional. Highest administrative subdivision which is used for postal
         * addresses of a country or region.
         * For example, this can be a state, a province, an oblast, or a prefecture.
         * Specifically, for Spain this is the province and not the autonomous
         * community (e.g. \"Barcelona\" and not \"Catalonia\").
         * Many countries don't use an administrative area in postal addresses. E.g.
         * in Switzerland this should be left unpopulated.
         */
        var administrativeArea: String? = null,
        /**
         * Optional. BCP-47 language code of the contents of this address (if
         * known). This is often the UI language of the input form or is expected
         * to match one of the languages used in the address' country/region, or their
         * transliterated equivalents.
         * This can affect formatting in certain countries, but is not critical
         * to the correctness of the data and will never affect any validation or
         * other non-formatting related operations.
         *
         * If this value is not known, it should be omitted (rather than specifying a
         * possibly incorrect default).
         *
         * Examples: \"zh-Hant\", \"ja\", \"ja-Latn\", \"en\".
         */
        var languageCode: String? = null,
        /**
         * Optional. Generally refers to the city/town portion of the address.
         * Examples: US city, IT comune, UK post town.
         * In regions of the world where localities are not well defined or do not fit
         * into this structure well, leave locality empty and use address_lines.
         */
        var locality: String? = null,
        /**
         * Optional. The name of the organization at the address.
         */
        var organization: String? = null,
        /**
         * Optional. Postal code of the address. Not all countries use or require
         * postal codes to be present, but where they are used, they may trigger
         * additional validation with other parts of the address (e.g. state/zip
         * validation in the U.S.A.).
         */
        var postalCode: String? = null,
        /**
         * Optional. The recipient at the address.
         * This field may, under certain circumstances, contain multiline information.
         * For example, it might contain \"care of\" information.
         */
        var recipients: MutableList<String>? = null,
        /**
         * Required. CLDR region code of the country/region of the address. This
         * is never inferred and it is up to the user to ensure the value is
         * correct. See http://cldr.unicode.org/ and
         * http://www.unicode.org/cldr/charts/30/supplemental/territory_information.html
         * for details. Example: \"CH\" for Switzerland.
         */
        var regionCode: String? = null,
        /**
         * The schema revision of the `PostalAddress`. This must be set to 0, which is
         * the latest revision.
         *
         * All new revisions **must** be backward compatible with old revisions.
         */
        var revision: Int? = null,
        /**
         * Optional. Additional, country-specific, sorting code. This is not used
         * in most regions. Where it is used, the value is either a string like
         * \"CEDEX\", optionally followed by a number (e.g. \"CEDEX 7\"), or just a
         * number alone, representing the \"sector code\" (Jamaica), \"delivery area
         * indicator\" (Malawi) or \"post office indicator\" (e.g. Côte d'Ivoire).
         */
        var sortingCode: String? = null,
        /**
         * Optional. Sublocality of the address.
         * For example, this can be neighborhoods, boroughs, districts.
         */
        var sublocality: String? = null
)

data class GoogleTypeTimeOfDay(
        /**
         * Hours of day in 24 hour format. Should be from 0 to 23. An API may choose
         * to allow the value \"24:00:00\" for scenarios like business closing time.
         */
        var hours: Int? = null,
        /**
         * Minutes of hour of day. Must be from 0 to 59.
         */
        var minutes: Int? = null,
        /**
         * Fractions of seconds in nanoseconds. Must be from 0 to 999,999,999.
         */
        var nanos: Double? = null,
        /**
         * Seconds of minutes of the time. Must normally be from 0 to 59. An API may
         * allow the value 60 if it allows leap-seconds.
         */
        var seconds: Int? = null
)
