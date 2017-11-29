package com.tmsdurham.actions.actions

import com.tmsdurham.dialogflow.*

data class ActionRequest(val conversation: Conversation? = null,
                         var inputs: MutableList<Input>? = null,
                         var user: User? = null,
                         var device: Device? = null,
                         var isInSandbox: Boolean? = null,
                         var surface: Surface? = null,
                         var availableSurfaces: MutableList<Surface>? = null,
                         var sender: Sender? = null)

data class Conversation(var type: String? = null,
                        var conversationToken: String? = null,
                        var conversationId: String? = null,
                        var inputs: MutableList<Input>? = null)

data class Input(var textValue: String? = null,
                 var intent: String? = null,
                 var arguments: MutableList<Arguments>? = null,
                 var rawInputs: MutableList<RawInput>? = null)

data class Sender(val id: String? = null)
