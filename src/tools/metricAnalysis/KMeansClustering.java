
package tools.metricAnalysis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import tools.Pair;
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
        //System.out.println(vector1);
        //System.out.println(vector2);
        for (int i = 0; i < vector1.size(); i++){
            sumOfSquares += Math.pow((vector1.get(i)-vector2.get(i)), 2);
        }
        return Math.sqrt(sumOfSquares);
    }

    @SuppressWarnings("unchecked")
    public static Pair<HashMap<String,Integer>,ArrayList<ArrayList<Double>>> performClustering(HashMap<String, ArrayList<Double>> levelsToVec, int desiredClusters) throws Exception{
        Iterator<ArrayList<Double>> arbitrary = levelsToVec.values().iterator();
        int vectorSize = arbitrary.next().size();
        Random random = new Random();
        ArrayList<ArrayList<Double>> centroids = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < desiredClusters; i++){
            ArrayList<Double> randomCentroid = new ArrayList<Double>();
            for (int j = 0; j < vectorSize; j++){
                randomCentroid.add(random.nextDouble());
            }
            centroids.add(randomCentroid);
        }
        //System.out.println(centroids);
        while (true){
            // Associate each level with its closest centroid via vector Euclidean distance"
            HashMap<String, Integer> levelsToCentroid = new HashMap<String, Integer>();
            for (String levelName : levelsToVec.keySet()){
                // Find best centroid for this level
                double minDistance = Integer.MAX_VALUE;
                int minIndex = -1;
                ArrayList<Double> centroidDistances = new ArrayList<Double>(desiredClusters);
                for (int i = 0; i < centroids.size(); i++){
                    double centroidDistance = euclideanDistance(levelsToVec.get(levelName), centroids.get(i));
                    centroidDistances.add(centroidDistance);
                    if (centroidDistance < minDistance){minDistance = centroidDistance; minIndex = i;}
                }
                levelsToCentroid.put(levelName, minIndex);
            }
            //System.out.println(levelsToCentroid);

            // Migrate each centroid to be the average of its associates
            ArrayList<ArrayList<Double>> newCentroids = new ArrayList<ArrayList<Double>>();
            for (int i = 0; i < desiredClusters; i++){
                ArrayList<Double> emptyCentroid = new ArrayList<Double>();
                for (int j = 0; j < vectorSize; j++){
                    emptyCentroid.add(0.0);
                }
                newCentroids.add(emptyCentroid);
            }
            // Calculate the sums of centroid associates to create the new centroid location
            ArrayList<Integer> centroidFrequency = new ArrayList<Integer>(); for (int i = 0; i < desiredClusters; i++){centroidFrequency.add(0);}
            for (String levelName : levelsToCentroid.keySet()){
                int levelCentroidIndex = levelsToCentroid.get(levelName);
                // Increment for division later
                centroidFrequency.set(levelCentroidIndex, centroidFrequency.get(levelCentroidIndex)+1);
                for (int i = 0; i < newCentroids.get(levelCentroidIndex).size(); i++){
                    // Perform piecewise addition to create the sum vector for the new centroid
                    newCentroids.get(levelCentroidIndex).set(i, newCentroids.get(levelCentroidIndex).get(i) + levelsToVec.get(levelName).get(i));
                }
            }
            // Normalize new centroid values according to their total number of associates
            for (int i = 0; i < centroidFrequency.size(); i++){
                for (int j = 0; j < newCentroids.get(i).size(); j++){
                    if (centroidFrequency.get(i) > 0){
                        newCentroids.get(i).set(j, newCentroids.get(i).get(j)/centroidFrequency.get(i));
                    }
                    else{
                        // Lonely centroid case, no normalization needed
                        // Because this centroid is set to 0 above, we will instead set it to be its previous iteration
                        newCentroids.set(i, (ArrayList<Double>) centroids.get(i).clone()); // Potential error source due to lack of deep copy
                    }
                    
                }
            }
            // Print cluster distribution
            System.out.println(centroidFrequency);
            
            ArrayList<Double> oldCentroidsConcat = new ArrayList<Double>();
            for (ArrayList<Double> oldCentroid : centroids){
                oldCentroidsConcat.addAll(oldCentroid);
            }
            ArrayList<Double> newCentroidsConcat = new ArrayList<Double>();
            for (ArrayList<Double> newCentroid : newCentroids){
                newCentroidsConcat.addAll(newCentroid);
            }
            if (newCentroidsConcat.equals(oldCentroidsConcat)){
                // Stable centroids found, exit the loop

                // We return the following
                // It contains a map from every level file name to the index of the centroid it was linked to. (These indices correspond to the centroids described by the second element of the returned tuple)
                Pair<HashMap<String, Integer>, ArrayList<ArrayList<Double>>> centroidMappingAndDetails = new Pair<HashMap<String, Integer>,ArrayList<ArrayList<Double>>>(levelsToCentroid, centroids);
                return centroidMappingAndDetails;

            }
            else{
                centroids = newCentroids;
            }
        }
        
        
    }

    public static void main(String[] args) throws Exception{
        // Performs k mean clustering analysis on the provided levelMetrics.json file
        String levelsJsonPath = "finalizedMetrics\\constructiveLevelGenerator\\levelMetrics.json";
        HashMap<String, ArrayList<Double>> levelsToVectors = parseLevelMetrics(levelsJsonPath);
        //System.out.println(levelsToVectors);

        /*System.out.println(euclideanDistance(
            levelsToVectors.get("generatedExamples/constructiveLevelGenerator/frogs/frogs_lvl045.txt"),
            levelsToVectors.get("generatedExamples/constructiveLevelGenerator/realsokoban/realsokoban_lvl705.txt")
        ));*/

        System.out.println(performClustering(levelsToVectors, 10));
    }

}