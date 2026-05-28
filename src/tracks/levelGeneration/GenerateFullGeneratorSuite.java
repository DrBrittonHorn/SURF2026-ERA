package tracks.levelGeneration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class GenerateFullGeneratorSuite {
    public static void main(String args[]) throws Exception{
        String randomLevelGenerator = "tracks.levelGeneration.randomLevelGenerator.LevelGenerator";
		String geneticGenerator = "tracks.levelGeneration.geneticLevelGenerator.LevelGenerator";
		String constructiveLevelGenerator = "tracks.levelGeneration.constructiveLevelGenerator.LevelGenerator";
		String languageModelGenerator = "tracks.levelGeneration.geminiLevelGenerator.LevelGenerator";

        // Generator Choice
        String selectedGenerator = languageModelGenerator;
        
        // Set to true when generating with the LLM generator to avoid API overuse.
        boolean delay = true;
        
        String generatorTitle = selectedGenerator.split("\\.")[2];
        int levelsToGenerate = 1000;
        System.out.println("Generating Suite of " + levelsToGenerate + " levels for " + generatorTitle);

        // Creates the correct generator folder if not present
        if (!Files.exists(Path.of("generatedExamples" + "/" + generatorTitle))){
            Files.createDirectory(Path.of("generatedExamples" + "/" + generatorTitle));
        }

        String[] selectedGamePaths = new String[] {"examples/gridphysics/aliens.txt", "examples/contphysics/mario.txt", "examples/contphysics/artillery.txt", "examples/gridphysics/zelda.txt", "examples/gridphysics/dungeon.txt", "examples/gridphysics/realsokoban.txt", "examples/gridphysics/towerdefense.txt", "examples/contphysics/asteroids.txt", "examples/gridphysics/x-racer.txt"};
        
        // Generates levelsToGenerate number of levels for the specified generator
        // Does not attempt to overwrite existing levels
        for (String gamePath : selectedGamePaths){
            String gameTitle = gamePath.split("/")[2];
            gameTitle = gameTitle.substring(0, gameTitle.length()-4);
            
            // Creates the correct level folder if not present
            if (!Files.exists(Path.of("generatedExamples" + "/" + generatorTitle + "/" + gameTitle))){
                Files.createDirectory(Path.of("generatedExamples" + "/" + generatorTitle + "/" + gameTitle));
            }

            ArrayList<String> outputFilePaths = new ArrayList<String>();
            for (int j = 0; j < levelsToGenerate; j++){
                outputFilePaths.add("generatedExamples" + "/" + generatorTitle + "/" + gameTitle + "/" + gameTitle + "_lvl" + j + ".txt");
            }
            //Remove existing generated levels from the list of paths of levels to be generated
            outputFilePaths.removeIf(item -> Files.exists(Path.of(item)));
            
            
            //System.out.println(outputFilePaths);
            
            String[] array = new String[outputFilePaths.size()];
            String[] outputFilePathsArray = outputFilePaths.toArray(array);
            LevelGenMachine.generateLevels(gamePath, selectedGenerator, outputFilePathsArray);
        }
        
//aliens_lvl0.txt
        
    }
}
