package com.li.kafka.hll

import java.io.File
import java.util.Properties
import net.sf.json.JSONObject
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import scala.io.Source

/**
 * Created by Administrator on 2018/7/3.
 */
object KafkaProducer {

  //private val BROKER_LIST ="localhost:9092"
  private val BROKER_LIST = "172.20.119.175:9092,172.20.47.150:9092,172.20.101.187:9092"
  private val TARGET_TOPIC = Array("bd_dw_dohkoTest")
  private val DIR = "D://data"
  private val props = new Properties()
  props.put("bootstrap.servers", BROKER_LIST)
  props.put("acks", "all")
  props.put("retries", "0")
  props.put("batch.size", "16384")
  props.put("linger.ms", "100")
  props.put("buffer.memory", "33554432")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("security.protocol", "SASL_PLAINTEXT")
  props.put("sasl.mechanism", "PLAIN")

  import org.apache.kafka.common.config.SaslConfigs

  props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"dw\" password=\"dw123\";")

  def main(args: Array[String]) {
    //    System.setProperty("java.security.auth.login.config", "D:\\kafka_client_jaas.conf")
    val file = new File("E:\\back")
    val files = file.listFiles()
    // for(file <- files){
    val reader = Source.fromFile("D:\\work\\workspaces\\btestilldata.txt", "GBK")
    //val reader=Source.fromFile("E:\\spark\\ods\\sourcebak\\2.txt")
    val producer = new KafkaProducer[String, String](props)
    var incre: Int = 0
    for (line <- reader.getLines()) {
      val data = JSONObject.fromObject(line)
      if (data.has("master")) {
        val jsonObject = data.getJSONObject("master")
        val shopID = jsonObject.getString("shopID")
        val orderKey = jsonObject.getString("orderKey")
        val key = shopID + "-" + orderKey
        val message = new ProducerRecord[String, String](this.TARGET_TOPIC(0), key, line)
        producer.send(message)
        incre = incre.+(1)
        println(incre + "--------")
      }
    }
    producer.close()
    //file.delete()
    println(incre)
    // }
  }
}
