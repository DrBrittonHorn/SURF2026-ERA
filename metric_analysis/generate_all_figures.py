from figure_types.create_density_estimation_chart import create_density_estimation
from figure_types.create_ERA_graph import create_ERA_graph
from figure_types.create_histogram import create_histogram
from figure_types.create_table import create_table

from metric_analysis.tools import create_attribute_dict

import os

# Usage: Update the metric paths list to choose which folders to generate all figures for

metric_paths = [
    "generatedExamples/constructiveLevelGenerator/metrics.json",
    "generatedExamples/fineTunedLLMGenerator/metrics.json",
    "generatedExamples/geminiLevelGenerator/metrics.json"
    "generatedExamples/geneticLevelGenerator/metrics.json",
    "generatedExamples/localLanguageModel/metrics.json",
    "generatedExamples/randomLevelGenerator/metrics.json",
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
    available_metrics.remove("Binning") # Not a true metric

    # Create histograms
    print(available_metrics)
    for metric in available_metrics:
        create_histogram(metric, json_path)
    # Create all 2d graph figures (ERA and Density)
    for metric1 in available_metrics:
        for metric2 in available_metrics:
            # Skip same-metric graphs
            if metric1 != metric2:
                create_density_estimation((metric1, metric2), json_path)
                create_ERA_graph((metric1, metric2), json_path)
    
    
    # TODO, one color transition, different color background
    # Roguelike LLM regeneration
    # Don't analyze non double/float metrics
    # Fix calculateAllMetrics for non avatar
    # Generate all graphs again
    # Leniency/difficulty for all games
    # Find most meaningful metrics