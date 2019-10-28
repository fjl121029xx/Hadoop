package com.li.elaticsearch.spark.iteblog_hadoop

import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.EsSpark
import org.elasticsearch.spark.rdd.Metadata._

case class Trip(departure: String, arrival: String)

object EsSparkDemo {

  //  def getSc: SparkContext = {
  //    val conf = new SparkConf()
  //      .setAppName("EsSparkDemo")
  //      .setMaster("local")
  //
  //    val sc = new SparkContext(conf)
  //    sc
  //  }

  def main(args: Array[String]): Unit = {
    //    val sc = getSc
    val conf = new SparkConf()
      .setAppName("EsSparkDemo")
      .setMaster("local")
    conf.set("es.index.auto.create", "true")
    conf.set("es.nodes", "192.168.65.130")
    //---->如果是连接的远程es节点，该项必须要设置
    conf.set("es.port", "9200")
    conf.set("es.settings.number_of_shards","2")
    conf.set("es.settings.number_of_replicas","0")


    val sc = new SparkContext(conf)
    map2Es(sc)
  }

  def map2Es(sc: SparkContext): Unit = {
    val numbers = Map("one" -> 1, "two" -> 2, "three" -> 3)
    val airports = Map("OTP" -> "Otopeni", "SFO" -> "San Fran")
    val r = sc.makeRDD(Seq(numbers, airports))
    r.saveToEs("iteblog/docs")
    //    r.foreach(println)
  }

  def class2Es(sc: SparkContext): Unit = {
    val upcomingTrip = Trip("OTP", "SFO")
    val lastWeekTrip = Trip("MUC", "OTP")
    val rdd = sc.makeRDD(Seq(upcomingTrip, lastWeekTrip))
    //    rdd.saveToEs("iteblog/class")

    EsSpark.saveToEs(rdd, "spark/docs")
  }

  def json2Es(sc: SparkContext): Unit = {
    val json1 = """{"id" : 1, "blog" : "www.iteblog.com", "weixin" : "iteblog_hadoop"}"""
    val json2 = """{"id" : 2, "blog" : "books.iteblog.com", "weixin" : "iteblog_hadoop"}"""
    sc.makeRDD(Seq(json1, json2)).saveJsonToEs("iteblog/json")
  }

  def mutilInsert(sc: SparkContext): Unit = {
    val game = Map("media_type" -> "game", "title" -> "FF VI", "year" -> "1994")
    val book = Map("media_type" -> "book", "title" -> "Harry Potter", "year" -> "2010")
    val cd = Map("media_type" -> "music", "title" -> "Surfing With The Alien")
    sc.makeRDD(Seq(game, book, cd)).saveToEs("iteblog/{media_type}")
  }

  def divId(sc: SparkContext): Unit = {
    val json1 = """{"id" : 1, "blog" : "www.iteblog.com", "weixin" : "iteblog_hadoop"}"""
    val json2 = """{"id" : 2, "blog" : "books.iteblog.com", "weixin" : "iteblog_hadoop"}"""
    val rdd = sc.makeRDD(Seq(json1, json2))
    EsSpark.saveToEs(rdd, "iteblog/docs", Map("es.mapping.id" -> "id"))
  }

  def divMeta(sc: SparkContext): Unit = {
    val otp = Map("iata" -> "OTP", "name" -> "Otopeni")
    val muc = Map("iata" -> "MUC", "name" -> "Munich")
    val sfo = Map("iata" -> "SFO", "name" -> "San Fran")
    val otpMeta = Map(ID -> 1, TTL -> "3h")
    val mucMeta = Map(ID -> 2, VERSION -> "23")
    val sfoMeta = Map(ID -> 3)
    val airportsRDD = sc.makeRDD(Seq((otpMeta, otp), (mucMeta, muc), (sfoMeta, sfo)))
    airportsRDD.saveToEsWithMeta("iteblog/2015")
  }
}
