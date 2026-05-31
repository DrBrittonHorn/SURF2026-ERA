package tracks.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import tools.com.google.gson.JsonObject;
import tracks.metricCalculation.metrics.Density;
import tracks.metricCalculation.metrics.NegativeSpace;

public class calculateMetrics {

    public static String createLevelMetricJson(String levelText){
        JsonObject levelMetrics = new JsonObject();
        
        // Individual Metrics added to the json object below
        levelMetrics.addProperty("Density", Density.calculateDensity(levelText));
        levelMetrics.addProperty("NegativeSpace", NegativeSpace.calculateNegativeSpace(levelText));
        //levelMetrics.addProperty("SomeMetric", SomeMetric.calculateSomeMetric(levelText));

        return levelMetrics.toString();
    }


    public static void main(String[] args) throws IOException{
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/aliens/aliens_lvl001.txt"));
        System.out.println(createLevelMetricJson(testLevel));
    }
}
