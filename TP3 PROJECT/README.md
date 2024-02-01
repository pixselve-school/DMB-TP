<h1 align="center">Traitement du graphe temporel CityBike avec Spark-GraphX</h1>
<h4 align="center">Mael KERICHARD - Cody ADAM</h4>
<p align="center">
   <img src="https://img.shields.io/badge/-ESIR-orange" alt="ESIR">
   <img src="https://img.shields.io/badge/-DMB-red" alt="DMB">
</p>

# Project

In this projet we will be analyzing the [NYC Yellow Taxi Trip Data](https://www.kaggle.com/datasets/elemento/nyc-yellow-taxi-trip-data)

## Dataset Columns

| Field Name            | Description                                                                                                           |
| --------------------- | --------------------------------------------------------------------------------------------------------------------- |
| VendorID              | A code indicating the TPEP provider that provided the record.                                                         |
| tpep_pickup_datetime  | The date and time when the meter was engaged.                                                                         |
| tpep_dropoff_datetime | The date and time when the meter was disengaged.                                                                      |
| Passenger_count       | The number of passengers in the vehicle. This is a driver-entered value.                                              |
| Trip_distance         | The elapsed trip distance in miles reported by the taximeter.                                                         |
| Pickup_longitude      | Longitude where the meter was engaged.                                                                                |
| Pickup_latitude       | Latitude where the meter was engaged.                                                                                 |
| RateCodeID            | The final rate code in effect at the end of the trip.                                                                 |
| Store_and_fwd_flag    | This flag indicates whether the trip record was held in vehicle memory before sending to the vendor,                  |
| Dropoff_longitude     | Longitude where the meter was disengaged.                                                                             |
| Dropoff_latitude      | Latitude where the meter was disengaged.                                                                              |
| Payment_type          | A numeric code signifying how the passenger paid for the trip.                                                        |
| Fare_amount           | The time-and-distance fare calculated by the meter.                                                                   |
| Extra                 | Miscellaneous extras and surcharges. Currently, this only includes. the $0.50 and $1 rush hour and overnight charges. |
| MTA_tax               | 0.50 MTA tax that is automatically triggered based on the metered rate in use.                                        |
| Improvement_surcharge | 0.30 improvement surcharge assessed trips at the flag drop. the improvement surcharge began being levied in 2015.     |
| Tip_amount            | Tip amount - This field is automatically populated for credit card tips.Cash tips are not included.                   |
| Tolls_amount          | Total amount of all tolls paid in trip.                                                                               |
| Total_amount          | The total amount charged to passengers. Does not include cash tips.                                                   |

## Questions we want to answer

Based on the dataset columns from the NYC Yellow Taxi Trip Data, here we choosed three questions of varying difficulty that we will explore using Apache Spark and Spark-GraphX:

### Easy Question:

**"What are the peak hours for taxi demand in NYC?"**

- **Objective**: Identify the busiest hours during the day for taxi services.
- **Approach**: Aggregate `tpep_pickup_datetime` by hour and count the number of trips in each hour slot. Use Spark SQL for time-based aggregations.

### Medium Question:

**"How does the average trip distance vary by time of day and day of the week?"**

- **Objective**: Understand trip distance patterns in relation to the time of the day and the day of the week.
- **Approach**:
  - Extract hour and weekday from `tpep_pickup_datetime`.
  - Group data by the extracted hour and weekday, and then calculate the average `Trip_distance` for each group.
  - Analyze variations and trends in trip distance across different times of the day and days of the week.

### Hard Question (Involving Spark-GraphX):

**"Can we identify communities/clusters of locations that are commonly connected through taxi trips?"**

- **Objective**: Discover clusters of locations that are frequently connected through taxi trips, which might indicate shared functional or social connections.
- **Approach**:
  - Construct a graph where nodes represent unique locations (geocoordinates rounded to a specific precision) and edges represent trips between these locations.
  - Use the pickup and dropoff latitude and longitude to define the nodes and the trips between these nodes as edges.
  - Apply graph clustering algorithms available in Spark-GraphX, like Label Propagation or Strongly Connected Components, to identify communities/clusters of locations.
  - Analyze the characteristics of these clusters, like their geographical spread, average trip distance, average fare, etc.


we aim to identify communities or clusters of locations that are commonly connected through taxi trips. We can use graph clustering algorithms available in Spark-GraphX, like Label Propagation or Strongly Connected Components, to identify these communities.

Below is the outline of the code to achieve this, with the focus on using the Label Propagation Algorithm (LPA) for community detection:

1. Objective and Approach Description: Describe the aim of the analysis and the method used.
2. Apply LPA: Use the Label Propagation Algorithm on the graph to identify communities.
3. Analyze Clusters: Aggregate data in the clusters, such as average trip distance, average fare, etc.
4. Generate Results: Format the results into a readable format.



Each of these questions increases in complexity, requiring more sophisticated data processing and analysis techniques. While the easy question involves basic data aggregation, the medium question adds the complexity of temporal data analysis. The hard question leverages the advanced graph processing capabilities of Spark-GraphX, enabling the exploration of complex relationships and patterns in the data.


## Results

Results are available here: [./results.md](results.md).

## Generate the results

### Requirements

- Java
- Scala
- SBT

### Start the project

```bash
sbt run
```

This will generate the results of the analysis in the `results.md` file.
