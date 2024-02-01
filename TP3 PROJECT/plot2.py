import matplotlib.pyplot as plt
import numpy as np

markdown = """
| Hour  | Mon  | Tue  | Wed  | Thu  | Fri  | Sat   | Sun   |
| ----- | ---- | ---- | ---- | ---- | ---- | ----- | ----- |
| 0:00  | 2.87 | 3.59 | 3.98 | 3.79 | 3.37 | 3.46  | 3.12  |
| 1:00  | 2.92 | 3.83 | 3.72 | 3.12 | 3.21 | 3.29  | 3.11 |
| 2:00  | 3.22 | 3.78 | 3.36 | 3.75 | 3.40 | 3.39  | 3.11  |
| 3:00  | 3.27 | 3.99 | 0.00 | 3.39 | 3.37 | 3.51  | 3.25  |
| 4:00  | 3.91 | 5.25 | 4.01 | 4.73 | 3.88 | 4.29  | 3.62  |
| 5:00  | 5.03 | 6.20 | 4.46 | 3.75 | 3.83 | 5.00  | 4.99  |
| 6:00  | 5.51 | 4.06 | 2.99 | 3.02 | 3.14 | 3.49  | 4.49  |
| 7:00  | 3.76 | 2.99 | 2.55 | 2.69 | 2.77 | 2.72  | 3.45  |
| 8:00  | 3.04 | 2.54 | 2.32 | 2.23 | 2.30 | 2.35  | 3.00  |
| 9:00  | 2.65 | 2.48 | 2.28 | 2.32 | 2.38 | 2.4 | 2.44  |
| 10:00 | 2.63 | 2.57 | 2.38 | 2.36 | 2.39 | 2.42  | 2.35  |
| 11:00 | 2.51 | 2.66 | 2.33 | 2.43 | 2.50 | 2.49  | 2.46  |
| 12:00 | 2.61 | 2.42 | 2.41 | 2.49 | 2.59 | 2.56  | 2.25  |
| 13:00 | 2.64 | 2.54 | 2.38 | 2.61 | 2.71 | 2.69  | 2.35  |
| 14:00 | 2.81 | 2.66 | 2.54 | 2.78 | 2.78 | 2.85  | 2.43  |
| 15:00 | 2.90 | 2.85 | 2.60 | 2.75 | 2.92 | 2.91  | 2.60  |
| 16:00 | 2.98 | 2.97 | 2.58 | 2.84 | 2.88 | 2.89  | 2.50  |
| 17:00 | 3.00 | 2.71 | 2.31 | 2.66 | 2.59 | 2.60  | 2.54  |
| 18:00 | 2.91 | 2.69 | 2.32 | 2.34 | 2.55 | 2.46  | 2.41  |
| 19:00 | 2.86 | 2.66 | 2.47 | 2.49 | 2.51 | 2.42  | 2.38  |
| 20:00 | 2.99 | 3.00 | 2.76 | 2.77 | 2.96 | 2.64  | 2.36  |
| 21:00 | 3.30 | 2.95 | 2.88 | 3.01 | 3.06 | 2.73  | 2.53  |
| 22:00 | 3.56 | 3.44 | 3.17 | 3.07 | 3.07 | 2.89  | 2.62  |
| 23:00 | 3.99 | 3.98 | 3.41 | 3.37 | 3.34 | 3.07  | 2.76  |
"""

data = markdown.split('\n')[3:-1]
data = [row.split('|')[2:-1] for row in data]
data = [[float(x) for x in row] for row in data]

# remove outliers <2 and >10
data = [[x if x > 2 and x < 10 else 0 for x in row] for row in data]

days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"]
hours = list(range(24))

# Convert the data to a 2D numpy array
data_array = np.array(data)

# Plot the heatmap with values inside the boxes
plt.figure(figsize=(10, 8))
heatmap = plt.imshow(data_array, cmap='viridis', aspect='auto')

plt.colorbar(heatmap, label='Values')

# Setting the labels
plt.xticks(ticks=np.arange(len(days)), labels=days)
plt.yticks(ticks=np.arange(len(hours)), labels=hours)
plt.xlabel('Days')
plt.ylabel('Hour')
plt.title('Weekly Data Heatmap')

# Adding the values inside the boxes with low opacity white
for i in range(len(hours)):
    for j in range(len(days)):
        text = plt.text(j, i, f"{data_array[i, j]:.2f}",
                       ha="center", va="center", color="w", fontsize=10, alpha=0.5)

plt.title('Average distribution of taxi trip distance per hour and day')
plt.show()
