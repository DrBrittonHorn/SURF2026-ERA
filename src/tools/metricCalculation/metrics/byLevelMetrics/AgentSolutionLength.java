package tools.metricCalculation.metrics.byLevelMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import tools.metricCalculation.metricTools;

public class AgentSolutionLength {
    // Returns -1 for levels that were not completed
    
    public static double calculateMetric(String levelPath) throws IOException{
        // return an error value if the level has no avatar
        if (!metricTools.getLevelTiles(Files.readString(Path.of(levelPath))).contains("A")){
            return -2;
        }
        if (!Files.isRegularFile(Path.of(levelPath.replace("generatedExamples", "generatedExamplesPlaytraces")))
            || Files.readString(Path.of(levelPath.replace("generatedExamples", "generatedExamplesPlaytraces"))).isBlank()){
            metricTools.createPlaytrace(levelPath);
            System.out.println("Making playtrace");
        }
        
        String[] actions = Files.readString(Path.of(levelPath.replace("generatedExamples", "generatedExamplesPlaytraces"))).split("\n");
        if (actions[0].charAt(2) == '0'){return -1;} // Check index of win/loss in playtrace data. Losses have no length so they return -1
        else {return actions.length-1;}
    }


    public static void main(String[] args) throws IOException{
        String testLevelPath1 = "generatedExamples/geminiLevelGenerator/aliens/aliens_lvl005.txt";
        String testLevelPath2 = "generatedExamples/geminiLevelGenerator/realsokoban/realsokoban_lvl001.txt";
        String testLevelPath3 = "generatedExamples/geminiLevelGenerator/zelda/zelda_lvl006.txt";
        System.out.println(calculateMetric(testLevelPath3));
    }
}
