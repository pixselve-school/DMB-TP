# %%
from pyspark.sql import SparkSession
from pyspark.sql.functions import avg, count

# %%
data_file_path = "../data/agg_match_stats_10000.csv"

# Create a SparkSession
spark = SparkSession.builder.appName("CSV Analysis").getOrCreate()

# Load the CSV file into a DataFrame
df = spark.read.csv(data_file_path, header=True, inferSchema=True)

all_columns = df.columns


# %%
# Show the schema of the DataFrame
df.printSchema()

# %%
# Show the first few rows of the DataFrame
df.show()

# %%
# Perform some basic operations
print("Number of rows: ", df.count())
print("Number of columns: ", len(df.columns))

# You can also perform more specific data analysis depending on your needs
# For example, to find the average of a column named 'column_name', you can do:
# df.select(avg("column_name")).show()

# %% [markdown]
# # 3 - Les meilleurs joueurs
# 
# L'objectif est d'être le dernier en vie, mais certains joueurs soutiennent qu'il est nécessaire d'éliminer
# un maximum de concurrents. Nous allons vérifier cette affirmation en comparant les joueurs selon ces deux
# conditions, à vous de choisir celle que vous souhaitez explorer. L'attribut player_kills donne le nombre
# d'éliminations et team_placement la position en fin de partie.
# 
# 1. Chargez le jeu de données. (voir textFile)
# 2. Pour chaque partie, obtenez uniquement le nom du joueur et son nombre d'éliminations ou sa position. (voir map)
# 3. Obtenez la moyenne des éliminations ou de la position de chaque joueur, ainsi que le nombre de parties concernées. (voir reduceByKey ou groupByKey)
# 4. Obtenez les 10 meilleurs joueurs selon les éliminations ou la position. (voir sortBy)
# 5. Certains joueurs n'ayant joué qu'une partie, nous souhaitions ne garder que ceux ayant au moins 4 parties. (voir filter)
# 6. Si vous observez un joueur particulier, traitez-le de la manière appropriée.
# 7. En partageant avec vos camarades qui ont exploré l'autre condition, donnez votre avis sur l'affirmation de départ.

# %%
selected_data = df.select("player_name", "player_kills", "team_placement")
selected_data.show()

# %%

player_stats = selected_data.groupBy("player_name").agg(
    avg("player_kills").alias("avg_kills"),
    avg("team_placement").alias("avg_placement"),
    count("player_name").alias("match_count")
)

player_stats.show()

# %%


top_players_by_kills = player_stats.sort("avg_kills", ascending=False).limit(20)
top_players_by_placement = player_stats.sort("avg_placement").limit(20)

top_players_by_kills.show()
top_players_by_placement.show()


# %%

filtered_stats = df.filter("player_name = '651651646'")
filtered_stats.show()


