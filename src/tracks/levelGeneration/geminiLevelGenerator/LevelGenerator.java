package tracks.levelGeneration.geminiLevelGenerator;

import core.game.Game;
import core.game.BasicGame;
import core.game.GameDescription;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import core.content.GameContent;
import core.generator.AbstractLevelGenerator;
import tools.ElapsedCpuTimer;
import core.content.GameContent;

import core.vgdl.VGDLFactory;
import core.vgdl.VGDLParser;
import core.vgdl.VGDLRegistry;
import tools.com.google.gson.Gson;
import tools.com.google.gson.JsonObject;
import tools.com.google.gson.JsonParser;
import tracks.ArcadeMachine;

public class LevelGenerator extends AbstractLevelGenerator{

    // Haven't been able to find a way to get this particular method to work as it needs to...

    public LevelGenerator(GameDescription description, ElapsedCpuTimer elapsedTimer){
        return;
    }
        
    

    public String generateLevel(GameDescription description, String gamePath, ElapsedCpuTimer elapsedTimer) throws IOException{
        return generateLevel(description, gamePath, null, elapsedTimer, 0);
    }

    public String generateLevel(GameDescription description, String gamePath, String outputPath, ElapsedCpuTimer elapsedTimer) throws IOException{
        return generateLevel(description, gamePath, outputPath, elapsedTimer, 0);
    }

    public String generateLevel(GameDescription description, String gamePath, String outputPath, ElapsedCpuTimer elapsedTimer, int promptNum) throws IOException{
        String prompts = Files.readString(Path.of("src/tracks/levelGeneration/languageModelLevelGenerator/prompts.json"));
        String levelRules = Files.readString(Path.of(gamePath));
        Gson g = new Gson();
        JsonObject j = g.fromJson(prompts, JsonObject.class);
        String prompt = j.get(String.valueOf(promptNum)).getAsString();
        
        String levels = "";
        for (int i = 0; i < 5; i++){
            try{
                levels += "[";
                levels += Files.readString(Path.of(gamePath.substring(0, gamePath.length()-4) + "_lvl" + String.valueOf(i) + ".txt"));
                levels += "]\n";
            }
            catch (FileNotFoundException f){
                System.out.println("Generating level for a game for which only " + i + "sample levels were found!");
            }
        }
        
        prompt += "\nGVDL Level Description:\n" + levelRules + "\nSample Levels:\n" + levels;
        //System.out.println(prompt);
        String response = GeminiAPI.generateText(prompt);
        

        if (outputPath != null){Files.writeString(Path.of(outputPath), response);}
        //System.out.println(response);
        return response.substring(1, response.length()-1);
    }

    //Example usage to generate a new level of aliens
    public static void main(String[] args) throws IOException{
        String gameName = "examples/gridphysics/aliens";
        //String gameName = "examples/contphysics/artillery";

        String gamePath =  gameName + ".txt";
        String newLevelPath = gameName + "_lvl0_llm.txt";

        LevelGenerator generator = new LevelGenerator(null, null);
        String level = generator.generateLevel(null, gamePath, newLevelPath, null);
        
        System.out.println(level);
        
        String recordActionsFile = null;
        ArcadeMachine.playOneGame(gamePath, newLevelPath, recordActionsFile, new Random().nextInt());

        }
}



