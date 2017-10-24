package com.tmsdurham.actions

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.tmsdurham.dialogflow.Data
import com.tmsdurham.dialogflow.google.GoogleData

class DataTypeAdapter(val gson: Gson) : TypeAdapter<Data>() {
    override fun write(out: JsonWriter, value: Data) {
        value.forEach {
            out.beginObject()
            out.name(it.key)
            if (it.value is GoogleData) {
                out.jsonValue(gson.toJson(it.value))
            }
        }

    }

    override fun read(reader: JsonReader): Data {
        var token = reader.peek()
        val data = Data()
        if (token == JsonToken.BEGIN_OBJECT) {
            reader.beginObject()
            while (!reader.peek().equals(JsonToken.END_OBJECT)) {
                if (reader.nextName() == "google") {
                    data.google = gson.fromJson(reader, GoogleData::class.java)
                } else {
                    reader.skipValue()
                }

            }
            reader.endObject()
        }
        return data
    }

}

class OrderUpdateTypeAdapter(val gson: Gson) : TypeAdapter<OrderUpdate>() {
    override fun write(out: JsonWriter?, value: OrderUpdate?) {
    }

    override fun read(reader: JsonReader): OrderUpdate {
        var token = reader.peek()
        val orderUpdate = OrderUpdate()
        if (token == JsonToken.BEGIN_OBJECT) {
            reader.beginObject()
            while (!reader.peek().equals(JsonToken.END_OBJECT)) {
                if (reader.peek().equals(JsonToken.NAME)) {
                    val name = reader.nextName()
                    when (name) {
                        "googleOrderId" -> orderUpdate.googleOrderId = reader.nextString()
                        "actionOrderId" -> orderUpdate.actionOrderId = reader.nextString()
                        "orderState" -> orderUpdate.orderState = gson.fromJson(reader, OrderState::class.java)
                        "lineItemUpdates" -> {
                            val type = object: TypeToken<MutableMap<String, OrderUpdate.LineItemUpdate>>(){}.type
                            orderUpdate.lineItemUpdates = gson.fromJson(reader, type)
                        }
                        "updateTime" -> orderUpdate.updateTime = gson.fromJson(reader, OrderUpdate.UpdateTime::class.java)
                        "orderManagementActions" -> {
                            val type = object: TypeToken<MutableList<OrderUpdate.OrderManagementAction>>() {}.type
                            orderUpdate.orderManagementActions = gson.fromJson(reader, type)
                        }
                        "userNotification" -> orderUpdate.userNotification = gson.fromJson(reader, OrderUpdate.UserNotification::class.java)
                        "totalPrice" -> orderUpdate.totalPrice = gson.fromJson(reader, GoogleData.TotalPrice::class.java)
                        else -> {
                            orderUpdate.put(name, gson.fromJson(reader, HashMap<String, Object>()::class.java))
                        }
                    }
                }
            }
            reader.endObject()
        }
        return orderUpdate
    }
}
