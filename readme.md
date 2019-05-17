## Kafka Streams (2.2.0) Environment Preparation

### Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

###S tart Kafka Server
bin/kafka-server-start.sh config/server.properties

### Create Input Topic
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-input

### Create Output Topic
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 2 --topic word-count-output

### Create Upstream Producer of Input Topic (where we write messages)
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic word-count-input

### Create Downstream Consumer of Output Topic (where we receive messages)
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic word-count-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
