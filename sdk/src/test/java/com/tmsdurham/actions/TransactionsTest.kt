package com.tmsdurham.actions

import com.google.gson.reflect.TypeToken
import com.winterbe.expekt.expect
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it


val item = LineItem("","")
fun order(json: String) = gson.fromJson(json, Order::class.java)
fun cart(json: String) = gson.fromJson(json, Cart::class.java)
fun lineItem(json: String) = gson.fromJson(json, LineItem::class.java)
fun lineItems(json: String): Array<LineItem> {
    val listType = object : TypeToken<Array<LineItem>>() {}.type

    return gson.fromJson<Array<LineItem>>(json, listType)
}
fun lineItemUpdate(json: String) = gson.fromJson(json, OrderUpdate.LineItemUpdate::class.java)
fun oUpdate(json: String) = gson.fromJson(json, OrderUpdate::class.java)
fun location(json: String) = gson.fromJson(json, TestLocation::class.java)
data class TestLocation(var type: TransactionValues.LocationType = TransactionValues.LocationType.UNKNOWN, var location: Order.Location)

/**
 * Describes the behavior for Order interface.
 */
object TransactionsTest: Spek({
    describe("Order") {
        describe("#constructor") {
            it("should create valid object") {
                var order: Order = Order("test_id")
                expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": []
                }"""))
            }
        }

        describe("#setCart") {
            var order = Order("test_id")

            beforeEachTest {
                order = Order("test_id")
            }

            it("should set the cart") {
                order.setCart(cart("""{
                    "test_property": "test_value"
                }"""))
                expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "cart": {
                    "test_property": "test_value"
                }
                }"""))
            }

            it("should overwrite previously set cart") {
                order.setCart(cart("""{
                    "test_property": "test_old_value"
                }"""))
                order.setCart(cart("""{
                    "test_property": "test_value"
                }"""))
                expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "cart": {
                    "test_property": "test_value"
                }
                }"""))
            }
        }

        describe("#addOtherItems") {
            var order = Order("test_id")

            beforeEachTest {
                order = Order("test_id")
            }

            it("should add a single other item") {
                order.addOtherItems(lineItem("""{
                    "new_item": "new_item"
                }"""))
                expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [{
                    "new_item": "new_item"
                }]
                }"""))
            }

            it("should add multiple other items") {
                order.addOtherItems(*lineItems("""[
                {
                    "new_item": "new_item"
                },
                {
                    "new_item": "new_item_2"
                }]"""))
                expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [
                    {
                        "new_item": "new_item"
                    },
                    {
                        "new_item": "new_item_2"
                    }]
                }"""
                ))
            }

            describe("#setImage") {
                var order = Order("test_id")

                beforeEachTest {
                    order = Order("test_id")
                }

                it("should set the image") {
                    order.setImage("http://image.com", "ALT_TEXT", 100, 150)
                    expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "image": {
                    "url": "http://image.com",
                    "accessibilityText": "ALT_TEXT",
                    "width": 100,
                    "height": 150
                }
                }"""))
                }

                it("should overwrite previously set image") {
                    order.setImage("http://image.com", "ALT_TEXT", 100, 150)
                    order.setImage("http://image.com/2", "ALT_TEXT_2")
                    expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "image": {
                    "url": "http://image.com/2",
                    "accessibilityText": "ALT_TEXT_2"
                }
                }"""))
                }
            }

            describe("#setTOS") {
                var order = Order("test_id")

                beforeEachTest {
                    order = Order("test_id")
                }

                it("should set the TOS") {
                    order.setTermsOfService("http://example.com")
                    expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "termsOfServiceUrl": "http://example.com"
                }"""))
                }

                it("should overwrite previously set TOS") {
                    order.setTermsOfService("http://example.com")
                    order.setTermsOfService("http://example.com/2")
                    expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "termsOfServiceUrl": "http://example.com/2"
                }"""))
                }
            }

            describe("#setTotalPrice") {
                var order = Order("test_id")

                beforeEachTest {
                    order = Order("test_id")
                }

                it("should set the price") {
                    order.setTotalPrice(TransactionValues.PriceType.ACTUAL, "USD", 30, 40)
                    expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "totalPrice": {
                    "type": "ACTUAL",
                    "amount": {
                    "currencyCode": "USD",
                    "units": 30,
                    "nanos": 40
                }
                }
                }"""))
                }

                it("should overwrite previously set price") {
                    order.setTotalPrice(TransactionValues.PriceType.ACTUAL, "USD", 30, 40)
                    order.setTotalPrice(TransactionValues.PriceType.ESTIMATE, "GBP", 60)
                    expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "totalPrice": {
                    "type": "ESTIMATE",
                    "amount": { 
                    "currencyCode": "GBP",
                    "units": 60,
                    "nanos": 0
                }
                }
                }"""))
                }
            }

            describe("#setTime") {
                var order = Order("test_id")

                beforeEachTest {
                    order = Order("test_id")
                }

                it("should set the time") {
                    order.setTime(TransactionValues.TimeType.RESERVATION_SLOT, "SAMPLE_TIME")
                    expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "extension":  {
                    "@type": "type.googleapis.com/google.actions.v2.orders.GenericExtension",
                    time: {
                    "type": "RESERVATION_SLOT",
                    time_iso8601: "SAMPLE_TIME"
                }
                }
                }"""))
                }

                it("should overwrite the previously set time") {
                    order.setTime(TransactionValues.TimeType.RESERVATION_SLOT, "SAMPLE_TIME")
                    order.setTime(TransactionValues.TimeType.RESERVATION_SLOT, "SAMPLE_TIME_2")
                    expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "extension":  {
                    "@type": "type.googleapis.com/google.actions.v2.orders.GenericExtension",
                    time: {
                    "type": "RESERVATION_SLOT",
                    time_iso8601: "SAMPLE_TIME_2"
                }
                }
                }"""))
                }
            }

            describe("#addLocation") {
                var order = Order("test_id")
                val loctionOneJson = """{
                "type": "UNKNOWN",
                "location": {
                "postalAddress": { "notes": "SAMPLE_ADDRESS_"}
            }
            }"""
                val locationTwoJson = """{
                "type": "UNKNOWN",
                "location": {
                "postalAddress": { "notes": "SAMPLE_ADDRESS_2"}
            }
            }"""
                val locationThreeJson = """{
                "type": "UNKNOWN",
                "location": {
                "postalAddress": { "notes": "SAMPLE_ADDRESS_3"}
            }
            }"""

                val locationOne = location(loctionOneJson)

                val locationTwo = location(locationTwoJson)

                val locationThree = location(locationThreeJson)

                beforeEachTest {
                    order = Order("test_id")
                }

                it("should add one location") {
                    order.addLocation(locationOne.type, locationOne.location)
                    expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "extension":  {
                    "@type": "type.googleapis.com/google.actions.v2.orders.GenericExtension",
                    "locations": [$loctionOneJson]
                }
                }"""))
                }

                it("should add a second location") {
                    order.addLocation(locationOne.type, locationOne.location)
                    order.addLocation(locationTwo.type, locationTwo.location)
                    expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "extension":  {
                    "@type": "type.googleapis.com/google.actions.v2.orders.GenericExtension",
                    "locations": [$loctionOneJson, $locationTwoJson]
                }
                }"""))
                }

                it("should not add a third location") {
                    order.addLocation(locationOne.type, locationOne.location)
                    order.addLocation(locationTwo.type, locationTwo.location)
                    order.addLocation(locationThree.type, locationThree.location)
                    expect(order).to.equal(order("""{
                    "id": "test_id",
                    "otherItems": [],
                    "extension":  {
                    "@type": "type.googleapis.com/google.actions.v2.orders.GenericExtension",
                    "locations": [$loctionOneJson, $locationTwoJson]
                }
                }"""))
                }
            }
        }

        /**
         * Describes the behavior for Cart interface.
         */
        describe("Cart") {
            describe("#constructor") {
                it("should create valid object") {
                    var cart = Cart("test_id")
                    expect(cart).to.equal(cart("""{
                    "id": "test_id",
                    "lineItems: [],
                    "otherItems": []
                }"""))
                }
            }

            describe("#setMerchant") {
                var cart = Cart("test_id")

                beforeEachTest {
                    cart = Cart("test_id")
                }

                it("should set the merchant") {
                    cart.setMerchant("merchant_id", "My Merchant")
                    expect(cart).to.equal(cart("""{
                    "id": "test_id",
                    "lineItems: [],
                    "otherItems": [],
                    "merchant": {
                    "id": "merchant_id",
                    "name": "My Merchant"
                }
                }"""))
                }

                it("should overwrite previously set merchant") {
                    cart.setMerchant("merchant_id", "My Merchant")
                    cart.setMerchant("merchant_id_2", "Your Merchant")
                    expect(cart).to.equal(cart("""{
                    "id": "test_id",
                    "lineItems: [],
                    "otherItems": [],
                    "merchant": {
                    "id": "merchant_id_2",
                    "name": "Your Merchant"
                }
                }"""))
                }
            }

            describe("#setNotes") {
                var cart = Cart("test_id")

                beforeEachTest {
                    cart = Cart("test_id")
                }

                it("should set the notes") {
                    cart.setNotes("order notes")
                    expect(cart).to.equal(cart("""{
                    "id": "test_id",
                    "lineItems: [],
                    "otherItems": [],
                    notes: "order notes"
                }"""))
                }

                it("should overwrite previously set notes") {
                    cart.setNotes("order notes")
                    cart.setNotes("order notes 2")
                    expect(cart).to.equal(cart("""{
                    "id": "test_id",
                    "lineItems: [],
                    "otherItems": [],
                    notes: "order notes 2"
                }"""))
                }
            }

            describe("#addLineItems") {
                var cart = Cart("test_id")

                beforeEachTest {
                    cart = Cart("test_id")
                }

                it("should add a single other item") {
                    cart.addLineItems(lineItem("""{
                    "new_item": "new_item"
                }"""))
                    expect(cart).to.equal(cart("""{
                    "id": "test_id",
                    "lineItems: [{
                    "new_item": "new_item"
                }],
                    "otherItems": []
                }"""))
                }

                it("should add multiple other items") {
                    cart.addLineItems(*lineItems("""[
                {
                    "new_item": "new_item"
                },
                {
                    "new_item": "new_item_2"
                }]"""))
                    expect(cart).to.equal(cart("""{
                    "id": "test_id",
                    "lineItems: [
                    {
                        "new_item": "new_item"
                    },
                    {
                        "new_item": "new_item_2"
                    }],
                    "otherItems": []
                }"""))
                }
            }

            describe("#addOtherItems") {
                var cart = Cart("test_id")

                beforeEachTest {
                    cart = Cart("test_id")
                }

                it("should add a single other item") {
                    cart.addOtherItems(lineItem("""{
                    "new_item": "new_item"
                }"""))
                    expect(cart).to.equal(cart("""{
                    "id": "test_id",
                    "lineItems: [],
                    "otherItems": [{
                    "new_item": "new_item"
                }]
                }"""))
                }

                it("should add multiple other items") {
                    cart.addOtherItems(*lineItems("""[
                {
                    "new_item": "new_item"
                },
                {
                    "new_item": "new_item_2"
                }]"""))
                    expect(cart).to.equal(cart("""{
                    "id": "test_id",
                    "lineItems: [],
                    "otherItems": [
                    {
                        "new_item": "new_item"
                    },
                    {
                        "new_item": "new_item_2"
                    }]
                }"""))
                }
            }
        }

        /**
         * Describes the behavior for LineItem interface.
         */
        describe("LineItem") {
            describe("#constructor") {
                it("should create valid object") {
                    var lineItem = LineItem("test_item_id", "test_item")
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item"
                }"""))
                }
            }

            describe("#addSublines") {
                var lineItem = item

                beforeEachTest {
                    lineItem = LineItem("test_item_id", "test_item")
                }

                it("should add a single subline") {
                    lineItem.addSublines(lineItem("""{
                    "new_item": "new_item"
                }"""))
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    sublines: [{
                    "new_item": "new_item"
                }]
                }"""))
                }

                it("should add multiple sublines") {
                    lineItem.addSublines(*lineItems("""[
                {
                    "new_item": "new_item"
                },
                {
                    "new_item": "new_item_2"
                }]"""))
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    sublines: [
                    {
                        "new_item": "new_item"
                    },
                    {
                        "new_item": "new_item_2"
                    }
                    ]
                }"""))
                }
            }

            describe("#setImage") {
                var lineItem = item

                beforeEachTest {
                    lineItem = LineItem("test_item_id", "test_item")
                }

                it("should set the image") {
                    lineItem.setImage("http://image.com", "ALT_TEXT", 100, 150)
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    image: {
                    url: "http://image.com",
                    accessibilityText: "ALT_TEXT",
                    width: 100,
                    height: 150
                }
                }"""))
                }

                it("should overwrite previously set image") {
                    lineItem.setImage("http://image.com", "ALT_TEXT", 100, 150)
                    lineItem.setImage("http://image.com/2", "ALT_TEXT_2")
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    image: {
                    url: "http://image.com/2",
                    accessibilityText: "ALT_TEXT_2"
                }
                }"""))
                }
            }

            describe("#setPrice") {
                var lineItem = item

                beforeEachTest {
                    lineItem = LineItem("test_item_id", "test_item")
                }

                it("should set the price") {
                    lineItem.setPrice(TransactionValues.PriceType.ACTUAL, "USD", 30, 40)
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    "price": { 
                    "type": "ACTUAL",
                    "amount": { 
                    "currencyCode": "USD",
                    "units": 30,
                    "nanos": 40
                }
                }
                }"""))
                }

                it("should overwrite previously set price") {
                    lineItem.setPrice(TransactionValues.PriceType.ACTUAL, "USD", 30, 40)
                    lineItem.setPrice(TransactionValues.PriceType.ESTIMATE, "GBP", 60)
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    "price": { 
                    "type": "ESTIMATE",
                    "amount": { 
                    "currencyCode": "GBP",
                    "units": 60,
                    "nanos": 0
                }
                }
                }"""))
                }
            }

            describe("#setType") {
                var lineItem = LineItem("test_item_id", "test_item")

                beforeEachTest {
                    lineItem = LineItem("test_item_id", "test_item")
                }

                it("should set the type") {
                    lineItem.setType(TransactionValues.ItemType.REGULAR)
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    "type": "REGULAR"
                }"""))
                }

                it("should overwrite previously set type") {
                    lineItem.setType(TransactionValues.ItemType.REGULAR)
                    lineItem.setType(TransactionValues.ItemType.FEE)
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    "type": "FEE"
                }"""))
                }
            }

            describe("#setQuantity") {
                var lineItem = item

                beforeEachTest {
                    lineItem = LineItem("test_item_id", "test_item")
                }

                it("should set the quantity") {
                    lineItem.setQuantity(1)
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    quantity: 1
                }"""))
                }

                it("should overwrite previously set quantity") {
                    lineItem.setQuantity(1)
                    lineItem.setQuantity(2)
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    quantity: 2
                }"""))
                }
            }

            describe("#setDescription") {
                var lineItem = item

                beforeEachTest {
                    lineItem = LineItem("test_item_id", "test_item")
                }

                it("should set the description") {
                    lineItem.setDescription("A great item")
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    "description": "A great item"
                }"""))
                }

                it("should overwrite previously set description") {
                    lineItem.setDescription("A great item")
                    lineItem.setDescription("A good item")
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    "description": "A good item"
                }"""))
                }
            }

            describe("#setOfferId") {
                var lineItem = item

                beforeEachTest {
                    lineItem = LineItem("test_item_id", "test_item")
                }

                it("should set the offerId") {
                    lineItem.setOfferId("offer")
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    "offerId": "offer"
                }"""))
                }

                it("should overwrite previously set offerId") {
                    lineItem.setOfferId("offer")
                    lineItem.setOfferId("24 hr offer")
                    expect(lineItem).to.equal(lineItem("""{
                    "id": "test_item_id",
                    "name": "test_item",
                    "offerId": "24 hr offer"
                }"""))
                }
            }
        }

        /**
         * Describes the behavior for OrderUpdate interface.
         */
        describe("OrderUpdate") {
            describe("#constructor") {
                it("should create valid object with Google order ID") {
                    var orderUpdate: OrderUpdate = OrderUpdate("order_id", true)
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": []
                }"""))
                }

                it("should create valid object with Action order ID") {
                    var orderUpdate: OrderUpdate = OrderUpdate("order_id", false)
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "actionOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": []
                }"""))
                }
            }

            describe("#setTotalPrice") {
                var orderUpdate = OrderUpdate("order_id", true)

                beforeEachTest {
                    orderUpdate = OrderUpdate("order_id", true)
                }

                it("should set the price") {
                    orderUpdate.setTotalPrice(TransactionValues.PriceType.ACTUAL, "USD", 30, 40)
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": [],
                    "totalPrice": {
                    "type": "ACTUAL",
                    "amount": { 
                    "currencyCode": "USD",
                    "units": 30,
                    "nanos": 40
                }
                }
                }"""))
                }

                it("should overwrite previously set price") {
                    orderUpdate.setTotalPrice(TransactionValues.PriceType.ACTUAL, "USD", 30, 40)
                    orderUpdate.setTotalPrice(TransactionValues.PriceType.ESTIMATE, "GBP", 60)
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": [],
                    "totalPrice": {
                    "type": "ESTIMATE",
                    "amount": { 
                    "currencyCode": "GBP",
                    "units": 60,
                    "nanos": 0
                }
                }
                }"""))
                }
            }

            describe("#setOrderState") {
                var orderUpdate = OrderUpdate("order_id", true)

                beforeEachTest {
                    orderUpdate = OrderUpdate("order_id", true)
                }

                it("should set the state") {
                    orderUpdate.setOrderState(TransactionValues.OrderState.CONFIRMED, "Your order was confirmed")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": [],
                    "orderState": {
                    "state": "CONFIRMED",
                    "label": "Your order was confirmed"
                }
                }"""))
                }

                it("should overwrite previously set state") {
                    orderUpdate.setOrderState(TransactionValues.OrderState.CONFIRMED, "Your order was confirmed")
                    orderUpdate.setOrderState(TransactionValues.OrderState.CANCELLED, "Your order was canceled")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": [],
                    "orderState": {
                    "state": "CANCELLED",
                    "label": "Your order was canceled"
                }
                }"""))
                }
            }

            describe("#setUpdateTime") {
                var orderUpdate = OrderUpdate("order_id", true)

                beforeEachTest {
                    orderUpdate = OrderUpdate("order_id", true)
                }

                it("should set the update time") {
                    orderUpdate.setUpdateTime(200, 300)
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": [],
                    updateTime: {
                    "seconds": 200,
                    "nanos": 300
                }
                }"""))
                }

                it("should overwrite previously set update time") {
                    orderUpdate.setUpdateTime(200, 300)
                    orderUpdate.setUpdateTime(100)
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": [],
                    updateTime: {
                    "seconds": 100,
                    "nanos": 0
                }
                }"""))
                }
            }

            describe("#setUserNotification") {
                var orderUpdate = OrderUpdate("order_id", true)

                beforeEachTest {
                    orderUpdate = OrderUpdate("order_id", true)
                }

                it("should set the user notification") {
                    orderUpdate.setUserNotification("Title", "Order updated!")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": [],
                    userNotification: {
                    title: "Title",
                    text: "Order updated!"
                }
                }"""))
                }

                it("should overwrite previously set user notification") {
                    orderUpdate.setUserNotification("Title", "Order updated!")
                    orderUpdate.setUserNotification("Title_2", "Your order updated!")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": [],
                    userNotification: {
                    title: "Title_2",
                    text: "Your order updated!"
                }
                }"""))
                }
            }

            describe("#addOrderManagementAction") {
                var orderUpdate = OrderUpdate("order_id", true)

                beforeEachTest {
                    orderUpdate = OrderUpdate("order_id", true)
                }

                it("should add order management actions") {
                    orderUpdate.addOrderManagementAction(TransactionValues.OrderAction.MODIFY, "Modify here",
                            "http://example.com")
                    orderUpdate.addOrderManagementAction(TransactionValues.OrderAction.CANCEL, "Cancel here",
                            "http://example.com/cancel")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": [{
                    "type": "MODIFY",
                    "button": {
                    title: "Modify here",
                    openUrlAction: {
                    url: "http://example.com"
                }
                }
                }, {
                    "type": "CANCEL",
                    "button": {
                    title: "Cancel here",
                    openUrlAction: {
                    url: "http://example.com/cancel"
                }
                }
                }]
                }"""))
                }
            }

            describe("#setInfo") {
                var orderUpdate = OrderUpdate("order_id", true)

                beforeEachTest {
                    orderUpdate = OrderUpdate("order_id", true)
                }

                it("should set the order update info") {
                    orderUpdate.setInfo(TransactionValues.OrderStateInfo.RECEIPT,
                            """{ "receipt_info": "value" }""")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": [],
                    "receipt": {
                    "receipt_info": "value"
                }
                }"""))
                }

                it("should override previously set order update info") {
                    orderUpdate.setInfo(TransactionValues.OrderStateInfo.RECEIPT,
                            """{ "receipt_info": "value" }""")
                    orderUpdate.setInfo(TransactionValues.OrderStateInfo.REJECTION,
                            """{ "reason": "value" }""")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": [],
                    "rejectionInfo": {
                    "reason": "value"
                }
                }"""))
                }
            }

            describe("#addLineItemPriceUpdate") {
                var orderUpdate = OrderUpdate("order_id", true)

                beforeEachTest {
                    orderUpdate = OrderUpdate("order_id", true)
                }

                it("should add a update for line item") {
                    orderUpdate.addLineItemPriceUpdate("item_id", TransactionValues.PriceType.ACTUAL, "USD", 30, 40,
                            "reason")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {
                    item_id: {
                    "price": { 
                    "type": "ACTUAL",
                    "amount": { 
                    "currencyCode": "USD",
                    "units": 30,
                    "nanos": 40
                }
                },
                    "reason": "reason"
                }
                },
                    "orderManagementActions": []
                }"""))
                }

                it("should fail for item update without reason") {
                    orderUpdate.addLineItemPriceUpdate("item_id", TransactionValues.PriceType.ACTUAL, "USD", 30, 40)
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {},
                    "orderManagementActions": []
                }"""))
                }

                it("should append price update for existing line item") {
                    orderUpdate.lineItemUpdates["item_id"] = lineItemUpdate("""{
                    order"state": "orderState",
                    "reason": "old_reason"
                }""")
                    orderUpdate.addLineItemPriceUpdate("item_id", TransactionValues.PriceType.ACTUAL, "USD", 30, 40,
                            "reason")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {
                    item_id: {
                    "price": { 
                    "type": "ACTUAL",
                    "amount": { 
                    "currencyCode": "USD",
                    "units": 30,
                    "nanos": 40
                }
                },
                    order"state": "orderState",
                    "reason": "reason"
                }
                },
                    "orderManagementActions": []
                }"""))
                }

                it("should fail to update price for existing line item without reason") {
                    orderUpdate.lineItemUpdates["item_id"] = lineItemUpdate("""{
                    order"state": "orderState"
                }""")
                    orderUpdate.addLineItemPriceUpdate("item_id", TransactionValues.PriceType.ACTUAL, "USD", 30, 40)
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {
                    item_id: {
                    order"state": "orderState"
                }
                },
                    "orderManagementActions": []
                }"""))
                }
            }

            describe("#addLineItemStateUpdate") {
                var orderUpdate = OrderUpdate("order_id", true)

                beforeEachTest {
                    orderUpdate = OrderUpdate("order_id", true)
                }

                it("should add a update for line item") {
                    orderUpdate.addLineItemStateUpdate("item_id", TransactionValues.OrderState.CONFIRMED,
                            "Confirmed item", "reason")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {
                    item_id: {
                    "orderState": {
                    "state": "CONFIRMED",
                    "label": "Confirmed item"
                },
                    "reason": "reason"
                }
                },
                    "orderManagementActions": []
                }"""))
                }

                it("should succeed for item update without reason") {
                    orderUpdate.addLineItemStateUpdate("item_id", TransactionValues.OrderState.CONFIRMED,
                            "Confirmed item")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {
                    item_id: {
                    "orderState": {
                    "state": "CONFIRMED",
                    "label": "Confirmed item"
                }
                }
                },
                    "orderManagementActions": []
                }"""))
                }

                it("should append state update for existing line item") {
                    orderUpdate.lineItemUpdates["item_id"] = lineItemUpdate("""{
                    "price": { 
                    "type": "ACTUAL",
                    "amount": { 
                    "currencyCode": "USD",
                    "units": 30,
                    "nanos": 40
                }
                },
                    "reason": "old_reason"
                }""")
                    orderUpdate.addLineItemStateUpdate("item_id", TransactionValues.OrderState.CONFIRMED,
                            "Confirmed item", "reason")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {
                    item_id: {
                    "price": { 
                    "type": "ACTUAL",
                    "amount": { 
                    "currencyCode": "USD",
                    "units": 30,
                    "nanos": 40
                }
                },
                    "orderState": {
                    "state": "CONFIRMED",
                    "label": "Confirmed item"
                },
                    "reason": "reason"
                }
                },
                    "orderManagementActions": []
                }"""))
                }

                it("should succeed to update state for existing line item without reason") {
                    orderUpdate.lineItemUpdates["item_id"] = lineItemUpdate("""{
                    "price": { 
                    "type": "ACTUAL",
                    "amount": { 
                    "currencyCode": "USD",
                    "units": 30,
                    "nanos": 40
                }
                }
                }""")
                    orderUpdate.addLineItemStateUpdate("item_id", TransactionValues.OrderState.CONFIRMED,
                            "Confirmed item")
                    expect(orderUpdate).to.equal(oUpdate("""{
                    "googleOrderId": "order_id",
                    "lineItemUpdates": {
                    item_id: {
                    "price": { 
                    "type": "ACTUAL",
                    "amount": { 
                    "currencyCode": "USD",
                    "units": 30,
                    "nanos": 40
                }
                },
                    "orderState": {
                    "state": "CONFIRMED",
                    "label": "Confirmed item"
                }
                }
                },
                    "orderManagementActions": []
                }"""))
                }
            }
        }
    }
})
