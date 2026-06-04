package tools.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

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
    public static JsonObject createFolderMetricJson(String levelFolderPath){
        JsonObject jsonObject = new JsonObject();
        try {
            
            Stream<Path> stream = Files.walk(Path.of(levelFolderPath));
            stream.forEach(f -> {
                if (f.toString().endsWith(".txt")){
                    try {
                        jsonObject.add(f.toString(), createLevelMetricJson(Files.readString(f)));
                        
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
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


    public static void main(String[] args) throws IOException{
        //System.out.println(createLevelMetricJson(testLevel));
        ArrayList<String> selectedFolders = new ArrayList<String>();
        
        // Uncomment to generate metrics for all levels
        /* 
        selectedFolders.add("generatedExamples/geminiLevelGenerator");
         selectedFolders.add("generatedExamples/constructiveLevelGenerator");
         selectedFolders.add("generatedExamples/geneticLevelGenerator");
         selectedFolders.add("generatedExamples/localLanguageModelGenerator");
         selectedFolders.add("generatedExamples/randomLevelGenerator");
        */
        for (String s: selectedFolders){
            System.out.println(createFolderMetricJson(s));
            Files.writeString(Path.of(s + "/" + "metrics.json"), createFolderMetricJson(s).toString());
        }
        
    }
}
