package tools.metricCalculation.metrics.byGeneratorMetrics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

public class OutputNGramSimilarity1D {
    public static double calculateMetric(String generatorFolderPath){
        ArrayList<Path> allLevelPaths = new ArrayList<Path>();
        try {
            Stream<Path> levelStream = Files.list(Path.of(generatorFolderPath)).filter(f -> f.toString().endsWith(".txt"));
            levelStream.forEach(path -> {
               allLevelPaths.add(path);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        double totalPlagiarism = 0;
        for (Path path : allLevelPaths){
            for (Path other : allLevelPaths){
                totalPlagiarism += 1.0/(allLevelPaths.size() * allLevelPaths.size()) * OutputNGramSimilarity2D.getSimilarity1D(path, other, 1);
            }
        }
        
        return totalPlagiarism;
    }

    public static void main(String[] args) throws IOException{

        String testFolder = "generatedExamples/geminiLevelGenerator/realsokoban";
        System.out.println(calculateMetric(testFolder));

    }
}

