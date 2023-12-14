# %%
from pyspark.sql import SparkSession

# %%
data_file_path = "data/agg_match_stats_10000.csv"

# Create a SparkSession
spark = SparkSession.builder.appName("CSV Analysis").getOrCreate()

# Load the CSV file into a DataFrame
df = spark.read.csv(data_file_path, header=True, inferSchema=True)

# Show the schema of the DataFrame
df.printSchema()


# %%
# Show the schema of the DataFrame
df.printSchema()

# Show the first few rows of the DataFrame
df.show()

# Perform some basic operations
print("Number of rows: ", df.count())
print("Number of columns: ", len(df.columns))

# You can also perform more specific data analysis depending on your needs
# For example, to find the average of a column named 'column_name', you can do:
# df.select(avg("column_name")).show()


