package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;

import tools.com.google.gson.JsonObject;
import tools.metricCalculation.metricTools;

public class MechanicUsage{
    public static JsonObject calculateMetric(String levelPath) throws IOException{
        // return an error value if the level has no avatar
        if (!metricTools.getLevelTiles(Files.readString(Path.of(levelPath))).contains("A")){
            return new JsonObject();
        }
        if (!Files.isRegularFile(Path.of(levelPath.replace("generatedExamples", "generatedExamplesPlaytracesLab-1k-80ms-v2")))
            || Files.readString(Path.of(levelPath.replace("generatedExamples", "generatedExamplesPlaytracesLab-1k-80ms-v2"))).isBlank()){
            metricTools.createPlaytrace(levelPath);
        }
        String[] actions = Files.readString(Path.of(levelPath.replace("generatedExamples", "generatedExamplesPlaytracesLab-1k-80ms-v2"))).split("\n");

        HashMap<String, Integer> seenActions = new HashMap<String, Integer>();
        for (int i = 1; i < actions.length; i++){ // Ignores first row as it is not a game action
            seenActions.put(actions[i], seenActions.getOrDefault(actions[i], 0)+1);
            //System.out.println(seenActions.get(actions[i]));
        }
        
        
        // There is a total of 5 known actions
        //ACTION_RIGHT
        //ACTION_LEFT
        //ACTION_UP
        //ACTION_DOWN
        //ACTION_USE
        //ACTION_NIL

        JsonObject usageMap = new JsonObject();
        for (String key : seenActions.keySet()){
            //System.out.println(seenActions.get(key));
            usageMap.addProperty(key, seenActions.get(key));
        }
        return usageMap;
        
        
    }

    public static void main(String[] args) throws IOException{
        String testLevelPath1 = "generatedExamples/geminiLevelGenerator/aliens/aliens_lvl005.txt";
        String testLevelPath2 = "generatedExamples/geminiLevelGenerator/realsokoban/realsokoban_lvl001.txt";
        String testLevelPath3 = "generatedExamples/geminiLevelGenerator/zelda/zelda_lvl076.txt";
        System.out.println(calculateMetric(testLevelPath2));
    }
}