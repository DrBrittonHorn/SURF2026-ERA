package tracks.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import tools.com.google.gson.JsonObject;
import tracks.metricCalculation.metrics.Density;
import tracks.metricCalculation.metrics.NegativeSpace;

public class calculateMetrics {

    public static JsonObject createLevelMetricJson(String levelText){
        JsonObject levelMetrics = new JsonObject();
        
        // Individual Metrics added to the json object below
        levelMetrics.addProperty("Density", Density.calculateDensity(levelText));
        levelMetrics.addProperty("NegativeSpace", NegativeSpace.calculateNegativeSpace(levelText));
        //levelMetrics.addProperty("SomeMetric", SomeMetric.calculateSomeMetric(levelText));

        return levelMetrics;
    }

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
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/aliens/aliens_lvl001.txt"));
        //System.out.println(createLevelMetricJson(testLevel));

        String testFolder = "generatedExamples/constructiveLevelGenerator";

        System.out.println(createFolderMetricJson(testFolder));
        Files.writeString(Path.of(testFolder + "/" + "metrics.json"), createFolderMetricJson(testFolder).toString());
    }
}
