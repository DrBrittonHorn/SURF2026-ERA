package tools.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class cleanFinalMetrics {
    // Removes level files from the finalMetrics folder
    public static void main(String[] args) throws IOException{
        Stream<Path> files = Files.walk(Path.of("finalizedMetrics")).parallel();

        files.forEach(file -> {
            if (file.toString().endsWith(".txt")){
                try {
                    System.out.println(file.toString());
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
}
