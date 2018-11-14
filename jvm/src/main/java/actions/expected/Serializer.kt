package actions.expected

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter


actual object Serializer {
    private val mapType by lazy { object : TypeToken<MutableMap<String, Any?>>() {}.type }
    lateinit var aogGson: Gson

    actual fun stringifyConversationToken(data: Any?): ConversationTokenData {
        val token = if (data == null)
            ConversationTokenData(data = Object())
        else
            ConversationTokenData(data)
        return token
    }

    actual fun serialize(any: Any?): String? {
        if (any == null) return "{}"
        return aogGson.toJson(any)
    }


    actual fun deserializeMap(json: String): MutableMap<String, Any?> {
        return aogGson.fromJson(json, mapType)
    }

}

class MapTypeAdapter(val gson: Gson): TypeAdapter<Map<String, Any?>>() {
    override fun write(out: JsonWriter, value: Map<String, Any?>) {
        value.forEach {
            out.beginObject()
            out.name(it.key)
            out.jsonValue(gson.toJson(it.value))
        }
    }

    override fun read(`in`: JsonReader): Map<String, Any?> {
        return mapOf()
    }

}