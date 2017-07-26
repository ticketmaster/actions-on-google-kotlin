package com.tmsdurham.actions


val ORDER_LOCATION_LIMIT = 2
val GENERIC_EXTENSION_TYPE = "type.googleapis.com/google.actions.v2.orders.GenericExtension"


sealed class TransactionConfig(val deliveryAddressRequired: Boolean? = null,
                               var type: String? = null,
                               val displayName: String? = null,
                               val tokenizationParameters: Any? = null,
                               val cardNetworks: MutableList<String>? = null,
                               val prepaidCardDisallowed: Boolean? = null,
                               val customerInfoOptions: CustomerInfoOptions? = null)

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
                                     customerInfoOptions: CustomerInfoOptions? = null) :
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
                                     customerInfoOptions: CustomerInfoOptions? = null) :
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

