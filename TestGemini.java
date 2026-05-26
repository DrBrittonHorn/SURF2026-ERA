import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


// Basic example functionality for calling the Gemini API
public class TestGemini{
    
    public static void main(String[] args){
        String apiKey = System.getenv("SURF-API-KEY-2");
        String modelName = "gemini-3.1-flash-lite";
        System.out.println(apiKey);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;
        String requestBody = """
                {
                    "contents" : [{
                        "parts": [{"text": "Why is the ocean blue?"}]}]
                    }
                """;
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        }
        catch (IOException e){
            System.out.println("An IO Exception occured!");
            e.printStackTrace();
        }
        catch (InterruptedException e){
            System.out.println("An interrupted Exception occured!");
            e.printStackTrace();
        }
        
        
    }
    

}

// Reference for API request format

/*
        curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent" \
  -H 'Content-Type: application/json' \
  -H 'X-goog-api-key: api_key_here' \
  -X POST \
  -d '{
    "contents": [
      {
        "parts": [
          {
            "text": "Explain how AI works in a few words"
          }
        ]
      }
    ]
  }'
        """;
        */