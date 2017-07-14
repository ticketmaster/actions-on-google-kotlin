package com.tmsdurham.actions.gui

import com.ticketmaster.apiai.google.GoogleData
import com.ticketmaster.banjo.domain.bot.output.gui.TransactionCompleteData
import com.tmsdurham.actions.SimpleResponse


data class Data(
        var simpleResponses: MutableList<SimpleResponse> = mutableListOf(),
        var suggestions: Suggestions = Suggestions.emptySuggestion,
        var basicCard: BasicCard = BasicCard.emptyBasicCard,
        var displayList: DisplayList = DisplayList.emptyDisplayList,
        var carousel: Carousel = Carousel.emptyCarousel,
        var linkOutChip: LinkOutChip = LinkOutChip.emptyLinkoutChip,
        var askForTransactionRequirements: TransactionCheck = TransactionCheck.emptyTransactionCheck,
        var paymentOption: GoogleData.PaymentOptions? = null,
        //        var transactionProposedOrder: TransactionProposedOrder? = null,
        var transactionCompleteData: TransactionCompleteData? = null,
        var permissionRequest: PermissionRequest? = null,
        var askForSignIn: Boolean = false,
        // Alexa-specific variables
        var shouldShowLinkAccountCard: Boolean = false,
        var shouldEndSession: Boolean = false) {

    fun suggestions(vararg chips: String): Unit {
        suggestions.chips = chips.map { Chip(it) }
    }

    fun addSimpleResponse(init: SimpleResponse.() -> Unit): Unit {
        val simpleResonse = SimpleResponse()
        simpleResonse.init()
        simpleResponses.add(simpleResonse)
    }

    fun basicCard(init: BasicCard.() -> Unit): Unit = basicCard.init()
    fun displayList(init: DisplayList.() -> Unit): Unit = displayList.init()
    fun carousel(init: Carousel.() -> Unit): Unit = carousel.init()
    fun linkOutChip(init: LinkOutChip.() -> Unit): Unit = linkOutChip.init()
    fun askForTransactionRequirements(init: TransactionCheck.() -> Unit): Unit = askForTransactionRequirements.init()

    companion object {
        val emptyData = Data()
    }
}

class PermissionRequest(val reason: String, vararg val permissions: Permission)

enum class Permission {
    NAME,
    DEVICE_PRECISE_LOCATION,
    DEVICE_COARSE_LOCATION
}

fun Array<out Permission>.toListOrStrings() = this.map { it.toString() }

//data class SimpleResponse(var speech: String = "", var displayText: String = "") {
//    fun isEmpty() = speech.isNullOrEmpty() && displayText.isNullOrBlank()
//}
//
