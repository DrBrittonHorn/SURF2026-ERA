from matplotlib import pyplot as plt
import sys, os

import numpy as np

# Fixes local import behavior
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))
from metric_analysis.figure_types import ERA_graph
from metric_analysis.tools import create_attribute_dict, get_generator_title, get_metric_title, parse_binning

def create_ERA_grid(json_path: str, exclude_malformed=True):
    dict = create_attribute_dict(json_path)
    metrics = list(next(iter(dict.values())))
    # Remove non-plotted metrics
    metrics = [m for m in metrics if "*" not in m]
    
    total_metrics = len(metrics)
    print(metrics)

    # Chooses a subset of metrics for testing, use len(metrics to produce a grid for all metrics)
    sample_metrics = [metrics[i] for i in range(len(metrics))]
    total_sample_metrics = len(sample_metrics)

    fig, axes = plt.subplots(nrows=total_sample_metrics, ncols=total_sample_metrics, figsize=(len(sample_metrics)*4, len(sample_metrics)*4))
    
    for i, m1 in enumerate(sample_metrics):
        for j, m2 in enumerate(sample_metrics):
            ERA_graph.create_ERA_graph(
                selected_metrics_tuple=(m1, m2),
                json_path=json_path,
                exclude_malformed=exclude_malformed,
                ax=axes[i, j]
            )

            axes[i, j].tick_params(axis='both', which='both', length=0, labelbottom=False, labelleft=False)

            axes[i, j].xaxis.set_label_position('top')
            # Set column labels and row labels (respectively)
            if i == 0: axes[i, j].set_xlabel(get_metric_title(m2), fontsize=35, rotation=30, ha="left", labelpad=20)
            if j == 0: axes[i, j].set_ylabel(get_metric_title(m1), fontsize=35, rotation=0, ha="right", va="center", labelpad=20)

    
    plt.subplots_adjust(wspace=0.1, hspace=0.1) 
    generator_name = json_path.split("/")[1]
    plt.suptitle(get_generator_title(json_path) + " All Metrics ERA", fontsize=75)
    save_file_name = "figures/" + generator_name + "/ERA_Grid"
    plt.savefig(save_file_name, dpi=300, bbox_inches='tight', pad_inches=1)

    # plt.show()


metric_path = "generatedExamples/geminiLevelGenerator/levelMetrics.json"
# metric_path = "generatedExamples/LocalLanguageModelGenerator/metrics.json"
# metric_path = "generatedExamples/constructiveLevelGenerator/metrics.json"
# metric_path = "generatedExamples/randomLevelGenerator/metrics.json"

create_ERA_grid(metric_path, True)