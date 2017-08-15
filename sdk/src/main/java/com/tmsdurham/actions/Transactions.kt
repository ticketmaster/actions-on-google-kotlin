package com.tmsdurham.actions

import com.ticketmaster.apiai.google.GoogleData
import com.ticketmaster.apiai.google.price
import com.ticketmaster.apiai.google.totalPrice


val ORDER_LOCATION_LIMIT = 2
val GENERIC_EXTENSION_TYPE = "type.googleapis.com/google.actions.v2.orders.GenericExtension"


/**
 * Order rejection info.
 * @typedef {Object} RejectionInfo
 * @property {string} type - One of Transaction.RejectionType.
 * @property {string} reason - Reason for the order rejection.
 */
data class RejectionInfo(var type: String? = null, var reason: String? = null)

/**
 * Order receipt info.
 * @typedef {Object} ReceiptInfo
 * @property {string} confirmedActionOrderId - Action provided order ID. Used
 *     when the order has been received by the integrator.
 */
data class ReceiptInfo(var confirmedActionOrderId: String? = null)

/**
 * Order cancellation info.
 * @typedef {Object} CancellationInfo
 * @property {string} reason - Reason for the cancellation.
 */
data class CancellationInfo(var reason: String? = null)

/**
 * Order transit info.
 * @typedef {Object} TransitInfo
 * @property {Object} updatedTime - UTC timestamp of the transit update.
 * @property {number} updatedTime.seconds - Seconds since Unix epoch.
 * @property {number=} updatedTime.nanos - Partial seconds since Unix epoch.
 */
data class TransitInfo(var updatedTime: TimeStamp? = null)

data class TimeStamp(var seconds: Long? = null, var nanos: Long? = null)

/**
 * Order fulfillment info.
 * @typedef {Object} FulfillmentInfo
 * @property {Object} deliveryTime - UTC timestamp of the fulfillment update.
 * @property {number} deliveryTime.seconds - Seconds since Unix epoch.
 * @property {number=} deliveryTime.nanos - Partial seconds since Unix epoch.
 */
data class FulfillmentInfo(var deliveryTime: TimeStamp? = null)

/**
 * Order return info.
 * @typedef {Object} ReturnInfo
 * @property {string} reason - Reason for the return.
 */
data class ReturnInfo(var reason: String? = null)


sealed class TransactionConfig(val deliveryAddressRequired: Boolean? = null,
                               var type: String? = null,
                               val displayName: String? = null,
                               val tokenizationParameters: Any? = null,
                               val cardNetworks: MutableList<TransactionValues.CardNetwork>? = null,
                               val prepaidCardDisallowed: Boolean? = null,
                               val customerInfoOptions: MutableList<String>? = null)

/**
 * Transaction config for transactions not involving a Google provided
 * payment instrument.
 * @typedef {Object} ActionPaymentTransactionConfig
 * @property {Boolean} deliveryAddressRequired - True if delivery address is
 *     required for the transaction.
 * @property {Boolean} type - One of Transactions.PaymentType.
 * @property {String} displayName - The name of the instrument displayed on
 *     receipt. For example, for card payment, could be "VISA-1234".
 * @property {CustomerInfoOptions=} customerInfoOptions
 */
class ActionPaymentTransactionConfig(deliveryAddressRequired: Boolean,
                                     type: String?,
                                     displayName: String,
                                     customerInfoOptions: MutableList<String>? = null) :
        TransactionConfig(
                deliveryAddressRequired = deliveryAddressRequired,
                type = type,
                displayName = displayName,
                customerInfoOptions = customerInfoOptions)

/**
 * Transaction config for transactions involving a Google provided payment
 * instrument.
 * @typedef {Object} GooglePaymentTransactionConfig
 * @property {boolean} deliveryAddressRequired - True if delivery address is
 *     required for the transaction.
 * @property {Object} tokenizationParameters - Tokenization parameters provided
 *     by payment gateway.
 * @property {Array<string>} cardNetworks - List of accepted card networks.
 *     Must be any number of Transactions.CardNetwork.
 * @property {boolean} prepaidCardDisallowed - True if prepaid cards are not
 *     allowed for transaction.
 * @property {CustomerInfoOptions=} customerInfoOptions
 */
class GooglePaymentTransactionConfig(deliveryAddressRequired: Boolean,
                                     tokenizationParameters: Any,
                                     cardNetworks: MutableList<TransactionValues.CardNetwork>,
                                     prepaidCardDisallowed: Boolean,
                                     customerInfoOptions: MutableList<String>? = null) :
        TransactionConfig(deliveryAddressRequired = deliveryAddressRequired,
                tokenizationParameters = tokenizationParameters,
                cardNetworks = cardNetworks,
                prepaidCardDisallowed = prepaidCardDisallowed,
                customerInfoOptions = customerInfoOptions)


/**
 * Customer information requested as part of the transaction
 * @typedef {Object} CustomerInfoOptions
 * @property {Array<string>} customerInfoProperties - one of
 *    Transactions.CustomerInfoProperties
 */
data class CustomerInfoOptions(val customerInfoProperties: MutableList<String>)

/**
 * Values related to supporting transactions.
 * @readonly
 * @type {Object}
 */
open class TransactionValues {
    /**
     * List of transaction card networks available when paying with Google.
     * @readonly
     * @enum {string}
     */
    enum class CardNetwork {
        /**
         * Unspecified.
         */
        UNSPECIFIED,
        /**
         * American Express.
         */
        AMEX,
        /**
         * Discover.
         */
        DISCOVER,
        /**
         * Master Card.
         */
        MASTERCARD,
        /**
         * Visa.
         */
        VISA,
        /**
         * JCB.
         */
        JCB
    }

    /**
     * List of possible item types.
     * @readonly
     * @enum {string}
     */
    enum class ItemType {
        /**
         * Unspecified.
         */
        UNSPECIFIED,
        /**
         * Regular.
         */
        REGULAR,
        /**
         * Tax.
         */
        TAX,
        /**
         * Discount
         */
        DISCOUNT,
        /**
         * Gratuity
         */
        GRATUITY,
        /**
         * Delivery
         */
        DELIVERY,
        /**
         * Subtotal
         */
        SUBTOTAL,
        /**
         * Fee. For everything else, there"s fee.
         */
        FEE
    }

    /**
     * List of price types.
     * @readonly
     * @enum {string}
     */
    enum class PriceType {
        /**
         * Unknown.
         */
        UNKNOWN,
        /**
         * Estimate.
         */
        ESTIMATE,
        /**
         * Actual.
         */
        ACTUAL
    }

    /**
     * List of possible item types.
     * @readonly
     * @enum {string}
     */
    enum class PaymentType {
        /**
         * Unspecified.
         */
        UNSPECIFIED,
        /**
         * Payment card.
         */
        PAYMENT_CARD,
        /**
         * Bank.
         */
        BANK,
        /**
         * Loyalty program.
         */
        LOYALTY_PROGRAM,
        /**
         * On order fulfillment, such as cash on delivery.
         */
        ON_FULFILLMENT,
        /**
         * Gift card.
         */
        GIFT_CARD
    }

    /**
     * List of customer information properties that can be requested.
     * @readonly
     * @enum {string}
     */
    enum class CustomerInfoProperties {
        EMAIL
    }

    /**
     * List of possible order confirmation user decisions
     * @readonly
     * @enum {string}
     */
    enum class ConfirmationDecision(val value: String) {
        /**
         * Order was approved by user.
         */
        ACCEPTED("ORDER_ACCEPTED"),
        /**
         * Order was declined by user.
         */
        REJECTED("ORDER_REJECTED"),
        /**
         * Order was not declined, but the delivery address was updated during
         * confirmation.
         */
        DELIVERY_ADDRESS_UPDATED("DELIVERY_ADDRESS_UPDATED"),
        /**
         * Order was not declined, but the cart was updated during confirmation.
         */
        CART_CHANGE_REQUESTED("CART_CHANGE_REQUESTED");

        override fun toString() = value
    }

    /**
     * List of possible order states.
     * @readonly
     * @enum {string}
     */
    enum class OrderState {
        /**
         * Order was rejected.
         */
        REJECTED,
        /**
         * Order was confirmed by integrator and is active.
         */
        CONFIRMED,
        /**
         * User cancelled the order.
         */
        CANCELLED,
        /**
         * Order is being delivered.
         */
        IN_TRANSIT,
        /**
         * User performed a return.
         */
        RETURNED,
        /**
         * User received what was ordered.
         */
        FULFILLED
    }

    /**
     * List of possible actions to take on the order.
     * @readonly
     * @enum {string}
     */
    enum class OrderAction {
        /**
         * View details.
         */
        VIEW_DETAILS,
        /**
         * Modify order.
         */
        MODIFY,
        /**
         * Cancel order.
         */
        CANCEL,
        /**
         * Return order.
         */
        RETURN,
        /**
         * Exchange order.
         */
        EXCHANGE,
        /**
         * Email.
         */
        EMAIL,
        /**
         * Call.
         */
        CALL,
        /**
         * Reorder.
         */
        REORDER,
        /**
         * Review.
         */
        REVIEW
    }

    /**
     * List of possible types of order rejection.
     * @readonly
     * @enum {string}
     */
    enum class RejectionType {
        /**
         * Unknown
         */
        UNKNOWN,
        /**
         * Payment was declined.
         */
        PAYMENT_DECLINED
    }

    /**
     * List of possible order state objects.
     * @readonly
     * @enum {string}
     */
    enum class OrderStateInfo(val value: String) {
        /**
         * Information about order rejection. Used with {@link RejectionInfo}.
         */
        REJECTION("rejectionInfo"),
        /**
         * Information about order receipt. Used with {@link ReceiptInfo}.
         */
        RECEIPT("receipt"),
        /**
         * Information about order cancellation. Used with {@link CancellationInfo}.
         */
        CANCELLATION("cancellationInfo"),
        /**
         * Information about in-transit order. Used with {@link TransitInfo}.
         */
        IN_TRANSIT("inTransitInfo"),
        /**
         * Information about order fulfillment. Used with {@link FulfillmentInfo}.
         */
        FULFILLMENT("fulfillmentInfo"),
        /**
         * Information about order return. Used with {@link ReturnInfo}.
         */
        RETURN("returnInfo");

        override fun toString() = value

        companion object {
            fun fromValue(value: String): OrderStateInfo? = values().find { it.value == value }
        }
    }

    /**
     * List of possible order transaction requirements check result types.
     * @readonly
     * @enum {string}
     */
    enum class ResultType(val value: String) {
        /**
         * Unspecified.
         */
        UNSPECIFIED("RESULT_TYPE_UNSPECIFIED"),
        /**
         * OK to continue transaction.
         */
        OK("OK"),
        /**
         * User is expected to take action, e.g. enable payments, to continue
         * transaction.
         */
        USER_ACTION_REQUIRED("USER_ACTION_REQUIRED"),
        /**
         * Transactions are not supported on current device/surface.
         */
        ASSISTANT_SURFACE_NOT_SUPPORTED("ASSISTANT_SURFACE_NOT_SUPPORTED"),
        /**
         * Transactions are not supported for current region/country.
         */
        REGION_NOT_SUPPORTED("ASSISTANT_SURFACE_NOT_SUPPORTED");

        override fun toString() = value
    }

    /**
     * List of possible user decisions to give delivery address.
     * @readonly
     * @enum {string}
     */
    enum class DeliveryAddressDecision(val value: String) {
        /**
         * Unknown.
         */
        UNKNOWN("UNKNOWN_USER_DECISION"),
        /**
         * User granted delivery address.
         */
        ACCEPTED("ACCEPTED"),
        /**
         * User denied to give delivery address.
         */
        REJECTED("REJECTED");

        override fun toString() = value
    }

    /**
     * List of possible order location types.
     * @readonly
     * @enum {string}
     */
    enum class LocationType {
        /**
         * Unknown.
         */
        UNKNOWN,
        /**
         * Delivery location for an order.
         */
        DELIVERY,
        /**
         * Business location of order provider.
         */
        BUSINESS,
        /**
         * Origin of the order.
         */
        ORIGIN,
        /**
         * Destination of the order.
         */
        DESTINATION
    }

    /**
     * List of possible order time types.
     * @readonly
     * @enum {string}
     */
    enum class TimeType {
        /**
         * Unknown.
         */
        UNKNOWN,
        /**
         * Date of delivery for the order.
         */
        DELIVERY_DATE,
        /**
         * Estimated Time of Arrival for order.
         */
        ETA,
        /**
         * Reservation time.
         */
        RESERVATION_SLOT
    }


}

/**
 * Class for initializing and constructing Order with chainable interface.
 *
 * Constructor for Order.
 *
 * ID for the order. Required.
 * @type {string}
 * @param {string} orderId Unique identifier for the order.
 */
data class Order(val id: String) {

    /**
     * Cart for the order.
     * @type {Cart}
     */
    var cart: Cart? = null

    /**
     * Items not held in the order cart.
     * @type {MutableList<LineItem>}
     */
    val otherItems = mutableListOf<LineItem>()

    /**
     * Image for the order.
     * @type {Image}
     */
    var image: Image? = null

    /**
     * TOS for the order.
     * @type {String}
     */
    var termsOfServiceUrl: String? = null

    /**
     * Total price for the order.
     * @type {Price}
     */
    internal var totalPrice: GoogleData.TotalPrice? = null

    /**
     * Extensions for this order. Used for vertical-specific order attributes,
     * like times and locations.
     * @type {Object}
     */
    var extension: Extension? = null

    /**
     * Set the cart for this order.
     *
     * @param {Cart} cart Cart for this order.
     * @return {Order} Returns current constructed Order.
     */
    fun setCart(cart: Cart): Order {
        this.cart = cart
        return this
    }

    /**
     * Adds a single item or list of items to the non-cart items list.
     *
     * @param {LineItem|Array<LineItem>} items Line Items to add.
     * @return {Order} Returns current constructed Order.
     */
    fun addOtherItems(vararg items: LineItem): Order {
        otherItems.addAll(items)
        return this
    }


    /**
     * Sets the image for this order.
     *
     * @param {String} url Image source URL.
     * @param {String} accessibilityText Text to replace for image for
     *     accessibility.
     * @param {Int?=} width Width of the image.
     * @param {Int?=} height Height of the image.
     * @return {Order} Returns current constructed Order.
     */
    fun setImage(url: String, accessibilityText: String, width: Int? = null, height: Int? = null): Order {
        if (accessibilityText.isEmpty()) {
            error("accessibilityText cannot be empty")
            return this
        }
        image = Image(url, accessibilityText)
        if (width != null) {
            image?.width = width
        }
        if (height != null) {
            image?.height = height
        }
        return this
    }

    /**
     * Set the TOS for this order.
     *
     * @param {String} tos String URL of the TOS.
     * @return {Order} Returns current constructed Order.
     */
    fun setTermsOfService(url: String): Order {
        if (url.isEmpty()) {
            error("Invalid TOS url")
            return this
        }
        termsOfServiceUrl = url
        return this
    }

    /**
     * Sets the total price for this order.
     *
     * @param {TransactionValues.PriceType} priceType One of TransactionValues.PriceType.
     * @param {String} currencyCode Currency code of price.
     * @param {Int} units Unit count of price.
     * @param {Int=} nanos Partial unit count of price.
     * @return {Order} Returns current constructed Order.
     */
    fun setTotalPrice(priceType: TransactionValues.PriceType, currencyCode: String, units: Int, nanos: Int = 0): Order {
        if (currencyCode.isEmpty()) {
            error("currencyCode cannot be empty")
            return this
        }

        totalPrice = totalPrice {
            type = priceType
            amount {
                this.currencyCode = currencyCode
                this.units = units
                this.nanos = nanos
            }

        }
        return this
    }

    data class Extension(val `@type`: String, var locations: MutableList<LocationInfo>? = null, var time: Time? = null)
    data class LocationInfo(var type: String? = null, var location: Location)
    data class Location(val postalAddress: PostalAddress? = null)


    data class PostalAddress(var regionCode: String? = null,
                             var languageCode: String? = null,
                             var postalCode: String? = null,
                             var administrativeArea: String? = null,
                             var locality: String? = null,
                             var addressLines: MutableList<String>? = null,
                             var recipients: String? = null,
                             var phoneNumber: String? = null,
                             var notes: String? = null)

    /**
     * Adds an associated location to the order. Up to 2 locations can be added.
     *
     * @param {TransactionValues.LocationType} type One of TransactionValues.LocationType.
     * @param {Location} location Location to add.
     * @return {Order} Returns current constructed Order.
     */
    fun addLocation(type: TransactionValues.LocationType, location: Location): Order {
        if (location == null) {
            error("location cannot be null")
            return this
        }
        if (extension != null) {
            extension = Extension(
                    `@type` = GENERIC_EXTENSION_TYPE)
        }
        if (extension?.locations == null) {
            extension?.locations = mutableListOf()
        }
        if (extension?.locations?.size ?: 0 >= ORDER_LOCATION_LIMIT) {
            error("Order can have no more than " + ORDER_LOCATION_LIMIT +
                    " associated locations")
            return this
        }
        extension?.locations?.add(LocationInfo(type.name, location))
        return this
    }


    /**
     * Sets an associated time to the order.
     *
     * @param {TransactionValues.TimeType} type One of TransactionValues.TimeType.
     * @param {String} time Time to add. Time should be ISO 8601 representation
     *     of time value. Could be date, datetime, or duration.
     * @return {Order} Returns current constructed Order.
     */
    fun setTime(type: TransactionValues.TimeType, time: String): Order {
        if (time.isEmpty()) {
            error("time cannot be empty")
            return this
        }
        if (extension == null) {
            extension = Extension(
                    `@type` = GENERIC_EXTENSION_TYPE)
        }
        extension?.time = Time(type = type, time_iso8601 = time)
        return this
    }
}

data class Time(var type: TransactionValues.TimeType = TransactionValues.TimeType.UNKNOWN, var time_iso8601: String? = null)
/**
 * Class for initializing and constructing Cart with chainable interface.
 *
 * Constructor for Cart.
 *
 * @param {string=} id Optional unique identifier for the cart.
 * ID for the cart. Optional.
 * @type {string}
 */
data class Cart(var id: String? = null) {

    /**
     * Merchant providing the cart.
     * @type {Object}
     */
    var merchant: Merchant? = null

    /**
     * Optional notes about the cart.
     * @type {String}
     */
    var notes: String? = null

    /**
     * Items held in the order cart.
     * @type {MutableList<LineItem>}
     */
    var lineItems: MutableList<LineItem> = mutableListOf()

    /**
     * Non-line items.
     * @type {MutableList<LineItem>}
     */
    var otherItems: MutableList<LineItem> = mutableListOf()

    /**
     * Set the merchant for this cart.
     *
     * @param {string} id Merchant ID.
     * @param {string} name Name of the merchant.
     * @return {Cart} Returns current constructed Cart.
     */
    fun setMerchant(id: String, name: String): Cart {
        if (id.isEmpty()) {
            error("Merchant ID cannot be empty")
            return this
        }
        if (name.isEmpty()) {
            error("Merchant name cannot be empty")
            return this
        }
        merchant = Merchant(id, name)
        return this
    }

    data class Merchant(var id: String, var name: String)

    /**
     * Set the notes for this cart.
     *
     * @param {String} notes Notes.
     * @return {Cart} Returns current constructed Cart.
     */
    fun setNotes(notes: String): Cart {
        if (notes.isEmpty()) {
            error("Notes cannot be empty")
            return this
        }
        this.notes = notes
        return this
    }

    /**
     * Adds a single item or list of items to the cart.
     *
     * @param {LineItem|Array<LineItem>} items Line Items to add.
     * @return {Cart} Returns current constructed Cart.
     */
    fun addLineItems(vararg items: LineItem): Cart {
        if (items.isEmpty()) {
            error("items cannot be null")
            return this
        }
        lineItems.addAll(items)
        return this
    }

    /**
     * Adds a single item or list of items to the non-items list of this cart.
     *
     * @param {LineItem|Array<LineItem>} items Line Items to add.
     * @return {Cart} Returns current constructed Cart.
     */
    fun addOtherItems(vararg items: LineItem): Cart {
        if (items.isEmpty()) {
            error("items cannot be null")
            return this
        }
        otherItems.addAll(items)
        return this
    }
}

/**
 * Class for initializing and constructing LineItem with chainable interface.
 *
 * Constructor for LineItem.
 *
 * @param {string} lineItemId Unique identifier for the item.
 * @param {string} name Name of the item.
 */
data class LineItem(var id: String, var name: String) {

    /**
     * Item price.
     * @type {Price}
     */
    var price: GoogleData.Price? = null

    /**
     * Sublines for current item. Only valid if item type is REGULAR.
     * @type {Array<string|LineItem>}
     */
    var sublines: MutableList<Any>? = null

    /**
     * Image of the item.
     * @type {Image}
     */
    var image: Image? = null

    /**
     * Type of the item. One of TransactionValues.ItemType.
     * @type {ItemType}
     */
    var type: TransactionValues.ItemType? = null

    /**
     * Quantity of the item.
     * @type {Int?}
     */
    var quantity: Int? = null

    /**
     * Description for the item.
     * @type {String}
     */
    var description: String? = null

    /**
     * Offer ID for the item.
     * @type {String}
     */
    var offerId: String? = null

    /**
     * Adds a single item or list of items or notes to the sublines. Only valid
     * if item type is REGULAR.
     *
     * @param {string|LineItem|Array<string|LineItem>} items Sublines to add.
     * @return {LineItem} Returns current constructed LineItem.
     */
    fun addSublines(vararg items: LineItem): LineItem {
        if (items.isEmpty()) {
            error("items cannot be null")
            return this
        }
        if (this.sublines != null) {
            this.sublines = mutableListOf()
        }
        sublines?.addAll(items)
        return this
    }

    /**
     * Sets the image for this item.
     *
     * @param {string} url Image source URL.
     * @param {string} accessibilityText Text to replace for image for
     *     accessibility.
     * @param {number=} width Width of the image.
     * @param {number=} height Height of the image.
     * @return {LineItem} Returns current constructed LineItem.
     */
    fun setImage(url: String, accessibilityText: String, width: Int? = null, height: Int? = null): LineItem {
        if (url.isEmpty()) {
            error("url cannot be empty")
            return this
        }
        if (accessibilityText.isEmpty()) {
            error("accessibilityText cannot be empty")
            return this
        }
        this.image = Image(url, accessibilityText)
        if (width != null) {
            image?.width = width
        }
        if (height != null) {
            image?.height = height
        }
        return this
    }

    /**
     * Sets the price of this item.
     *
     * @param {String} priceType One of TransactionValues.PriceType.
     * @param {String} currencyCode Currency code of price.
     * @param {Int} units Unit count of price.
     * @param {Int} nanos Partial unit count of price.
     * @return {LineItem} Returns current constructed LineItem.
     */
    fun setPrice(priceType: TransactionValues.PriceType, currencyCode: String, units: Int, nanos: Int = 0): LineItem {
        if (currencyCode.isEmpty()) {
            error("currencyCode cannot be empty")
            return this
        }

        price = price {
            type = priceType
            amount {
                this.currencyCode = currencyCode
                this.units = units
                this.nanos = nanos
            }
        }
        return this
    }

    /**
     * Set the type of the item.
     *
     * @param {TransactionValues.ItemType} type Type of the item. One of TransactionValues.ItemType.
     * @return {LineItem} Returns current constructed LineItem.
     */
    fun setType(type: TransactionValues.ItemType): LineItem {
        this.type = type
        return this
    }

    /**
     * Set the quantity of the item.
     *
     * @param {Int} quantity Quantity of the item.
     * @return {LineItem} Returns current constructed LineItem.
     */
    fun setQuantity(quantity: Int): LineItem {
        if (quantity == 0) {
            error("quantity cannot be empty")
            return this
        }
        this.quantity = quantity
        return this
    }

    /**
     * Set the description of the item.
     *
     * @param {String} description Description of the item.
     * @return {LineItem} Returns current constructed LineItem.
     */
    fun setDescription(description: String): LineItem {
        if (description.isEmpty()) {
            error("description cannot be empty")
            return this
        }
        this.description = description
        return this
    }

    /**
     * Set the Offer ID of the item.
     *
     * @param {String} offerId Offer ID of the item.
     * @return {LineItem} Returns current constructed LineItem.
     */
    fun setOfferId(offerId: String): LineItem {
        if (offerId.isEmpty()) {
            error("offerId cannot be empty")
            return this
        }
        this.offerId = offerId
        return this
    }
}

/**
 * Class for initializing and constructing OrderUpdate with chainable interface.
 *
 * Constructor for OrderUpdate.
 *
 * @param {string} orderId Unique identifier of the order.
 * @param {boolean} isGoogleOrderId True if the order ID is provided by
 *     Google. False if the order ID is app provided.
 */
class OrderUpdate(orderId: String? = null, val isGoogleOrderId: Boolean = false) : MutableMap<String, Any?> by mutableMapOf<String, Any?>() {
    /**
     * Google provided identifier of the order.
     * @type {string}
     */
    var googleOrderId: String? by this

    /**
     * App provided identifier of the order.
     * @type {string}
     */
    var actionOrderId: String? by this

    /**
     * State of the order.
     * @type {Object}
     */
    var orderState: OrderState? by this

    /**
     * Updates for items in the order. Mapped by item id to state or price.
     * @type {Object}
     */
    var lineItemUpdates: MutableMap<String, LineItemUpdate> by this

    /**
     * UTC timestamp of the order update.
     * @type {Object}
     */
    var updateTime: UpdateTime? by this

    data class UpdateTime(var seconds: Long, var nanos: Long)

    /**
     * Actionable items presented to the user to manage the order.
     * @type {Object}
     */
    var orderManagementActions: MutableList<OrderManagementAction>? by this

    /**
     * Notification content to the user for the order update.
     * @type {Object}
     */
    var userNotification: UserNotification? by this

    /**
     * Updated total price of the order.
     * @type {TotalPrice}
     */
    var totalPrice: GoogleData.TotalPrice? by this

    init {
        googleOrderId = if (isGoogleOrderId) orderId else null
        actionOrderId = if (!isGoogleOrderId) orderId else null
        lineItemUpdates = mutableMapOf<String, LineItemUpdate>()
        orderManagementActions = mutableListOf<OrderManagementAction>()
    }

    /**
     * Set the Google provided order ID of the order.
     *
     * @param {String} orderId Google provided order ID.
     * @return {OrderUpdate} Returns current constructed OrderUpdate.
     */
    fun setGoogleOrderId(orderId: String): OrderUpdate {
        if (orderId.isEmpty()) {
            error("orderId cannot be empty")
            return this
        }
        googleOrderId = orderId
        return this
    }

    /**
     * Set the Action provided order ID of the order.
     *
     * @param {String} orderId Action provided order ID.
     * @return {OrderUpdate} Returns current constructed OrderUpdate.
     */
    fun setActionOrderId(orderId: String): OrderUpdate {
        if (orderId.isEmpty()) {
            error("orderId cannot be empty")
            return this
        }
        actionOrderId = orderId
        return this
    }

    /**
     * Set the state of the order.
     *
     * @param {TransactionValues.OrderState} state One of TransactionValues.OrderState.
     * @param {String} label Label for the order state.
     * @return {OrderUpdate} Returns current constructed OrderUpdate.
     */
    fun setOrderState(state: TransactionValues.OrderState, label: String): OrderUpdate {
        if (label.isEmpty()) {
            error("label cannot be empty")
            return this
        }
        orderState = OrderState(state, label)
        return this
    }

    /**
     * Set the update time of the order.
     *
     * @param {Long} seconds Seconds since Unix epoch.
     * @param {Long=} nanos Partial time units.
     * @return {OrderUpdate} Returns current constructed OrderUpdate.
     */
    fun setUpdateTime(seconds: Long, nanos: Long = 0): OrderUpdate {
        if (seconds < 0) {
            error("Invalid seconds")
            return this
        }
        updateTime = UpdateTime(seconds, nanos)
        return this
    }

    /**
     * Set the user notification content of the order update.
     *
     * @param {String} title Title of the notification.
     * @param {String} text Text of the notification.
     * @return {OrderUpdate} Returns current constructed OrderUpdate.
     */
    fun setUserNotification(title: String, text: String): OrderUpdate {
        if (title.isEmpty()) {
            error("title cannot be empty")
            return this
        }
        if (text.isEmpty()) {
            error("text cannot be empty")
            return this
        }
        userNotification = UserNotification(title, text)
        return this
    }

    data class UserNotification(var title: String, var text: String)

    /**
     * Sets the total price for this order.
     *
     * @param {TransactionValues.PriceType} priceType One of TransactionValues.PriceType.
     * @param {string} currencyCode Currency code of price.
     * @param {number} units Unit count of price.
     * @param {number=} nanos Partial unit count of price.
     * @return {OrderUpdate} Returns current constructed OrderUpdate.
     */
    fun setTotalPrice(priceType: TransactionValues.PriceType, currencyCode: String, units: Int, nanos: Int = 0): OrderUpdate {
        if (currencyCode.isEmpty()) {
            error("currencyCode cannot be empty")
            return this
        }
        totalPrice = totalPrice {
            type = priceType
            amount {
                this.currencyCode = currencyCode
                this.units = units
                this.nanos = nanos
            }
        }
        return this
    }

    /**
     * Adds an actionable item for the user to manage the order.
     *
     * @param {TransactionValues.OrderUpdate} type One of TransactionValues.OrderActions.
     * @param {String} label Button label.
     * @param {String} url URL to open when button is clicked.
     * @return {OrderUpdate} Returns current constructed OrderUpdate.
     */
    fun addOrderManagementAction(type: TransactionValues.OrderAction, label: String, url: String): OrderUpdate {
        if (label.isEmpty()) {
            error("label cannot be empty")
            return this
        }
        if (url.isEmpty()) {
            error("URL cannot be empty")
            return this
        }
        (orderManagementActions as MutableList<OrderManagementAction>).add(OrderManagementAction(
                type = type,
                button = Button(
                        title = label,
                        openUrlAction = OpenUrlAction(
                                url = url))
        )
        )
        return this
    }

    data class OrderManagementAction(var type: TransactionValues.OrderAction, var button: Button? = null,
                                     var title: String? = null, var openUrlAction: OpenUrlAction? = null)


    /**
     * Adds a single price update for a particular line item in the order.
     *
     * @param {String} itemId Line item ID for the order item updated.
     * @param {TransactionValue.PriceType} priceType One of TransactionValues.PriceType.
     * @param {String} currencyCode Currency code of new price.
     * @param {Int} units Unit count of new price.
     * @param {Int} nanos Partial unit count of new price.
     * @param {String} reason Reason for the price change. Required unless a
     *     reason for this line item change was already declared in
     *     addLineItemStateUpdate.
     * @return {OrderUpdate} Returns current constructed OrderUpdate.
     */
    fun addLineItemPriceUpdate(itemId: String, priceType: TransactionValues.PriceType,
                               currencyCode: String, units: Int, nanos: Int = 0, reason: String): OrderUpdate {
        if (itemId.isEmpty()) {
            error("itemId cannot be empty")
            return this
        }
        if (currencyCode.isEmpty()) {
            error("currencyCode cannot be empty")
            return this
        }

        val newPrice = price {
            type = priceType
            amount {
                this.currencyCode = currencyCode
                this.units = units
                this.nanos = nanos
            }
        }

        if (lineItemUpdates.containsKey(itemId) && lineItemUpdates.get(itemId)?.reason?.isNotBlank() ?: false) {
            lineItemUpdates[itemId]?.price = newPrice
            lineItemUpdates[itemId]?.reason = if (reason.isNotBlank()) reason else
                lineItemUpdates[itemId]?.reason
        } else if (lineItemUpdates.get(itemId) != null && reason.isNotBlank()) {
            lineItemUpdates[itemId]?.price = newPrice
            lineItemUpdates[itemId]?.reason = reason
        } else if (lineItemUpdates.get(itemId) == null && reason.isNotBlank()) {
            lineItemUpdates[itemId] = LineItemUpdate(
                    price = newPrice,
                    reason = reason)
        } else {
            error("reason cannot be empty")
            return this
        }
        return this
    }

    data class LineItemUpdate(var price: GoogleData.Price? = null, var reason: String? = null,
                              var orderState: OrderState? = null)

    /**
     * Adds a single state update for a particular line item in the order.
     *
     * @param {String} itemId Line item ID for the order item updated.
     * @param {TransactionValues.OrderState} state One of TransactionValues.OrderState.
     * @param {String} label Label for the new item state.
     * @param {String=} reason Reason for the price change. This will overwrite
     *     any reason given in addLineitemPriceUpdate.
     * @return {OrderUpdate} Returns current constructed OrderUpdate.
     */
    fun addLineItemStateUpdate(itemId: String, state: TransactionValues.OrderState, label: String, reason: String? = null): OrderUpdate {
        if (itemId.isEmpty()) {
            error("itemId cannot be empty")
            return this
        }
        if (label.isEmpty()) {
            error("label cannot be empty")
            return this
        }

        var lineItemUpdates = lineItemUpdates

        lineItemUpdates[itemId] = lineItemUpdates[itemId] ?: LineItemUpdate()
        lineItemUpdates[itemId]?.orderState = OrderState(state, label)
        lineItemUpdates[itemId]?.reason = if (reason != null) reason else lineItemUpdates[itemId]?.reason

        return this
    }

    /**
     * Sets some extra information about the order. Takes an order update info
     * type, and any accompanying data. This should only be called once per
     * order update.
     *
     * @param {String} type One of TransactionValues.OrderStateInfo.
     * @param {Object} data Proper Object matching the data necessary for the info
     *     type. For instance, for the TransactionValues.OrderStateInfo.RECEIPT info
     *     type, use the {@link ReceiptInfo} data type.
     * @return {OrderUpdate} Returns current constructed OrderUpdate.
     */
    fun setInfo(type: TransactionValues.OrderStateInfo, data: Any): OrderUpdate {
        TransactionValues.OrderStateInfo.values().forEach {
            remove(it.value)
        }

        this[type.value] = data
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (other is OrderUpdate) {
            var tmp = true
            this.keys.forEach {
                if (this[it] != other[it]) {
                    tmp = false
                }
            }
            return tmp
        } else {
            return super.equals(other)
        }
    }

    /* overiden to give better logging and error in tests */
    override fun toString(): String {
        return this.toMutableMap().toString()
    }

    fun error(msg: String) = logger.warning(msg)
}




