package tracks.levelGeneration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class GenerateFullGeneratorSuite {
    public static void main(String args[]) throws Exception{
        String randomLevelGenerator = "tracks.levelGeneration.randomLevelGenerator.LevelGenerator";
		String geneticGenerator = "tracks.levelGeneration.geneticLevelGenerator.LevelGenerator";
		String constructiveLevelGenerator = "tracks.levelGeneration.constructiveLevelGenerator.LevelGenerator";
		String geminiLevelGenerator = "tracks.levelGeneration.geminiLevelGenerator.LevelGenerator";
		String localLanguageModelGenerator = "tracks.levelGeneration.localLanguageModelGenerator.LevelGenerator";
        String FineTunedLLMGenerator = "tracks.levelGeneration.FineTunedLLMGenerator.LevelGenerator";

        // Generator Choice
        String selectedGenerator = FineTunedLLMGenerator;
        
        // Determines padding size for file numbers
        DecimalFormat df = new DecimalFormat("000");

        String generatorTitle = selectedGenerator.split("\\.")[2];
        int levelsToGenerate = 1000;
        //Only increase pauseSeconds for geminiGenerator
        int pauseSeconds = 0;
        int levelTotal = 0;
        System.out.println("Generating Suite of " + levelsToGenerate + " levels for " + generatorTitle);
        System.out.println("Start time of " + LocalDateTime.now());

        // Creates the correct generator folder if not present
        if (!Files.exists(Path.of("generatedExamples" + "/" + generatorTitle))){
            Files.createDirectory(Path.of("generatedExamples" + "/" + generatorTitle));
        }

        String[] selectedGamePaths = new String[] {"examples/gridphysics/aliens.txt", "examples/contphysics/mario.txt", "examples/contphysics/artillery.txt", "examples/gridphysics/zelda.txt", "examples/gridphysics/dungeon.txt", "examples/gridphysics/realsokoban.txt", "examples/gridphysics/towerdefense.txt", "examples/contphysics/asteroids.txt", "examples/gridphysics/roguelike.txt", "examples/gridphysics/frogs.txt"};
        
        for (int j = 0; j < levelsToGenerate; j++){
            for (String gamePath : selectedGamePaths){
                String gameTitle = gamePath.split("/")[2];
                gameTitle = gameTitle.substring(0, gameTitle.length()-4);

                if (!Files.exists(Path.of("generatedExamples" + "/" + generatorTitle + "/" + gameTitle))){
                    Files.createDirectory(Path.of("generatedExamples" + "/" + generatorTitle + "/" + gameTitle));
                }
                
                String outputFilePath = "generatedExamples" + "/" + generatorTitle + "/" + gameTitle + "/" + gameTitle + "_lvl" + df.format(j) + ".txt";
                if (!Files.exists(Path.of(outputFilePath))){
                    System.out.println(outputFilePath);
                    LevelGenMachine.generateOneLevel(gamePath, selectedGenerator, outputFilePath);
                    levelTotal++;
                    System.out.println("Completed Levels Total: " + levelTotal);
                    if (pauseSeconds > 0){
                        Thread.sleep(pauseSeconds * 1000);
                    }
                }
            }
                
        }
        
        System.out.println("Generated " + levelTotal + " total levels");
        System.out.println("End time of " + LocalDateTime.now());

        // Generates levelsToGenerate number of levels for the specified generator
        // Does not attempt to overwrite existing levels
        // Generates all the levels for one game at a time
        /*
        for (String gamePath : selectedGamePaths){
            String gameTitle = gamePath.split("/")[2];
            gameTitle = gameTitle.substring(0, gameTitle.length()-4);
            
            // Creates the correct level folder if not present
            if (!Files.exists(Path.of("generatedExamples" + "/" + generatorTitle + "/" + gameTitle))){
                Files.createDirectory(Path.of("generatedExamples" + "/" + generatorTitle + "/" + gameTitle));
            }

            ArrayList<String> outputFilePaths = new ArrayList<String>();
            for (int j = 0; j < levelsToGenerate; j++){
                outputFilePaths.add("generatedExamples" + "/" + generatorTitle + "/" + gameTitle + "/" + gameTitle + "_lvl" + df.format(j) + ".txt");
            }
            //Remove existing generated levels from the list of paths of levels to be generated
            outputFilePaths.removeIf(item -> Files.exists(Path.of(item)));
            levelTotal += outputFilePaths.size();
            
            //System.out.println(outputFilePaths);
            
            String[] array = new String[outputFilePaths.size()];
            String[] outputFilePathsArray = outputFilePaths.toArray(array);
            LevelGenMachine.generateLevels(gamePath, selectedGenerator, outputFilePathsArray);
        }
        System.out.println("Generated " + levelTotal + "levels");
        System.out.println("End time of " + LocalDateTime.now());
        */


//aliens_lvl0.txt
        
    }
}
