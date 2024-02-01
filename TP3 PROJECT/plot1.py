import matplotlib.pyplot as plt
import numpy as np

markdown = """
| Hour  | Number of trips |
| ----- | --------------- |
| 0:00  | 19122           |
| 1:00  | 12272           |
| 2:00  | 9583            |
| 3:00  | 7107            |
| 4:00  | 4248            |
| 5:00  | 4767            |
| 6:00  | 9010            |
| 7:00  | 19929           |
| 8:00  | 21812           |
| 9:00  | 23416           |
| 10:00 | 21310           |
| 11:00 | 20486           |
| 12:00 | 25130           |
| 13:00 | 23321           |
| 14:00 | 28414           |
| 15:00 | 26135           |
| 16:00 | 25564           |
| 17:00 | 26215           |
| 18:00 | 31262           |
| 19:00 | 33607           |
| 20:00 | 28356           |
| 21:00 | 28291           |
| 22:00 | 26237           |
| 23:00 | 24405           |
"""

# data = markdown.split('\n')[3:-1]
# data = [row.split('|')[2:-1] for row in data]
# data = [[float(x) for x in row] for row in data]
number_of_trips = markdown.split("\n")[3:-1]
number_of_trips = [row.split('|')[2:-1] for row in number_of_trips]
number_of_trips = [float(row[0]) for row in number_of_trips]

# Data for the histogram
hours = [f"{hour}:00" for hour in range(24)]


# Creating the histogram
plt.figure(figsize=(10, 8))
plt.bar(hours, number_of_trips, color='skyblue')

# Setting the labels
plt.xlabel('Hour of the Day')
plt.ylabel('Number of Trips')
plt.title('Number of Trips by Hour')
plt.xticks(rotation=45)
plt.tight_layout()  # Adjusts the plot to ensure everything fits without overlapping

plt.show()
