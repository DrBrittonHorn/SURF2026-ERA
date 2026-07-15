package tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import tools.metricCalculation.metricTools;

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

    /**
     * pads the first and last rows to match lengths
     * @param folder the desired generator and game to fix
     * @throws IOException 
     */
    public static void FixWhitespace(String folder) throws IOException {
        String levelsPath = "generatedExamples/" + folder;
        Stream<Path> levels = Files.walk(Path.of(levelsPath)).filter(f -> f.toString().endsWith(".txt"));
        levels.forEach(fileName -> {
            System.out.println(fileName.toString());

            try {
                String levelTotal = Files.readString(fileName);
                String levelTiles;
                if (levelTotal.contains("LevelDescription")){
                    levelTiles = levelTotal.split("LevelDescription")[1].strip();
                }
                else{
                    levelTiles = levelTotal.strip();
                }
                //System.out.println(levelTiles);

                ArrayList<ArrayList<Character>> levelChars = metricTools.toArray(levelTiles);
                //Find max
                int maxLength = 0;
                for (ArrayList<Character> arr : levelChars){
                    if (arr.size() > maxLength){maxLength = arr.size();}
                }
                //Add padding
                for (ArrayList<Character> arr : levelChars){
                    int addNum = maxLength - arr.size();
                    for (int i = 0; i < addNum; i++){arr.add(' ');}
                }
                // Recompose
                StringBuilder levelTilesPadded = new StringBuilder();
                if (levelTotal.contains("LevelDescription")){
                    levelTilesPadded.append(levelTotal.split("LevelDescription")[0] + "LevelDescription");
                }
                for (ArrayList<Character> arr : levelChars){
                    levelTilesPadded.append('\n');
                    for (Character c : arr){levelTilesPadded.append(c);}
                }
                Files.writeString(fileName, levelTilesPadded.toString());
                

            } catch (IOException e) {e.printStackTrace();}

        });
    }

    public static void main(String[] args) throws IOException {
        FixWhitespace("geneticLevelGenerator/asteroids");
    }
}
