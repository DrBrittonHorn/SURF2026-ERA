package tools.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class repairGameDescriptions {
    // Fixes enhanced claude generator not having tile mappings
    public static void main(String args[]) throws IOException{
        /*Stream<Path> levels = Files.walk(Path.of("generatedExamples/enhancedClaudeGenerator"));

        levels.forEach(path -> {

            //System.out.println(path.toString());
            Path replacementPath = Path.of(path.toString().replace("enhancedClaudeGenerator", "constructiveLevelGenerator"));
            try {
                String replacementLevel = Files.readString(replacementPath);
                String replacementMapping = replacementLevel.split("LevelDescription")[0];
                String replacementContent = ("" + replacementMapping +  "LevelDescription" + "\r\n" + Files.readString(path));
                System.out.println(replacementContent);
                //Files.writeString(path, replacementContent);

            } catch (IOException e) {}
            
            
        });*/

        removeDuplicateMapping();
    }

    public static void removeDuplicateMapping() throws IOException{
        Stream<Path> levels = Files.walk(Path.of("generatedExamples/enhancedClaudeGenerator/aliens"));
        levels.forEach(path -> {
            try {
                String content = Files.readString(path);
                String[] split = content.split("LevelDescription");
                String newContent = split[0] + "LevelDescription" + split[2];
                System.out.println(path.toString());
                System.out.println(newContent);
                Files.writeString(path, newContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public static void fixAliensNaming() throws IOException{
        Stream<Path> levels = Files.walk(Path.of("generatedExamplesPlaytracesLab-1k-80ms/enhancedClaudeGenerator/aliens"));
        levels.forEach(path -> {
            if (!Files.isDirectory(path)){
                String pathString = path.toString();
                //System.out.println(pathString);
                String number = pathString.split("_lvl")[1].split(".txt")[0];
                //System.out.println(number);
                String numberFormatted = String.format("%03d", Integer.parseInt(number));
                System.out.println(numberFormatted);

                String newPath = pathString.split("_lvl")[0] + "_lvl" + numberFormatted + ".txt";
                System.out.println(newPath);

                try {
                    Files.move(path, Path.of(newPath));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }




            

        });
    }
}
