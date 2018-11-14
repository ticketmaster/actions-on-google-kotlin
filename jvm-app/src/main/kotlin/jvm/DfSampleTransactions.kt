package jvm

import actions.ApiClientObjectMap
import actions.expected.deliveryAddress
import actions.expected.log
import actions.service.actionssdk.api.*
import actions.service.actionssdk.conversation.question.transaction.DeliveryAddress
import actions.service.actionssdk.conversation.question.transaction.TransactionDecision
import actions.service.actionssdk.conversation.question.transaction.TransactionRequirements
import actions.service.actionssdk.conversation.response.OrderUpdate
import actions.service.dialogflow.DialogflowApp


const val GENERIC_EXTENSION_TYPE =
        "type.googleapis.com/google.actions.v2.orders.GenericExtension"
const val UNIQUE_ORDER_ID = "<UNIQUE_ORDER_ID>"

fun initTransactionSample(dfApp: DialogflowApp<*, *, *>) {

    dfApp.intent("transaction.check.action") { conv ->
        conv.hasScreen()
        conv.ask(TransactionRequirements {
            orderOptions = GoogleActionsV2OrdersOrderOptions(
                    requestDeliveryAddress = false)
            paymentOptions = GoogleActionsV2OrdersPaymentOptions(
                    actionProvidedOptions = GoogleActionsV2OrdersActionProvidedPaymentOptions(
                            displayName = "VISA-1234",
                            paymentType = GoogleActionsV2OrdersActionProvidedPaymentOptionsPaymentType.PAYMENT_CARD)
            )
        })
    }

    dfApp.intent("transaction.check.google") { conv ->
        conv.ask(TransactionRequirements {
            orderOptions = GoogleActionsV2OrdersOrderOptions(requestDeliveryAddress = false)
            paymentOptions = GoogleActionsV2OrdersPaymentOptions(
                    googleProvidedOptions = GoogleActionsV2OrdersGoogleProvidedPaymentOptions(
                            prepaidCardDisallowed = false,
                            supportedCardNetworks = mutableListOf(GoogleActionsV2OrdersGoogleProvidedPaymentOptionsSupportedCardNetworks.VISA, GoogleActionsV2OrdersGoogleProvidedPaymentOptionsSupportedCardNetworks.AMEX),
                            // These will be provided by payment processor,
                            // like Stripe, Braintree, or Vantiv.
                            tokenizationParameters = GoogleActionsV2OrdersPaymentMethodTokenizationParameters()))
        })
    }

    dfApp.intent("transaction.check.complete") { conv ->
        val arg = conv.arguments.get("TRANSACTION_REQUIREMENTS_CHECK_RESULT")
        if (arg != null && arg.resultType == GoogleActionsV2TransactionRequirementsCheckResultResultType.OK) {
            // Normally take the user through cart building flow
            conv.ask("""Looks like you're good to go!
                    Try saying "Get Delivery Address".""")
        } else {
            conv.close("Transaction failed.")
        }
    }

    dfApp.intent("delivery.address") { conv ->
        conv.ask(DeliveryAddress {
            addressOptions = GoogleActionsV2DeliveryAddressValueSpecAddressOptions(
                    reason = "To know where to send the order")
        })
    }

    dfApp.intent("delivery.address.complete") { conv ->
        val arg = conv.arguments.get("DELIVERY_ADDRESS_VALUE")
        if (arg?.userDecision == "ACCEPTED") {
            log("DELIVERY ADDRESS: " +
                    arg.location?.postalAddress?.addressLines?.get(0))
            conv.data["deliveryAddress"] = arg.location
            conv.ask("""Great, got your address! Now say " confirm transaction".""")
        } else {
            conv.close("I failed to get your delivery address.")
        }
    }

    dfApp.intent("transaction.decision.action") { conv ->
        val order = order {
            id = UNIQUE_ORDER_ID
            cart {
                merchant {
                    id = "book_store_1"
                    name = "Book Store"
                }
                lineItems({
                    name = "My Memoirs"
                    id = "memoirs_1"
                    price {
                        amount {
                            currencyCode = "USD"
                            nanos = 990000000
                            units = 3
                        }
                        type = GoogleActionsV2OrdersPriceType.ACTUAL
                    }
                    quantity = 1
                    subLines({
                        note = "Note from the author"
                    })
                    type = GoogleActionsV2OrdersLineItemType.REGULAR
                }, {
                    name = "Memoirs of a person"
                    id = "memoirs_2"
                    price {
                        amount {
                            currencyCode = "USD"
                            nanos = 990000000
                            units = 5
                        }
                        type = GoogleActionsV2OrdersPriceType.ACTUAL
                    }
                    quantity = 1
                    subLines({
                        note = "Special introduction by author"
                    })
                    type = GoogleActionsV2OrdersLineItemType.REGULAR
                }, {
                    name = "Their memoirs"
                    id = "memoirs_3"
                    price {
                        amount {
                            currencyCode = "USD"
                            nanos = 750000000
                            units = 15
                        }
                        type = GoogleActionsV2OrdersPriceType.ACTUAL
                    }
                    quantity = 1
                    subLines({
                        lineItem {
                            name = "Special memoir epilogue"
                            id = "memoirs_epilogue"
                            price {
                                amount {
                                    currencyCode = "USD"
                                    nanos = 990000000
                                    units = 3
                                }
                                type = GoogleActionsV2OrdersPriceType.ACTUAL
                            }
                            quantity = 1
                            type = GoogleActionsV2OrdersLineItemType.REGULAR
                        }
                    })
                    type = GoogleActionsV2OrdersLineItemType.REGULAR
                }, {
                    name = "Our memoirs"
                    id = "memoirs_4"
                    price {
                        amount {
                            currencyCode = "USD"
                            nanos = 490000000
                            units = 6
                        }
                        type = GoogleActionsV2OrdersPriceType.ACTUAL
                    }
                    quantity = 1
                    subLines({
                        note = "Special introduction by author"
                    })
                    type = GoogleActionsV2OrdersLineItemType.REGULAR
                })
                notes = "The Memoir collection"
                otherItems({
                    name = "Subtotal"
                    id = "subtotal"
                    price {
                        amount {
                            currencyCode = "USD"
                            nanos = 220000000
                            units = 32
                        }
                        type = GoogleActionsV2OrdersPriceType.ESTIMATE
                    }
                    type = GoogleActionsV2OrdersLineItemType.SUBTOTAL
                }, {
                    name = "Tax"
                    id = "tax"
                    price {
                        amount {
                            currencyCode = "USD"
                            nanos = 780000000
                            units = 2
                        }
                        type = GoogleActionsV2OrdersPriceType.ESTIMATE
                    }
                    type = GoogleActionsV2OrdersLineItemType.TAX
                })
            }
            otherItems = mutableListOf()
            totalPrice {
                amount {
                    currencyCode = "USD"
                    nanos = 0
                    units = 35
                }
                type = GoogleActionsV2OrdersPriceType.ESTIMATE
            }
        }

        if (conv.data["deliveryAddress"] != null) {
            order.extension {
                `@type` = GENERIC_EXTENSION_TYPE
                locations({
                    type = GoogleActionsV2OrdersOrderLocationType.DELIVERY
                    location {
                        postalAddress = conv.data.deliveryAddress?.postalAddress
                    }
                })
            }
        }

        // To test payment w/ sample,
        // uncheck the "Testing in Sandbox Mode" box in the
        // Actions console simulator
        conv.ask(TransactionDecision {
            orderOptions {
                requestDeliveryAddress = true
            }
            paymentOptions {
                actionProvidedOptions {
                    paymentType = GoogleActionsV2OrdersActionProvidedPaymentOptionsPaymentType.PAYMENT_CARD
                    displayName = "VISA-1234"
                }
                proposedOrder = order
            }
        })

        /*
      // If using Google provided payment instrument instead
      conv.ask(new TransactionDecision({
      orderOptions: {
        requestDeliveryAddress: false,
      },
      paymentOptions: {
        googleProvidedOptions: {
          prepaidCardDisallowed: false,
          supportedCardNetworks: ["VISA", "AMEX"],
          // These will be provided by payment processor,
          // like Stripe, Braintree, or Vantiv.
          tokenizationParameters: {},
        },
      },
      proposedOrder: order,
    }))
    */
    }

    dfApp.intent("transaction.decision.complete") { conv ->
        log("Transaction decision complete")
        val arg = conv.arguments.get("TRANSACTION_DECISION_VALUE")
        if (arg != null && arg.userDecision == "ORDER_ACCEPTED") {
            val finalOrderId = arg.order?.finalOrder?.id

            // Confirm order and make any charges in order processing backend
            // If using Google provided payment instrument:
            // val paymentDisplayName = arg.order.paymentInfo.displayName
            conv.ask(OrderUpdate {
                actionOrderId = finalOrderId
                orderState {
                    label = "Order created"
                    state = "CREATED"
                }
                lineItemUpdates = ApiClientObjectMap()
                //TODO make setting updatetime easier
                updateTime = GoogleTypeTimeOfDay()
                receipt {
                    confirmedActionOrderId = UNIQUE_ORDER_ID
                }
                // Replace the URL with your own customer service page
                orderManagementActions({
                    button {
                        openUrlAction {
                            url = "http://example.com/customer-service"
                        }
                        title = "Customer Service"
                    }
                    type = GoogleActionsV2OrdersOrderUpdateActionType.CUSTOMER_SERVICE
                })
                userNotification {
                    text = "Notification text."
                    title = "Notification Title"
                }
            })
            conv.ask("Transaction completed! You're all set!")
        } else if (arg != null && arg.userDecision == "DELIVERY_ADDRESS_UPDATED") {
            conv.ask(DeliveryAddress {
                addressOptions {
                    reason = "To know where to send the order"
                }
            })
        } else {
            conv.close("Transaction failed.")
        }
    }

}

