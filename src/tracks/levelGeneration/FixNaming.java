package tracks.levelGeneration;

import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

public class FixNaming {
    public static void main(String[] args) throws IOException{
        Path startPath = Path.of("generatedExamples/geminiLevelGenerator");
        DecimalFormat df = new DecimalFormat("000");
        Stream<Path> stream = Files.walk(startPath);{
            int total = 0;
            stream.filter(Files::isRegularFile).forEach(e -> {
                    
                    if (e.toString().endsWith("txt")){
                        Path oldPath = Path.of(e.toString());
                        int number = Integer.parseInt(e.toString().split("lvl")[1].split(".txt")[0]);
                        Path newPath = Path.of(e.toString().split("lvl")[0] + "lvl" + df.format(number) + ".txt");
                        System.out.println("New: " + newPath);
                        try {
                            Files.move(oldPath, newPath);
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                
                }
            );
        };



    }
}
