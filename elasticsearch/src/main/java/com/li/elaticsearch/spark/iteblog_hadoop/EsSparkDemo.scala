package com.li.elaticsearch.spark.iteblog_hadoop

import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.EsSpark

case class Trip(departure: String, arrival: String)

object EsSparkDemo {

  def getSc: SparkContext = {
    val conf = new SparkConf()
      .setAppName("EsSparkDemo")
      .setMaster("local")

    val sc = new SparkContext(conf)
    sc
  }

  def main(args: Array[String]): Unit = {
    map2Es
  }

  def map2Es: Unit = {
    val sc = getSc
    val numbers = Map("one" -> 1, "two" -> 2, "three" -> 3)
    val airports = Map("OTP" -> "Otopeni", "SFO" -> "San Fran")
    val r = sc.makeRDD(Seq(numbers, airports))
    r.saveToEs("iteblog/docs")
  }

  def class2Es: Unit = {
    val sc = getSc
    val upcomingTrip = Trip("OTP", "SFO")
    val lastWeekTrip = Trip("MUC", "OTP")
    val rdd = sc.makeRDD(Seq(upcomingTrip, lastWeekTrip))
    //    rdd.saveToEs("iteblog/class")

    EsSpark.saveToEs(rdd, "spark/docs")
  }

  def json2Es: Unit = {
    val sc = getSc
    val json1 = """{"id" : 1, "blog" : "www.iteblog.com", "weixin" : "iteblog_hadoop"}"""
    val json2 = """{"id" : 2, "blog" : "books.iteblog.com", "weixin" : "iteblog_hadoop"}"""
    sc.makeRDD(Seq(json1, json2)).saveJsonToEs("iteblog/json")
  }


}
