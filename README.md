# Actions On Google Client Library


Unofficial Kotlin SDK for building Actions on Google on the JVM.  This is a port of the [official nodejs SDK](https://github.com/actions-on-google/actions-on-google-nodejs) to Kotlin.  This can also be used from Java.

__Goals of this project__:

 * Port the actions-on-google SDK to Kotlin so Kotlin and Java developers can quickly start building Actions for Google Assistant.
 * match nodejs API
 * match implementation of nodejs closely so code can be maintained easily as features are added
 * pass all tests from nodejs SDK (using Spek framework)
 * support Api.Ai and Actions SDK (Api.Ai is top priority)
 * port samples
 * release artifact to maven central

# *** Work in progress ***

This project is in early stages and is a work in progress.  Currently the Multimodal API and conversation-components sample are 100% ported. You are welcome to use, make contributions, and report issues on this project.

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

Currently this lib is not synced to maven central, however it will be soon.  For now you must add this repository:

    repositories {
        mavenCentral()
            maven {
                url 'https://dl.bintray.com/patjackson52/maven/'
            }
        }
    }
    
    dependencies {
        compile 'com.tmsdurham.actions:actions-on-google:0.2'
    }


Looking at Webhook.kt is a contains an example of how to make a basic webhook using Google App Engine Standard Environment.

MORE COMING

## License
See LICENSE.md.
