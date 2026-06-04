package tools.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

        String testFolder = "generatedExamples/constructiveLevelGenerator";

        System.out.println(createFolderMetricJson(testFolder));
        Files.writeString(Path.of(testFolder + "/" + "metrics.json"), createFolderMetricJson(testFolder).toString());
    }
}
