package tracks.levelGeneration.FineTunedLLMGenerator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import core.game.Game;
import core.game.GameDescription;
import core.generator.AbstractLevelGenerator;
import tools.ElapsedCpuTimer;
import tools.com.google.gson.Gson;
import tools.com.google.gson.JsonObject;
import tracks.ArcadeMachine;
import tracks.levelGeneration.OllamaAPI;

public class LevelGenerator extends AbstractLevelGenerator{

    public LevelGenerator(GameDescription description, ElapsedCpuTimer elapsedCpuTimer){
        return;
    }

    public String generateLevel(GameDescription description, String gamePath, ElapsedCpuTimer elapsedTimer) throws IOException{
        return generateLevel(description, gamePath, null, elapsedTimer, 2);
    }

    public String generateLevel(GameDescription description, String gamePath, String outputPath, ElapsedCpuTimer elapsedTimer) throws IOException{
        return generateLevel(description, gamePath, outputPath, elapsedTimer, 2);
    }

    public String generateLevel(GameDescription description, String gamePath, String outputPath, ElapsedCpuTimer elapsedTimer, int promptNum) throws IOException{
        String prompts = "";
        String levelRules = "";
        try {
            prompts = Files.readString(Path.of("src/tracks/levelGeneration//prompts.json"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            levelRules = Files.readString(Path.of(gamePath));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Gson g = new Gson();
        JsonObject j = g.fromJson(prompts, JsonObject.class);
        String promptStub = j.get(String.valueOf(2)).getAsString();
        
        String prompt = promptStub + "\n" + levelRules + "\n" + Files.readString(Path.of(gamePath.substring(0, gamePath.length()-4) + "_lvl4" + ".txt"));
        
        System.out.println(prompt);
        //String response  = OllamaAPI.generateText(prompt, "levelGen-Model:latest");
        // Most up to date model?
        String response  = OllamaAPI.generateText(prompt, "Llama-3.1-8B-Instruct-bnb-4bit-Level-Gen:latest");
        
        response = response.replace("\\r\\n", System.lineSeparator());

        if (outputPath != null){Files.writeString(Path.of(outputPath), response);}
        return response;
        
    }

    public static void main(String[] args) throws IOException{
        String gameName = "examples/gridphysics/sokoban";
        //String gameName = "examples/contphysics/artillery";

        String gamePath =  gameName + ".txt";
        String newLevelPath = gameName + "_lvl0_localLlm.txt";

        LevelGenerator generator = new LevelGenerator(null, null);
        String level = generator.generateLevel(null, gamePath, newLevelPath, null);
        
        System.out.println(level);
        
        String recordActionsFile = null;
        ArcadeMachine.playOneGame(gamePath, newLevelPath, recordActionsFile, new Random().nextInt());

        }
}
