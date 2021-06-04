if [ "x$KAFKA_HEAP_OPTS" = "x" ]; then
    export KAFKA_HEAP_OPTS="-Xmx512M"
fi
exec $(dirname $0)/kafka-run-class.sh kafka.tools.ConsoleProducer --producer.config ../config/security_producer.properties "$@"

#bootstrap.servers=localhost:9092
#compression.type=gzip
#security.protocol=SASL_PLAINTEXT
#sasl.mechanism=PLAIN
#sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="admin" password="JRcWTh9K0Mvr";
