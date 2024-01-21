import org.apache.spark.graphx.{Edge, Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}



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

  /**
   * Calculate the distance between two points in kilometers
   * @param lngDegreeA : longitude of point A
   * @param latDegreeA : latitude of point A
   * @param lngDegreeB : longitude of point B
   * @param latDegreeB : latitude of point B
   * @return : distance in kilometers
   */
  private def getDistKilometers(lngDegreeA : Double, latDegreeA : Double, lngDegreeB : Double, latDegreeB: Double) : Double = {

    val longRadiansA = Math.toRadians(lngDegreeA)
    val latRadiansA = Math.toRadians(latDegreeA)
    val longRadiansB = Math.toRadians(lngDegreeB)
    val latRadiansB = Math.toRadians(latDegreeB)

    val deltaLon = longRadiansB - longRadiansA
    val deltaLat = latRadiansB - latRadiansA
    val a = Math.pow(Math.sin(deltaLat / 2), 2) +
      Math.cos(latRadiansA) *
        Math.cos(latRadiansB) *
        Math.pow(Math.sin(deltaLon / 2), 2)

    val c = 2 * Math.asin(Math.sqrt(a))

    val r = 6371 // Radius of earth in kilometers
    c*r
  }


  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("graphXTP").setMaster("local[1]")
    val sc = new SparkContext(sparkConf)

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

    val bikeGraph = Graph(stations, tripsRDD)

    bikeGraph.vertices.collect()
    bikeGraph.edges.collect()

    // Extrayez le sous-graphe dont les intervalles temporelles de ces trajets (relations) existent entre 05-12- 2021 et 25-12-2021. (subgraph)
    val subGraph = bikeGraph.subgraph(epred = e => e.attr.startTimestamp > timeToLong("2021-12-05 00:00:00") && e.attr.startTimestamp < timeToLong("2021-12-25 00:00:00"))
    subGraph.triplets.foreach(t => println(s"🚴 ${t.srcAttr.name} -> ${t.dstAttr.name}"))
    // Calculez le nombre total de trajets entrants et sortants de chaque station et affichez les 10 stations ayant
    // plus de trajets sortants et ceux ayants plus de trajets entrants. (aggregateMessages)
    val outDegreeVertexRDD: VertexRDD[Int] = subGraph.aggregateMessages[Int](
      triplet => {
        triplet.sendToDst(1)
      },
      (a, b) => (a + b)
    )

    val inDegreeVertexRDD: VertexRDD[Int] = subGraph.aggregateMessages[Int](
      triplet => {
        triplet.sendToSrc(1)
      },
      (a, b) => (a + b)
    )

    // trajets sortants
    outDegreeVertexRDD
      .sortBy(_._2, ascending = false)
      .take(10)
      .map(t => s"${subGraph.vertices.filter(_._1 == t._1).first()._2.name} -> ${t._2}")
      .foreach(println)

    // trajets entrants
    inDegreeVertexRDD
      .sortBy(_._2, ascending = false)
      .take(10)
      .map(t => s"${t._2} -> ${subGraph.vertices.filter(_._1 == t._1).first()._2.name}")
      .foreach(println)

    // Trouvez et affichez la station la plus proche de la station JC013 tel que la distance du trajet (relation)
    // entre les deux stations soit minimale. (aggregateMessage) (Pour le calcul de distance, utilisez la
    // fonction getDistKilometers).

    val jc013 = bikeGraph.vertices.filter(_._2.id == "JC013").first()








  }
}
