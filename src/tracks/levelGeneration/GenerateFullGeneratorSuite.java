package tracks.levelGeneration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import core.game.Game;
import core.vgdl.VGDLFactory;
import core.vgdl.VGDLParser;
import core.vgdl.VGDLRegistry;
import tracks.levelGeneration.claudeLevelGenerator.LevelGenerator;

public class GenerateFullGeneratorSuite {
    public static void main(String args[]) throws Exception{
        String randomLevelGenerator = "tracks.levelGeneration.randomLevelGenerator.LevelGenerator";
		String geneticGenerator = "tracks.levelGeneration.geneticLevelGenerator.LevelGenerator";
		String constructiveLevelGenerator = "tracks.levelGeneration.constructiveLevelGenerator.LevelGenerator";
		String geminiLevelGenerator = "tracks.levelGeneration.geminiLevelGenerator.LevelGenerator";
		String localLanguageModelGenerator = "tracks.levelGeneration.localLanguageModelGenerator.LevelGenerator";
        String FineTunedLLMGenerator = "tracks.levelGeneration.FineTunedLLMGenerator.LevelGenerator";
        String claudeLevelGenerator = "tracks.levelGeneration.claudeLevelGenerator.LevelGenerator";

        // Generator Choice
        String selectedGenerator = geneticGenerator;

        // Determines padding size for file numbers
        DecimalFormat df = new DecimalFormat("000");

        String generatorTitle = selectedGenerator.split("\\.")[2];
        int levelsToGenerate = 1000;
        //Only increase pauseSeconds for geminiGenerator
        int pauseSeconds = 3;
        int levelTotal = 0;
        System.out.println("Generating Suite of " + levelsToGenerate + " levels for " + generatorTitle);
        System.out.println("Start time of " + LocalDateTime.now());

        // Creates the correct generator folder if not present
        if (!Files.exists(Path.of("generatedExamples" + "/" + generatorTitle))){
            Files.createDirectory(Path.of("generatedExamples" + "/" + generatorTitle));
        }

        String[] selectedGamePaths = new String[] {"examples/gridphysics/aliens.txt", "examples/contphysics/mario.txt", "examples/contphysics/artillery.txt", "examples/gridphysics/zelda.txt", "examples/gridphysics/dungeon.txt", "examples/gridphysics/realsokoban.txt", "examples/gridphysics/towerdefense.txt", "examples/contphysics/asteroids.txt", "examples/gridphysics/roguelike.txt", "examples/gridphysics/frogs.txt"};
        //String[] selectedGamePaths = new String[] {"examples/gridphysics/realsokoban.txt"};

        if (selectedGenerator.equals(claudeLevelGenerator)) {
            // Batch mode: mirrors the single-level loop order — round-robin across all games,
            // batchSize levels per game per round, so no game gets far ahead of another.
            int batchSize = 10;
            LevelGenerator claude = new LevelGenerator(null, null);
            VGDLFactory.GetInstance().init();
            VGDLRegistry.GetInstance().init();
            // Pre-parse all games once so we have char mappings without re-parsing each round.
            Game[] games = new Game[selectedGamePaths.length];
            for (int g = 0; g < selectedGamePaths.length; g++) {
                games[g] = new VGDLParser().parseGame(selectedGamePaths[g]);
            }

            for (int j = 0; j < levelsToGenerate; j += batchSize) {
                for (int g = 0; g < selectedGamePaths.length; g++) {
                    String gamePath = selectedGamePaths[g];
                    String gameTitle = gamePath.split("/")[2];
                    gameTitle = gameTitle.substring(0, gameTitle.length()-4);
                    Path gameDir = Path.of("generatedExamples", generatorTitle, gameTitle);
                    Files.createDirectories(gameDir);

                    int thisBatch = Math.min(batchSize, levelsToGenerate - j);

                    // Skip this batch if all output files already exist.
                    boolean allExist = true;
                    for (int k = 0; k < thisBatch; k++) {
                        if (!Files.exists(Path.of(gameDir + "/" + gameTitle + "_lvl" + df.format(j + k) + ".txt"))) {
                            allExist = false;
                            break;
                        }
                    }

                    if (!allExist) {
                        System.out.println(gameTitle + " levels " + df.format(j) + "–" + df.format(j + thisBatch - 1));
                        List<String> levels = claude.generateLevels(null, gamePath, thisBatch, null, 0);
                        if (levels != null) {
                            for (int k = 0; k < levels.size(); k++) {
                                String outPath = gameDir + "/" + gameTitle + "_lvl" + df.format(j + k) + ".txt";
                                if (!Files.exists(Path.of(outPath))) {
                                    LevelGenMachine.saveLevel(levels.get(k), outPath, games[g].getCharMapping());
                                    levelTotal++;
                                }
                            }
                            System.out.println("Completed Levels Total: " + levelTotal);
                        } else {
                            System.out.println("API call failed for " + gameTitle + " batch starting at " + j);
                        }
                    }
                }
            }
        } else {

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

        } // end else
        
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
