
package tools.metricCalculation.metrics.byGeneratorMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import tools.metricCalculation.metricTools;

public class PlaytraceDiversity{
    public static double calculateMetric(String generatorFolderPath) throws IOException{
        
        Stream<Path> levels = Files.list(Path.of(generatorFolderPath)).filter(path -> path.toString().endsWith(".txt"));

        ArrayList<ArrayList<String>> allLevelActions = new ArrayList<ArrayList<String>>();

        levels.forEach(path -> {
            
            Path playtracePath = Path.of(generatorFolderPath.replace("generatedExamples", "generatedExamplesPlaytraces"));
            if (!Files.isRegularFile(playtracePath)){
                try {
                    //System.out.println("Making playtrace for " + path.toString());
                    metricTools.createPlaytrace(path.toString());
                    String playtraceData = Files.readString(path);
                    List<String> l = Arrays.asList(playtraceData.split("\n"));
                    ArrayList<String> playtraceList = new ArrayList<String>();
                    playtraceList.addAll(l);
                    allLevelActions.add(playtraceList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        // TODO Use edit distance to evaluate allLevelActions 
        return 0;
    }

    public static void main(String[] args) throws IOException{

        String testFolder = "generatedExamples/geminiLevelGenerator/asteroids";
        System.out.println(calculateMetric(testFolder));

    }
}