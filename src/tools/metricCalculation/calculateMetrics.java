package tools.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import tools.com.google.gson.Gson;
import tools.com.google.gson.JsonElement;
import tools.com.google.gson.JsonObject;
import tools.metricCalculation.metrics.*;

public class calculateMetrics {

    public static JsonObject createLevelMetricJson(String levelText){
        JsonObject levelMetrics = new JsonObject();
        
        // Individual Metrics added to the json object below
        levelMetrics.addProperty("Density", Density.calculateMetric(levelText));
        //System.out.println("Density Calculations Complete");
        levelMetrics.addProperty("NegativeSpace", NegativeSpace.calculateMetric(levelText));
        //System.out.println("Negative Space Calculations Complete");
        levelMetrics.addProperty("ShannonEntropy", ShannnonEntropy.calculateMetric(levelText));
        //System.out.println("Shannon Entropy Calculations Complete");
        levelMetrics.addProperty("FloodReachability", FloodReachability.calculateMetric(levelText));
        //System.out.println("Flood Reachability Calculations Complete");
        levelMetrics.addProperty("WallFloorRatio", WallFloorRatio.calculateMetric(levelText));
        //System.out.println("Wall/Floor Ratio Calculations Complete");
        levelMetrics.addProperty("NaiveSimilarity", NaiveSimilarity.calculateMetric(levelText));
        //levelMetrics.addProperty("SomeMetric", SomeMetric.calculateSomeMetric(levelText));
        //System.out.println("Some Metric Calculations Complete");

        // Finally, we add binning information. these should all be booleans that represent whether a level passes or fails a certain requirement
        JsonObject binningProperty = new JsonObject();
        binningProperty.addProperty("ConsistentRows", BinGeneratedLevels.consistentRows(levelText));
        binningProperty.addProperty("ContainsPlayer", BinGeneratedLevels.containsPlayer(levelText));
        binningProperty.addProperty("NotEmpty", BinGeneratedLevels.notEmpty(levelText));
        
        levelMetrics.add("Binning", binningProperty);

        

        return levelMetrics;
    }

    // Creates a json file that maps each level path in the provided folder to its metric results. By default, this json file is also placed in the provided. 
    /* No longer used, use recursive version instead
    public static JsonObject createFolderMetricJson(String levelFolderPath){
        JsonObject jsonObject = new JsonObject();
        try {
            
            Stream<Path> stream = Files.walk(Path.of(levelFolderPath));
            stream.forEach(f -> {
                if (f.toString().endsWith(".txt")){
                    try {
                        jsonObject.add(f.toString(), createLevelMetricJson(Files.readString(f)));
                        
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            });
            return jsonObject;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
        */

    // Creates metric.json files for a generator as well as its individual games
    public static void createFolderMetricsRecursive(String levelFolderPath){
        try {
            ArrayList<JsonObject> fullGeneratorJsonList = new ArrayList<JsonObject>();
            // Here we calculate metrics by game so that we are able to save a metric file for each game in each generator along with the metric file for all games for a generator
            Stream<Path> streamByGame = Files.list(Path.of(levelFolderPath)).filter(f -> !f.toString().endsWith(".json"));
            streamByGame.forEach(game -> {
                try {
                    JsonObject fullGameJson = new JsonObject();
                    
                    Stream<Path> streamByLevel = Files.walk(Path.of(game.toString() + "/"));
                    System.out.println("Creating metrics for... " + levelFolderPath + "/" + game.toString());
                    streamByLevel.forEach(level -> {
                        // If level file
                        if (level.toString().endsWith(".txt")){
                            try {
                                fullGameJson.add(level.toString(), createLevelMetricJson(Files.readString(level)));
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        
                    });
                    
                    //System.out.println(gameJson);
                    // Creates a metrics.json for each game in each generator for single-game analysis
                    Files.writeString(Path.of(game + "/" + "metrics.json"), fullGameJson.toString());
                    fullGeneratorJsonList.add(fullGameJson);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
            /*String fullJsonString = "";
            for (JsonObject j : fullGeneratorJsonList){
                String jString = j.toString();
                fullJsonString += jString.substring(1, jString.length()-1) + ",";
            }
            fullJsonString = "{" + fullJsonString.substring(0, fullJsonString.length()) + "}";
            Files.writeString(Path.of(levelFolderPath + "/" + "metrics.json"), fullJsonString);*/

        JsonObject baseJson = fullGeneratorJsonList.get(0);

        for (int i = 1; i < fullGeneratorJsonList.size(); i++) {
            JsonObject incomingJson = fullGeneratorJsonList.get(i);
            
            // Safely copy all entries from the current object into the base object
            for (Map.Entry<String, JsonElement> entry : incomingJson.entrySet()) {
                baseJson.add(entry.getKey(), entry.getValue());
            }
        }

        Files.writeString(Path.of(levelFolderPath + "/" + "metrics.json"), baseJson.toString());

        }
        catch (IOException e1){
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException{
        //System.out.println(createLevelMetricJson(testLevel));
        ArrayList<String> selectedFolders = new ArrayList<String>();
        

        //selectedFolders.add("generatedExamples/geminiLevelGenerator");
        // Uncomment to generate metrics for all levels
    
        selectedFolders.add("generatedExamples/geminiLevelGenerator");
         selectedFolders.add("generatedExamples/constructiveLevelGenerator");
         selectedFolders.add("generatedExamples/geneticLevelGenerator");
         selectedFolders.add("generatedExamples/localLanguageModelGenerator");
         selectedFolders.add("generatedExamples/randomLevelGenerator");
        
        for (String s: selectedFolders){
            //System.out.println(createFolderMetricJson(s));
            //Files.writeString(Path.of(s + "/" + "metrics.json"), createFolderMetricJson(s).toString());
            createFolderMetricsRecursive(s);
        }
        
    }
}
