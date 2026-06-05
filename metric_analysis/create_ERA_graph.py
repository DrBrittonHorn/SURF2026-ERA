from matplotlib import pyplot as plt
import numpy as np
import sys, os
# Fixes local import behavior
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from metric_analysis.tools import create_attribute_dict, parseBinning

def create_graph(selected_metrics_tuple: tuple, json_path: str, exclude_malformed=True):
    
    dict = create_attribute_dict(json_path)
    
    listX = []
    listY = []
    for level_path, metrics in dict.items():
        # Create lists of the variables used in this graph
        if (metrics[selected_metrics[0]] > 0 and metrics[selected_metrics[1]] > 0): # Revisit this, a negative number may not mean error for all metrics...
            if (not exclude_malformed or (parseBinning(level_path, dict))): 
                listX.append(metrics[selected_metrics[0]])
                listY.append(metrics[selected_metrics[1]])
            

    print(f"Creating a chart with {len(listX)} levels as data points")
    
    fig, ax = plt.subplots()


    # ax.hist2d(listX, listY, bins=(25))
    ax.hexbin(listX, listY, bins=(5000), gridsize=50)
    


    # Sets bounds for figure
    ax.set(xlim=(min(listX), max(listX)), ylim=(min(listY), max(listY)))
    
    # Set exterior characteristics
    generator_name = json_path.split("/")[1]
    if json_path.split("/")[2] != "metrics.json": game_name = json_path.split("/")[2].capitalize() 
    else: game_name = ""
    ax.set_title(generator_name + " " + game_name + " ERA Chart")
    ax.set_xlabel(selected_metrics[0])
    ax.set_ylabel(selected_metrics[1])

    # "Y axis to X axis"
    plt.savefig(("figures/ERA/" + generator_name + game_name + selected_metrics[1] + "To" + selected_metrics[0] + ".png"), dpi=300, bbox_inches="tight")
    plt.show()

# USAGE: Select a metrics.json file path, then determinr the graph's x and y axis by completing the selected metrics tuple

# metric_path = "generatedExamples/geminiLevelGenerator/metrics.json"
# metric_path = "generatedExamples/LocalLanguageModelGenerator/metrics.json"
# metric_path = "generatedExamples/constructiveLevelGenerator/metrics.json"
# metric_path = "generatedExamples/randomLevelGenerator/metrics.json"


metric_path = "generatedExamples/constructiveLevelGenerator/dungeon/metrics.json"
# metric_path = "generatedExamples/geminiLevelGenerator/frogs/metrics.json"

selected_metrics = ("Density", "FloodReachability")
create_graph(selected_metrics, metric_path, exclude_malformed=True)

# TODO Summary tables, legend for ERA chart, total levels for ERA and histogram, email for thing