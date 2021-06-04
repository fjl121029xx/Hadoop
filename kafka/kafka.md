exec $(dirname $0)/kafka-run-class.sh kafka.tools.ConsoleConsumer --consumer.config ../config/security_consumer.properties "$@"

#username="admin"
#password="JRcWTh9K0Mvr"

#security
security.protocol=SASL_PLAINTEXT
sasl.mechanism=PLAIN
#sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="supply" password="s9HNPUgPtV1m";
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="admin" password="JRcWTh9K0Mvr";
#sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="order" password="U5Rzj7eOYLxy";

#sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="int" password="wurFqyX5tPq4";v
