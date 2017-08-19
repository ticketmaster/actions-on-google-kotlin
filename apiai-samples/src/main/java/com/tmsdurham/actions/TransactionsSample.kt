package com.tmsdurham.actions

import main.java.com.tmsdurham.apiai.sample.ApiAiAction
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// APIAI Actions
val TRANSACTION_CHECK_NO_PAYMENT = "transaction.check.no.payment"
val TRANSACTION_CHECK_ACTION_PAYMENT = "transaction.check.action"
val TRANSACTION_CHECK_GOOGLE_PAYMENT = "transaction.check.google"
val TRANSACTION_CHECK_COMPLETE = "transaction.check.complete"
val DELIVERY_ADDRESS = "delivery.address"
val DELIVERY_ADDRESS_COMPLETE = "delivery.address.complete"
val TRANSACTION_DECISION_ACTION_PAYMENT = "transaction.decision.action"
val TRANSACTION_DECISION_COMPLETE = "transaction.decision.complete"

@WebServlet("/transaction")
class TransactionSample : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        ApiAiAction(req, resp).handleRequest(transactionMap)
    }

}

fun transactionCheckNoPayment(app: ApiAiApp) {
    app.askForTransactionRequirements()
}

fun transactionCheckActionPayment(app: ApiAiApp) {
    app.askForTransactionRequirements(ActionPaymentTransactionConfig(
            type = TransactionValues.PaymentType.PAYMENT_CARD.toString(),
            displayName = "VISA-1234",
            deliveryAddressRequired = false)
    )
}

fun transactionCheckGooglePayment(app: ApiAiApp) {
    app.askForTransactionRequirements( GooglePaymentTransactionConfig(
            // These will be provided by payment processor, like Stripe, Braintree, or
            // Vantiv
            tokenizationParameters = listOf<Any>(),
    cardNetworks = mutableListOf(
    TransactionValues.CardNetwork.VISA,
    TransactionValues.CardNetwork.AMEX
    ),
    prepaidCardDisallowed = false,
    deliveryAddressRequired = false)
    )
}

fun transactionCheckComplete(app: ApiAiApp) {
    if (app.getTransactionRequirementsResult() ==
            TransactionValues.ResultType.OK) {
        // Normally take the user through cart building flow
        app.ask("Looks like you're good to go! Try saying ' Get Delivery Address '.")
    } else {
        app.tell("Transaction failed.")
    }
}

fun deliveryAddress(app: ApiAiApp) {
    app.askForDeliveryAddress("To know where to send the order")
}

fun deliveryAddressComplete(app: ApiAiApp) {
    if (app.getDeliveryAddress() != null) {
        logger.info("DELIVERY ADDRESS: " +
                app.getDeliveryAddress()?.postalAddress?.addressLines?.get(0))
        app.ask("Great, got your address! Now say 'confirm transaction'.")
    } else {
        app.tell("Transaction failed.")
    }
}

fun transactionDecision(app: ApiAiApp) {
    val order = app.buildOrder("<UNIQUE_ORDER_ID>")
            .setCart(app.buildCart().setMerchant("book_store_1", "Book Store")
                    .addLineItems(
                            app.buildLineItem("memoirs_1", "My Memoirs")
                                    .setPrice(TransactionValues.PriceType.ACTUAL, "USD", 3, 990000000)
                                    .setQuantity(1),
                            app.buildLineItem("memoirs_2", "Memoirs of a person")
                                    .setPrice(TransactionValues.PriceType.ACTUAL, "USD", 5, 990000000)
                                    .setQuantity(1),
                            app.buildLineItem("memoirs_3", "Their memoirs")
                                    .setPrice(TransactionValues.PriceType.ACTUAL, "USD", 15, 750000000)
                                    .setQuantity(1),
                            app.buildLineItem("memoirs_4", "Our memoirs")
                                    .setPrice(TransactionValues.PriceType.ACTUAL, "USD", 6, 490000000)
                                    .setQuantity(1)
                    ).setNotes("The Memoir collection"))
            .addOtherItems(
                    app.buildLineItem("subtotal", "Subtotal")
                            .setType(TransactionValues.ItemType.SUBTOTAL)
                            .setQuantity(1)
                            .setPrice(TransactionValues.PriceType.ESTIMATE, "USD", 32, 220000000),
                    app.buildLineItem("tax", "Tax")
                            .setType(TransactionValues.ItemType.TAX)
                            .setQuantity(1)
                            .setPrice(TransactionValues.PriceType.ESTIMATE, "USD", 2, 780000000)
            )
            .setTotalPrice(TransactionValues.PriceType.ESTIMATE, "USD", 35)

    // If in sandbox testing mode, do not require payment
    if (app.isInSandbox()) {
        app.askForTransactionDecision(order)
    } else {
        // To test this sample, uncheck the "Testing in Sandbox Mode" box in the
        // Actions console simulator
        app.askForTransactionDecision(order, ActionPaymentTransactionConfig(
                type = TransactionValues.PaymentType.PAYMENT_CARD.toString(),
                displayName = "VISA-1234",
                deliveryAddressRequired = true)
        )

        /*
          // If using Google provided payment instrument instead
          app.askForTransactionDecision(order, {
            // These will be provided by payment processor, like Stripe,
            // Braintree, or Vantiv
            tokenizationParameters: {},
            cardNetworks: [
              TransactionValues.CardNetwork.VISA,
              TransactionValues.CardNetwork.AMEX
            ],
            prepaidCardDisallowed: false,
            deliveryAddressRequired: false
          })
        */
    }
}

fun transactionDecisionComplete(app: ApiAiApp) {
    if (app.getTransactionDecision()?.userDecision ==
                    TransactionValues.ConfirmationDecision.ACCEPTED.toString()) {
        val googleOrderId = app.getTransactionDecision()?.order?.googleOrderId

        // Confirm order and make any charges in order processing backend
        // If using Google provided payment instrument:
        // let paymentToken = app.getTransactionDecision().order.paymentInfo
        //   .googleProvidedPaymentInstrument.instrumentToken

        app.tell(app.buildRichResponse().addOrderUpdate(
                app.buildOrderUpdate(googleOrderId!!, true)
                        .setOrderState(TransactionValues.OrderState.CONFIRMED, "Order created")
                        .setInfo(TransactionValues.OrderStateInfo.RECEIPT,
                            ReceiptInfo(confirmedActionOrderId = "<UNIQUE_ORDER_ID>")
                        ))
                .addSimpleResponse("Transaction completed! You're all set!"))
    } else if (app.getTransactionDecision()?.userDecision ==
                    TransactionValues.ConfirmationDecision.DELIVERY_ADDRESS_UPDATED.value) {
        return deliveryAddress(app = app)
    } else {
        app.tell("Transaction failed.")
    }
}

val transactionMap = mapOf(
        TRANSACTION_CHECK_NO_PAYMENT to ::transactionCheckNoPayment,
        TRANSACTION_CHECK_ACTION_PAYMENT to ::transactionCheckActionPayment,
        TRANSACTION_CHECK_GOOGLE_PAYMENT to ::transactionCheckGooglePayment,
        TRANSACTION_CHECK_COMPLETE to ::transactionCheckComplete,
        DELIVERY_ADDRESS to ::deliveryAddress,
        DELIVERY_ADDRESS_COMPLETE to ::deliveryAddressComplete,
        TRANSACTION_DECISION_ACTION_PAYMENT to ::transactionDecision,
        TRANSACTION_DECISION_COMPLETE to ::transactionDecisionComplete)
