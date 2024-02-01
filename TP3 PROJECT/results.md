<h4 align="center">Analysis of the graph with NYC Yellow Taxi Trip Data using Spark-GraphX</h1>
<h1 align="center">Results Generated</h4>

## Analysis 1 (Easy):

**"What are the peak hours for taxi demand in NYC?"**

- **Objective**: Identify the busiest hours during the day for taxi services.
- **Approach**: Aggregate `tpep_pickup_datetime` by hour and count the number of trips in each hour slot. Use Spark SQL for time-based aggregations.

We summarize the data in the following table:

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

## Analysis 2 (Medium):

**"How does the average trip distance vary by time of day and day of the week?"**

- **Objective**: Understand trip distance patterns in relation to the time of the day and the day of the week.
- **Approach**:
  - Extract hour and weekday from `tpep_pickup_datetime`.
  - Group data by the extracted hour and weekday, and then calculate the average `Trip_distance` for each group.
  - Analyze variations and trends in trip distance across different times of the day and days of the week.

We summarize the data in the following table:

This table shows the average trip distance in Km for each hour of the day and each day of the week.

| Hour  | Mon  | Tue  | Wed  | Thu  | Fri  | Sat   | Sun   |
| ----- | ---- | ---- | ---- | ---- | ---- | ----- | ----- |
| 0:00  | 2.87 | 3.59 | 3.98 | 3.79 | 3.37 | 3.46  | 3.12  |
| 1:00  | 2.92 | 3.83 | 3.72 | 3.12 | 3.21 | 3.29  | 11.27 |
| 2:00  | 3.22 | 3.78 | 3.36 | 3.75 | 3.40 | 3.39  | 3.11  |
| 3:00  | 3.27 | 3.99 | 0.00 | 3.39 | 3.37 | 3.51  | 3.25  |
| 4:00  | 3.91 | 5.25 | 4.01 | 4.73 | 3.88 | 4.29  | 3.62  |
| 5:00  | 5.03 | 6.20 | 4.46 | 3.75 | 3.83 | 5.00  | 4.99  |
| 6:00  | 5.51 | 4.06 | 2.99 | 3.02 | 3.14 | 3.49  | 4.49  |
| 7:00  | 3.76 | 2.99 | 2.55 | 2.69 | 2.77 | 2.72  | 3.45  |
| 8:00  | 3.04 | 2.54 | 2.32 | 2.23 | 2.30 | 2.35  | 3.00  |
| 9:00  | 2.65 | 2.48 | 2.28 | 2.32 | 2.38 | 25.61 | 2.44  |
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

## Analysis 3 (Complex):

**"Can we identify communities/clusters of locations that are commonly connected through taxi trips?"**

- **Objective**: Discover clusters of locations that are frequently connected through taxi trips, indicating shared functional or social connections.
- **Approach**:
  - Construct a graph where nodes represent unique locations (geocoordinates rounded to a specific precision) and edges represent trips between these locations.
  - Apply the Label Propagation Algorithm (LPA) from Spark-GraphX to identify communities/clusters of locations.
  - Analyze the characteristics of these clusters, such as their geographical spread, average trip distance, average fare, etc.

We summarize the data in the following table:

| Community ID | Average Trip Distance | Average Fare | Number of Trips |
| ------------ | --------------------- | ------------ | --------------- |
| 48           | 2.52                  | 12.77        | 232738200       |
| -2082163804  | 2.43                  | 11.08        | 1925            |
| -2077780953  | 2.39                  | 10.69        | 1809            |
| -1629598498  | 2.22                  | 10.38        | 1449            |
| -719305056   | 2.32                  | 10.51        | 918             |
| -2025446586  | 2.39                  | 11.09        | 580             |
| -1938735503  | 1.96                  | 9.82         | 546             |
| -1093139016  | 2.66                  | 11.63        | 480             |
| -2122626181  | 2.53                  | 11.18        | 468             |
| -1562380001  | 2.26                  | 10.95        | 390             |
| -852406799   | 2.42                  | 10.92        | 388             |
| -1658666590  | 2.21                  | 10.14        | 365             |
| -1693114219  | 2.18                  | 11.13        | 332             |
| -1952075019  | 3.39                  | 10.80        | 275             |
| -1317413     | 2.10                  | 10.26        | 250             |
| -1580560822  | 1.88                  | 8.96         | 240             |
| -908280740   | 2.00                  | 9.58         | 234             |
| -1151439202  | 2.56                  | 11.14        | 234             |
| -170703469   | 2.50                  | 10.96        | 234             |
| -131751003   | 2.25                  | 10.72        | 220             |
| ...          | ...                   | ...          | ...             |
| -448989988   | 1.00                  | 8.00         | 1               |
| -2084646859  | 3.10                  | 11.00        | 1               |
| 992431860    | 13.50                 | 38.00        | 1               |
| -717963664   | 1.90                  | 9.50         | 1               |
| -17194477    | 16.20                 | 52.00        | 1               |
| -1352095945  | 1.00                  | 4.70         | 1               |
| -804955462   | 4.30                  | 15.50        | 1               |
| 2088899046   | 0.76                  | 0.01         | 1               |
| 51140736     | 2.80                  | 13.00        | 1               |
| 1532231355   | 0.70                  | 8.00         | 1               |
| -349262794   | 0.30                  | 4.50         | 1               |
| -1894534567  | 12.20                 | 36.50        | 1               |
| 1947709461   | 7.24                  | 20.50        | 1               |
| 1051274637   | 0.00                  | 2.20         | 1               |
| 1409099280   | 2.00                  | 0.00         | 1               |
| 1732736646   | 1.74                  | 8.00         | 1               |
| -1523193952  | 0.80                  | 9.00         | 1               |
| -2010052522  | 2.50                  | 10.00        | 1               |
| -1441354852  | 0.01                  | 2.50         | 1               |
| 1520117313   | 5.20                  | 55.00        | 1               |
