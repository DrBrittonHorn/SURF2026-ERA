package tracks.levelGeneration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Stream;

public class BinGeneratedLevels {
    
    public static boolean consistentRowLength(String level){
        if (level.length() == 0){return true;}
        if (level.contains("LevelDescription")){level = level.split("LevelDescription")[1];}
        String[] rows = Arrays.stream(level.split("\\r\\n|\\r|\\n")).filter(a -> !a.isEmpty()).toArray(String[]::new);

        //System.out.println(Arrays.toString(rows)); 
        int startingLength = rows[0].length();
        for (int i = 0; i < rows.length; i++){
            if (rows[i].length() != startingLength){
                //System.out.println(rows[0]);
                //System.out.println(rows[i]);
                return false;
            }
            
        }
        return true;
    }

    public static boolean containsPlayer(String level){
        Character avatar = 'A';
        if (level.contains("LevelDescription")){
            // TODO Add logic to find possible custom avatar character
            String desc = level.split("LevelDescription")[0];
            level = level.split("LevelDescription")[1];
        }
        for (int i = 0; i < level.length(); i++){
            if (level.charAt(i) == avatar){
                return true;
            }
        }
        return false;
    }

    public static boolean levelNotEmpty(String level){
        if (level.contains("LevelDescription")){level = level.split("LevelDescription")[1];}
        if (level.length() == 0){return false;}
        else{return true;}
    }

    public static String createValidityString(String level){
        String ret = "";
        if (!levelNotEmpty(level)){
            ret += "LevelEmpty";
        }
        if (!containsPlayer(level)){
            ret += "NoPlayer;";
        }
        if (!consistentRowLength(level)){
            ret += "InconsistentRows;";
        }
        if (ret.length() == 0){return "NoMalformationsDetected";}
        return ret.substring(0, ret.length()-1);
    }
        

    // Takes a folder path as input and creates a csv that categorizes the levels in that folder by several criteria.

    public static void generateBinJSON(String folderPath){
        Path startPath = Paths.get(folderPath);
        Path jsonPath = Path.of(folderPath + "/binning.json");
        try {  
            // Opening curly-brace
            Files.writeString(jsonPath, "{");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String results = "";

        try (
            Stream<Path> stream = Files.walk(startPath)) {
                
                stream.filter(Files::isRegularFile)
                  .forEach(e -> {
                    try {
                        if (e.toString().endsWith("txt")){
                            // Writes one line of the json
                            Files.write(jsonPath, ("\"" + e.toString() + "\":\"" + createValidityString(Files.readString(e)) + "\",\n").replace("\\", "\\\\").getBytes(), StandardOpenOption.APPEND);
                        }
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            
            if (Files.readString(jsonPath).length() <= 2){
                System.out.println("No level files found in " + folderPath);
                //Chop of unnecessary starting curly brace
                Files.writeString(jsonPath, "");
            }
            else{
                //Chop of extra comma and and ending curly-brace
                Files.writeString(jsonPath, Files.readString(jsonPath).substring(0, Files.readString(jsonPath).length()-2));
                Files.write(jsonPath, "}".getBytes(), StandardOpenOption.APPEND);
                System.out.println("Created binning file for " + folderPath);
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Creates a binning csv for all the present generators
    public static void main(String[] args){

        generateBinJSON("generatedExamples/constructiveLevelGenerator");
        generateBinJSON("generatedExamples/geminiLevelGenerator");
        generateBinJSON("generatedExamples/geneticLevelGenerator");
        generateBinJSON("generatedExamples/localLanguageModelGenerator");
        generateBinJSON("generatedExamples/randomLevelGenerator");
    }
}
