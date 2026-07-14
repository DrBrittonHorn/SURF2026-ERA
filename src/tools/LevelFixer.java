package tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class LevelFixer {
    
    /**
     * replace instances of 'B' with 'A' in frogs levels
     * @param generator the desired generator of which the frogs levels need to be fixed
     * @throws IOException 
     */
    public static void FixFrogs(String generator) throws IOException {
        String LevelMapping = Files.readString(Path.of("examples/gridphysics/frogs_glvl.txt")).split("wwwwwwwwwwwwwwwww")[0];
        String path = null;
        String LevelDescription = null;
        int count = 0;
        ArrayList<Integer> levels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            path = ("generatedExamples\\" + generator + "\\" + "frogs" + "\\" + "frogs_lvl00" + i + ".txt");
            LevelDescription = Files.readString(Path.of(path)).split("LevelDescription")[1].strip();
            if (LevelDescription.contains("B")) {
                String newLevelDescription = LevelDescription.replace("B", "A");
                Files.writeString(Path.of(path), (LevelMapping + newLevelDescription));
                count++;
                levels.add(i);
            };
        }
        for (int i = 10; i < 100; i++) {
            path = ("generatedExamples\\" + generator + "\\" + "frogs" + "\\" + "frogs_lvl0" + i + ".txt");
            LevelDescription = Files.readString(Path.of(path)).split("LevelDescription")[1].strip();
            if (LevelDescription.contains("B")) {
                String newLevelDescription = LevelDescription.replace("B", "A");
                Files.writeString(Path.of(path), (LevelMapping + newLevelDescription));
                count++;
                levels.add(i);
            };
        }
        for (int i = 100; i < 1000; i++) {
            path = ("generatedExamples\\" + generator + "\\" + "frogs" + "\\" + "frogs_lvl" + i + ".txt");
            LevelDescription = Files.readString(Path.of(path)).split("LevelDescription")[1].strip();
            if (LevelDescription.contains("B")) {
                String newLevelDescription = LevelDescription.replace("B", "A");
                Files.writeString(Path.of(path), (LevelMapping + newLevelDescription));
                count++;
                levels.add(i);
            };
        }

        System.out.println("number of fixed levels: " + count);
        System.out.println("list of fixed levels: " + levels);
    }

    public static void FixAsteroids(String generator) {
        
    }

    /**
     * pads the first and last rows to match the length of the rest of them in mario levels
     * @param generator the desired generator of which the mario levels need to be fixed
     * @throws IOException 
     */
    public static void FixMario(String generator) throws IOException {
        
    }

    public static void main(String[] args) throws IOException {
        FixFrogs("sturgeonLevelGenerator1x1");
        FixFrogs("sturgeonLevelGenerator2x2");
        FixFrogs("sturgeonLevelGenerator3x3");
    }
}
