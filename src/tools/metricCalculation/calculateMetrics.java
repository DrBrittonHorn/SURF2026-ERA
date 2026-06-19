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
import tools.metricCalculation.metrics.byGeneratorMetrics.OutputNGramSimilarity1D;
import tools.metricCalculation.metrics.byLevelMetrics.AgentSolutionLength;
import tools.metricCalculation.metrics.byLevelMetrics.BalanceHorizontal;
import tools.metricCalculation.metrics.byLevelMetrics.BalanceVertical;
import tools.metricCalculation.metrics.byLevelMetrics.CompressionDistance;
import tools.metricCalculation.metrics.byLevelMetrics.DecorationFrequency;
import tools.metricCalculation.metrics.byLevelMetrics.Density;
import tools.metricCalculation.metrics.byLevelMetrics.FloodReachability;
import tools.metricCalculation.metrics.byLevelMetrics.HazardTileRatio;
import tools.metricCalculation.metrics.byLevelMetrics.JensenShannonDivergence1D;
import tools.metricCalculation.metrics.byLevelMetrics.JensenShannonDivergence2D;
import tools.metricCalculation.metrics.byLevelMetrics.KLDivergence1D;
import tools.metricCalculation.metrics.byLevelMetrics.Linearity;
import tools.metricCalculation.metrics.byLevelMetrics.MechanicUsage;
import tools.metricCalculation.metrics.byLevelMetrics.NGramSimilarity1D;
import tools.metricCalculation.metrics.byLevelMetrics.NaiveSimilarity;
import tools.metricCalculation.metrics.byLevelMetrics.NegativeSpace;
import tools.metricCalculation.metrics.byLevelMetrics.RewardDensity;
import tools.metricCalculation.metrics.byLevelMetrics.ShannnonEntropy;
import tools.metricCalculation.metrics.byLevelMetrics.KLDivergence2D;
import tools.metricCalculation.metrics.byLevelMetrics.WallFloorRatio;

public class calculateMetrics {

    public static JsonObject createLevelMetricJson(Path levelPath) throws IOException{
        String levelText = Files.readString(levelPath);
        String levelPathString = levelPath.toString();
        JsonObject levelMetrics = new JsonObject();
        //First, add all the metrics that are calculated on a per level basis

        // Individual Metrics added to the json object below
        levelMetrics.addProperty("Density", Density.calculateMetric(levelText));
        levelMetrics.addProperty("NegativeSpace", NegativeSpace.calculateMetric(levelText));
        levelMetrics.addProperty("ShannonEntropy", ShannnonEntropy.calculateMetric(levelText));
        levelMetrics.addProperty("FloodReachability", FloodReachability.calculateMetric(levelText));
        levelMetrics.addProperty("WallFloorRatio", WallFloorRatio.calculateMetric(levelText));
        levelMetrics.addProperty("NaiveSimilarity", NaiveSimilarity.calculateMetric(levelText));
        levelMetrics.addProperty("NGramSimilarity1D", NGramSimilarity1D.calculateMetric(levelText, 3));
        levelMetrics.addProperty("NGramSimilarity2D", NGramSimilarity1D.calculateMetric(levelText, 3));
        levelMetrics.addProperty("Linearity", Linearity.calculateMetric(levelText));
        levelMetrics.addProperty("CompressionDistance", CompressionDistance.calculateMetric(levelText));
        levelMetrics.addProperty("KLDivergence2D", KLDivergence1D.calculateMetric(levelText));
        levelMetrics.addProperty("KLDivergence2D", KLDivergence2D.calculateMetric(levelText, 3));
        levelMetrics.addProperty("BalanceHorizontal", BalanceHorizontal.calculateMetric(levelText));
        levelMetrics.addProperty("BalanceVertical", BalanceVertical.calculateMetric(levelText));
        levelMetrics.addProperty("JensenShannonDivergence1D", JensenShannonDivergence1D.calculateMetric(levelText));
        levelMetrics.addProperty("JensenShannonDivergence2D", JensenShannonDivergence2D.calculateMetric(levelText, 3));
        levelMetrics.addProperty("DecorationFrequenct", DecorationFrequency.calculateMetric(levelText));
        levelMetrics.addProperty("HazardTileRation", HazardTileRatio.calculateMetric(levelText));
        levelMetrics.addProperty("RewardDensity", RewardDensity.calculateMetric(levelText));

        // Put metrics that require the levelPath here
        levelMetrics.addProperty("AgentSolutionLength", AgentSolutionLength.calculateMetric(levelPathString));

        // Put metrics with special formats here and explain why
        levelMetrics.add("MechanicUsage", MechanicUsage.calculateMetric(levelPathString)); // Produces a json object (histogram); Requires a level's path

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
    public static void createMetricsByLevel(String levelFolderPath){
        try {
            ArrayList<JsonObject> fullGeneratorJsonList = new ArrayList<JsonObject>();
            // Here we calculate metrics by game so that we are able to save a metric file for each game in each generator along with the metric file for all games for a generator
            Stream<Path> streamByGame = Files.list(Path.of(levelFolderPath)).filter(f -> !f.toString().endsWith(".json"));
            streamByGame.forEach(game -> {
                try {
                    JsonObject fullGameJson = new JsonObject();
                    
                    Stream<Path> streamByLevel = Files.walk(Path.of(game.toString() + "/")).parallel();
                    System.out.println("Creating metrics for... " + levelFolderPath + "/" + game.toString());
                    streamByLevel.forEach(level -> {
                        // If level file
                        if (level.toString().endsWith(".txt")){
                            try {
                                fullGameJson.add(level.toString(), createLevelMetricJson(level));
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        
                    });
                    
                    //System.out.println(gameJson);
                    // Creates a metrics.json for each game in each generator for single-game analysis
                    Files.writeString(Path.of(game + "/" + "levelMetrics.json"), fullGameJson.toString());
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

        Files.writeString(Path.of(levelFolderPath + "/" + "levelMetrics.json"), baseJson.toString());

        }
        catch (IOException e1){
            e1.printStackTrace();
        }
    }
    
    public static void createMetricsByFolderRecursive(String generatorFolderPath) throws IOException{
        ArrayList<JsonObject> jsonsByGame = new ArrayList<JsonObject>();
        
        Stream<Path> streamByGame = (Files.list(Path.of(generatorFolderPath)).filter(f -> !f.toString().endsWith(".json")));
        streamByGame.forEach(game -> {
            JsonObject fullGameJson = new JsonObject();
            System.out.println("Creating metrics for the folder... " + generatorFolderPath + "/" + game.toString());
            //Here, add metrics that only make sense within the context of a folder of levels (ex. comparing output level diversity)
            fullGameJson.addProperty("OutputNGramSimilarity1D", OutputNGramSimilarity1D.calculateMetric(game.toString(), 5));
            try {
                Files.writeString(Path.of(game + "/" + "folderMetrics.json"), fullGameJson.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jsonsByGame.add(fullGameJson);
        });

        JsonObject finalFolderJson = new JsonObject();

        // Next we average metrics between each game subfolder (We assume each game folder has the same number of levels, lest we weigh some games more than others)
        // Sum folder metrics betweeen games
        for (JsonObject j : jsonsByGame){
            for (String metric : j.keySet()){
                if (!finalFolderJson.has(metric)){
                    finalFolderJson.add(metric, j.get(metric));}
                else{
                    finalFolderJson.addProperty(metric, finalFolderJson.get(metric).getAsDouble() + j.get(metric).getAsDouble());
                    
                }
            }
        }
        // Divide by the number of games
        
        for (String metric : finalFolderJson.keySet()){
            finalFolderJson.addProperty(metric, finalFolderJson.get(metric).getAsDouble() / jsonsByGame.size());
        }
        Files.writeString(Path.of(generatorFolderPath + "/" + "folderMetrics.json"), finalFolderJson.toString());


        
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
            
            System.out.println("CALCULATING METRICS BY LEVEL FOR " + s);
            createMetricsByLevel(s);
            System.out.println("CALCULATING METRICS BY FOLDER FOR " + s);
            createMetricsByFolderRecursive(s);
        }
        
    }
}
