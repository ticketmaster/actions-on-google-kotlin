# Actions On Google Client Library


Unofficial Kotlin SDK for building Actions on Google.  This is a port of the official nodejs SDK to Kotlin.  

__Goals__:

 * Port the actions-on-google SDK to Kotlin so Kotlin and Java developers can quickly start building Actions for Google Assistant.
 * match nodejs API
 * match implementation of nodejs closely so code can be maintained easily as features are added
 * pass all tests from nodejs SDK (using Spek framework)
 * support Api.Ai and Actions SDK (Api.Ai is top priority)
 * release artifact to maven central

# *** Work in progress ***

This project is in early stages and is a work in progress.  Currently the conversation-components are functional. You are welcome to use or make contributions

This Actions On Google client library makes it easy to create your apps for the Google Assistant.

The client library supports both the Actions SDK webhook and API.ai fulfillment.

Sample of what actions-on-google-koltin looks like:

    fun welcome(app: MyAction) =
        app.ask(app.buildRichResponse()
                .addSimpleResponse(speech = "Hi there!", displayText = "Hello there!")
                .addSimpleResponse(
                        speech = """I can show you basic cards, lists and carousels as well as
                    "suggestions on your phone""",
                        displayText = """"I can show you basic cards, lists and carousels as
                    "well as suggestions"""")
                .addSuggestions("Basic Card", "List", "Carousel", "Suggestions"))
                
    fun normalAsk(app: MyAction) = app.ask("Ask me to show you a list, carousel, or basic card")

    fun suggestions(app: MyAction) {
        app.ask(app
            .buildRichResponse()
            .addSimpleResponse("This is a simple response for suggestions")
            .addSuggestions("Suggestion Chips")
            .addSuggestions("Basic Card", "List", "Carousel")
            .addSuggestionLink("Suggestion Link", "https://assistant.google.com/"))
    }
    
    val actionMap = mapOf(
        WELCOME to ::welcome,
        SUGGESTIONS to ::suggestions)
      
    
    @WebServlet(name = "ActionsWebhook", value = "/test")
    class WebHook : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
            GAction(req, resp, Parameters::class.java).handleRequest(actionMap)
        }
    }



## Setup Instructions

Currently you will have to build this lib yourself or include the source.  Maven artifacts will be published soon.

Looking at Webhook.kt is a good starting point.

## License
See LICENSE.md.
