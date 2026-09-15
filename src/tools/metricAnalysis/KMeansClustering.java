
package tools.metricAnalysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tools.com.google.gson.Gson;
import tools.com.google.gson.JsonObject;
import tools.com.google.gson.reflect.TypeToken;




public class KMeansClustering {

    

    public static HashMap<String, ArrayList<Double>> parseLevelMetrics(String levelsJsonPath) throws IOException{
        String jsonBody = Files.readString(Path.of(levelsJsonPath));
        HashMap<String, HashMap<String, Object>> resultMap = new Gson().fromJson(jsonBody, new TypeToken<HashMap<String, HashMap<String, Object>>>(){}.getType());
        // Converts level data into a hashmap that maps levelNames to their metric values (for all metrics that are expressed as doubles)
        HashMap<String, ArrayList<Double>> levelsToVectors = new HashMap<String, ArrayList<Double>>();
        int usedMetrics = 0;
        for (String levelName : resultMap.keySet()){
            ArrayList<Double> levelVector = new ArrayList<>(50);
            HashMap<String, Object> levelMetrics = resultMap.get(levelName);
            for (String key : levelMetrics.keySet()){
                if (levelMetrics.get(key) instanceof Double){
                    levelVector.add((Double) levelMetrics.get(key));
                }
            }
            levelsToVectors.put(levelName, levelVector);
            usedMetrics = levelVector.size();
        }
        //System.out.println(levelsToVectors);
        // Normalize vectors according to their max and min values for equally weighted distance calculation.
        ArrayList<Double> maxValues = new ArrayList<Double>();
        ArrayList<Double> minValues = new ArrayList<Double>();
        for (int i = 0; i < usedMetrics; i++){
            maxValues.add((double) Integer.MIN_VALUE);
            minValues.add((double) Integer.MAX_VALUE);
        }
        // Find max and min values for within each metric for normalization
        for (String level : levelsToVectors.keySet()){
            ArrayList<Double> levelVector = levelsToVectors.get(level);
            for (int i = 0; i < levelVector.size(); i++){
                if (levelVector.get(i) > maxValues.get(i)){
                    maxValues.set(i, levelVector.get(i));
                }
                if (levelVector.get(i) < minValues.get(i)){
                    minValues.set(i, levelVector.get(i));
                }
            }
        }
        // Apply normalization
        HashMap<String, ArrayList<Double>> levelsToNormalizedVectors = new HashMap<String, ArrayList<Double>>();
        for (String levelName : levelsToVectors.keySet()){
            ArrayList<Double> normalizedLevelVector = new ArrayList<Double>(50);
            for (int i = 0; i < levelsToVectors.get(levelName).size(); i++){
                double normalizedValue = (levelsToVectors.get(levelName).get(i)-minValues.get(i))/(maxValues.get(i)-minValues.get(i));
                normalizedLevelVector.add(normalizedValue);
            }
            levelsToNormalizedVectors.put(levelName, normalizedLevelVector);
        }
        return levelsToNormalizedVectors;
    }

    public static double euclideanDistance(ArrayList<Double> vector1, ArrayList<Double> vector2) throws Exception{
        if (vector1.size() != vector2.size()){throw new Exception("Lists should be of equal length!");}
        double sumOfSquares = 0;
        System.out.println(vector1);
        System.out.println(vector2);
        for (int i = 0; i < vector1.size(); i++){
            sumOfSquares += Math.pow((vector1.get(i)-vector2.get(i)), 2);
        }
        return Math.sqrt(sumOfSquares);
    }

    public static void main(String[] args) throws Exception{
        // Performs k mean clustering analysis on the provided levelMetrics.json file
        String levelsJsonPath = "finalizedMetrics\\constructiveLevelGenerator\\levelMetrics.json";
        HashMap<String, ArrayList<Double>> levelsToVectors = parseLevelMetrics(levelsJsonPath);
        //System.out.println(levelsToVectors);

        System.out.println(euclideanDistance(
            levelsToVectors.get("generatedExamples/constructiveLevelGenerator/frogs/frogs_lvl045.txt"),
            levelsToVectors.get("generatedExamples/constructiveLevelGenerator/realsokoban/realsokoban_lvl705.txt")
        ));
        //System.out.println(levelToVector(levelMap, "generatedExamples/constructiveLevelGenerator/realsokoban/realsokoban_lvl795.txt"));
    }

}