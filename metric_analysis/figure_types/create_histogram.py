from matplotlib import pyplot as plt
import numpy as np
import sys, os
# Fixes local import behavior
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))
from metric_analysis.tools import create_attribute_dict, get_official_generator_title, get_official_metric_title, parse_binning

def create_histogram(selected_metric, json_path: str, exclude_malformed=True):
    # plt.style.use('_mpl-gallery')
    dict = create_attribute_dict(json_path)
    listX = []

    for level_path, metrics in dict.items():
        # Create lists of the variables used in this graph
        if (metrics[selected_metric] > 0): # Revisit this, a negative number may not mean error for all metrics...
            if (not exclude_malformed or (parse_binning(level_path, dict))): 
                listX.append(metrics[selected_metric])
        # print(listX)

    print(f"Creating a Histogram chart with {len(listX)} levels as data points")


    fig, ax = plt.subplots()

    ax.hist(listX, bins=100, linewidth=0.5, edgecolor="white")

    ax.set(xlim = (min(listX), max(listX)))
    # Set exterior characteristics
    generator_name = json_path.split("/")[1]
    if json_path.split("/")[2] != "metrics.json": game_name = json_path.split("/")[2].capitalize() 
    else: game_name = ""
    ax.set_title(selected_metric + "" + game_name + " Histogram")
    ax.set_xlabel(get_official_metric_title(selected_metric))
    
    
    # Save and show
    save_file_name = "figures/" + generator_name + "/Histograms/" + game_name + selected_metric + ".png"
    if os.path.isfile(save_file_name): os.remove(save_file_name)
    plt.savefig((save_file_name), dpi=300, bbox_inches="tight")
    # plt.show()
    plt.close()


# USAGE: Select a metrics.json file path, then determinr the graph's selected_metric variable

metric_path = "generatedExamples/geminiLevelGenerator/metrics.json"
# metric_path = "generatedExamples/LocalLanguageModelGenerator/metrics.json"
# metric_path = "generatedExamples/constructiveLevelGenerator/metrics.json"
# metric_path = "generatedExamples/randomLevelGenerator/metrics.json"

# metric_path = "generatedExamples/constructiveLevelGenerator/dungeon/metrics.json"
# metric_path = "generatedExamples/geminiLevelGenerator/frogs/metrics.json"

selected_metric = "FloodReachability"
# create_histogram(selected_metric, metric_path, exclude_malformed=True)