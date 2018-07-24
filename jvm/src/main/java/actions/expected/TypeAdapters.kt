package actions.expected

import actions.service.actionssdk.api.*
import actions.service.actionssdk.conversation.response.OrderUpdate
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.google.gson.Gson


class OrderUpdateTypeAdapter(val gson: Gson) : TypeAdapter<OrderUpdate>() {
    override fun write(out: JsonWriter?, value: OrderUpdate?) {
    }

    override fun read(reader: JsonReader): OrderUpdate {
        val token = reader.peek()
        val orderUpdate = OrderUpdate()
        if (token == JsonToken.BEGIN_OBJECT) {
            reader.beginObject()
            while (!reader.peek().equals(JsonToken.END_OBJECT)) {
                if (reader.peek().equals(JsonToken.NAME)) {
                    val name = reader.nextName()
                    when (name) {
                        "googleOrderId" -> orderUpdate.googleOrderId = reader.nextString()
                        "actionOrderId" -> orderUpdate.actionOrderId = reader.nextString()
                        "orderDate" -> orderUpdate.orderDate = reader.nextString()
                        "locale" -> orderUpdate.locale = reader.nextString()
                        "orderState" -> orderUpdate.orderState = gson.fromJson(reader, GoogleActionsV2OrdersOrderState::class.java)
                        "lineItemUpdates" -> {
                            val type = object : TypeToken<MutableMap<String, GoogleActionsV2OrdersLineItemUpdate>>() {}.type
                            orderUpdate.lineItemUpdates = gson.fromJson(reader, type)
                        }
                        "updateTime" -> orderUpdate.updateTime = gson.fromJson(reader, GoogleTypeTimeOfDay::class.java)
                        "orderManagementActions" -> {
                            val type = object : TypeToken<MutableList<GoogleActionsV2OrdersOrderUpdateAction>>() {}.type
                            orderUpdate.orderManagementActions = gson.fromJson(reader, type)
                        }
                        "userNotification" -> orderUpdate.userNotification = gson.fromJson(reader, GoogleActionsV2OrdersOrderUpdateUserNotification::class.java)
                        "totalPrice" -> orderUpdate.totalPrice = gson.fromJson(reader, GoogleActionsV2OrdersPrice::class.java)
                        else -> {
                            if (reader.peek().equals(JsonToken.STRING)) {
                                orderUpdate.put(name, reader.nextString())
                            } else if (reader.peek().equals(JsonToken.BEGIN_OBJECT)) {
                                orderUpdate.put(name, gson.fromJson(reader, HashMap<String, Object>()::class.java))
                            }
                        }
                    }
                }
            }
            reader.endObject()
        }
        return orderUpdate
    }
}