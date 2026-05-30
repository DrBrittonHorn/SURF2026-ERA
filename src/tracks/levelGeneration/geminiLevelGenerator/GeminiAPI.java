package tracks.levelGeneration.geminiLevelGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import tools.com.google.gson.JsonObject;
import tools.com.google.gson.JsonParser;



public class GeminiAPI {
    
    public static String generateText(String prompt){
        return generateText(prompt, "gemini-3.1-flash-lite");
    }

    public static String generateText(String prompt, String modelName){
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
        .header("Content-Type", "application/json; charset=UTF-8")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
        .build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            JsonObject jsonObject = new JsonParser().parse(response.body()).getAsJsonObject();
            
            if (jsonObject
                    .getAsJsonArray("candidates") == null){
                        System.out.println(response.body());
                        //throw new Exception("API Error!");
                        return null;
                    }
            
            String responseString = jsonObject
                    .getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
            return responseString;
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
    /*
    public static void main(String args[]){
        // Main function to test if the calls are working
        System.out.println(generateText("Why is Neptune Blue?"));
    }
        */
}
