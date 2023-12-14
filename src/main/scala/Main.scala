import org.apache.spark.{SparkConf, SparkContext}


object Main {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("graphXTP").setMaster("local[1]")
    val sc = new SparkContext(sparkConf)


    // Chargez le jeu de données. (voir textFile)
    val data = sc.textFile("data/agg_match_stats_0_100000.csv")

    // date,game_size,match_id,match_mode,party_size,player_assists,player_dbno,player_dist_ride,player_dist_walk,player_dmg,player_kills,player_name,player_survive_time,team_id,team_placement
    val headerRow = data.first()
    val caract_rdd_clean = data.filter(row => row(0) != headerRow(0))

    // Pour chaque partie, obtenez uniquement le nom du joueur et son nombre d’éliminations ou sa position.
    val caract_rdd = caract_rdd_clean
      .map(row => row.split(","))
      .map(row => (row(2), (row(11).toInt, row(12))))
    // Obtenez la moyenne des éliminations ou de la position de chaque joueur, ainsi que le nombre de parties concernées. (voir reduceByKey ou groupByKey)

  }
}
