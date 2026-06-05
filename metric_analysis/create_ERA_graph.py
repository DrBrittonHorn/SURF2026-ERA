from matplotlib import pyplot as plt
import numpy as np
import sys, os
# Fixes local import behavior
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from metric_analysis.tools import create_attribute_dict, parseBinning

def create_graph(metric_tuple: tuple, json_path: str, exclude_malformed=True, binning_path=None):
    
    dict = create_attribute_dict(metric_path)
    
    listX = []
    listY = []
    for level_path, metrics in dict.items():
        # Create lists of the variables used in this graph
        if (metrics[selected_metrics[0]] > 0 and metrics[selected_metrics[1]] > 0):
            if (not exclude_malformed or (parseBinning(level_path, dict))): 
                listX.append(metrics[selected_metrics[0]])
                listY.append(metrics[selected_metrics[1]])
            

    print(f"Creating a chart with {len(listX)} levels as data points")
    
    fig, ax = plt.subplots()


    # ax.hist2d(listX, listY, bins=(25))
    ax.hexbin(listX, listY, bins=(500), gridsize=50)
    


    # Sets bounds for figure
    ax.set(xlim=(min(listX), max(listX)), ylim=(min(listY), max(listY)))
    
    # Set exterior characteristics
    ax.set_title(json_path.split("/")[1] + " ERA Chart")
    ax.set_xlabel(selected_metrics[0])
    ax.set_ylabel(selected_metrics[1])

    plt.show()


# USAGE: Select a metrics.json file path, then determinr the graph's x and y axis by completing the selected metrics tuple

# metric_path = "generatedExamples/geminiLevelGenerator/metrics.json"
# metric_path = "generatedExamples/LocalLanguageModelGenerator/metrics.json"
metric_path = "generatedExamples/constructiveLevelGenerator/metrics.json"
# metric_path = "generatedExamples/randomLevelGenerator/metrics.json"


# metric_path = "generatedExamples/constructiveLevelGenerator/dungeon/metrics.json"
# metric_path = "generatedExamples/geminiLevelGenerator/frogs/metrics.json"

selected_metrics = ("Density", "ShannonEntropy")
create_graph(selected_metrics, metric_path, exclude_malformed=True)

# TODO Saving charts, Histograms, summary tables