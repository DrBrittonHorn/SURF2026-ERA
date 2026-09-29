import enum

import numpy as np
import pandas as pd
from sklearn.preprocessing import StandardScaler
from sklearn.decomposition import PCA
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import confusion_matrix
import matplotlib.pyplot as plt
import seaborn as sns
from matplotlib.colors import BoundaryNorm, ListedColormap

if __package__:
    from .tools import create_attribute_dict, create_game_colorbars
else:
    from tools import create_attribute_dict, create_game_colorbars # type: ignore

    # See https://www.geeksforgeeks.org/data-analysis/principal-component-analysis-pca/ for more details
def PCA_analysis(dataframe, cols_to_drop, col_to_predict):

    # Step 3: Standardizing the Data
    df = pd.DataFrame(dataframe)
    print(df)

    X = df.drop(cols_to_drop, axis=1)
    print(X)
    y = df[col_to_predict]

    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)

    # Step 4: Applying PCA algorithm

    pca = PCA(n_components=2)
    X_pca = pca.fit_transform(X_scaled)

    X_train, X_test, y_train, y_test = train_test_split(X_pca, y, test_size=0.3, random_state=42)

    model = LogisticRegression()
    model.fit(X_train, y_train)

    y_pred = model.predict(X_test)

    # Step 5: Evaluating with Confusion Matrix

    cm = confusion_matrix(y_test, y_pred)

    plt.figure(figsize=(5,4))
    sns.heatmap(cm, annot=True, fmt='d', cmap='Blues', xticklabels=['Female', 'Male'], yticklabels=['Female', 'Male'])
    plt.xlabel('Predicted Label')
    plt.ylabel('True Label')
    plt.title('Confusion Matrix')
    plt.show()


    # Step 6: Visualizing PCA Result

    y_numeric, class_labels = pd.factorize(y)
    color_map = ListedColormap([cmap(1.0) for cmap in create_game_colorbars(num_games=len(class_labels))])
    color_norm = BoundaryNorm(np.arange(-0.5, len(class_labels) + 0.5), color_map.N)

    plt.figure(figsize=(12, 5))

    plt.subplot(1, 2, 1)
    plt.scatter(X_scaled[:, 0], X_scaled[:, 1], c=y_numeric, cmap=color_map, norm=color_norm, edgecolor='k', s=30, alpha=0.05)
    plt.xlabel('Original Feature 1')
    plt.ylabel('Original Feature 2')
    plt.title('Before PCA: Using First 2 Standardized Features')
    colorbar = plt.colorbar(ticks=np.arange(len(class_labels)))
    colorbar.set_label('Target classes')
    colorbar.set_ticklabels(class_labels)

    plt.subplot(1, 2, 2)
    plt.scatter(X_pca[:, 0], X_pca[:, 1], c=y_numeric, cmap=color_map, norm=color_norm, edgecolor='none', s=10, alpha=0.05)
    plt.xlabel('Principal Component 1')
    plt.ylabel('Principal Component 2')
    plt.title('After PCA: Projected onto 2 Principal Components')
    colorbar = plt.colorbar(ticks=np.arange(len(class_labels)))
    colorbar.set_label('Target classes')
    colorbar.set_ticklabels(class_labels)

    plt.tight_layout()
    plt.show()

if __name__ == "__main__":

    example_data = {
            'Height': [170, 165, 180, 175, 160, 172, 168, 177, 162, 158],
            'Weight': [65, 59, 75, 68, 55, 70, 62, 74, 58, 54],
            'Age': [30, 25, 35, 28, 22, 32, 27, 33, 24, 21],
            'Gender': [1, 0, 1, 1, 0, 1, 0, 1, 0, 0]  # 1 = Male, 0 = Female (Used as the ground truth class)
        }
    df = pd.DataFrame(example_data)
    # print(df)

    generator_metric_paths = [
    ("generatedExamples/constructiveLevelGenerator/levelMetrics.json", 0),
    ("generatedExamples/claudeLevelGenerator/levelMetrics.json", 1),
    ("generatedExamples/enhancedClaudeGenerator/levelMetrics.json", 2),
    ("generatedExamples/geminiLevelGenerator/levelMetrics.json", 3),
    ("generatedExamples/geneticLevelGenerator/levelMetrics.json", 4),
    ("generatedExamples/randomLevelGenerator/levelMetrics.json", 5),
    ("generatedExamples/sturgeonLevelGenerator1x1/levelMetrics.json", 6),
    ("generatedExamples/sturgeonLevelGenerator2x2/levelMetrics.json", 7),
    ("generatedExamples/sturgeonLevelGenerator3x3/levelMetrics.json", 8),
    ("generatedExamples/sturgeonLevelGenerator4x4/levelMetrics.json", 9)
    ]

    games = [("aliens", 0), ("artillery", 1), ("asteroids", 2), ("dungeon", 3), ("frogs", 4), ("mario", 5), ("realsokoban", 6), ("roguelike", 7), ("towerdefense", 8), ("zelda", 9)]

    metric_dict = create_attribute_dict(generator_metric_paths[0][0])
    one_level = metric_dict[list(metric_dict.keys())[0]]
    metric_dimensions = [key for key in one_level.keys() if "*" not in key]
    metric_dimensions.append("Generator"); metric_dimensions.append("Game") # Append generator and game dimensions for clustering prediction
    print(f"Metric dimensions are... {metric_dimensions}")

    all_level_vectors = []
    
    for metric_path in generator_metric_paths:
        generator_metric_dict = create_attribute_dict(metric_path[0])
        # generator_name = metric_path[0].split("/")[1]  # Extract generator name from path
        generator_id = metric_path[1]
        for level in generator_metric_dict:
            level_game = level.split("/")[-2]
            level_game_id = next((game_id for game, game_id in games if game == level_game), None)
            level_details = generator_metric_dict[level]
            level_vector = [level_details[key] for key in metric_dimensions[:-2]] 
            
            level_vector.append(generator_id)  # Ground truth for generator
            level_vector.append(level_game_id)  # Ground truth for game
            print(f"Level vector for {level}: {level_vector}")
            all_level_vectors.append(level_vector)
    print(f"Total level vectors created: {len(all_level_vectors)}")
    # print(f"Level vectors created: {all_level_vectors}")

    levels_df = pd.DataFrame(all_level_vectors, columns=metric_dimensions)
    print(f"Levels DataFrame created: {levels_df.shape[0]} rows, {levels_df.shape[1]} columns")
    #print(levels_df)
    #print(df)

    # PCA_analysis(levels_df, cols_to_drop=["Generator", "Game"], col_to_predict = "Generator")
    PCA_analysis(levels_df, cols_to_drop=["Generator", "Game"], col_to_predict = "Game")