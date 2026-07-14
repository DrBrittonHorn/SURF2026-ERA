package tools.metricCalculation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import tools.com.google.gson.Gson;
import tools.com.google.gson.JsonObject;

public class summarizeBinning {
    public static void main(String args[]) throws IOException{
        //Summarizes level validity for each generator
        String[] generators = {
            //"claudeLevelGenerator",
            "constructiveLevelGenerator",
            "enhancedClaudeGenerator",
            "geminiLevelGenerator",
            "geneticLevelGenerator",
            "randomLevelGenerator",
            "sturgeonLevelGenerator1x1",
            "sturgeonLevelGenerator2x2",
            "sturgeonLevelGenerator3x3",
            "sturgeonLevelGenerator4x4"
        };

        // Choose whether malformed level paths are printed
        boolean printMalformed = true;

        // Prints binning information for each generator
        System.out.println("Generator Summary");
        for (String generator : generators){
            // Change folder to finalized metrics as needed
            String metricPath = "generatedExamples/" + generator + "/levelMetrics.json";
            String jsonString = Files.readString(Path.of(metricPath));
            Gson gson = new Gson();
            JsonObject generatorJson = gson.fromJson(jsonString, JsonObject.class);

            // Every level in the metric set
            Set<String> levelPathKeys = generatorJson.keySet();
            int totalLevels = generatorJson.keySet().size();
            int validTotal = 0;
            int consistentRowsTotal = 0;
            int containsPlayerTotal = 0;
            int rowColumnQuotaTotal = 0;
            int notEmptyTotal = 0;

            System.out.println("***" + generator + " has " + totalLevels + " levels***");

            // Information by Generator
            for (String level : levelPathKeys){
                
                if (validBinning(generatorJson.getAsJsonObject(level).get("Binning*").getAsJsonObject())){
                    validTotal++;
                }
                else if (printMalformed == true){System.out.println(level);}
                if (generatorJson.getAsJsonObject(level).getAsJsonObject("Binning*").get("ConsistentRows").getAsBoolean()){
                    consistentRowsTotal++;
                }
                if (generatorJson.getAsJsonObject(level).getAsJsonObject("Binning*").get("ContainsPlayer").getAsBoolean()){
                    containsPlayerTotal++;
                }
                if (generatorJson.getAsJsonObject(level).getAsJsonObject("Binning*").get("RowColumnQuota").getAsBoolean()){
                    rowColumnQuotaTotal++;
                }
                if (generatorJson.getAsJsonObject(level).getAsJsonObject("Binning*").get("NotEmpty").getAsBoolean()){
                    notEmptyTotal++;
                }
                
            }
            

            String validRatio = String.format("%.2f", (double) validTotal / totalLevels);
            String consistentRowsRatio = String.format("%.2f", (double) consistentRowsTotal / totalLevels);
            String containsPlayerRatio = String.format("%.2f", (double) containsPlayerTotal / totalLevels);
            String rowColumnQuotaRatio = String.format("%.2f", (double) rowColumnQuotaTotal / totalLevels);
            String notEmptyRatio = String.format("%.2f", (double) notEmptyTotal / totalLevels);


            
            System.out.println(validTotal + " levels are valid overall (" + validRatio + ")");
            System.out.println(consistentRowsTotal + " have consistent rows (" + consistentRowsRatio + ")");
            System.out.println(containsPlayerTotal + " contain a player (" + containsPlayerRatio + ")");
            System.out.println(rowColumnQuotaTotal + " have sufficient rows and columns (" + rowColumnQuotaRatio + ")");
            System.out.println(notEmptyTotal + " are not empty (" + notEmptyRatio + ")");

            HashMap<String, Integer> validTotalsByGame = new HashMap<String, Integer>();
            HashMap<String, Integer> levelTotalsByGame = new HashMap<String, Integer>();

            for (String level : levelPathKeys){
                String gameName = level.split("\\\\|/")[2];
                boolean levelIsValid = validBinning(generatorJson.getAsJsonObject(level).get("Binning*").getAsJsonObject());
                levelTotalsByGame.put(gameName, levelTotalsByGame.getOrDefault(gameName, 0)+1);
                if (levelIsValid){
                    validTotalsByGame.put(gameName, validTotalsByGame.getOrDefault(gameName, 0)+1);
                }else{
                    validTotalsByGame.put(gameName, validTotalsByGame.getOrDefault(gameName, 0)+0);
                }
                
            }

            HashMap<String, Double> validRatiosByGame = new HashMap<String, Double>();
            for (String key : validTotalsByGame.keySet()){
                validRatiosByGame.put(key, (double) validTotalsByGame.get(key) / levelTotalsByGame.get(key));
            }

            for (String key : validRatiosByGame.keySet()){
                System.out.println(key + ": " + validTotalsByGame.get(key) + " were valid " + "(" + String.format("%.2f", validRatiosByGame.get(key)) + ").");
            }
            System.out.println(); System.out.println();
            
        }


    }

    private static boolean validBinning(JsonObject binningJson){
        Set<String> keySet = binningJson.keySet();
        for (String key : keySet){
            if (!binningJson.get(key).getAsBoolean()){
                return false;
            }
        }
        return true;
    }
}
