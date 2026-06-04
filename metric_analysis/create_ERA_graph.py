from matplotlib import pyplot as plt
import numpy as np
import sys, os
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from metric_analysis.tools import create_attribute_dict

def create_graph(metric_tuple: tuple, json_path: str):
    dict = create_attribute_dict(metric_path)
    
    # plt.style.use('_mpl-gallery-nogrid')

    listX = []
    listY = []
    for level_path, metrics in dict.items():
        # Create lists of the variables used in this graph
        listX.append(metrics[selected_metrics[0]])
        listY.append(metrics[selected_metrics[1]])

    print(len(listX))
    print(len(listY))

    X = np.array(listX)
    Y = np.array(listY)
    
    fig, ax = plt.subplots()


    # ax.hist2d(X, Y, bins=(np.arange(0, 1, 0.01), np.arange(0, 1, 0.01)))
    ax.hist2d(X, Y, bins=(50))


    ax.set(xlim=(0, 1), ylim=(0, 1))

    plt.show()



# metric_path = "generatedExamples/geminiLevelGenerator/metrics.json"
# metric_path = "generatedExamples/LocalLanguageModelGenerator/metrics.json"
metric_path = "generatedExamples/constructiveGenerator/metrics.json"

selected_metrics = ("Density", "NegativeSpace")

create_graph(selected_metrics, metric_path)