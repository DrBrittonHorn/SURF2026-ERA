import subprocess
import glob
import os
from multiprocessing import Pool

def generate_level(output_path : str):
    subprocess.run(["java",
                     "-cp", 
                     "task_parallelization/geneticClasses", 
                     "tracks.levelGeneration.generateGeneticLevels",
                     output_path])
    
if __name__ == "__main__":

    print("Starting subprocesses")

    subprocess.run(["javac", 
                    "-d", 
                    "task_parallelization/geneticClasses", 
                    "-sourcepath", 
                    "src", 
                    r"src/tracks/levelGeneration/generateGeneticLevels.java"],
                    check=True, cwd=r".")
    # Sample use
    # generate_level("generatedExamples/geneticParallel/aliens/aliens_lvl001.txt")
    
    games = ["aliens", "artillery", "asteroids", "dungeon", "frogs", "mario", "realsokoban", "roguelike", "towerdefense", "zelda"]

    to_generate = []

    desired_interval = (0, 0)
    for i in range(desired_interval[0], desired_interval[1]):
        for game in games:
            output_location = (
            "generatedExamples/geneticParallel/" +
            game + "/" + game + "_lvl" + f"{i:03}" + ".txt"
            )
            to_generate.append(output_location)

    to_generate = [level for level in to_generate if not os.path.exists(level)]
    
    print(to_generate)
    print(f"Generating genetic levels with {len(to_generate)} left to complete")

    with Pool(12) as p:
        (p.map(generate_level, to_generate))
        
    