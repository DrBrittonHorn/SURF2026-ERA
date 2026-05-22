package tracks.levelGeneration.languageModelLevelGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import tools.com.google.gson.JsonObject;

public class GeminiAPI {
    
    public static String generateText(String prompt){
        return generateContent(prompt, "gemini-3.1-flash-lite");
    }

    public static String generateContent(String prompt, String modelName){
        String apiKey = System.getenv("SURF-API-KEY-2");
        //System.out.println(apiKey);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;
        String requestBody = """
                {
                    "contents" : [{
                        "parts": [{"text":"prompt"}]}]
                    }
                """;
        requestBody = requestBody.replace("prompt", prompt);
        //System.out.println(requestBody);
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // EXTREMELY subpar way to do this. //TODO
            // Ideally, we would use a json text to json library, but I haven't figured out how to import non-java libraries yet.
            String text = response.body();
            text = text.split("text")[1].substring(4);
            text = text.split("\",\n\"thoughtSignature")[0];
            return text;
        }
        catch (IOException e){
            System.out.println("An IO Exception occured!");
            e.printStackTrace();
        }
        catch (InterruptedException e){
            System.out.println("An interrupted Exception occured!");
            e.printStackTrace();
        }
        return requestBody;
    }

    public static void main(String args[]){
        // Main function to test if the calls are working
        System.out.println(generateText("Why is Neptune Blue?"));
    }
}
