# Actions On Google Client Library


This is a port of the [official Node.js SDK](https://github.com/actions-on-google/actions-on-google-nodejs) to Kotlin.  This can also be used from Java and any JVM language.

__Quick Facts__

 * Port of the actions-on-google SDK to Kotlin.  Kotlin and Java developers can quickly start building Actions for Google Assistant.
 * Closely matches Node.js Client Library API
 * Closely matches implementation of Node.js sdk so code can be maintained easily as features are added
 * All tests ported from nodejs SDK (using [Spek framework](http://spekframework.org/)) & 100% passing
 * Api.Ai and Actions SDK support
 * Conversation Components & Transaction Sample ported
 * Supports v2 of Actions on Google API (if v1 is needed, make an issue please)

## Setup Instructions

This library is available on jCenter and Maven Central.  If your using gradle simply add the dependency as follows:

__Gradle:__

    repositories {
            jCenter()
        }
    }
    
    dependencies {
        compile 'com.tmsdurham.actions:actions-on-google:1.2'
    }

The above artifact should fit the needs of most developers, however, if you are not using ```java.servlet.http.HttpServlet```, or do not want to use [Gson](https://github.com/google/gson) for deserialization, you can use the ```actions-on-google-core lib```.  For example how to use the core library, reading through the sdk-gson-servlet module.    

   	compile 'com.tmsdurham.actions:actions-on-google-core:1.2'. //only if not using Servlets




### Using Kotlin

    fun welcome(app: ApiAiApp) =
        app.ask(app.buildRichResponse()
                .addSimpleResponse(speech = "Hi there!", displayText = "Hello there!")
                .addSimpleResponse(
                        speech = """I can show you basic cards, lists and carousels as well as
                    "suggestions on your phone""",
                        displayText = """"I can show you basic cards, lists and carousels as
                    "well as suggestions"""")
                .addSuggestions("Basic Card", "List", "Carousel", "Suggestions"))
                
    fun normalAsk(app: ApiAiApp) = app.ask("Ask me to show you a list, carousel, or basic card")

    fun suggestions(app: ApiAiApp) {
        app.ask(app
            .buildRichResponse()
            .addSimpleResponse("This is a simple response for suggestions")
            .addSuggestions("Suggestion Chips")
            .addSuggestions("Basic Card", "List", "Carousel")
            .addSuggestionLink("Suggestion Link", "https://assistant.google.com/"))
    }
    
    val actionMap = mapOf(
        WELCOME to ::welcome,
        NORMAL_ASK to ::normalAsk,
        SUGGESTIONS to ::suggestions)
      
    
    @WebServlet("/conversation")
    class WebHook : HttpServlet() {

    	override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        	ApiAiAction(req, resp).handleRequest(actionMap)
       }
    }

### Using Java

	@WebServlet("/conversation/java")
	public class ConversationComponentsSampleJava extends HttpServlet {
    	private static final Logger logger = Logger.getAnonymousLogger();

		Function1<ApiAiApp, Object> welcome = app -> {
        	app.ask(app.buildRichResponse()
                .addSimpleResponse("Hi there from Java!", "Hello there from Java!")
                .addSimpleResponse(
                        "I can show you basic cards, lists and carousels as well as suggestions on your phone",
                        "I can show you basic cards, lists and carousels as well as suggestions")
                .addSuggestions("Basic Card", "List", "Carousel", "Suggestions"), null);
        	return Unit.INSTANCE;
    	};

    	Function1<ApiAiApp, Object> normalAsk = app ->
       	     app.ask("Ask me to show you a list, carousel, or basic card");

    	Function1<ApiAiApp, Object> suggestions = app ->
       	     app.ask(app.buildRichResponse(null)
                    .addSimpleResponse("This is a simple response for suggestions", null)
                    .addSuggestions("Suggestion Chips")
                    .addSuggestions("Basic Card", "List", "Carousel")
                    .addSuggestionLink("Suggestion Link", "https://assistant.google.com/"));

		private Map<String, Function1<String, Object>> intentMap = new HashMap() {{
        	put(ConversationComponentsSampleKt.WELCOME, welcome);
        	put(ConversationComponentsSampleKt.NORMAL_ASK, normalAsk);
        	put(ConversationComponentsSampleKt.SUGGESTIONS, suggestions);
    	}};

    	@Override
    	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         	 ApiAiAction action = new ApiAiAction(req, resp);
       	 	 action.handleRequest(intentMap);
    	}
    }

## License
See [LICENSE.md.](https://github.com/TicketmasterMobileStudio/actions-on-google-kotlin/blob/master/LICENSE)
