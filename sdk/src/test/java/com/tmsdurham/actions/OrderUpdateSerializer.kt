package com.tmsdurham.actions

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.ticketmaster.apiai.google.GoogleData


class OrderUpdateTypeAdapter(gson: Gson) : TypeAdapter<OrderUpdate>() {
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
