package tracks.levelGeneration.languageModelLevelGenerator;

import core.game.Game;
import core.game.BasicGame;
import core.game.GameDescription;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import core.content.GameContent;
import core.generator.AbstractLevelGenerator;
import src.core.vgdl.VGDLParser;
import tools.ElapsedCpuTimer;
import core.content.GameContent;

import core.vgdl.VGDLFactory;
import core.vgdl.VGDLParser;
import core.vgdl.VGDLRegistry;
import tools.com.google.gson.Gson;
import tools.com.google.gson.JsonObject;
import tools.com.google.gson.JsonParser;

public class LevelGenerator extends AbstractLevelGenerator{

    // Haven't been able to find a way to get this particular method to work as it needs to...
    public String generateLevel(GameDescription game, ElapsedCpuTimer elapsedTimer){
        //BasicGame g = (BasicGame) game.getCurrentGame(); // Only basic levels have the correctly initialized content field?
        //GameContent c = g.getContent();
        //System.out.println(c.line);
        //return c.line;
        return "Used other method format";
    }

    public String generateLevel(String gamePath, ElapsedCpuTimer elapsedTimer) throws IOException{
        return generateLevel(gamePath, elapsedTimer, 0);
    }

    public String generateLevel(String gamePath, ElapsedCpuTimer elapsedTimer, int promptNum) throws IOException{
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
        return response.split("[")[1].split("]")[0];
    }

    //Example usage to generate a new level of aliens
    public static void main(String[] args) throws IOException{
          
        String gamePath = "examples/gridphysics/aliens.txt";
        LevelGenerator generator = new LevelGenerator();
        String level = generator.generateLevel(gamePath, null);
        System.out.println(level);

        /*
        System.out.println("Testing Level Generation");
        VGDLFactory.GetInstance().init(); // This always first thing to do.
        VGDLRegistry.GetInstance().init();

        Game game = new VGDLParser().parseGame("examples/gridphysics/aliens.txt");
        GameDescription gameDesc = new GameDescription(game);
        System.out.println("Game: " + game.toString());
        System.out.println("Game Description: " + gameDesc + " as " + gameDesc.toString());
        System.out.println("Game Description's Current Game: " + gameDesc.getCurrentGame());
        System.out.println("Current Game's ___" + gameDesc.getCurrentGame());
        
        LevelGenerator lG = new LevelGenerator();
        System.out.println(lG.generateLevel(gameDesc, new ElapsedCpuTimer()));
        */
            
        }
}



