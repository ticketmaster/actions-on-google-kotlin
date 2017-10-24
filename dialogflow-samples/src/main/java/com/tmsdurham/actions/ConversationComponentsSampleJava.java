package com.tmsdurham.actions;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import main.java.com.tmsdurham.dialogflow.sample.DialogflowAction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.tmsdurham.actions.ConversationComponentsSampleKt.*;

/**
 * Conversation sample in Java.
 */
@WebServlet("/conversation/java")
public class ConversationComponentsSampleJava extends HttpServlet {
    private static final Logger logger = Logger.getAnonymousLogger();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DialogflowAction action = new DialogflowAction(req, resp);
        action.handleRequest(intentMap);
    }

    Function1<DialogflowApp, Object> welcome = app -> {
        app.ask(app.buildRichResponse()
                .addSimpleResponse("Hi there from Java!", "Hello there from Java!")
                .addSimpleResponse(
                        "I can show you basic cards, lists and carousels as well as suggestions on your phone",
                        "I can show you basic cards, lists and carousels as well as suggestions")
                .addSuggestions("Basic Card", "List", "Carousel", "Suggestions"), null);
        return Unit.INSTANCE;
    };

    Function1<DialogflowApp, Object> normalAsk = app ->
            app.ask("Ask me to show you a list, carousel, or basic card");

    Function1<DialogflowApp, Object> suggestions = app ->
            app.ask(app.buildRichResponse(null)
                    .addSimpleResponse("This is a simple response for suggestions", null)
                    .addSuggestions("Suggestion Chips")
                    .addSuggestions("Basic Card", "List", "Carousel")
                    .addSuggestionLink("Suggestion Link", "https://assistant.google.com/"));

    Function1<DialogflowApp, Object> basicCard = app ->
            app.ask(app.buildRichResponse()
                    .addSimpleResponse("This is the first simple response for a basic card")
                    .addSuggestions(
                            "Basic Card", "List", "Carousel", "Suggestions")
                    // Create a basic card and add it to the rich response
                    .addBasicCard(app.buildBasicCard("This is a basic card.  Text in a "
                            + "basic card can include \"quotes\" and most other unicode characters "
                            + "including emoji 📱.  Basic cards also support some markdown "
                            + "formatting like *emphasis* or _italics_, **strong** or __bold__, "
                            + "and ***bold itallic*** or ___strong emphasis___ as well as other things "
                            + "like line  \nbreaks") // Note the two spaces before "\n" required for a"
                            // line break to be rendered in the card
                            .setSubtitle("This is a subtitle")
                            .setTitle("Title: this is a title")
                            .addButton("This is a button", "https://assistant.google.com/")
                            .setImage(IMG_URL_AOG, "Image alternate text"))
                    .addSimpleResponse("This is the 2nd simple response ",
                            "This is the 2nd simple response")
            );


    Function1<DialogflowApp, Object> list = app ->
            app.askWithList(app.buildRichResponse()
                            .addSimpleResponse("This is a simple response for a list")
                            .addSuggestions("Basic Card", "List", "Carousel", "Suggestions"),
                    // Build a list
                    app.buildList("List Title")
                            // Add the first item to the list
                            .addItems(app.buildOptionItem(SELECTION_KEY_ONE,
                                    "synonym of title 1", "synonym of title 2", "synonym of title 3")
                                    .setTitle("Title of First List Item")
                                    .setDescription("This is a description of a list item")
                                    .setImage(IMG_URL_AOG, "Image alternate text"))
                            // Add the second item to the list
                            .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_HOME,
                                    "Google Home Assistant", "Assistant on the Google Home")
                                    .setTitle("Google Home")
                                    .setDescription("Google Home is a voice-activated speaker powered by the Google Assistant.")
                                    .setImage(IMG_URL_GOOGLE_HOME, "Google Home")
                            )
                            // Add third item to the list
                            .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_PIXEL,
                                    "Google Pixel XL", "Pixel", "Pixel XL")
                                    .setTitle("Google Pixel")
                                    .setDescription("Pixel. Phone by Google.")
                                    .setImage(IMG_URL_GOOGLE_PIXEL, "Google Pixel")
                            )
                            // Add last item of the list
                            .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_ALLO)
                                    .setTitle("Google Allo")
                                    .setDescription("Introducing Google Allo, a smart messaging app" +
                                            "that helps you say more and do more.")
                                    .setImage(IMG_URL_GOOGLE_ALLO, "Google Allo Logo")
                                    .addSynonyms("Allo")
                            )
            );

    // Carousel
    Function1<DialogflowApp, Object> carousel = app ->
            app.askWithCarousel(app.buildRichResponse()
                            .addSimpleResponse("This is a simple response for a carousel")
                            .addSuggestions("Basic Card", "List", "Carousel", "Suggestions"),
                    app.buildCarousel()
                            // Add the first item to the carousel
                            .addItems(app.buildOptionItem(SELECTION_KEY_ONE,
                                    "synonym of title 1", "synonym of title 2", "synonym of title 3")
                                    .setTitle("Title of First List Item")
                                    .setDescription("This is a description of a carousel item")
                                    .setImage(IMG_URL_AOG, "Image alternate text"))
                            // Add the second item to the carousel
                            .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_HOME,
                                    "Google Home Assistant", "Assistant on the Google Home")
                                    .setTitle("Google Home")
                                    .setDescription("Google Home is a voice-activated speaker powered by"
                                            + "the Google Assistant.")
                                    .setImage(IMG_URL_GOOGLE_HOME, "Google Home")
                            )
                            // Add third item to the carousel
                            .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_PIXEL,
                                    "Google Pixel XL", "Pixel", "Pixel XL")
                                    .setTitle("Google Pixel")
                                    .setDescription("Pixel. Phone by Google.")
                                    .setImage(IMG_URL_GOOGLE_PIXEL, "Google Pixel")
                            )
                            // Add last item of the carousel
                            .addItems(app.buildOptionItem(SELECTION_KEY_GOOGLE_ALLO)
                                    .setTitle("Google Allo")
                                    .setDescription("Introducing Google Allo, a smart messaging app" +
                                            "that helps you say more and do more.")
                                    .setImage(IMG_URL_GOOGLE_ALLO, "Google Allo Logo")
                                    .addSynonyms("Allo")
                            )
            );

    // React to list or carousel selection
    Function1<DialogflowApp, Object> itemSelected = app -> {
        app.getIntent();
        Object param = app.getSelectedOption();
        logger.info("USER SELECTED: $param");
        if (param == null) {
            app.ask("You did not select any item from the list or carousel");
        } else {
            switch ((String) param) {
                case SELECTION_KEY_ONE:
                    app.ask("You selected the first item in the list or carousel");
                    break;
                case SELECTION_KEY_GOOGLE_HOME:
                    app.ask("You selected the Google Home!");
                    break;
                case SELECTION_KEY_GOOGLE_PIXEL:
                    app.ask("You selected the Google Pixel!");
                    break;
                case SELECTION_KEY_GOOGLE_ALLO:
                    app.ask("You selected Google Allo!");
                    break;
                default:
                    app.ask("You selected an unknown item from the list or carousel");
            }
        }
        return Unit.INSTANCE;
    };

    // Receive a rich response from Dialogflow and modify it
    Function1<DialogflowApp, Object> cardBuilder = app ->
            app.ask(app.getIncomingRichResponse()
                    .addBasicCard(app.buildBasicCard("Actions on Google let you build for"
                            + "the Google Assistant.Reach users right when they need you.Users don’t"
                            + "need to pre-enable skills or install new apps.  \n  \nThis was written in the fulfillment webhook!")
                            .setSubtitle("Engage users through the Google Assistant")
                            .setTitle("Actions on Google")
                            .addButton("Developer Site", "https://developers.google.com/actions/")
                            .setImage("https://lh3.googleusercontent.com/Z7LtU6hhrhA-5iiO1foAfGB"
                                    + "75OsO2O7phVesY81gH0rgQFI79sjx9aRmraUnyDUF_p5_bnBdWcXaRxVm2D1Rub92"
                                    + "L6uxdLBl=s1376", "Actions on Google")));

    // Leave conversation with card
    Function1<DialogflowApp, Object> byeCard = app ->
            app.tell(app.buildRichResponse()
                    .addSimpleResponse("Goodbye, World!")
                    .addBasicCard(app.buildBasicCard("This is a goodbye card.")));

    Function1<DialogflowApp, Object> byeResponse = app ->
            app.tell("Okay see you later", "OK see you later!");

    Function1<DialogflowApp, Object> normalBye = app -> app.tell("Okay see you later!");

    private Map<String, Function1<String, Object>> intentMap = new HashMap() {{
        put(ConversationComponentsSampleKt.WELCOME, welcome);
        put(ConversationComponentsSampleKt.NORMAL_ASK, normalAsk);
        put(ConversationComponentsSampleKt.BASIC_CARD, basicCard);
        put(ConversationComponentsSampleKt.LIST, list);
        put(ConversationComponentsSampleKt.ITEM_SELECTED, itemSelected);
        put(ConversationComponentsSampleKt.CAROUSEL, carousel);
        put(ConversationComponentsSampleKt.SUGGESTIONS, suggestions);
        put(ConversationComponentsSampleKt.BYE_CARD, byeCard);
        put(ConversationComponentsSampleKt.NORMAL_BYE, normalBye);
        put(ConversationComponentsSampleKt.BYE_RESPONSE, byeResponse);
        put(ConversationComponentsSampleKt.CARD_BUILDER, cardBuilder);
    }};

}

