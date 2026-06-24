package tools.metricCalculation.metrics.byGeneratorMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import tools.metricCalculation.metricTools;
import tools.metricCalculation.metrics.byLevelMetrics.StructuralSimilarityToCorpus;

public class OutputStructuralSimilarity {
    public static double calculateMetric(String generatorFolderPath) {
        ArrayList<String> levels = new ArrayList<String>();
        Stream<Path> levelPaths;
        try {
            levelPaths = Files.walk(Path.of(generatorFolderPath)).filter(p -> p.toString().endsWith(".txt"));
            levelPaths.forEach(path -> {
                try {
                    String levelMap = metricTools.getLevelTiles(Files.readString(path));
                    levels.add(levelMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            double totalSimilarity = 0;
            for (String s1 : levels){
                for (String s2 : levels){
                    //System.out.println(normalizedHammingDistance(s1, s2));
                    double levelStructuralSimilarity = StructuralSimilarityToCorpus.structuralSimilarity(metricTools.toArray(s1), metricTools.toArray(s2));
                    //System.out.println(levelStructuralSimilarity);
                    totalSimilarity += levelStructuralSimilarity;
                }
            }
            return totalSimilarity / (levels.size() * levels.size());
        } catch (IOException e) {
                e.printStackTrace();
        }
        
        return -1;
    }

    public static void main(String[] args) throws IOException{

        String testFolder = "generatedExamples/geminiLevelGenerator/aliens";
        System.out.println(calculateMetric(testFolder));

    }
}
