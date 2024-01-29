import org.apache.spark.graphx.{Edge, Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import java.io.{File, PrintWriter}

case class Station( id: String,  name: String,  latitude: Double, longitude: Double)

case class Trip( startTimestamp: Long,  endTimestamps: Long)

/*
Header of CSV :
ride_id,rideable_type,started_at,ended_at,start_station_name,start_station_id,end_station_name,end_station_id,start_lat,start_lng,end_lat,end_lng,member_casual
*/
object Main {
  /**
   * Convert a string to a long timestamp
   * @param tString : string to convert
   * @return : timestamp in long
   */
  private def timeToLong(tString: String): Long = {
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    format.parse(tString).getTime
  }


  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("graphXTP").setMaster("local[1]")
    val sc = new SparkContext(sparkConf)

    val mdString = new StringBuilder // String to write to the file

    mdString ++= "<h4 align=\"center\">Traitement du graphe temporel CityBike avec Spark-GraphX</h1>\n<h1 align=\"center\">Résultats générés</h4>\n\n"

    val bikeGraph = part1(sc)

    part2(mdString, bikeGraph)

    part3(mdString, bikeGraph)

    // Write the string to a file
    val pw = new PrintWriter(new File("results.md" ))
    pw.write(mdString.toString())
    pw.close()

  }

  private def part1(sc: SparkContext): Graph[Station, Trip] = {
    val DATASET_PATH = "data/"
    val trips = sc.textFile(DATASET_PATH + "JC-202112-citibike-tripdata.csv")
    val tripProcessed = trips
      .mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
      .filter(line => line.split(',').length == 13)
      .map(line => line.split(','))

      // remove rows with missing values
      .filter(row => !row.contains(""))

    // Créez un graphe dont les noeuds représentent des stations de vélos et les relations représentent des trajets de vélos entre deux stations.
    val stations: RDD[(VertexId, Station)] = tripProcessed.flatMap { row =>
      val startStation = Station(row(5), row(4), row(8).toDouble, row(9).toDouble)
      val endStation = Station(row(7), row(6), row(10).toDouble, row(11).toDouble)
      List((row(5).hashCode.toLong, startStation), (row(7).hashCode.toLong, endStation))
    }.distinct()

    val tripsRDD: RDD[Edge[Trip]] = tripProcessed.map { row =>
      Edge(row(5).hashCode.toLong, row(7).hashCode.toLong, Trip(timeToLong(row(2)), timeToLong(row(3))))
    }

    Graph(stations, tripsRDD)
  }


  private def part2(mdString: StringBuilder, bikeGraph: Graph[Station, Trip]): Unit = {
    mdString ++= "# 2️⃣ Trajets entre le 05-12-2021 et le 25-12-2021\n"


    // Extrayez le sous-graphe dont les intervalles temporelles de ces trajets (relations) existent entre 05-12- 2021 et 25-12-2021. (subgraph)
    val subGraph = bikeGraph.subgraph(epred = e => e.attr.startTimestamp > timeToLong("2021-12-05 00:00:00") && e.attr.startTimestamp < timeToLong("2021-12-25 00:00:00"))

    // Calculez le nombre total de trajets entrants et sortants de chaque station et affichez les 10 stations ayant
    // plus de trajets sortants et ceux ayants plus de trajets entrants. (aggregateMessages)

    // Calculate the total number of outgoing trips for each station
    val outgoingTrips = subGraph.aggregateMessages[Int](
      triplet => triplet.sendToSrc(1),
      (a, b) => a + b
    )

    // Calculate the total number of incoming trips for each station
    val incomingTrips = subGraph.aggregateMessages[Int](
      triplet => triplet.sendToDst(1),
      (a, b) => a + b
    )

    // Join with the stations RDD to get the station names
    val outgoingTripsWithNames = outgoingTrips.join(subGraph.vertices)
    val incomingTripsWithNames = incomingTrips.join(subGraph.vertices)

    // Get the top 10 stations with the most outgoing trips
    val topOutgoingStations = outgoingTripsWithNames.sortBy(_._2._1, ascending = false).take(10)

    // Get the top 10 stations with the most incoming trips
    val topIncomingStations = incomingTripsWithNames.sortBy(_._2._1, ascending = false).take(10)

    mdString ++= "## ⬅️ Top 10 des stations avec le plus de trajets sortants:\n"
    topOutgoingStations.foreach { case (_, (count, station)) =>
      mdString ++= s"- Station **${station.name}**: **$count** trajets\n"
    }

    mdString ++= "## ➡️ Top 10 des stations avec le plus de trajets entrants:\n"
    topIncomingStations.foreach { case (_, (count, station)) =>
      mdString ++= s"- Station **${station.name}**: **$count** trajets\n"
    }
  }

  private def part3(mdString: StringBuilder, bikeGraph: Graph[Station, Trip]): Unit = {
  /*
  1. Trouvez et affichez la station la plus proche de la station JC013 tel que la distance du trajet (relation) entre les deux stations soit minimale. (aggregateMessage)
  (Pour le calcul de distance, utilisez la fonction getDistKilometers).
   */

    /*
    2. Trouvez et affichez la station la plus proche de la station JC013 tel
    que la durée du trajet (relation) entre les deux stations soit minimale. (aggregateMessage)
     */

    mdString ++= "# 3️⃣ Proximité entre les stations\n"

    val JC013Id = "JC013".hashCode.toLong

    // Calculate the distance and duration of trips from JC013 to all other stations
    val tripDistancesAndDurations = bikeGraph.aggregateMessages[(Double, Long)](
      triplet => {
        if (triplet.srcId == JC013Id && triplet.dstId != JC013Id) {
          val distance = getDistKilometers(
            triplet.srcAttr.longitude, triplet.srcAttr.latitude,
            triplet.dstAttr.longitude, triplet.dstAttr.latitude
          )
          val duration = triplet.attr.endTimestamps - triplet.attr.startTimestamp
          triplet.sendToDst((distance, duration))
        }
      },
      (a, b) => (Math.min(a._1, b._1), Math.min(a._2, b._2)) // Keep the minimum distance and duration
    )

    // Join with the stations RDD to get the station names
    val tripDistancesAndDurationsWithNames = tripDistancesAndDurations.join(bikeGraph.vertices)

    // Find the station with the minimum distance and duration
    val minDistanceStation = tripDistancesAndDurationsWithNames.min()(Ordering.by(_._2._1._1))
    val minDurationStation = tripDistancesAndDurationsWithNames.min()(Ordering.by(_._2._1._2))

    // Convert duration from milliseconds to minutes
    val minDurationInMinutes = minDurationStation._2._1._2 / 60000.0

    // Print the results
    mdString ++= s"\n## La station la plus proche de `JC013` par distance :\n- Station ${minDistanceStation._2._2.name}: ${minDistanceStation._2._1._1} km\n"
    mdString ++= s"\n## La station la plus proche de `JC013` par durée de trajet :\n- Station ${minDurationStation._2._2.name}: $minDurationInMinutes ms\n"
  }

  private def part4(): Unit = {

  }
}


