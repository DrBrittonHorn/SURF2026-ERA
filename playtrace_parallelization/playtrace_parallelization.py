import subprocess
import glob
import os
from multiprocessing import Pool

def f(x):
        return x*x

# Runs createPlaytraces for one level (level path String is used as input)
def create_playtrace(level_path : str):
    subprocess.run(["java", "-cp", "playtrace_parallelization/playtraceClasses", "tools.metricCalculation.createPlaytraces", level_path], 
                cwd=r"C:\Users\justi\Coding\GVGAI Local Desktop\SURF2026-ERA")


if __name__ == '__main__':

    print('Starting subprocesses')
    # Compiles and our latest createPlaytraces file for use here.
    subprocess.run(["javac", "-d", "playtrace_parallelization/playtraceClasses", "-sourcepath", "src", r"src\tools\metricCalculation\createPlaytraces.java"], 
                   check=True, cwd=r"C:\Users\justi\Coding\GVGAI Local Desktop\SURF2026-ERA")
    
    # Get paths of levels that need playtraces
    selected_generator = "constructiveLevelGenerator"
    files = glob.glob(f'./generatedExamples/{selected_generator}/*/*')
    # filter for level files
    for file in files:
         if not file.endswith(".txt"): files.remove(file); print(file)
         if os.path.isdir(file): files.remove(file); print(file)
    # Create list of levels that need playtraces
    to_compute = []
    # print(files)
    for file in files:
         if not os.path.exists(file.replace("generatedExamples", "generatedExamplesPlaytraces")):
              to_compute.append(file)


    with Pool(12) as p:
        (p.map(create_playtrace, to_compute))



