package tracks.levelGeneration;

import java.util.Random;

public class TestLevelGeneration {


    @SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {

		// Available Level Generators
		String randomLevelGenerator = "tracks.levelGeneration.randomLevelGenerator.LevelGenerator";
		String geneticLevelGenerator = "tracks.levelGeneration.geneticLevelGenerator.LevelGenerator";
		String constructiveLevelGenerator = "tracks.levelGeneration.constructiveLevelGenerator.LevelGenerator";
		String languageModelGenerator = "tracks.levelGeneration.languageModelLevelGenerator.LevelGenerator";

		String gamesPath = "examples/gridphysics/";
		String physicsGamesPath = "examples/contphysics/";
		String generateLevelPath = gamesPath;
		//String generateLevelPath = physicsGamesPath;


		String games[] = new String[] { "aliens", "angelsdemons", "assemblyline", "avoidgeorge", "bait", // 0-4
				"beltmanager", "blacksmoke", "boloadventures", "bomber", "bomberman", // 5-9
				"boulderchase", "boulderdash", "brainman", "butterflies", "cakybaky", // 10-14
				"camelRace", "catapults", "chainreaction", "chase", "chipschallenge", // 15-19
				"clusters", "colourescape", "chopper", "cookmepasta", "cops", // 20-24
				"crossfire", "defem", "defender", "digdug", "dungeon", // 25-29
				"eighthpassenger", "eggomania", "enemycitadel", "escape", "factorymanager", // 30-34
				"firecaster", "fireman", "firestorms", "freeway", "frogs", // 35-39
				"garbagecollector", "gymkhana", "hungrybirds", "iceandfire", "ikaruga", // 40-44
				"infection", "intersection", "islands", "jaws", "killBillVol1", // 45-49
				"labyrinth", "labyrinthdual", "lasers", "lasers2", "lemmings", // 50-54
				"missilecommand", "modality", "overload", "pacman", "painter", // 55-59
				"pokemon", "plants", "plaqueattack", "portals", "raceBet", // 60-64
				"raceBet2", "realportals", "realsokoban", "rivers", "roadfighter", // 65-69
				"roguelike", "run", "seaquest", "sheriff", "shipwreck", // 70-74
				"sokoban", "solarfox", "superman", "surround", "survivezombies", // 75-79
				"tercio", "thecitadel", "thesnowman", "waitforbreakfast", "watergame", // 80-84
				"waves", "whackamole", "wildgunman", "witnessprotection", "wrapsokoban", // 85-89
				"zelda", "zenpuzzle", "towerdefense", //90, 91, 92
				"mario", "artillery", "asteroids"}; // 93, 94, 95 // continous physics


		String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
										// + levelIdx + "_" + seed + ".txt";
										// where to record the actions
										// executed. null if not to save.

		// Other settings
		int seed = new Random().nextInt();
		int gameIdx = 92;
		String recordLevelFile = generateLevelPath + games[gameIdx] + "_glvl.txt";
		String game = generateLevelPath + games[gameIdx] + ".txt";


		// 1. This starts a game, in a generated level created by a specific level generator
		if(LevelGenMachine.generateOneLevel(game, constructiveLevelGenerator, recordLevelFile)){
		    LevelGenMachine.playOneGeneratedLevel(game, recordActionsFile, recordLevelFile, seed);
		}

		// 2. This generates numberOfLevels levels.
		// int GamePick = 67; // 0-95
		// String GameName = games[GamePick];
		// String GeneratorName = "constructiveLevelGenerator"; // "randomLevelGenerator" or "geneticLevelGenerator" or "constructiveLevelGenerator" or "languageModelGenerator"

		// String levelGenerator = "tracks.levelGeneration." + GeneratorName + ".LevelGenerator";
		// int numberOfLevels = 1;
		// tracks.levelGeneration.randomLevelGenerator.LevelGenerator.includeBorders = true;

		// String ThisPath = "generatedExamples/" + GeneratorName + "/" + GameName + "/";

		// game = generateLevelPath + GameName + ".txt";
		// for (int i = 0; i < numberOfLevels; i++) {
		// 	recordLevelFile = ThisPath + GameName + "_lvl" + i + ".txt";
		// 	LevelGenMachine.generateOneLevel(game, levelGenerator, recordLevelFile);
		// 	System.out.println("generated level " + (i+1));
		// }


    }
}
