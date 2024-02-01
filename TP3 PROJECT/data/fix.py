import os
from pathlib import Path

# Relative path to the file from the script's directory
relative_path_to_script = "yellow_tripdata_2015-01.csv"

# Get the directory of the current script
script_directory = Path(__file__).parent

# Construct the absolute path to the file
file_path = script_directory / relative_path_to_script

# Task: Lower the number of lines to 500000
with open(file_path, 'r') as file:
    lines = file.readlines()

# Writing the first 500000 lines to a new file in the script's directory
with open(script_directory / "yellow_tripdata_2015-01-small.csv", 'w') as new_file:
    for line in lines[:500000]:
        new_file.write(line)

print(f"File has been reduced to {len(lines[:500000])} lines.")
