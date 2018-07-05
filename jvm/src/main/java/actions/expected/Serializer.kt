package actions.expected

actual object Serializer {
    actual fun stringifyConversationToken(data: Any?): ConversationTokenData {
        val token = if (data == null)
            ConversationTokenData(data = Object())
        else
            ConversationTokenData(data)
        return token
    }

    actual fun serialize(any: Any?): String? {
        return gson.toJson(any)
    }

    actual fun <T> deserialize(json: String): T {
//        return gson.fromJson<T>(json)
        return Any() as T
    }

}