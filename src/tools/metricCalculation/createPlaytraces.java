package tools.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

// Include these so that the compiled version of this program can find the agents it needs
import tracks.singlePlayer.advanced.olets.Agent;
/*
import tracks.singlePlayer.simple.sampleRandom.Agent;
import tracks.singlePlayer.simple.doNothing.Agent;
import tracks.singlePlayer.simple.sampleonesteplookahead.Agent;
import tracks.singlePlayer.simple.greedyTreeSearch.Agent;
import tracks.singlePlayer.advanced.sampleMCTS.Agent;
import tracks.singlePlayer.advanced.sampleRS.Agent;
import tracks.singlePlayer.advanced.sampleRHEA.Agent;
*/

// To compile file
//javac -d out -sourcepath src src\tools\metricCalculation\metrics\createPlaytraces.java
// To run in python
//java -cp out tools.metricCalculation.metrics.createPlaytraces

public class createPlaytraces {
    // Creates playtrace data for all levels in a selected folder
    public static void createManyPlaytraces(String folderPath) throws IOException{
        // Stream of level files
        Stream<Path> levels = Files.walk(Path.of(folderPath)).filter(f -> f.toString().endsWith(".txt"));

        levels.forEach(levelPath -> {
            try {
                metricTools.createPlaytrace(levelPath.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public static void createOnePlaytrace(String levelPath) throws IOException{
        metricTools.createPlaytrace(levelPath.toString());
    }


    public static void main(String[] args) throws IOException{
        String levelFolder = "generatedExamples\\constructiveLevelGenerator\\dungeon\\";
        levelFolder = "generatedExamples\\constructiveLevelGenerator\\";
        String levelPath = args[0];
        createOnePlaytrace(levelPath);
        //createManyPlaytraces(levelFolder);
    }
}


