package tracks.levelGeneration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

import tools.com.google.gson.JsonObject;
import tools.com.google.gson.JsonParser;

public class OllamaAPI {
    public static String defaultModel = "llama3.1:8b"; //All are too poor quality for level generation
    
    //public static String defaultModel = "qwen3.5:27b";
    //public static String defaultModel = "llama3.1:70b";

    
    public static String generateText(String prompt) throws IOException{
        return generateText(prompt, defaultModel);
    }
    
    public static String generateText(String prompt, String modelName) throws IOException{
        String link = "http://localhost:11434/api/generate";
        URL url = new URL("http://localhost:11434/api/generate");
        
        // Set up the URL and connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        // Set up JSON body
        /* String jsonInputString = String.format(
            "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
            modelName, prompt
        ); */
        
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", modelName);
        requestBody.addProperty("prompt", prompt);   // Gson handles all escaping
        requestBody.addProperty("stream", false);

        String jsonInputString = requestBody.toString();


        //System.out.println(jsonInputString);

        try(OutputStream os = conn.getOutputStream()){
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Get Response Code
        int code = conn.getResponseCode();
        //System.out.println("Response Code: " + code);

        // Read the response body
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null){
            response.append(line);
        }
        in.close();

        //System.out.println("Response Body: " + response.toString());
        String responseText = new JsonParser().parse(response.toString()).getAsJsonObject().get("response").getAsString();
        return responseText;

    }


    public static void main(String[] args){
        try {
            System.out.println(generateText("Why are sapphires blue?", defaultModel));}
         catch (IOException e) {
            e.printStackTrace();
        }
    }
}
