package tracks.levelGeneration.claudeLevelGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import core.game.GameDescription;
import core.generator.AbstractLevelGenerator;
import tools.ElapsedCpuTimer;
import tools.com.google.gson.Gson;
import tools.com.google.gson.JsonArray;
import tools.com.google.gson.JsonElement;
import tools.com.google.gson.JsonObject;
import tools.com.google.gson.JsonParser;
import tracks.ArcadeMachine;

/**
 * Level generator that calls the Anthropic Claude API to produce a new level
 * for a GVGAI game. It is given the game description plus five sample levels
 * from examples folder.
 *
 * prompt pulled from src/tracks/levelGeneration/prompts.json
 *
 * Mirrors the structure of the existing geminiLevelGenerator
 */
public class LevelGenerator extends AbstractLevelGenerator {

    private static final String PROMPTS_PATH = "src/tracks/levelGeneration/prompts.json";

    private static final int NUM_SAMPLE_LEVELS = 5;

    private static final String MODEL = "claude-haiku-4-5";
    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final int MAX_TOKENS = 200;

    public LevelGenerator(GameDescription description, ElapsedCpuTimer elapsedTimer) {
        return;
    }

    @Override
    public String generateLevel(GameDescription description, String gamePath, ElapsedCpuTimer elapsedTimer)
            throws IOException {
        return generateLevel(description, gamePath, null, elapsedTimer, 0);
    }

    public String generateLevel(GameDescription description, String gamePath, String outputPath,
            ElapsedCpuTimer elapsedTimer) throws IOException {
        return generateLevel(description, gamePath, outputPath, elapsedTimer, 0);
    }

    /**
     * Generate a level for the supplied game.
     *
     * @param description  GVGAI game description
     * @param gamePath     path to the game description file, e.g. "examples/gridphysics/aliens.txt"
     * @param outputPath   optional file to write the generated level to (null to skip)
     * @param elapsedTimer level-generation timer (unused)
     * @param promptNum    which prompt in prompts.json to use as the system prompt
     * @return the generated level string, or null if the API call failed
     */
    public String generateLevel(GameDescription description, String gamePath, String outputPath,
            ElapsedCpuTimer elapsedTimer, int promptNum) throws IOException {

        String prompts = Files.readString(Path.of(PROMPTS_PATH));
        JsonObject promptObj = new Gson().fromJson(prompts, JsonObject.class);
        JsonElement chosen = promptObj.get(String.valueOf(promptNum));
        if (chosen == null) {
            throw new IOException("Prompt number " + promptNum + " not found in " + PROMPTS_PATH);
        }
        String systemPrompt = chosen.getAsString();

        String levelRules = Files.readString(Path.of(gamePath));

        // Read the five sample levels
        String baseName = gamePath.substring(0, gamePath.length() - 4); // strip ".txt"
        StringBuilder levels = new StringBuilder();
        for (int i = 0; i < NUM_SAMPLE_LEVELS; i++) {
            Path levelPath = Path.of(baseName + "_lvl" + i + ".txt");
            try {
                levels.append("Level ").append(i).append(":\n");
                levels.append(Files.readString(levelPath));
                levels.append("\n");
            } catch (IOException f) {
                System.out.println("Only " + i + " sample levels found for " + gamePath
                        + " (expected " + NUM_SAMPLE_LEVELS + ").");
                break;
            }
        }

        String userMessage = "GVGAI Game Description:\n" + levelRules
                + "\n\nSample Levels:\n" + levels;

        String response = callClaude(systemPrompt, userMessage);
        if (response == null) {
            return null;
        }

        String level = extractLevel(response);

        if (outputPath != null) {
            Files.writeString(Path.of(outputPath), level);
        }
        return level;
    }

    /**
     * Pull the level out of the model response. Returns the text between the
     * first '[' and the last ']' if both are present, otherwise the trimmed
     * response.
     */
    private static String extractLevel(String response) {
        int start = response.indexOf('[');
        int end = response.lastIndexOf(']');
        if (start != -1 && end != -1 && end > start) {
            // strip() drops the leading/trailing newlines around the bracketed
            // block so the level doesn't start with an empty row.
            return response.substring(start + 1, end).strip();
        }
        return response.trim();
    }

    /**
     * Call the Claude Messages API and return the concatenated text content of
     * the response, or null on error.
     */
    private static String callClaude(String systemPrompt, String userMessage) {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("ANTHROPIC_API_KEY environment variable is not set.");
            return null;
        }

        // Build the request body with gson so prompt content is JSON-escaped
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);

        JsonArray messages = new JsonArray();
        messages.add(userMsg);

        JsonObject body = new JsonObject();
        body.addProperty("model", MODEL);
        body.addProperty("max_tokens", MAX_TOKENS);
        body.addProperty("system", systemPrompt);
        body.add("messages", messages);

        String requestBody = new Gson().toJson(body);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("x-api-key", apiKey)
                .header("anthropic-version", ANTHROPIC_VERSION)
                .header("content-type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            JsonObject json = new JsonParser().parse(response.body()).getAsJsonObject();

            // On error the API returns {"type":"error","error":{...}} with no
            // "content" array.
            JsonArray content = json.getAsJsonArray("content");
            if (content == null) {
                System.out.println("Claude API error (HTTP " + response.statusCode() + "): "
                        + response.body());
                return null;
            }
            System.out.println("Claude response:");
            System.out.println(content.toString());
            // The content array might contain thinking blocks before the text;
            StringBuilder text = new StringBuilder();
            for (JsonElement element : content) {
                JsonObject block = element.getAsJsonObject();
                JsonElement type = block.get("type");
                if (type != null && "text".equals(type.getAsString())) {
                    text.append(block.get("text").getAsString());
                }
            }
            return text.toString();
        } catch (IOException e) {
            System.out.println("An IO Exception occurred while calling the Claude API.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("The Claude API call was interrupted.");
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /*
    // Example usage: generate a new level for a gridphysics or contphysics game.
    public static void main(String[] args) throws IOException {
        String gameName = "examples/gridphysics/aliens";
        // String gameName = "examples/contphysics/artillery";

        String gamePath = gameName + ".txt";
        String newLevelPath = gameName + "_lvl0_claude.txt";

        LevelGenerator generator = new LevelGenerator(null, null);
        String level = generator.generateLevel(null, gamePath, newLevelPath, null);

        System.out.println(level);

        if (level != null) {
            String recordActionsFile = null;
            ArcadeMachine.playOneGame(gamePath, newLevelPath, recordActionsFile, new Random().nextInt());
        }
    }
    */
}
