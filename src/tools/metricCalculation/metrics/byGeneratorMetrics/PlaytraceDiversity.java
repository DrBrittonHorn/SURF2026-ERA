
package tools.metricCalculation.metrics.byGeneratorMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import tools.metricCalculation.metricTools;
import tools.metricCalculation.metrics.byLevelMetrics.LevenshteinDistance;

// Tests the diversity of pllaytraces via Levenshtein distance between all metrics

public class PlaytraceDiversity{
    public static double calculateMetric(String generatorFolderPath) throws IOException{
        
        Stream<Path> levels = Files.walk(Path.of(generatorFolderPath)).filter(path -> path.toString().endsWith(".txt"));

        ArrayList<ArrayList<String>> allLevelActions = new ArrayList<ArrayList<String>>();

        levels.forEach(path -> {
            
            Path playtracePath = Path.of(path.toString().replace("generatedExamples", "generatedExamplesPlaytraces"));
            System.out.println(playtracePath.toString());
            try {
                if (!Files.isRegularFile(playtracePath) && metricTools.getLevelTiles(Files.readString(path)).contains("A")){
                    try {
                        //System.out.println("Making playtrace for " + path.toString());
                        metricTools.createPlaytrace(path.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String playtraceData;
            try {
                playtraceData = Files.readString(playtracePath);
                List<String> l = Arrays.asList(playtraceData.split("\n"));
                ArrayList<String> playtraceList = new ArrayList<String>();
                playtraceList.addAll(l);
                playtraceList.remove(0); // Removes the first line containing win/loss info
                allLevelActions.add(playtraceList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            

        });
        ArrayList<String> processedPlaytraceStrings = new ArrayList<String>();
        HashMap<String, Character> actionToChar = new HashMap<String, Character>();
        for (ArrayList<String> actions : allLevelActions){
            for (String action : actions){
                if (!actionToChar.containsKey(action)){
                    actionToChar.put(action, ((char) ('a' + actionToChar.size())));
                }
            }
        }
        //System.out.println(actionToChar);
        for (ArrayList<String> actions : allLevelActions){
            String levelActionsProcessed = "";
            for (String action : actions){
                levelActionsProcessed += actionToChar.get(action);
            }
            processedPlaytraceStrings.add(levelActionsProcessed);
        }
        processedPlaytraceStrings.remove(""); // Remove empty element

        //System.out.println(processedPlaytraceStrings);

        double accumulatedDifference = 0;
        int totalComparisons = 0;

        // Calculate distances between all playtraces
        for (int i = 0; i < processedPlaytraceStrings.size(); i++){
            for (int j = i; j < processedPlaytraceStrings.size(); j++){
                double difference = LevenshteinDistance.levenshteinFullMatrix(processedPlaytraceStrings.get(i), processedPlaytraceStrings.get(j));
                int max = Math.max(processedPlaytraceStrings.get(i).length(), processedPlaytraceStrings.get(j).length());
                accumulatedDifference += difference / max;
                totalComparisons++;
            }
        }

        return accumulatedDifference/totalComparisons;
    }

    public static void main(String[] args) throws IOException{

        String testFolder = "generatedExamples/randomLevelGenerator/";
        System.out.println(calculateMetric(testFolder));

    }
}