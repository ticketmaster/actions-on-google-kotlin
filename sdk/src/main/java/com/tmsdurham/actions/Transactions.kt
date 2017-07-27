package com.tmsdurham.actions


val ORDER_LOCATION_LIMIT = 2
val GENERIC_EXTENSION_TYPE = "type.googleapis.com/google.actions.v2.orders.GenericExtension"


sealed class TransactionConfig(val deliveryAddressRequired: Boolean? = null,
                               var type: String? = null,
                               val displayName: String? = null,
                               val tokenizationParameters: Any? = null,
                               val cardNetworks: MutableList<String>? = null,
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
                                     cardNetworks: MutableList<String>,
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
class TransactionValues {
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
        REGION_NOT_SUPPORTED("ASSISTANT_SURFACE_NOT_SUPPORTED)";

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
