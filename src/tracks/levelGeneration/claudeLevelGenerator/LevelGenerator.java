package tracks.levelGeneration.claudeLevelGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
 * Level generator that calls the Anthropic Claude API to produce new level(s)
 * for a GVGAI game. Uses forced tool use (structured output) so the model
 * cannot emit explanatory text — it must fill a JSON schema directly.
 * Supports batch generation of multiple levels in a single API call.
 *
 * Prompt pulled from src/tracks/levelGeneration/prompts.json.
 * Mirrors the structure of the existing geminiLevelGenerator.
 */
public class LevelGenerator extends AbstractLevelGenerator {

    private static final String PROMPTS_PATH = "src/tracks/levelGeneration/prompts.json";
    private static final int NUM_SAMPLE_LEVELS = 5;

    private static final String MODEL = "claude-haiku-4-5";
    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    // Token budget: 2048 per level, capped at 32 K (well within Haiku's 64 K output limit).
    private static final int MAX_TOKENS_PER_LEVEL = 2048;
    private static final int MAX_TOKENS_CAP = 32768;

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
     * Generate a single level. Delegates to generateLevels(count=1).
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
        List<String> levels = generateLevels(description, gamePath, 1, elapsedTimer, promptNum);
        if (levels == null || levels.isEmpty()) return null;
        String level = levels.get(0);
        if (outputPath != null) {
            Files.writeString(Path.of(outputPath), level);
        }
        return level;
    }

    /**
     * Generate multiple levels in a single API call for credit efficiency.
     *
     * @param description  GVGAI game description (may be null)
     * @param gamePath     path to the game description file, e.g. "examples/gridphysics/aliens.txt"
     * @param count        number of levels to generate (1–100)
     * @param elapsedTimer level-generation timer (unused)
     * @param promptNum    which prompt in prompts.json to use as the system prompt
     * @return list of generated level strings, or null if the API call failed
     */
    public List<String> generateLevels(GameDescription description, String gamePath, int count,
            ElapsedCpuTimer elapsedTimer, int promptNum) throws IOException {

        String prompts = Files.readString(Path.of(PROMPTS_PATH));
        JsonObject promptObj = new Gson().fromJson(prompts, JsonObject.class);
        JsonElement chosen = promptObj.get(String.valueOf(promptNum));
        if (chosen == null) {
            throw new IOException("Prompt number " + promptNum + " not found in " + PROMPTS_PATH);
        }
        String systemPrompt = chosen.getAsString();

        String levelRules = Files.readString(Path.of(gamePath));

        String baseName = gamePath.substring(0, gamePath.length() - 4); // strip ".txt"
        StringBuilder sampleLevels = new StringBuilder();
        for (int i = 0; i < NUM_SAMPLE_LEVELS; i++) {
            Path levelPath = Path.of(baseName + "_lvl" + i + ".txt");
            try {
                sampleLevels.append("Level ").append(i).append(":\n");
                sampleLevels.append(Files.readString(levelPath));
                sampleLevels.append("\n");
            } catch (IOException f) {
                System.out.println("Only " + i + " sample levels found for " + gamePath + ".");
                break;
            }
        }

        String countClause = count == 1
                ? "Generate exactly 1 new level."
                : "Generate exactly " + count + " distinct new levels. Each must differ from the others.";

        String userMessage = "GVGAI Game Description:\n" + levelRules
                + "\n\nSample Levels:\n" + sampleLevels
                + "\n\n" + countClause;

        int maxTokens = Math.min(count * MAX_TOKENS_PER_LEVEL, MAX_TOKENS_CAP);
        return callClaude(systemPrompt, userMessage, maxTokens);
    }

    /**
     * Call the Claude Messages API with forced tool use so the model must return
     * a JSON object containing only the level strings — no explanatory text.
     *
     * The tool schema is:
     *   { "levels": ["row1\nrow2\n...", "row1\nrow2\n...", ...] }
     *
     * Setting tool_choice to {"type":"tool","name":"output_level"} prevents the
     * model from emitting any free text before the level content, which was causing
     * token-cap failures on complex games like sokoban.
     */
    private static List<String> callClaude(String systemPrompt, String userMessage, int maxTokens) {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("ANTHROPIC_API_KEY environment variable is not set.");
            return null;
        }

        // ── messages ────────────────────────────────────────────────────────
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);

        JsonArray messages = new JsonArray();
        messages.add(userMsg);

        // ── tool definition ─────────────────────────────────────────────────
        // levels: array of strings, one complete level per element
        JsonObject itemSchema = new JsonObject();
        itemSchema.addProperty("type", "string");
        itemSchema.addProperty("description",
                "ONLY the level characters, nothing else. Rows of game characters separated by newlines. "
                + "No explanations, no analysis, no commentary — only the characters from the game's level mapping.");

        JsonObject levelsProp = new JsonObject();
        levelsProp.addProperty("type", "array");
        levelsProp.add("items", itemSchema);

        JsonObject properties = new JsonObject();
        properties.add("levels", levelsProp);

        JsonArray required = new JsonArray();
        required.add("levels");

        JsonObject inputSchema = new JsonObject();
        inputSchema.addProperty("type", "object");
        inputSchema.add("properties", properties);
        inputSchema.add("required", required);

        JsonObject tool = new JsonObject();
        tool.addProperty("name", "output_level");
        tool.addProperty("description", "Output the generated GVGAI level(s) as an array of strings of equal length.");
        tool.add("input_schema", inputSchema);

        JsonArray tools = new JsonArray();
        tools.add(tool);

        // ── force the tool call — no free-text preamble allowed ─────────────
        JsonObject toolChoice = new JsonObject();
        toolChoice.addProperty("type", "tool");
        toolChoice.addProperty("name", "output_level");

        // ── request body ────────────────────────────────────────────────────
        JsonObject body = new JsonObject();
        body.addProperty("model", MODEL);
        body.addProperty("max_tokens", maxTokens);
        body.addProperty("system", systemPrompt);
        body.add("messages", messages);
        body.add("tools", tools);
        body.add("tool_choice", toolChoice);

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

            JsonArray content = json.getAsJsonArray("content");
            if (content == null) {
                System.out.println("Claude API error (HTTP " + response.statusCode() + "): "
                        + response.body());
                return null;
            }

            // Find the tool_use block and extract input.levels
            for (JsonElement element : content) {
                JsonObject block = element.getAsJsonObject();
                JsonElement type = block.get("type");
                if (type != null && "tool_use".equals(type.getAsString())) {
                    JsonObject input = block.getAsJsonObject("input");
                    if (input != null) {
                        JsonArray levelsArray = input.getAsJsonArray("levels");
                        if (levelsArray != null) {
                            List<String> result = new ArrayList<>();
                            for (JsonElement lvl : levelsArray) {
                                String levelStr = lvl.getAsString().strip();
                                if (!levelStr.isEmpty()) {
                                    result.add(levelStr);
                                }
                            }
                            System.out.println("Claude generated " + result.size() + " level(s).");
                            return result;
                        }
                    }
                }
            }

            System.out.println("No tool_use block with levels found in Claude response: " + content);
            return null;

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
    // Example usage: generate levels for a gridphysics or contphysics game.
    public static void main(String[] args) throws IOException {
        String gameName = "examples/gridphysics/aliens";
        // String gameName = "examples/contphysics/artillery";
        // String gameName = "examples/gridphysics/sokoban";

        String gamePath = gameName + ".txt";
        LevelGenerator generator = new LevelGenerator(null, null);

        // Single level
        String level = generator.generateLevel(null, gamePath, gameName + "_lvl_claude.txt", null);
        System.out.println("Single level:\n" + level);

        // Batch: 10 levels in one API call
        List<String> levels = generator.generateLevels(null, gamePath, 10, null, 0);
        for (int i = 0; i < levels.size(); i++) {
            Files.writeString(Path.of(gameName + "_lvl_claude_" + i + ".txt"), levels.get(i));
            System.out.println("Level " + i + ":\n" + levels.get(i) + "\n");
        }

        ArcadeMachine.playOneGame(gamePath, gameName + "_lvl_claude_0.txt", null, new Random().nextInt());
    }
    */
}
