package com.li.elaticsearch.java.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class EsUtil {


    private final static String HOST = "192.168.65.130";
    private final static int PORT = 9300;
    private final static String _cluster_name = "skynet_es_cluster";

    public final static String _index = "es1024";
    public final static String _type = "t_link";

    private Client client = null;


    public EsUtil() {

    }

    public void openConnectES() throws UnknownHostException {

        Settings settings = Settings.builder()
                .put("cluster.name", _cluster_name)
                .build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName(HOST), PORT));
    }

    public void closeConnectES() {
        if (client != null) {

            client.close();
            client = null;
        }
    }

    //创建索引index--类似于数据库
    public void createIndex() throws IOException {
        XContentBuilder mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("settings")
                .field("number_of_shards", 2)    //分片数量
                .field("number_of_replicas", 0)    //副本数量
                .endObject()
                .endObject()
                .startObject()
                .startObject("t_type")    //表名称
                .startObject("properties")    //列属性
                .startObject("type").field("type", "string").field("store", "yes")
                .endObject()
                .startObject("eventCount").field("type", "long").field("store", "yes")
                .endObject()
                .startObject("eventDate").field("type", "date")
                .field("format", "dateOptionalTime").field("stroe", "yes")
                .endObject()
                .startObject("message").field("type", "string")
                .field("index", "not_analyzed").field("stroe", "yes")
                .endObject()
                .endObject()
                .endObject()
                .endObject();
        CreateIndexRequestBuilder cirb = client.admin().indices()
                .prepareCreate("i_index")
                .setSource(mapping);
        CreateIndexResponse response = cirb.execute().actionGet();
        if (response.isAcknowledged()) {
            System.out.println("Index created.");
        } else {
            System.err.println("Index creation failed.");
        }

    }

    public void insert() throws IOException {
        IndexResponse response = client
                .prepareIndex("i_index", "t_type", "1")
                .setSource(
                        jsonBuilder().startObject()
                                .field("type", "syslog")
                                .field("eventCount", 1)
                                .field("eventDate", new Date())
                                .field("message", "i_index insert doc test")
                                .endObject()).get();
        System.out.println("index" + response.getIndex()
                + " insert doc id:" + response.getId()
                + " result:" + response.getResult());
    }

    public void query() throws UnknownHostException {
        GetResponse response = client.prepareGet("i_index", "t_type", "1").get();
        String source = response.getSource().toString();
        long version = response.getVersion();
        String indexName = response.getIndex();
        String type = response.getType();
        String id = response.getId();

//
        System.out.println(response);

    }

}
