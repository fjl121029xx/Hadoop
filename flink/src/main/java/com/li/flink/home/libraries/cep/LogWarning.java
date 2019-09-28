package com.li.flink.home.libraries.cep;

import com.li.flink.home.streaming.connector.KafkaConsumer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

import java.io.Serializable;
import java.util.*;

public class LogWarning {

    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


        DataStream<LoginEvent> loginEventStream = env.fromCollection(
                Arrays.asList(
                        new LoginEvent("1", "192.168.0.1", "fail"),
                        new LoginEvent("1", "192.168.0.1", "fail"),
                        new LoginEvent("1", "192.168.0.1", "fail"),
                        new LoginEvent("3", "192.168.0.2", "fail"),
                        new LoginEvent("4", "192.168.0.3", "fail"),
                        new LoginEvent("3", "192.168.0.2", "fail"),
                        new LoginEvent("5", "192.168.0.5", "fail"),
                        new LoginEvent("5", "192.168.0.5", "fail"),
                        new LoginEvent("5", "192.168.0.5", "fail"),
                        new LoginEvent("5", "192.168.0.5", "fail"),
                        new LoginEvent("5", "192.168.0.5", "fail"),
                        new LoginEvent("5", "192.168.0.5", "fail"),
                        new LoginEvent("5", "192.168.0.5", "fail"),
                        new LoginEvent("5", "192.168.0.5", "fail"),
                        new LoginEvent("5", "192.168.0.5", "fail"),
                        new LoginEvent("5", "192.168.0.5", "fail"),
                        new LoginEvent("2", "192.168.10,10", "fail"),
                        new LoginEvent("2", "192.168.10,10", "fail"),
                        new LoginEvent("2", "192.168.10,10", "success"),
                        new LoginEvent("2", "192.168.10,10", "fail")
                ));


        Pattern<LoginEvent, LoginEvent> loginFailPattern = Pattern.<LoginEvent>begin("start").where(
                new SimpleCondition<LoginEvent>() {
                    @Override
                    public boolean filter(LoginEvent le) throws Exception {
                        return le.type.equals("fail");
                    }
                }
        ).next("middle").subtype(LoginEvent.class).where(
                new SimpleCondition<LoginEvent>() {
                    @Override
                    public boolean filter(LoginEvent le) throws Exception {
                        return le.type.equals("fail");
                    }
                }
        ).followedBy("end").where(
                new SimpleCondition<LoginEvent>() {
                    @Override
                    public boolean filter(LoginEvent le) throws Exception {
                        return le.type.equals("fail");
                    }
                }
        ).within(Time.seconds(1));


//        Pattern<LoginEvent, LoginEvent> loginFailPattern = Pattern.<LoginEvent>
//                begin("start")
//                .where(new IterativeCondition<LoginEvent>() {
//                    @Override
//                    public boolean filter(LoginEvent loginEvent, Context context) throws Exception {
//                        return loginEvent.getType().equals("fail");
//                    }
//                })
//                .next("next")
//                .where(new IterativeCondition<LoginEvent>() {
//                    @Override
//                    public boolean filter(LoginEvent loginEvent, Context context) throws Exception {
//                        return loginEvent.getType().equals("fail");
//                    }
//                })
//                .timesOrMore(5);

        PatternStream<LoginEvent> patternStream = CEP.pattern(
                loginEventStream.keyBy(LoginEvent::getUser), loginFailPattern
        );

        DataStream<LoginWarning> loginFailDataStream = patternStream.select(new PatternSelectFunction<LoginEvent, LoginWarning>() {
            @Override
            public LoginWarning select(Map<String, List<LoginEvent>> map) throws Exception {

                List<LoginEvent> first = map.get("start");
                Iterator<LoginEvent> iterator = first.iterator();
                LoginEvent next = iterator.next();

                return new LoginWarning(next.user, next.type, next.ip);
            }
        });

        loginFailDataStream.print();
        env.execute("KafkaConsumer");
    }

    public static class LoginEvent implements Serializable {

        public String user;
        public String ip;
        public String type;

        public LoginEvent() {
        }

        public LoginEvent(String user, String ip, String type) {
            this.user = user;
            this.ip = ip;
            this.type = type;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class LoginWarning implements Serializable {
        private String userId;
        private String type;
        private String ip;

        public LoginWarning() {
        }

        public LoginWarning(String userId, String type, String ip) {
            this.userId = userId;
            this.type = type;
            this.ip = ip;
        }

        @Override
        public String toString() {
            return "LoginWarning{" +
                    "userId='" + userId + '\'' +
                    ", type='" + type + '\'' +
                    ", ip='" + ip + '\'' +
                    '}';
        }
    }


}




