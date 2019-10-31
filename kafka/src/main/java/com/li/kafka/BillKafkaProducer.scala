package com.li.kafka

import java.io.File
import java.util.Properties

import com.alibaba.fastjson.JSON
import net.sf.json.JSONObject
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerConfig, ProducerRecord, RecordMetadata}

object BillKafkaProducer {

  //private val BROKER_LIST ="localhost:9092"
  private val BROKER_LIST = "172.20.119.175:9092,172.20.47.150:9092,172.20.101.187:9092"
  private val TARGET_TOPIC = Array("bd_canal_order_tbl_order_master")

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

  //  props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"dw\" password=\"dw123\";")

  def main(args: Array[String]) {
    System.setProperty("java.security.auth.login.config", "D:\\kafka_client_jaas.conf")
    val producer = new KafkaProducer[String, String](props)

    val arr = Array("a", "b")
    var count_a = 0
    var count_b = 0
    val s = System.currentTimeMillis()
    //    println(JSON.parseObject(stuJson).toJSONString)
    for (i <- 1 to 100) {
      //      val index = if (Math.random() > 0.5) 1 else 0
      //
      //      val value = arr(index)
      //      if (value.eq("a")) {
      //        count_a += 1
      //      } else if (value.eq("b")) {
      //        count_b += 13
      //      }

      //      for (j <- 1 to 3) {
      val message = new ProducerRecord[String, String](this.TARGET_TOPIC(0), "{\"id\":1" + (i * i) + ",\"name\":\"stu_" + i + "\",\"score\":9.02}")
      producer.send(message)
      //      }

//      val message2 = new ProducerRecord[String, String](this.TARGET_TOPIC(0), "{\"i2d\":1" + i + ",\"name\":\"stu_" + i + "\",\"score\":9.02}")
//      producer.send(message2)
      //            Thread.sleep((Math.random() * 1L).toLong)
      //      Thread.sleep(1000L)

      println(i)
    }

    println("------------------------------------------------------------------------------------")
    val e = System.currentTimeMillis()
    println(e - s)
    println(count_a)
    println(count_b)
    producer.close()
  }
}
