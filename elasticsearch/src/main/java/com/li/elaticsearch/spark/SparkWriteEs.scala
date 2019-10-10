package com.li.elaticsearch.spark

import com.alibaba.fastjson.JSONObject
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.elasticsearch.spark.rdd.EsSpark
//↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
import org.elasticsearch.spark.sql._

object SparkWriteEs {

  def getSparkSession(): SparkSession = {

    val sparkConf = new SparkConf()
      .setAppName("writeEs")
      .setMaster("local")
      .set("es.index.auto.create", "true")
      .set("es.nodes", "ELASTIC_SEARCH_IP")
      .set("es.port", "9200")
      .set("es.nodes.wan.only", "true");

    val session = SparkSession.builder()
      .config(sparkConf)
      .getOrCreate()
    session
  }

  def main(args: Array[String]): Unit = {

    val session = getSparkSession

    val sc = session.sparkContext

    val j1 = new JSONObject()
    j1.put("name", "tome")

    val arr = Array(j1)

    val stuText = sc.parallelize(arr)

    EsSpark.saveToEs(stuText, "")

  }

  def readEs() = {
    val spark = getSparkSession()
    val startTime = "1519660800000"
    val endTime = "1519747200000"
    val query = "{\"query\":{\"range\":{\"recordtime\":{\"gte\":" + startTime + ",\"lte\":" + endTime + "}}}}"
    val tableName = "_index/_type"
    spark.esDF(tableName, query)
  }

  def writeEs(): Unit = {

    val spark = getSparkSession()
    val dataFrame = spark.createDataFrame(Seq(
      (1, 1, "2", "5"),
      (2, 2, "3", "6"),
      (3, 2, "36", "69")
    )).toDF("id", "label", "col1", "col2")
    dataFrame.saveToEs("_index/_type", Map("es.mapping.id" -> "id"))
  }

  def updateEs(): Unit = {
    val spark = getSparkSession()
    val dataFrame1 = spark.createDataFrame(Seq(
      (3, 2, "4", "7")
    )).toDF("id", "label", "col1", "col2")
    dataFrame1.saveToEs("_index/_type", Map("es.mapping.id" -> "id"))
  }
}
