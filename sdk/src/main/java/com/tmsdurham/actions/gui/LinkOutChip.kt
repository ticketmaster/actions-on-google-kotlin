package com.tmsdurham.actions.gui

data class LinkOutChip(
        val destinationName: String = "",
        val url: String = "") {

    fun isEmpty() = this == emptyLinkoutChip
    fun isNotEmpty() = !isEmpty()

    companion object {
        val emptyLinkoutChip = LinkOutChip()
    }
}
