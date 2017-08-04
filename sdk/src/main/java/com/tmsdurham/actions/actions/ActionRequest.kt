package com.tmsdurham.actions.actions

import com.ticketmaster.apiai.RawInput

data class ActionRequest(val conversation: Conversation? = null,
                         var inputs: MutableList<Input>? = null)

data class Conversation(var type: String? = null,
                        val conversationToken: ConversationToken? = null,
                        var conversationId: String? = null,
                        var inputs: MutableList<Input>? = null)

data class Input(var textValue: String? = null,
                 var intent: String? = null,
                 var rawInputs: MutableList<RawInput>? = null)

data class ConversationToken(var state: String? = null,
                             var data: Any?)