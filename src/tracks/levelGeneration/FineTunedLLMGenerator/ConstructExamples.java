package tracks.levelGeneration.FineTunedLLMGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import tools.com.google.gson.Gson;
import tools.com.google.gson.JsonObject;
import utils.com.google.gson.GsonBuilder;


// See https://www.youtube.com/watch?v=pTaSDVz0gok&t=366s for more information of fine-tuning
// Only using games from examples/contphysics and examples/gridphysics

public class ConstructExamples {
    public static void main(String [] args) throws IOException{
        ArrayList<HashMap<String, String>> examples = new ArrayList<HashMap<String, String>>();
        ArrayList<HashMap<String, String>> evaluationExamples = new ArrayList<HashMap<String, String>>();
        Stream<Path> contPhysics = Files.list(Path.of("examples/contphysics"));
        Stream<Path> gridphysics = Files.list(Path.of("examples/gridphysics"));
        
        // Gets prompts file, chooses prompt 2
        Gson g = new Gson();
        JsonObject j = g.fromJson(Files.readString(Path.of("src\\tracks\\levelGeneration\\prompts.json")), JsonObject.class);
        String promptStub = j.get(String.valueOf(3)).getAsString(); // (2 for old, 3 for new)
        
        Stream<Path> gamesFolder = Stream.concat(contPhysics, gridphysics);
        // Filters relevant game folders to only contain one file referencing each game
        
        
        //Old version, only used for a few games
        /*gamesFolder.filter(f -> (!f.toString().contains("lvl") && !f.toString().contains("_"))).forEach(gamePath -> {
            try {
                // Level 0 will be used as an input during fine tuning
                // Level 1-3 will be used as fine ground truth fine tuning answers
                // Level 4's are excluded as they will be used as prompts during test evaluation
                // So we get 3 training examples per game
                String gameVGDL = Files.readString(gamePath);
                String gameName = gamePath.toString().split(".txt")[0];
                String[] gameLevels = new String[5];
                for (int i = 0; i < gameLevels.length; i++){
                    gameLevels[i] = Files.readString(Path.of(gameName + "_lvl" + i + ".txt"));
                }
                // Building prompt
                String prompt = promptStub + "\n" + gameVGDL + "\n" + gameLevels[0];
                // Iterating through designated training examples to add them to the broader examples data structure
                for (int i = 1; i < gameLevels.length-1; i++){
                    HashMap<String, String> example = new HashMap<String, String>();
                    example.put("input", prompt);
                    example.put("output", gameLevels[i]);
                    examples.add(example);
                }
                // Repeating process for one evaluation example
                HashMap<String, String> evaluationExample = new HashMap<String, String>();
                evaluationExample.put("input", prompt);
                evaluationExample.put("output", gameLevels[4]);
                evaluationExamples.add(evaluationExample);

            } catch (IOException e) {e.printStackTrace();}
            

        });*/

        gamesFolder.filter(f -> (!f.toString().contains("lvl") && !f.toString().contains("_"))).forEach(gamePath -> {
            try {
                // Level 0 will be used as an input during fine tuning
                // Level 1-3 will be used as fine ground truth fine tuning answers
                // Level 4's are excluded as they will be used as prompts during test evaluation
                // So we get 3 training examples per game
                String gameVGDL = Files.readString(gamePath);
                String gameName = gamePath.toString().split(".txt")[0];
                String[] gameLevels = new String[5];
                for (int i = 0; i < gameLevels.length; i++){
                    gameLevels[i] = Files.readString(Path.of(gameName + "_lvl" + i + ".txt"));
                }
                // Building prompt
                String prompt = promptStub + "\n" + gameLevels[0];
                // Iterating through designated training examples to add them to the broader examples data structure
                for (int i = 1; i < gameLevels.length; i++){
                    HashMap<String, String> example = new HashMap<String, String>();
                    example.put("input", prompt);
                    example.put("output", gameLevels[i]);
                    examples.add(example);
                }    

            } catch (IOException e) {e.printStackTrace();}
            

        });

        // Writes examples to file
        utils.com.google.gson.Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Files.writeString(Path.of("src\\tracks\\levelGeneration\\FineTunedLLMGenerator\\examples.json"), gson.toJson(examples));
        System.out.println("Constructed " + examples.size() + " examples!");

        // Write evaluation examples
        Files.writeString(Path.of("src\\tracks\\levelGeneration\\FineTunedLLMGenerator\\evaluationExamples.json"), gson.toJson(evaluationExamples));


        System.out.println("wwwwwwwwwwwwwwwwwwwwww\r\nwwww    ddddddd      w\r\nwwww        ddd      w\r\nw     G       d     dw\r\nw                    w\r\nw   d      G   d     w\r\nw    ddd          d  w\r\nw      dddddddddddd  w\r\nw              dd    w\r\nw  G   d        d    w\r\nw      G         G   w\r\nwA    wwwwww         w\r\nwwwwwwwwwwwwwwwwwwwwww");

        System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww\r\nw..A...g.....t...g.ggtt.gt....w\r\nw........../............\"wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww\r\nw..A...g.....t...g.ggtt.gt....w\r\nw........../................/w\r\nw........./t+./.++.........../w\r\nw.........///////..++//.wwww.wt.\r\nwww...www//+/////.../w\r\nw........./t+./.++.........../w\r\nw.........///////..++//.wwww.wt.\r\nwww...www//+//////////////+gw+\r\nwgggggggggvttttttttttttttttgtyy\r\nw..............................w\r\nwww/../...////////+gw+\r\nwgggggggggvttttttttttttttttgtyy\r\nw..............................w\r\nwww/../...../.t.++++//../../w/\r\nwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
    }

}

