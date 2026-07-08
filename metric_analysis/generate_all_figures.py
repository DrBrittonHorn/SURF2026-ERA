import os
import sys

# Ensure the repository root is on the import path when running this script directly.
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from metric_analysis.figure_types.ERA_by_generator import create_ERA_by_Generator
from metric_analysis.figure_types.density_estimation_chart import create_density_estimation
from metric_analysis.figure_types.generator_ERA import create_ERA_graph
from metric_analysis.figure_types.generator_ERA_by_game import create_ERA_graph_by_game

from metric_analysis.figure_types.histogram import create_histogram
from metric_analysis.figure_types.table import create_table

from metric_analysis.tools import create_attribute_dict


# Usage: Update the metric paths list to choose which folders to generate all figures for

metric_paths = [
    "generatedExamples/constructiveLevelGenerator/levelMetrics.json",
    #"generatedExamples/claudeLevelGenerator/levelMetrics.json",
    #"generatedExamples/fineTunedLLMGenerator/levelMetrics.json",
    "generatedExamples/geminiLevelGenerator/levelMetrics.json",
    "generatedExamples/geneticLevelGenerator/levelMetrics.json",
    #"generatedExamples/localLanguageModel/levelMetrics.json",
    "generatedExamples/randomLevelGenerator/levelMetrics.json",
    ]


for json_path in metric_paths:
    # Prepare save directories
    generator_name = json_path.split("/")[1]
    os.makedirs("figures/" + generator_name + "/ERA/", exist_ok=True)
    os.makedirs("figures/" + generator_name + "/Density/", exist_ok=True)
    os.makedirs("figures/" + generator_name + "/Histograms/", exist_ok=True)
    os.makedirs("figures/" + generator_name + "/Tables/", exist_ok=True)

    # Create tables
    create_table(json_path)
    # Create list of metrics to cycle through
    metric_dict = create_attribute_dict(json_path)
    exampleKey = next(iter(metric_dict))
    keys = metric_dict[exampleKey].keys()
    available_metrics = []
    for key in keys:
        available_metrics.append(key)
    # Remove non-plottable fields (ex. Binning*) (These fields are demarcated by an asterisk).
    available_metrics = [m for m in available_metrics if "*" not in m]

    # Create histograms
    print(available_metrics)
    for metric in available_metrics:
        # create_histogram(metric, json_path)
        pass
    # Create all 2d graph figures (ERA and Density)
    for metric1 in available_metrics:
        for metric2 in available_metrics:
        
            # Skip same-metric graphs
            if metric1 != metric2:
                create_density_estimation((metric1, metric2), json_path)
                create_ERA_graph((metric1, metric2), json_path)
                create_ERA_graph_by_game((metric1, metric2), json_path)
                #create_ERA_by_Generator((metric1, metric2))
    
    