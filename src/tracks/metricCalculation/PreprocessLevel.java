package tracks.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PreprocessLevel {
    
    


    public static String alterLevelMapping(String level){
        try {
            
            final String aliensMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String artilleryMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String asteroidsMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String dungeonMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String frogsMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String marioMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String realsokobanMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String roguelikeMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String towerdefenseMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];
            String zeldaMapping = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt")).split("LevelDescription")[0];

            String[] levelParts = level.split("LevelDescription");

            switch (levelParts[1]){
                case aliensMapping:
                    System.out.println("AAA");
                    break;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String args[]) throws IOException{
        String testLevel = Files.readString(Path.of("generatedExamples/constructiveLevelGenerator/dungeon/dungeon_lvl001.txt"));
        String charMap = testLevel.split("LevelDescription")[0];
        System.out.println(charMap.equals(DungeonMapping));
        System.out.println(charMap);
        System.out.println(DungeonMapping);

    }
}
