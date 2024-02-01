import org.apache.spark.graphx.{Edge, Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx.lib.LabelPropagation
import java.io.{File, PrintWriter}

case class Location(id: String, longitude: Double, latitude: Double)
case class Trip(
    pickupTime: Long,
    dropoffTime: Long,
    passengerCount: Int,
    tripDistance: Double,
    fareAmount: Double,
    totalAmount: Double
)

/*
Header of CSV :
- VendorID
- tpep_pickup_datetime
- tpep_dropoff_datetime
- passenger_count
- trip_distance
- pickup_longitude
- pickup_latitude
- RateCodeID
- store_and_fwd_flag
- dropoff_longitude
- dropoff_latitude
- payment_type
- fare_amount
- extra
- mta_tax
- tip_amount
- tolls_amount
- improvement_surcharge
- total_amount
 */
object Main {

  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("graphXTP").setMaster("local[1]")
    val sc = new SparkContext(sparkConf)

    val md = new StringBuilder // String to write to the file
    md ++= "<h4 align=\"center\">Analysis of the graph with NYC Yellow Taxi Trip Data using Spark-GraphX</h1>\n<h1 align=\"center\">Results Generated</h4>\n\n"

    // Parsing the graph
    println("Starting to parse the graph...")
    val g = parseGraph(sc)
    println("Graph parsing completed.")

    // Print graph information to check its contents
    println(s"Number of vertices: ${g.vertices.count()}")
    println(s"Number of edges: ${g.edges.count()}")
    println("Sample vertices:")
    g.vertices.take(5).foreach(println) // Print first 5 vertices
    println("Sample edges:")
    g.edges.take(5).foreach(println) // Print first 5 edges

    // Analysis 1
    println("Starting analysis 1: Peak hours for taxi demand...")
    md ++= analysis1(g)
    println("Analysis 1 completed.")

    // Analysis 2
    println(
      "Starting analysis 2: Average trip distance by time of day and day of the week..."
    )
    md ++= analysis2(g)
    println("Analysis 2 completed.")

    // Analysis 3
    println(
      "Starting analysis 3: Identifying communities/clusters of locations..."
    )
    md ++= analysis3(g)
    println("Analysis 3 completed.")

    // Write the string to a file
    println("Writing the results to the file...")
    val pw = new PrintWriter(new File("results.md"))
    pw.write(md.toString())
    pw.close()
    println("Results have been written to results.md.")
  }

  private def parseGraph(sc: SparkContext): Graph[Location, Trip] = {
    val DATASET_PATH = "data/yellow_tripdata_2015-01-small.csv"
    val trips = sc.textFile(DATASET_PATH)
    println(s"Number of records before filtering: ${trips.count()}")
    val tripProcessed = trips
      .mapPartitionsWithIndex { (idx, iter) =>
        if (idx == 0) iter.drop(1) else iter
      }
      .map(_.split(','))
      .filter(row => row.length == 19) // Ensure the row has 18 columns
      .filter(row => row.forall(_.nonEmpty)) // no empty string
    println(s"Number of records after filtering: ${tripProcessed.count()}")

    val locations: RDD[(VertexId, Location)] = tripProcessed
      .flatMap { row =>
        val pickupLocation =
          Location(row(5) + row(6), row(5).toDouble, row(6).toDouble)
        val dropoffLocation =
          Location(row(9) + row(10), row(9).toDouble, row(10).toDouble)
        List(
          (pickupLocation.id.hashCode.toLong, pickupLocation),
          (dropoffLocation.id.hashCode.toLong, dropoffLocation)
        )
      }
      .distinct()

    val tripsRDD: RDD[Edge[Trip]] = tripProcessed.map { row =>
      Edge(
        row(5).hashCode.toLong,
        row(9).hashCode.toLong,
        Trip(
          timeToLong(row(1)),
          timeToLong(row(2)),
          row(3).toInt,
          row(4).toDouble,
          row(12).toDouble,
          row(17).toDouble
        )
      )
    }

    Graph(locations, tripsRDD)
  }

  /** Convert a string to a long timestamp
    * @param tString
    *   : string to convert
    * @return
    *   : timestamp in long
    */
  private def timeToLong(tString: String): Long = {
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    format.parse(tString).getTime
  }

  /** **"What are the peak hours for taxi demand in NYC?"**
    *
    * \- **Objective**: Identify the busiest hours during the day for taxi
    * services. \- **Approach**: Aggregate `tpep_pickup_datetime` by hour and
    * count the number of trips in each hour slot. Use Spark SQL for time-based
    * aggregations.
    */
  private def analysis1(graph: Graph[Location, Trip]): String = {
    val pickupTimes =
      graph.edges.map(edge => (hourOfDay(edge.attr.pickupTime), 1))
    val pickupCountsByHour = pickupTimes.reduceByKey(_ + _).collect().sorted
    val result = pickupCountsByHour
      .map { case (hour, count) => s"| $hour:00 | $count |" }
      .mkString("\n")

    val intro = """
## Analysis 1 (Easy):

**"What are the peak hours for taxi demand in NYC?"**

- **Objective**: Identify the busiest hours during the day for taxi services.
- **Approach**: Aggregate `tpep_pickup_datetime` by hour and count the number of trips in each hour slot. Use Spark SQL for time-based aggregations.

We summarize the data in the following table:

"""

    val tableHeader =
      "| Hour | Number of trips |\n| ---- | ----- |\n"
    intro + tableHeader + result + "\n\n"
  }

  // Helper function to convert timestamp to hour of day
  private def hourOfDay(timestamp: Long): Int = {
    val date = new java.util.Date(timestamp)
    val calendar = java.util.Calendar.getInstance()
    calendar.setTime(date)
    calendar.get(java.util.Calendar.HOUR_OF_DAY)
  }

  /** **"How does the average trip distance vary by time of day and day of the
    * week?"**
    *
    * \- **Objective**: Understand trip distance patterns in relation to the
    * time of the day and the day of the week. \- **Approach**: \- Extract hour
    * and weekday from `tpep_pickup_datetime`. \- Group data by the extracted
    * hour and weekday, and then calculate the average `Trip_distance` for each
    * group. \- Analyze variations and trends in trip distance across different
    * times of the day and days of the week.
    */
  private def analysis2(graph: Graph[Location, Trip]): String = {
    val distancesByTime = graph.edges.map(edge =>
      (
        (hourOfDay(edge.attr.pickupTime), dayOfWeek(edge.attr.pickupTime)),
        (edge.attr.tripDistance, 1)
      )
    )
    val totalDistanceByTime =
      distancesByTime.reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2))
    val averageDistanceByTime = totalDistanceByTime.mapValues {
      case (totalDistance, count) => totalDistance / count
    }

    // Collect and organize data in a map
    val dataMap = averageDistanceByTime.collect().toMap

    // Generate headers
    val hoursOfDay = 0 until 24

    // Define days of the week and map to integers
    val daysOfWeek = Seq("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val daysOfWeekMap = daysOfWeek.zipWithIndex.toMap.mapValues(_ + 1)

    // Prepare table header
    val tableHeader = "| Hour | " + daysOfWeek.mkString(
      " | "
    ) + " |\n" + ("--- | " * (daysOfWeek.size + 1)) + " |\n"

    // Prepare table body
    val tableBody = hoursOfDay
      .map { hour =>
        val rowData = daysOfWeek
          .map { dayName =>
            val dayIndex =
              daysOfWeekMap(dayName) // use integer representation of the day
            val key = (hour, dayIndex)
            f"${dataMap.getOrElse(key, 0.0)}%.2f"
          }
          .mkString(" | ")
        s"$hour | " + rowData
      }
      .mkString("\n")

    val intro = """
## Analysis 2 (Medium):

**"How does the average trip distance vary by time of day and day of the week?"**

- **Objective**: Understand trip distance patterns in relation to the time of the day and the day of the week.
- **Approach**:
  - Extract hour and weekday from `tpep_pickup_datetime`.
  - Group data by the extracted hour and weekday, and then calculate the average `Trip_distance` for each group.
  - Analyze variations and trends in trip distance across different times of the day and days of the week.

We summarize the data in the following table:

This table shows the average trip distance in Km for each hour of the day and each day of the week.

"""

    intro + tableHeader + tableBody + "\n\n"
  }

// Helper function to convert timestamp to day of week
  private def dayOfWeek(timestamp: Long): Int = {
    val date = new java.util.Date(timestamp)
    val calendar = java.util.Calendar.getInstance()
    calendar.setTime(date)
    calendar.get(java.util.Calendar.DAY_OF_WEEK)
  }

  /** **"Can we identify communities/clusters of locations that are commonly
    * connected through taxi trips?"**
    *
    * \- **Objective**: Discover clusters of locations that are frequently
    * connected through taxi trips, which might indicate shared functional or
    * social connections. \- **Approach**: \- Construct a graph where nodes
    * represent unique locations (geocoordinates rounded to a specific
    * precision) and edges represent trips between these locations. \- Use the
    * pickup and dropoff latitude and longitude to define the nodes and the
    * trips between these nodes as edges. \- Apply graph clustering algorithms
    * available in Spark-GraphX, like Label Propagation or Strongly Connected
    * Components, to identify communities/clusters of locations. \- Analyze the
    * characteristics of these clusters, like their geographical spread, average
    * trip distance, average fare, etc.
    */
  private def analysis3(graph: Graph[Location, Trip]): String = {
    // Running Label Propagation Algorithm (LPA) for community detection
    val lpaGraph = LabelPropagation.run(graph, maxSteps = 5)

    // Joining the LPA results with the original graph to get the location information
    val communityByLocation = lpaGraph.vertices.join(graph.vertices).map {
      case (vertexId, (communityId, location)) => (communityId, location)
    }

    // Calculating statistics for each community
    val statsByCommunity = communityByLocation
      .join(graph.edges.map(e => (e.srcId, e.attr)))
      .map { case (communityId, (location, trip)) =>
        (communityId, (trip.tripDistance, trip.fareAmount, 1))
      }
      .reduceByKey((a, b) => (a._1 + b._1, a._2 + b._2, a._3 + b._3))
      .mapValues { case (totalDistance, totalFare, tripCount) =>
        (totalDistance / tripCount, totalFare / tripCount, tripCount)
      }

    // Collecting and sorting the results
    val sortedResults = statsByCommunity.collect().sortBy(_._1)

    // Formatting the results
    val intro = """
## Analysis 3 (Complex):

**"Can we identify communities/clusters of locations that are commonly connected through taxi trips?"**

- **Objective**: Discover clusters of locations that are frequently connected through taxi trips, indicating shared functional or social connections.
- **Approach**:
  - Construct a graph where nodes represent unique locations (geocoordinates rounded to a specific precision) and edges represent trips between these locations.
  - Apply the Label Propagation Algorithm (LPA) from Spark-GraphX to identify communities/clusters of locations.
  - Analyze the characteristics of these clusters, such as their geographical spread, average trip distance, average fare, etc.

We summarize the data in the following table:
  
"""

    val tableHeader =
      "| Community ID | Average Trip Distance | Average Fare | Number of Trips |\n| ------------- | --------------------- | ------------ | --------------- |\n"

    val tableBody = sortedResults
      .map { case (communityId, (avgDistance, avgFare, tripCount)) =>
        f"| $communityId | $avgDistance%.2f | $avgFare%.2f | $tripCount |"
      }
      .mkString("\n")

    intro + tableHeader + tableBody + "\n\n"
  }
}
